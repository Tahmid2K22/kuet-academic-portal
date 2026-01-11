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

import com.example.kuet_academic_portal.adapter.StudentAdapter;
import com.example.kuet_academic_portal.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageStudentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private EditText etSearchRoll;
    private Button btnSearch, btnBack, btnAddStudent;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        studentList = new ArrayList<>();

        initializeViews();
        setupRecyclerView();
        loadStudents();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewStudents);
        progressBar = findViewById(R.id.progressBar);
        etSearchRoll = findViewById(R.id.etSearchRoll);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        btnSearch.setOnClickListener(v -> searchStudent());
        btnBack.setOnClickListener(v -> finish());
        btnAddStudent.setOnClickListener(v -> showAddStudentDialog());
    }

    private void setupRecyclerView() {
        adapter = new StudentAdapter(studentList, this::showEditDialog, this::deleteStudent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Students")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                studentList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Student student = document.toObject(Student.class);
                    student.setId(document.getId());
                    studentList.add(student);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading students", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
    }

    private void searchStudent() {
        String roll = etSearchRoll.getText().toString().trim();
        if (roll.isEmpty()) {
            loadStudents();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        db.collection("Students")
            .document(roll)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                studentList.clear();
                if (documentSnapshot.exists()) {
                    Student student = documentSnapshot.toObject(Student.class);
                    student.setId(documentSnapshot.getId());
                    studentList.add(student);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error searching student", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
    }

    private void showEditDialog(Student student) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_student, null);

        EditText etName = dialogView.findViewById(R.id.etEditName);
        EditText etDepartment = dialogView.findViewById(R.id.etEditDepartment);
        EditText etTerm = dialogView.findViewById(R.id.etEditTerm);
        EditText etYear = dialogView.findViewById(R.id.etEditYear);
        EditText etSection = dialogView.findViewById(R.id.etEditSection);
        EditText etEmail = dialogView.findViewById(R.id.etEditEmail);
        EditText etPhone = dialogView.findViewById(R.id.etEditPhone);

        etName.setText(student.getName());
        etDepartment.setText(student.getDepartment());
        etTerm.setText(student.getTerm());
        etYear.setText(student.getYear());
        etSection.setText(student.getSection());
        etEmail.setText(student.getEmail());
        etPhone.setText(student.getPhone());

        new AlertDialog.Builder(this)
            .setTitle("Edit Student: " + student.getRoll())
            .setView(dialogView)
            .setPositiveButton("Update", (dialog, which) -> {
                String termStr = etTerm.getText().toString().trim();
                String yearStr = etYear.getText().toString().trim();
                String phoneStr = etPhone.getText().toString().trim();

                Map<String, Object> updates = new HashMap<>();
                updates.put("name", etName.getText().toString().trim());
                updates.put("department", etDepartment.getText().toString().trim());
                updates.put("section", etSection.getText().toString().trim());
                updates.put("email", etEmail.getText().toString().trim());

                if (!termStr.isEmpty()) {
                    updates.put("term", Integer.parseInt(termStr));
                }
                if (!yearStr.isEmpty()) {
                    updates.put("year", Integer.parseInt(yearStr));
                }
                if (!phoneStr.isEmpty()) {
                    updates.put("phone", Long.parseLong(phoneStr));
                } else {
                    updates.put("phone", "");
                }

                db.collection("Students")
                    .document(student.getRoll())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show();
                        loadStudents();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating student", Toast.LENGTH_SHORT).show();
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showAddStudentDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_student, null);

        EditText etRoll = dialogView.findViewById(R.id.etAddRoll);
        EditText etName = dialogView.findViewById(R.id.etAddName);
        EditText etDepartment = dialogView.findViewById(R.id.etAddDepartment);
        EditText etTerm = dialogView.findViewById(R.id.etAddTerm);
        EditText etYear = dialogView.findViewById(R.id.etAddYear);
        EditText etSection = dialogView.findViewById(R.id.etAddSection);
        EditText etEmail = dialogView.findViewById(R.id.etAddEmail);
        EditText etPhone = dialogView.findViewById(R.id.etAddPhone);
        EditText etPassword = dialogView.findViewById(R.id.etAddPassword);

        new AlertDialog.Builder(this)
            .setTitle("Add New Student")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String roll = etRoll.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String department = etDepartment.getText().toString().trim();
                String termStr = etTerm.getText().toString().trim();
                String yearStr = etYear.getText().toString().trim();
                String section = etSection.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (roll.isEmpty()) {
                    Toast.makeText(this, "Roll number required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                if (email.isEmpty()) {
                    Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                
                
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        if (authResult.getUser() != null) {
                            String uid = authResult.getUser().getUid();
                            saveStudentToFirestore(roll, name, department, termStr, yearStr, section, email, phone, uid);
                        } else {
                            Toast.makeText(this, "Authentication succeeded but user is null", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void saveStudentToFirestore(String roll, String name, String department, String termStr, String yearStr, String section, String email, String phone, String uid) {
        try {
            Map<String, Object> student = new HashMap<>();
            student.put("roll", Long.parseLong(roll));
            student.put("name", name);
            student.put("department", department);
            student.put("term", Integer.parseInt(termStr));
            student.put("year", Integer.parseInt(yearStr));
            student.put("section", section);
            student.put("email", email);
            student.put("uid", uid);
            student.put("role", "student");

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

            db.collection("Students")
                .document(roll)
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
                    loadStudents();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding student details: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteStudent(Student student) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete " + student.getName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                db.collection("Students")
                    .document(student.getRoll())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show();
                        loadStudents();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error deleting student", Toast.LENGTH_SHORT).show();
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}

