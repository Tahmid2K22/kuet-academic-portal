package com.example.kuet_academic_portal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kuet_academic_portal.model.StudentSession;
import com.example.kuet_academic_portal.session.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAttendanceActivity extends AppCompatActivity {

    private BarChart chartSubjectWise, chartMonthly, chartPresentAbsent;
    private ProgressBar progressBar;
    private TextView tvStats, tvStudentInfo;
    private Button btnBack;
    private FirebaseFirestore db;
    private SessionManager sessionManager;
    private String studentRoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        StudentSession session = sessionManager.getSession();
        if (session == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        studentRoll = session.getRoll();

        initializeViews();
        setupCharts();
        displayStudentInfo(session);
        loadAttendanceData();
    }

    private void initializeViews() {
        chartSubjectWise = findViewById(R.id.chartSubjectWise);
        chartMonthly = findViewById(R.id.chartMonthly);
        chartPresentAbsent = findViewById(R.id.chartPresentAbsent);
        progressBar = findViewById(R.id.progressBar);
        tvStats = findViewById(R.id.tvStats);
        tvStudentInfo = findViewById(R.id.tvStudentInfo);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupCharts() {
        setupBarChart(chartSubjectWise, "Subject-wise Attendance %");
        setupBarChart(chartMonthly, "Monthly Attendance Count");
        setupBarChart(chartPresentAbsent, "Present vs Absent");
    }

    private void setupBarChart(BarChart chart, String description) {
        chart.getDescription().setText(description);
        chart.getDescription().setTextSize(12f);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(true);
    }

    private void displayStudentInfo(StudentSession session) {
        String info = "Name: " + session.getName() + "\n" +
                     "Roll: " + session.getRoll() + "\n" +
                     "Department: " + session.getDepartment() + "\n" +
                     "Semester: " + session.getTerm();
        tvStudentInfo.setText(info);
    }

    private void loadAttendanceData() {
        progressBar.setVisibility(View.VISIBLE);

        try {
            long rollNumber = Long.parseLong(studentRoll);

            db.collection("attendance")
                .whereEqualTo("roll", rollNumber)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Toast.makeText(this, "Found " + queryDocumentSnapshots.size() + " attendance records for roll: " + rollNumber, Toast.LENGTH_LONG).show();

                    Map<String, Integer> coursePresent = new HashMap<>();
                    Map<String, Integer> courseTotal = new HashMap<>();
                    Map<String, Integer> monthlyPresent = new HashMap<>();
                    int totalPresent = 0;
                    int totalAbsent = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Log each document for debugging
                        String course = document.getString("course");
                        String status = document.getString("status");
                        Long rollFromDoc = document.getLong("roll");
                        com.google.firebase.Timestamp timestamp = document.getTimestamp("date");

                        // Debug log
                        android.util.Log.d("AttendanceDebug", "Document: course=" + course + ", status=" + status + ", roll=" + rollFromDoc + ", date=" + timestamp);

                        if (course != null && status != null) {
                            courseTotal.put(course, courseTotal.getOrDefault(course, 0) + 1);

                            if ("Present".equals(status)) {
                                coursePresent.put(course, coursePresent.getOrDefault(course, 0) + 1);
                                totalPresent++;

                                if (timestamp != null) {
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(timestamp.toDate());
                                    String month = getMonthName(cal.get(Calendar.MONTH));
                                    monthlyPresent.put(month, monthlyPresent.getOrDefault(month, 0) + 1);
                                }
                            } else {
                                totalAbsent++;
                            }
                        } else {
                            android.util.Log.w("AttendanceDebug", "Null course or status in document: " + document.getId());
                        }
                    }

                    android.util.Log.d("AttendanceDebug", "Total Present: " + totalPresent + ", Total Absent: " + totalAbsent);
                    android.util.Log.d("AttendanceDebug", "Course totals: " + courseTotal.toString());

                    if (totalPresent == 0 && totalAbsent == 0) {
                        Toast.makeText(this, "No attendance data found. Data may still be loading or no records exist.", Toast.LENGTH_LONG).show();
                        tvStats.setText("No attendance records found.\n\nRoll: " + studentRoll + "\n\nPlease check if attendance has been marked for this student.");
                    }

                    displaySubjectWiseChart(coursePresent, courseTotal);
                    displayMonthlyChart(monthlyPresent);
                    displayPresentAbsentChart(totalPresent, totalAbsent);
                    displayStats(totalPresent, totalAbsent, courseTotal.size());

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid roll number format", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void displaySubjectWiseChart(Map<String, Integer> coursePresent, Map<String, Integer> courseTotal) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : courseTotal.entrySet()) {
            String course = entry.getKey();
            int total = entry.getValue();
            int present = coursePresent.getOrDefault(course, 0);
            float percentage = (present * 100f) / total;

            entries.add(new BarEntry(index, percentage));
            labels.add(course);
            index++;
        }

        if (entries.isEmpty()) {
            chartSubjectWise.clear();
            chartSubjectWise.setNoDataText("No data available");
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Attendance %");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        chartSubjectWise.setData(barData);
        chartSubjectWise.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartSubjectWise.getXAxis().setLabelCount(labels.size());
        chartSubjectWise.invalidate();
    }

    private void displayMonthlyChart(Map<String, Integer> monthlyPresent) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : monthlyPresent.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        if (entries.isEmpty()) {
            chartMonthly.clear();
            chartMonthly.setNoDataText("No data available");
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Present Count");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        chartMonthly.setData(barData);
        chartMonthly.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartMonthly.getXAxis().setLabelCount(labels.size());
        chartMonthly.invalidate();
    }

    private void displayPresentAbsentChart(int present, int absent) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, present));
        entries.add(new BarEntry(1, absent));

        List<String> labels = new ArrayList<>();
        labels.add("Present");
        labels.add("Absent");

        BarDataSet dataSet = new BarDataSet(entries, "Count");
        dataSet.setColors(new int[]{Color.GREEN, Color.RED});
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        chartPresentAbsent.setData(barData);
        chartPresentAbsent.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartPresentAbsent.getXAxis().setLabelCount(2);
        chartPresentAbsent.getAxisLeft().setAxisMinimum(0f);
        chartPresentAbsent.invalidate();
    }

    private void displayStats(int present, int absent, int subjects) {
        int total = present + absent;
        float percentage = total > 0 ? (present * 100f) / total : 0;

        String stats = String.format(
            "Total Classes: %d\nPresent: %d\nAbsent: %d\nOverall Attendance: %.2f%%\nSubjects: %d",
            total, present, absent, percentage, subjects
        );

        tvStats.setText(stats);
    }

    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }
}

