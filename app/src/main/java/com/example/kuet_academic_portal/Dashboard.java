package com.example.kuet_academic_portal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import com.example.kuet_academic_portal.model.StudentSession;
import com.example.kuet_academic_portal.session.SessionManager;
import com.example.kuet_academic_portal.NoticeActivity;

public class Dashboard extends AppCompatActivity {

    private MaterialCardView classRoutineCard, assignmentsCard, attendanceCard;
    private MaterialCardView resultsCard, noticesCard, contactsCard;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        initializeViews();
        displayStudentInfo();
        setupClickListeners();
    }

    private void initializeViews() {
        classRoutineCard = findViewById(R.id.classRoutineCard);
        assignmentsCard = findViewById(R.id.assignmentsCard);
        attendanceCard = findViewById(R.id.attendanceCard);
        resultsCard = findViewById(R.id.resultsCard);
        noticesCard = findViewById(R.id.noticesCard);
        contactsCard = findViewById(R.id.contactsCard);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void displayStudentInfo() {
        StudentSession session = sessionManager.getSession();
        if (session != null) {
            String welcomeMessage = "Welcome, " + session.getName() + "!\n" +
                    session.getDepartment() + " | Roll: " + session.getRoll();
            Toast.makeText(this, welcomeMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    private void setupClickListeners() {
        // Logout Button
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Class Routine Card
        classRoutineCard.setOnClickListener(v -> openClassRoutine());

        // Assignments Card
        assignmentsCard.setOnClickListener(v -> openAssignments());

        // Attendance Card
        attendanceCard.setOnClickListener(v -> openAttendance());

        // Results Card
        resultsCard.setOnClickListener(v -> openResults());

        // Notices Card
        noticesCard.setOnClickListener(v -> openNotices());

        // Contacts Card
        contactsCard.setOnClickListener(v -> openContacts());
    }

    // Navigation methods
    private void openClassRoutine() {
        Intent intent = new Intent(this, cls_routine.class);
        startActivity(intent);
    }

    private void openAssignments() {
        Intent intent = new Intent(this, StudentAssignmentsActivity.class);
        startActivity(intent);
    }

    private void openAttendance() {
        Intent intent = new Intent(this, StudentAttendanceActivity.class);
        startActivity(intent);
    }

    private void openResults() {
        // TODO: Create ResultsActivity
        Toast.makeText(this, "Opening Results...", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, ResultsActivity.class);
        // startActivity(intent);
    }

    private void openNotices() {
        // TODO: Create NoticesActivity
        Toast.makeText(this, "Opening Notices...", Toast.LENGTH_SHORT).show();
         Intent intent = new Intent(this, NoticeActivity.class);
         startActivity(intent);
    }

    private void openContacts() {
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.putExtra("isAdmin", false);
        startActivity(intent);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", null)
                .show();
    }

    private void performLogout() {
        mAuth.signOut();
        sessionManager.clearSession();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Dashboard.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

