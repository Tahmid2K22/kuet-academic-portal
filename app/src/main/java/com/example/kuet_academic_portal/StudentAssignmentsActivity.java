package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.adapter.StudentAssignmentAdapter;
import com.example.kuet_academic_portal.model.Assignment;
import com.example.kuet_academic_portal.model.StudentSession;
import com.example.kuet_academic_portal.session.SessionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentAssignmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAssignmentAdapter adapter;
    private List<Assignment> assignmentList;
    private FirebaseFirestore db;
    private SessionManager sessionManager;
    private ProgressBar progressBar;
    private TextView tvNoAssignments;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignments);

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);
        assignmentList = new ArrayList<>();

        initializeViews();
        setupRecyclerView();
        loadAssignments();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewAssignments);
        progressBar = findViewById(R.id.progressBar);
        tvNoAssignments = findViewById(R.id.tvNoAssignments);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new StudentAssignmentAdapter(assignmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadAssignments() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        StudentSession session = sessionManager.getSession();
        int term = session.getTerm();
        int year = session.getYear();
        String department = session.getDepartment();

        Log.d("StudentAssignments", "Loading for Term: " + term + ", Year: " + year + ", Dept: " + department);

        if (term <= 0 || year <= 0) {
            Toast.makeText(this, "Profile incomplete (Term/Year missing). Showing all assignments.", Toast.LENGTH_LONG).show();
            loadAllAssignmentsAndFilter(department);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvNoAssignments.setVisibility(View.GONE);

        db.collection("assignment")
            .whereEqualTo("term", term)
            .whereEqualTo("year", year)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                assignmentList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    try {
                        Assignment assignment = document.toObject(Assignment.class);
                        assignment.setId(document.getId());

                        assignmentList.add(assignment);
                    } catch (Exception e) {
                        Log.e("StudentAssignments", "Error parsing assignment", e);
                    }
                }

                if (assignmentList.isEmpty()) {
                    tvNoAssignments.setText("No assignments found for Term " + term + ", Year " + year);
                    tvNoAssignments.setVisibility(View.VISIBLE);
                } else {
                    tvNoAssignments.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading assignments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                Log.e("StudentAssignments", "Firestore error", e);
            });
    }

    private void loadAllAssignmentsAndFilter(String userDepartment) {
        progressBar.setVisibility(View.VISIBLE);
        tvNoAssignments.setVisibility(View.GONE);

        db.collection("assignment")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                assignmentList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    try {
                        Assignment assignment = document.toObject(Assignment.class);
                        assignment.setId(document.getId());

                        if (userDepartment != null && !userDepartment.isEmpty() &&
                            assignment.getDepartment() != null) {
                            String assignDept = assignment.getDepartment().trim();
                            if (!assignDept.equalsIgnoreCase(userDepartment.trim())) {
                                continue;
                            }
                        }

                        assignmentList.add(assignment);
                    } catch (Exception e) {
                        Log.e("StudentAssignments", "Error parsing assignment", e);
                    }
                }

                if (assignmentList.isEmpty()) {
                    tvNoAssignments.setText("No assignments found.");
                    tvNoAssignments.setVisibility(View.VISIBLE);
                } else {
                    tvNoAssignments.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading assignments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
    }
}

