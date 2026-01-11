package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.adapter.AttendanceMarkAdapter;
import com.example.kuet_academic_portal.model.Student;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkAttendanceActivity extends AppCompatActivity {

    private EditText etCourse, etTeacher, etDepartment, etTerm, etYear, etSelectedDate;
    private Button btnSelectDate, btnLoadStudents, btnSubmitAttendance, btnBack;
    private RecyclerView recyclerViewStudents;
    private ProgressBar progressBar;

    private AttendanceMarkAdapter adapter;
    private List<Student> studentList;
    private FirebaseFirestore db;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        db = FirebaseFirestore.getInstance();
        studentList = new ArrayList<>();
        selectedDate = new Date();

        initializeViews();
        setupRecyclerView();
        setupListeners();
    }

    private void initializeViews() {
        etCourse = findViewById(R.id.etCourse);
        etTeacher = findViewById(R.id.etTeacher);
        etDepartment = findViewById(R.id.etDepartment);
        etTerm = findViewById(R.id.etTerm);
        etYear = findViewById(R.id.etYear);
        etSelectedDate = findViewById(R.id.etSelectedDate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnLoadStudents = findViewById(R.id.btnLoadStudents);
        btnSubmitAttendance = findViewById(R.id.btnSubmitAttendance);
        btnBack = findViewById(R.id.btnBack);
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        progressBar = findViewById(R.id.progressBar);

        Calendar cal = Calendar.getInstance();
        etSelectedDate.setText(formatDate(cal.getTime()));
    }

    private void setupRecyclerView() {
        adapter = new AttendanceMarkAdapter(studentList);
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStudents.setAdapter(adapter);
    }

    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnLoadStudents.setOnClickListener(v -> loadStudents());
        btnSubmitAttendance.setOnClickListener(v -> submitAttendance());
        btnBack.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = new Date(selection);
            etSelectedDate.setText(formatDate(selectedDate));
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.format("%02d/%02d/%d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR));
    }

    private void loadStudents() {
        String department = etDepartment.getText().toString().trim();
        String term = etTerm.getText().toString().trim();
        String year = etYear.getText().toString().trim();

        if (department.isEmpty() || term.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please enter department, term and year", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        try {
            
            int termInt = Integer.parseInt(term);
            int yearInt = Integer.parseInt(year);

            android.util.Log.d("MarkAttendance", "Loading students: dept=" + department + ", term=" + termInt + ", year=" + yearInt);

            db.collection("Students")
                .whereEqualTo("department", department)
                .whereEqualTo("term", termInt)
                .whereEqualTo("year", yearInt)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    studentList.clear();
                    android.util.Log.d("MarkAttendance", "Query returned " + queryDocumentSnapshots.size() + " documents");

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Student student = document.toObject(Student.class);
                            student.setId(document.getId());
                            studentList.add(student);
                            android.util.Log.d("MarkAttendance", "Added student: " + student.getName() + " (Roll: " + student.getRoll() + ")");
                        } catch (Exception e) {
                            android.util.Log.e("MarkAttendance", "Error parsing student document: " + document.getId(), e);
                            Toast.makeText(this, "Error parsing student data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (studentList.isEmpty()) {
                        Toast.makeText(this, "No students found for Department: " + department + ", Term: " + term + ", Year: " + year, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Loaded " + studentList.size() + " students", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("MarkAttendance", "Error loading students", e);
                    Toast.makeText(this, "Error loading students: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Term and Year must be valid numbers", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void submitAttendance() {
        String course = etCourse.getText().toString().trim();
        String teacher = etTeacher.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String term = etTerm.getText().toString().trim();
        String year = etYear.getText().toString().trim();

        if (course.isEmpty() || teacher.isEmpty() || department.isEmpty() || term.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (studentList.isEmpty()) {
            Toast.makeText(this, "Please load students first", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitAttendance.setEnabled(false); 
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> attendanceStatus = adapter.getAttendanceStatus();

        int totalStudents = studentList.size();
        final int[] processedCount = {0};

        for (Student student : studentList) {
            String status = attendanceStatus.get(student.getRoll());
            if (status == null) status = "Absent";

            Map<String, Object> attendance = new HashMap<>();

            try {
                attendance.put("roll", Long.parseLong(student.getRoll()));  
            } catch (NumberFormatException e) {
                attendance.put("roll", student.getRoll()); 
            }

            attendance.put("studentName", student.getName());
            attendance.put("course", course);
            attendance.put("teacher", teacher);
            attendance.put("date", new Timestamp(selectedDate));
            attendance.put("status", status);
            attendance.put("department", department);
            attendance.put("term", Integer.parseInt(term));
            attendance.put("year", Integer.parseInt(year));

            db.collection("attendance")
                .add(attendance)
                .addOnSuccessListener(documentReference -> {
                    processedCount[0]++;
                    if (processedCount[0] == totalStudents) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmitAttendance.setEnabled(true);
                        Toast.makeText(this, "Attendance submitted successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    processedCount[0]++;
                    if (processedCount[0] == totalStudents) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmitAttendance.setEnabled(true);
                        Toast.makeText(this, "Some errors occurred", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }
}
