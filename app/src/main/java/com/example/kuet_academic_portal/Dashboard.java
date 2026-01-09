package com.example.kuet_academic_portal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class Dashboard extends AppCompatActivity {

    private MaterialCardView classRoutineCard, assignmentsCard, attendanceCard;
    private MaterialCardView resultsCard, noticesCard, contactsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        classRoutineCard = findViewById(R.id.classRoutineCard);
        assignmentsCard = findViewById(R.id.assignmentsCard);
        attendanceCard = findViewById(R.id.attendanceCard);
        resultsCard = findViewById(R.id.resultsCard);
        noticesCard = findViewById(R.id.noticesCard);
        contactsCard = findViewById(R.id.contactsCard);
    }



    private void setupClickListeners() {
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
        // TODO: Create AssignmentsActivity
        Toast.makeText(this, "Opening Assignments...", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, AssignmentsActivity.class);
        // startActivity(intent);
    }

    private void openAttendance() {
        // TODO: Create AttendanceActivity
        Toast.makeText(this, "Opening Attendance...", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, AttendanceActivity.class);
        // startActivity(intent);
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
        // Intent intent = new Intent(this, NoticesActivity.class);
        // startActivity(intent);
    }

    private void openContacts() {
        // TODO: Create ContactsActivity
        Toast.makeText(this, "Opening Contacts...", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, ContactsActivity.class);
        // startActivity(intent);
    }
}

