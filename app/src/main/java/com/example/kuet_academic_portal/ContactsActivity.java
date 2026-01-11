package com.example.kuet_academic_portal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.adapter.ContactListAdapter;
import com.example.kuet_academic_portal.model.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements ContactListAdapter.OnContactClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddContact;
    private ProgressBar progressBar;
    private TextView tvNoContacts;
    private ContactListAdapter adapter;
    private List<Contact> contactList;
    private FirebaseFirestore db;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        db = FirebaseFirestore.getInstance();
        contactList = new ArrayList<>();

        if (getIntent().hasExtra("isAdmin")) {
            isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        }

        initializeViews();
        setupRecyclerView();

        if (isAdmin) {
            fabAddContact.setVisibility(View.VISIBLE);
            fabAddContact.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddContactActivity.class);
                startActivity(intent);
            });
        } else {
            fabAddContact.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewContacts);
        fabAddContact = findViewById(R.id.fabAddContact);
        progressBar = findViewById(R.id.progressBar);
        tvNoContacts = findViewById(R.id.tvNoContacts);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ContactListAdapter(contactList, isAdmin, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void loadContacts() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("contacts")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    contactList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Contact contact = document.toObject(Contact.class);
                            if (contact != null) {
                                contact.setId(document.getId());
                                contactList.add(contact);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        tvNoContacts.setVisibility(View.GONE);
                    } else {
                        tvNoContacts.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading contacts", Toast.LENGTH_SHORT).show();
                    Log.e("ContactsActivity", "Error loading contacts", e);
                });
    }

    @Override
    public void onDeleteClick(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete " + contact.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteContact(contact))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteContact(Contact contact) {
        if (contact.getId() == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("contacts").document(contact.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                    loadContacts(); // Reload list
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error deleting contact", Toast.LENGTH_SHORT).show();
                });
    }
}
