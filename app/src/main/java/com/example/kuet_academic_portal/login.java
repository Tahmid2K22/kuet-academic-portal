package com.example.kuet_academic_portal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kuet_academic_portal.model.StudentSession;
import com.example.kuet_academic_portal.session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
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
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard();
        }
    }

    private void loginUser() {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

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
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userEmail = user.getEmail();
                            if (userEmail != null) {
                                fetchStudentDataAndCreateSession(userEmail);
                            }
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(login.this, "Authentication failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                    }
                });
    }

    private void fetchStudentDataAndCreateSession(String email) {
        db.collection("students").whereEqualTo("email", email).limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                             StudentSession session = new StudentSession();
                        session.setEmail(email);
                        session.setName(document.getString("name") != null ? document.getString("name") : "Student");
                        session.setDepartment(document.getString("department") != null ? document.getString("department") : "");
                        session.setPhone(document.getString("phone") != null ? document.getString("phone") : "");
                        session.setRoll(document.getString("roll") != null ? document.getString("roll") : "");
                        session.setSection(document.getString("section") != null ? document.getString("section") : "");

                        Long termLong = document.getLong("term");
                        Long yearLong = document.getLong("year");
                        session.setTerm(termLong != null ? termLong.intValue() : 0);
                        session.setYear(yearLong != null ? yearLong.intValue() : 0);

                        session.setRole(document.getString("role") != null ? document.getString("role") : "student");

                        sessionManager.saveSession(session);

                        Toast.makeText(login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        if ("admin".equals(session.getRole())) {
                            Intent intent = new Intent(login.this, AdminDashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            navigateToDashboard();
                        }
                    } else {
                        StudentSession session = new StudentSession();
                        session.setEmail(email);
                        session.setName("Student");
                        session.setDepartment("");
                        session.setPhone("");
                        session.setRoll("");
                        session.setSection("");
                        session.setTerm(0);
                        session.setYear(0);
                        session.setRole("student");

                        sessionManager.saveSession(session);

                        Toast.makeText(login.this, "Login successful! Please complete your profile.", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching student data", e);

                    StudentSession session = new StudentSession();
                    session.setEmail(email);
                    session.setName("Student");
                    session.setDepartment("");
                    session.setPhone("");
                    session.setRoll("");
                    session.setSection("");
                    session.setTerm(0);
                    session.setYear(0);
                    session.setRole("student");

                    sessionManager.saveSession(session);

                    Toast.makeText(login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToDashboard();
                });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(login.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

