package com.example.kuet_academic_portal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kuet_academic_portal.session.SessionManager;
import com.example.kuet_academic_portal.model.StudentSession;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {

    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();

        TextView adminNameText = findViewById(R.id.adminNameText);
        Button logoutButton = findViewById(R.id.logoutButton);

        StudentSession session = sessionManager.getSession();
        if (session != null) {
            adminNameText.setText("Welcome, " + session.getName());
        }

        setupCardListeners();

        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        mAuth.signOut();
        sessionManager.clearSession();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminDashboard.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupCardListeners() {
        MaterialCardView addStudentsCard = findViewById(R.id.addStudentsCard);
        MaterialCardView updateStudentCard = findViewById(R.id.updateStudentCard);
        MaterialCardView addAssignmentCard = findViewById(R.id.addAssignmentCard);
        MaterialCardView addAttendanceCard = findViewById(R.id.addAttendanceCard);
        MaterialCardView addNoticeCard = findViewById(R.id.addNoticeCard);
        MaterialCardView addResultCard = findViewById(R.id.addResultCard);
        MaterialCardView addRoutineCard = findViewById(R.id.addRoutineCard);
        MaterialCardView updateRoutineCard = findViewById(R.id.updateRoutineCard);

        addStudentsCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AddStudentActivity.class);
            startActivity(intent);
        });

        updateStudentCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, ManageStudentsActivity.class);
            startActivity(intent);
        });

        addAssignmentCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, ManageAssignmentsActivity.class);
            startActivity(intent);
        });

        addAttendanceCard.setOnClickListener(v -> showAttendanceMenu());

        addNoticeCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AddNoticeActivity.class);
            startActivity(intent);
        });

        addResultCard.setOnClickListener(v ->
            Toast.makeText(this, "Add Result feature coming soon", Toast.LENGTH_SHORT).show()
        );

        addRoutineCard.setOnClickListener(v ->
            Toast.makeText(this, "Add Routine feature coming soon", Toast.LENGTH_SHORT).show()
        );

        updateRoutineCard.setOnClickListener(v ->
            Toast.makeText(this, "Update Routine feature coming soon", Toast.LENGTH_SHORT).show()
        );
    }

    private void showAttendanceMenu() {
        String[] options = {"Mark Attendance", "View Analytics"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Attendance Management")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        Intent markIntent = new Intent(AdminDashboard.this, MarkAttendanceActivity.class);
                        startActivity(markIntent);
                        break;
                    case 1:
                        Intent analyticsIntent = new Intent(AdminDashboard.this, AdminAttendanceAnalyticsActivity.class);
                        startActivity(analyticsIntent);
                        break;
                }
            })
            .show();
    }
}

