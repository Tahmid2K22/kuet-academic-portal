package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.adapter.RoutineAdapter;
import com.example.kuet_academic_portal.model.Routine;
import com.example.kuet_academic_portal.model.StudentSession;
import com.example.kuet_academic_portal.session.SessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class cls_routine extends AppCompatActivity {

    private RoutineAdapter adapter;
    private List<Routine> routineList;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cls_routine);

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);
        routineList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRoutine);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView tvNoRoutine = findViewById(R.id.tvNoRoutine);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoutineAdapter(routineList);
        recyclerView.setAdapter(adapter);

        loadRoutine(progressBar, tvNoRoutine);
    }

    private void loadRoutine(ProgressBar progressBar, TextView tvNoRoutine) {
        StudentSession session = sessionManager.getSession();
        if (session == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            
            return;
        }

        String department = session.getDepartment();
        int year = session.getYear();
        int term = session.getTerm();

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
                        tvNoRoutine.setVisibility(View.GONE);
                    } else {
                        tvNoRoutine.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading routine", Toast.LENGTH_SHORT).show();
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
            int day1 = order.indexOf(r1.getDay());
            int day2 = order.indexOf(r2.getDay());
            if (day1 != day2) {
                return Integer.compare(day1, day2);
            }
            return r1.getStartTime().compareTo(r2.getStartTime());
        });
    }
}
