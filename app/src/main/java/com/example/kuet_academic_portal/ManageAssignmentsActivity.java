package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.adapter.AssignmentAdapter;
import com.example.kuet_academic_portal.model.Assignment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageAssignmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private EditText etSearchCourse;
    private Button btnSearch, btnBack, btnAddAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_assignments);

        db = FirebaseFirestore.getInstance();
        assignmentList = new ArrayList<>();

        initializeViews();
        setupRecyclerView();
        loadAssignments();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewAssignments);
        progressBar = findViewById(R.id.progressBar);
        etSearchCourse = findViewById(R.id.etSearchCourse);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnAddAssignment = findViewById(R.id.btnAddAssignment);

        btnSearch.setOnClickListener(v -> searchAssignment());
        btnBack.setOnClickListener(v -> finish());
        btnAddAssignment.setOnClickListener(v -> showAddAssignmentDialog());
    }

    private void setupRecyclerView() {
        adapter = new AssignmentAdapter(assignmentList, this::showEditDialog, this::deleteAssignment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadAssignments() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("assignment")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                assignmentList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Assignment assignment = document.toObject(Assignment.class);
                    assignment.setId(document.getId());
                    assignmentList.add(assignment);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading assignments", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
    }

    private void searchAssignment() {
        String course = etSearchCourse.getText().toString().trim();
        if (course.isEmpty()) {
            loadAssignments();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        db.collection("assignment")
            .whereEqualTo("course", course)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                assignmentList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Assignment assignment = document.toObject(Assignment.class);
                    assignment.setId(document.getId());
                    assignmentList.add(assignment);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error searching assignments", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
    }

    private void showAddAssignmentDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_assignment, null);

        EditText etCourse = dialogView.findViewById(R.id.etCourse);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDepartment = dialogView.findViewById(R.id.etDepartment);
        EditText etTerm = dialogView.findViewById(R.id.etTerm);
        EditText etYear = dialogView.findViewById(R.id.etYear);
        EditText etDueDate = dialogView.findViewById(R.id.etDueDate);

        new AlertDialog.Builder(this)
            .setTitle("Add New Assignment")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String course = etCourse.getText().toString().trim();
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String department = etDepartment.getText().toString().trim();
                String termStr = etTerm.getText().toString().trim();
                String yearStr = etYear.getText().toString().trim();
                String dueDate = etDueDate.getText().toString().trim();

                if (course.isEmpty() || title.isEmpty() || department.isEmpty()) {
                    Toast.makeText(this, "Required: Course, Title, Department", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> assignmentData = new HashMap<>();
                assignmentData.put("course", course);
                assignmentData.put("title", title);
                assignmentData.put("description", description);
                assignmentData.put("department", department);
                assignmentData.put("dueDate", dueDate);

                if (!termStr.isEmpty()) {
                    assignmentData.put("term", Integer.parseInt(termStr));
                }
                if (!yearStr.isEmpty()) {
                    assignmentData.put("year", Integer.parseInt(yearStr));
                }

                db.collection("assignment")
                    .add(assignmentData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Assignment added", Toast.LENGTH_SHORT).show();
                        loadAssignments();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding assignment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showEditDialog(Assignment assignment) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_assignment, null);

        EditText etCourse = dialogView.findViewById(R.id.etCourse);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDepartment = dialogView.findViewById(R.id.etDepartment);
        EditText etTerm = dialogView.findViewById(R.id.etTerm);
        EditText etYear = dialogView.findViewById(R.id.etYear);
        EditText etDueDate = dialogView.findViewById(R.id.etDueDate);

        etCourse.setText(assignment.getCourse());
        etTitle.setText(assignment.getTitle());
        etDescription.setText(assignment.getDescription());
        etDepartment.setText(assignment.getDepartment());
        etTerm.setText(assignment.getTerm());
        etYear.setText(assignment.getYear());
        etDueDate.setText(assignment.getDueDate());

        new AlertDialog.Builder(this)
            .setTitle("Edit Assignment")
            .setView(dialogView)
            .setPositiveButton("Update", (dialog, which) -> {
                String course = etCourse.getText().toString().trim();
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String department = etDepartment.getText().toString().trim();
                String termStr = etTerm.getText().toString().trim();
                String yearStr = etYear.getText().toString().trim();
                String dueDate = etDueDate.getText().toString().trim();

                Map<String, Object> updates = new HashMap<>();
                updates.put("course", course);
                updates.put("title", title);
                updates.put("description", description);
                updates.put("department", department);
                updates.put("dueDate", dueDate);

                if (!termStr.isEmpty()) {
                    updates.put("term", Integer.parseInt(termStr));
                }
                if (!yearStr.isEmpty()) {
                    updates.put("year", Integer.parseInt(yearStr));
                }

                db.collection("assignment")
                    .document(assignment.getId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Assignment updated", Toast.LENGTH_SHORT).show();
                        loadAssignments();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating assignment", Toast.LENGTH_SHORT).show();
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteAssignment(Assignment assignment) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Assignment")
            .setMessage("Delete " + assignment.getTitle() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                db.collection("assignment")
                    .document(assignment.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Assignment deleted", Toast.LENGTH_SHORT).show();
                        loadAssignments();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error deleting assignment", Toast.LENGTH_SHORT).show();
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}

