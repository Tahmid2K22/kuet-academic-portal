package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AddResultActivity extends AppCompatActivity {

    private EditText etRoll, etYear, etTerm, etGpa, etCtMarks;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_result);

        db = FirebaseFirestore.getInstance();

        etRoll = findViewById(R.id.etRoll);
        etYear = findViewById(R.id.etYear);
        etTerm = findViewById(R.id.etTerm);
        etGpa = findViewById(R.id.etGpa);
        etCtMarks = findViewById(R.id.etCtMarks);
        Button btnSaveResult = findViewById(R.id.btnSaveResult);

        btnSaveResult.setOnClickListener(v -> saveResult());
    }

    private void saveResult() {
        String roll = etRoll.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String termStr = etTerm.getText().toString().trim();
        String gpaStr = etGpa.getText().toString().trim();
        String ctMarksStr = etCtMarks.getText().toString().trim();

        if (TextUtils.isEmpty(roll) || TextUtils.isEmpty(yearStr) || TextUtils.isEmpty(termStr)) {
            Toast.makeText(this, "Please fill Roll, Year and Term", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(gpaStr) && TextUtils.isEmpty(ctMarksStr)) {
            Toast.makeText(this, "Please provide either GPA or CT Marks", Toast.LENGTH_SHORT).show();
            return;
        }

        int year, term;
        try {
            year = Integer.parseInt(yearStr);
            term = Integer.parseInt(termStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format for Year or Term", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("roll", roll);
        resultData.put("year", year);
        resultData.put("term", term);

        if (!TextUtils.isEmpty(gpaStr)) {
            try {
                double gpa = Double.parseDouble(gpaStr);
                resultData.put("gpa", gpa);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid GPA format", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.isEmpty(ctMarksStr)) {
            Map<String, Float> ctMarksMap = new HashMap<>();
            String[] pairs = ctMarksStr.split(",");
            for (String pair : pairs) {
                String[] entry = pair.split(":");
                if (entry.length == 2) {
                    try {
                        String course = entry[0].trim();
                        float marks = Float.parseFloat(entry[1].trim());
                        ctMarksMap.put(course, marks);
                    } catch (NumberFormatException e) {
                        
                    }
                }
            }
            if (!ctMarksMap.isEmpty()) {
                resultData.put("ctMarks", ctMarksMap);
            }
        }

        
        db.collection("results").document(roll + "_" + year + "_" + term)
                .set(resultData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Result saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                    Toast.makeText(this, "Error saving result: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
