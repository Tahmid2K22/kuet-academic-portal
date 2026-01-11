package com.example.kuet_academic_portal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAttendanceAnalyticsActivity extends AppCompatActivity {

    private EditText etDepartment, etTerm, etYear;
    private Button btnLoadAnalytics, btnBack;
    private BarChart chartSubjectWise, chartPresentAbsent;
    private ProgressBar progressBar;
    private TextView tvStats;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance_analytics);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupListeners();
        setupCharts();
    }

    private void initializeViews() {
        etDepartment = findViewById(R.id.etDepartment);
        etTerm = findViewById(R.id.etTerm);
        etYear = findViewById(R.id.etYear);
        btnLoadAnalytics = findViewById(R.id.btnLoadAnalytics);
        btnBack = findViewById(R.id.btnBack);
        chartSubjectWise = findViewById(R.id.chartSubjectWise);
        chartPresentAbsent = findViewById(R.id.chartPresentAbsent);
        progressBar = findViewById(R.id.progressBar);
        tvStats = findViewById(R.id.tvStats);
    }

    private void setupListeners() {
        btnLoadAnalytics.setOnClickListener(v -> loadAnalytics());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupCharts() {
        setupBarChart(chartSubjectWise, "Subject-wise Attendance %");
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

    private void loadAnalytics() {
        String department = etDepartment.getText().toString().trim();
        String term = etTerm.getText().toString().trim();
        String year = etYear.getText().toString().trim();

        if (department.isEmpty() || term.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please enter department, term and year", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        
        int termInt = Integer.parseInt(term);
        int yearInt = Integer.parseInt(year);

        db.collection("attendance")
            .whereEqualTo("department", department)
            .whereEqualTo("term", termInt)
            .whereEqualTo("year", yearInt)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Map<String, Integer> coursePresent = new HashMap<>();
                Map<String, Integer> courseTotal = new HashMap<>();
                int totalPresent = 0;
                int totalAbsent = 0;

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String course = document.getString("course");
                    String status = document.getString("status");

                    if (course != null && status != null) {
                        courseTotal.put(course, courseTotal.getOrDefault(course, 0) + 1);

                        if ("Present".equals(status)) {
                            coursePresent.put(course, coursePresent.getOrDefault(course, 0) + 1);
                            totalPresent++;
                        } else {
                            totalAbsent++;
                        }
                    }
                }

                displaySubjectWiseChart(coursePresent, courseTotal);
                displayPresentAbsentChart(totalPresent, totalAbsent);
                displayStats(totalPresent, totalAbsent, courseTotal.size());

                progressBar.setVisibility(View.GONE);

                if (totalPresent == 0 && totalAbsent == 0) {
                    Toast.makeText(this, "No attendance records found for Department: " + department + ", Term: " + term + ", Year: " + year, Toast.LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading analytics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
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
        chartPresentAbsent.invalidate();
    }

    private void displayStats(int present, int absent, int subjects) {
        int total = present + absent;
        float percentage = total > 0 ? (present * 100f) / total : 0;

        String stats = String.format(
            "Total Records: %d\nPresent: %d\nAbsent: %d\nOverall Attendance: %.2f%%\nSubjects: %d",
            total, present, absent, percentage, subjects
        );

        tvStats.setText(stats);
    }
}

