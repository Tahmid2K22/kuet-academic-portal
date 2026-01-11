package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kuet_academic_portal.model.Contact;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddContactActivity extends AppCompatActivity {

    private EditText etName, etDesignation, etDepartment, etEmail, etPhone;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        db = FirebaseFirestore.getInstance();

        initializeViews();
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etDesignation = findViewById(R.id.etDesignation);
        etDepartment = findViewById(R.id.etDepartment);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> saveContact());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveContact() {
        String name = etName.getText().toString().trim();
        String designation = etDesignation.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }

        Contact contact = new Contact(name, designation, email, phone, department);

        db.collection("contacts")
            .add(contact)
            .addOnSuccessListener(documentReference -> {
                contact.setId(documentReference.getId());
                Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e ->
                Toast.makeText(this, "Error adding contact: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
    }
}
