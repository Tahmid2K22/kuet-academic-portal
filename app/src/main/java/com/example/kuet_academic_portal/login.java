package com.example.kuet_academic_portal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.kuet_academic_portal.model.StudentSession;
import com.example.kuet_academic_portal.session.SessionManager;

public class login extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.Login);

        loginButton.setOnClickListener(v -> loginUser());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && sessionManager.isLoggedIn()) {
            navigateToDashboard();
        }
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        fetchStudentDataAndSaveSession(email);
                    } else {
                        Toast.makeText(login.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                    }
                });
    }

    private void fetchStudentDataAndSaveSession(String email) {
        db.collection("Students")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);

                        StudentSession student = new StudentSession();
                        student.setDepartment(doc.getString("department"));
                        student.setEmail(doc.getString("email"));
                        student.setName(doc.getString("name"));

                        Object phoneObj = doc.get("phone");
                        student.setPhone(phoneObj != null ? String.valueOf(phoneObj) : "");

                        Object rollObj = doc.get("roll");
                        student.setRoll(rollObj != null ? String.valueOf(rollObj) : "");

                        student.setSection(doc.getString("section"));

                        Long termLong = doc.getLong("term");
                        student.setTerm(termLong != null ? termLong.intValue() : 0);

                        Long yearLong = doc.getLong("year");
                        student.setYear(yearLong != null ? yearLong.intValue() : 0);

                        sessionManager.saveSession(student);

                        Toast.makeText(login.this, "Welcome, " + student.getName() + "!",
                                Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        Toast.makeText(login.this, "Student data not found in database",
                                Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(login.this, "Error fetching student data: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                });
    }


    private void navigateToDashboard() {
        Intent intent = new Intent(login.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

