package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    private EditText etRoll, etName, etDepartment, etTerm, etYear, etSection, etEmail, etPhone, etPassword;
    private Button btnAddStudent, btnBack;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etRoll = findViewById(R.id.etRoll);
        etName = findViewById(R.id.etName);
        etDepartment = findViewById(R.id.etDepartment);
        etTerm = findViewById(R.id.etTerm);
        etYear = findViewById(R.id.etYear);
        etSection = findViewById(R.id.etSection);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnAddStudent.setOnClickListener(v -> addStudent());
        btnBack.setOnClickListener(v -> finish());
    }

    private void addStudent() {
        String roll = etRoll.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String term = etTerm.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String section = etSection.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (roll.isEmpty()) {
            etRoll.setError("Roll required");
            return;
        }
        if (name.isEmpty()) {
            etName.setError("Name required");
            return;
        }
        if (department.isEmpty()) {
            etDepartment.setError("Department required");
            return;
        }
        if (term.isEmpty()) {
            etTerm.setError("Term required");
            return;
        }
        if (year.isEmpty()) {
            etYear.setError("Year required");
            return;
        }
        if (section.isEmpty()) {
            etSection.setError("Section required");
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email required for account creation");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password required");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        try {
            Long.parseLong(roll); 

            
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        String uid = authResult.getUser().getUid();
                        saveStudentToFirestore(roll, name, department, term, year, section, email, phone, uid);
                    } else {
                        Toast.makeText(this, "Authentication succeeded but user is null", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        } catch (NumberFormatException e) {
            android.util.Log.e("AddStudent", "Number format validation error", e);
            Toast.makeText(this, "Invalid number format in Roll", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveStudentToFirestore(String roll, String name, String department, String term, String year, String section, String email, String phone, String uid) {
        try {
            Map<String, Object> student = new HashMap<>();
            student.put("roll", Long.parseLong(roll));
            student.put("name", name);
            student.put("department", department);
            student.put("term", Integer.parseInt(term));
            student.put("year", Integer.parseInt(year));
            student.put("section", section);
            student.put("email", email);
            student.put("uid", uid); 

            if (!phone.isEmpty()) {
                
                String numericPhone = phone.replaceAll("[^0-9]", "");
                if (!numericPhone.isEmpty()) {
                     try {
                        student.put("phone", Long.parseLong(numericPhone));
                     } catch (NumberFormatException nfe) {
                        student.put("phone", phone); 
                     }
                } else {
                     student.put("phone", "");
                }
            } else {
                student.put("phone", "");
            }

            student.put("role", "student");

            android.util.Log.d("AddStudent", "Adding student: " + roll);

            db.collection("Students")
                .document(roll)
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("AddStudent", "Student added successfully: " + roll);
                    Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("AddStudent", "Error adding student to Firestore", e);
                    Toast.makeText(this, "Student account created but failed to save details. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        } catch (NumberFormatException e) {
            android.util.Log.e("AddStudent", "Number format validation error", e);
            Toast.makeText(this, "Invalid number format in details", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etRoll.setText("");
        etName.setText("");
        etDepartment.setText("");
        etTerm.setText("");
        etYear.setText("");
        etSection.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etPassword.setText("");
    }
}

