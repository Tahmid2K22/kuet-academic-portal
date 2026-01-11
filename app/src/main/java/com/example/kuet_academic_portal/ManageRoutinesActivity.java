package com.example.kuet_academic_portal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.adapter.RoutineAdapter;
import com.example.kuet_academic_portal.model.Routine;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ManageRoutinesActivity extends AppCompatActivity {

    private EditText etDepartment, etYear, etTerm;
    private Button btnLoad;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RoutineAdapter adapter;
    private List<Routine> routineList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_routines);

        db = FirebaseFirestore.getInstance();
        routineList = new ArrayList<>();

        initializeViews();
        setupRecyclerView();

        btnLoad.setOnClickListener(v -> loadRoutines());
    }

    private void initializeViews() {
        etDepartment = findViewById(R.id.etDepartment);
        etYear = findViewById(R.id.etYear);
        etTerm = findViewById(R.id.etTerm);
        btnLoad = findViewById(R.id.btnLoad);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        adapter = new RoutineAdapter(routineList, this::showOptionsDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void showOptionsDialog(Routine routine) {
        String[] options = {"Edit", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle("Select Action")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        
                        Intent intent = new Intent(ManageRoutinesActivity.this, AddRoutineActivity.class);
                        intent.putExtra("id", routine.getId());
                        intent.putExtra("department", routine.getDepartment());
                        intent.putExtra("year", routine.getYear());
                        intent.putExtra("term", routine.getTerm());
                        intent.putExtra("day", routine.getDay());
                        intent.putExtra("startTime", routine.getStartTime());
                        intent.putExtra("endTime", routine.getEndTime());
                        intent.putExtra("courseCode", routine.getCourseCode());
                        intent.putExtra("teacher", routine.getTeacher());
                        intent.putExtra("room", routine.getRoom());
                        startActivity(intent);
                    } else {
                        
                        deleteRoutine(routine);
                    }
                })
                .show();
    }

    private void deleteRoutine(Routine routine) {
        if (routine.getId() == null) return;
        progressBar.setVisibility(View.VISIBLE);
        db.collection("routines").document(routine.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Routine deleted", Toast.LENGTH_SHORT).show();
                    loadRoutines(); 
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error deleting routine", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!routineList.isEmpty()) {
            loadRoutines();
        }
    }

    private void loadRoutines() {
        String department = etDepartment.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String termStr = etTerm.getText().toString().trim();

        if (TextUtils.isEmpty(department) || TextUtils.isEmpty(yearStr) || TextUtils.isEmpty(termStr)) {
            Toast.makeText(this, "Please fill Department, Year and Term", Toast.LENGTH_SHORT).show();
            return;
        }

        int year, term;
        try {
            year = Integer.parseInt(yearStr);
            term = Integer.parseInt(termStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        db.collection("routines")
                .whereEqualTo("department", department)
                .whereEqualTo("year", year)
                .whereEqualTo("term", term)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    routineList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            try {
                                Routine routine = new Routine();
                                routine.setId(doc.getId());
                                routine.setDepartment(doc.getString("department"));
                                routine.setDay(doc.getString("day"));
                                routine.setStartTime(doc.getString("startTime"));
                                routine.setEndTime(doc.getString("endTime"));
                                routine.setCourseCode(doc.getString("courseCode"));
                                routine.setTeacher(doc.getString("teacher"));
                                routine.setRoom(doc.getString("room"));

                                
                                Object yearObj = doc.get("year");
                                if (yearObj instanceof Number) {
                                    routine.setYear(((Number) yearObj).intValue());
                                } else if (yearObj instanceof String) {
                                    try {
                                        routine.setYear(Integer.parseInt((String) yearObj));
                                    } catch (NumberFormatException e) {
                                        routine.setYear(0);
                                    }
                                }

                                Object termObj = doc.get("term");
                                if (termObj instanceof Number) {
                                    routine.setTerm(((Number) termObj).intValue());
                                } else if (termObj instanceof String) {
                                    try {
                                        routine.setTerm(Integer.parseInt((String) termObj));
                                    } catch (NumberFormatException e) {
                                        routine.setTerm(0);
                                    }
                                }

                                routineList.add(routine);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        
                        sortRoutines(routineList);

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No routines found", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading routines: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sortRoutines(List<Routine> routines) {
        List<String> order = new ArrayList<>();
        order.add("Sunday");
        order.add("Monday");
        order.add("Tuesday");
        order.add("Wednesday");
        order.add("Thursday");
        order.add("Friday");
        order.add("Saturday");

        routines.sort((r1, r2) -> {
            String day1Str = r1.getDay() != null ? r1.getDay() : "";
            String day2Str = r2.getDay() != null ? r2.getDay() : "";
            int day1 = order.indexOf(day1Str);
            int day2 = order.indexOf(day2Str);

            if (day1 != day2) {
                return Integer.compare(day1, day2);
            }

            String time1 = r1.getStartTime() != null ? r1.getStartTime() : "";
            String time2 = r2.getStartTime() != null ? r2.getStartTime() : "";
            return time1.compareTo(time2);
        });
    }
}
