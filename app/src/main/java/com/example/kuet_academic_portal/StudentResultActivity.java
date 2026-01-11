package com.example.kuet_academic_portal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kuet_academic_portal.model.Result;
import com.example.kuet_academic_portal.model.StudentSession;
import com.example.kuet_academic_portal.session.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentResultActivity extends AppCompatActivity {

    private BarChart chartCGPA, chartCT;
    private Spinner spinnerTerms;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String studentRoll;
    private List<Result> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_result);

        
        SessionManager sessionManager = new SessionManager(this);
        StudentSession session = sessionManager.getSession();
        if (session == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        studentRoll = session.getRoll();

        db = FirebaseFirestore.getInstance();
        resultList = new ArrayList<>();

        initializeViews();
        setupCharts();
        loadResults();
    }

    private void initializeViews() {
        chartCGPA = findViewById(R.id.chartCGPA);
        chartCT = findViewById(R.id.chartCT);
        spinnerTerms = findViewById(R.id.spinnerTerms);
        progressBar = findViewById(R.id.progressBar);

        spinnerTerms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!resultList.isEmpty() && position < resultList.size()) {
                    displayCTChart(resultList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupCharts() {
        configureChart(chartCGPA);
        configureChart(chartCT);
    }

    private void configureChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
    }

    private void loadResults() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("results")
                .whereEqualTo("roll", studentRoll)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    resultList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            
                            Result result = new Result();
                            result.setRoll(doc.getString("roll"));

                            
                            Object yearObj = doc.get("year");
                            if (yearObj instanceof Number) {
                                result.setYear(((Number) yearObj).intValue());
                            } else if (yearObj instanceof String) {
                                try {
                                    result.setYear(Integer.parseInt((String) yearObj));
                                } catch (NumberFormatException e) {
                                    result.setYear(0);
                                }
                            }

                            
                            Object termObj = doc.get("term");
                            if (termObj instanceof Number) {
                                result.setTerm(((Number) termObj).intValue());
                            } else if (termObj instanceof String) {
                                try {
                                    result.setTerm(Integer.parseInt((String) termObj));
                                } catch (NumberFormatException e) {
                                    result.setTerm(0);
                                }
                            }

                            
                            Double gpa = doc.getDouble("gpa");
                            result.setGpa(gpa);

                            
                            Map<String, Object> ctMarksRaw = (Map<String, Object>) doc.get("ctMarks");
                            if (ctMarksRaw != null) {
                                Map<String, Float> ctMarks = new HashMap<>();
                                for (Map.Entry<String, Object> entry : ctMarksRaw.entrySet()) {
                                    if (entry.getValue() instanceof Number) {
                                        ctMarks.put(entry.getKey(), ((Number) entry.getValue()).floatValue());
                                    }
                                }
                                result.setCtMarks(ctMarks);
                            }

                            resultList.add(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    
                    resultList.sort(Comparator.comparingInt(Result::getYear)
                            .thenComparingInt(Result::getTerm));

                    if (resultList.isEmpty()) {
                        Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
                    } else {
                        displayCGPAChart();
                        setupSpinner();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading results", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayCGPAChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            Result result = resultList.get(i);
            float gpaVal = result.getGpa() != null ? result.getGpa().floatValue() : 0f;
            entries.add(new BarEntry(i, gpaVal));
            labels.add("Y" + result.getYear() + "-" + "T" + result.getTerm());
        }

        BarDataSet dataSet = new BarDataSet(entries, "CGPA");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        chartCGPA.setData(barData);
        chartCGPA.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartCGPA.getXAxis().setLabelCount(labels.size());
        chartCGPA.invalidate();
    }

    private void setupSpinner() {
        List<String> terms = new ArrayList<>();
        for (Result r : resultList) {
            terms.add("Year " + r.getYear() + " Term " + r.getTerm());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, terms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTerms.setAdapter(adapter);
    }

    private void displayCTChart(Result result) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Map<String, Float> ctMarks = result.getCtMarks();

        if (ctMarks == null || ctMarks.isEmpty()) {
            chartCT.clear();
            chartCT.setNoDataText("No CT marks available for this term");
            chartCT.invalidate();
            return;
        }

        int index = 0;
        for (Map.Entry<String, Float> entry : ctMarks.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "CT Marks");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f); 

        chartCT.setData(barData);
        chartCT.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartCT.getXAxis().setLabelCount(labels.size());
        chartCT.invalidate();
    }
}
