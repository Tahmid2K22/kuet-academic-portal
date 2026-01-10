package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddNoticeActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etDescription, etTerm, etYear;
    private MaterialButton btnSubmit, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        initializeViews();
        db = FirebaseFirestore.getInstance();
        setupClickListeners();

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showCancelDialog();
            }
        });
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etTerm = findViewById(R.id.etTerm);
        etYear = findViewById(R.id.etYear);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> validateAndSubmit());
        btnCancel.setOnClickListener(v -> showCancelDialog());
    }

    private void validateAndSubmit() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String termStr = etTerm.getText() != null ? etTerm.getText().toString().trim() : "";
        String yearStr = etYear.getText() != null ? etYear.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(termStr)) {
            etTerm.setError("Term is required");
            etTerm.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(yearStr)) {
            etYear.setError("Year is required");
            etYear.requestFocus();
            return;
        }

        int term, year;
        try {
            term = Integer.parseInt(termStr);
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid term or year", Toast.LENGTH_SHORT).show();
            return;
        }

        if (term < 1 || term > 4) {
            etTerm.setError("Term must be between 1 and 4");
            etTerm.requestFocus();
            return;
        }

        if (year < 1 || year > 4) {
            etYear.setError("Year must be between 1 and 4");
            etYear.requestFocus();
            return;
        }

        submitNotice(title, description, term, year);
    }

    private void submitNotice(String title, String description, int term, int year) {
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss 'UTC'XXX", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
        String currentDate = sdf.format(new Date());

        Map<String, Object> notice = new HashMap<>();
        notice.put("title", title);
        notice.put("description", description);
        notice.put("date", currentDate);
        notice.put("term", term);
        notice.put("year", year);

        db.collection("notices")
                .add(notice)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);

                    new AlertDialog.Builder(this)
                            .setTitle("Success")
                            .setMessage("Notice published successfully!")
                            .setPositiveButton("OK", (dialog, which) -> {
                                clearForm();
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void clearForm() {
        etTitle.setText("");
        etDescription.setText("");
        etTerm.setText("");
        etYear.setText("");
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel")
                .setMessage("Are you sure you want to cancel? All data will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}

