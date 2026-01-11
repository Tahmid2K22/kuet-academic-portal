package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kuet_academic_portal.model.Routine;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddRoutineActivity extends AppCompatActivity {

    private EditText etDepartment, etYear, etTerm, etStartTime, etEndTime, etCourseCode, etTeacher, etRoom;
    private Spinner spinnerDay;
    private FirebaseFirestore db;
    private String routineId; // For edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);

        db = FirebaseFirestore.getInstance();

        etDepartment = findViewById(R.id.etDepartment);
        etYear = findViewById(R.id.etYear);
        etTerm = findViewById(R.id.etTerm);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etCourseCode = findViewById(R.id.etCourseCode);
        etTeacher = findViewById(R.id.etTeacher);
        etRoom = findViewById(R.id.etRoom);
        spinnerDay = findViewById(R.id.spinnerDay);
        Button btnSaveRoutine = findViewById(R.id.btnSaveRoutine);

        setupSpinner();

        // Check for edit mode
        if (getIntent().hasExtra("id")) {
            routineId = getIntent().getStringExtra("id");
            etDepartment.setText(getIntent().getStringExtra("department"));
            etYear.setText(String.valueOf(getIntent().getIntExtra("year", 0)));
            etTerm.setText(String.valueOf(getIntent().getIntExtra("term", 0)));
            etStartTime.setText(getIntent().getStringExtra("startTime"));
            etEndTime.setText(getIntent().getStringExtra("endTime"));
            etCourseCode.setText(getIntent().getStringExtra("courseCode"));
            etTeacher.setText(getIntent().getStringExtra("teacher"));
            etRoom.setText(getIntent().getStringExtra("room"));

            String day = getIntent().getStringExtra("day");
            if (day != null) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerDay.getAdapter();
                int position = adapter.getPosition(day);
                if (position >= 0) spinnerDay.setSelection(position);
            }
            btnSaveRoutine.setText("Update Routine");
        }

        btnSaveRoutine.setOnClickListener(v -> saveRoutine());
    }

    private void setupSpinner() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);
    }

    private void saveRoutine() {
        String department = etDepartment.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String termStr = etTerm.getText().toString().trim();
        String day = spinnerDay.getSelectedItem().toString();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String courseCode = etCourseCode.getText().toString().trim();
        String teacher = etTeacher.getText().toString().trim();
        String room = etRoom.getText().toString().trim();

        if (TextUtils.isEmpty(department) || TextUtils.isEmpty(yearStr) || TextUtils.isEmpty(termStr) ||
                TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(courseCode)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int year = Integer.parseInt(yearStr);
        int term = Integer.parseInt(termStr);

        Routine routine = new Routine(department, year, term, day, startTime, endTime, courseCode, teacher, room);

        if (routineId != null) {
            // Update
            // Ensure the ID is preserved in the object if needed, though usually ID is separate in Firestore operations
            // However, if we overwrite the document, we want to ensure we don't lose data or change structure unexpectedly.
            routine.setId(routineId);

            db.collection("routines").document(routineId)
                    .set(routine)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Routine updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                        Toast.makeText(this, "Error updating routine: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        } else {
            // Add
            db.collection("routines")
                    .add(routine)
                    .addOnSuccessListener(documentReference -> {
                        routine.setId(documentReference.getId());
                        Toast.makeText(this, "Routine added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                        Toast.makeText(this, "Error adding routine: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
