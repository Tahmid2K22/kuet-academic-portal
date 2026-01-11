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

import com.example.kuet_academic_portal.model.Notice;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ManageNoticesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoticeAdminAdapter adapter;
    private List<Notice> noticeList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button btnAddNotice, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notices);

        db = FirebaseFirestore.getInstance();
        noticeList = new ArrayList<>();

        initializeViews();
        setupRecyclerView();
        loadNotices();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewNotices);
        progressBar = findViewById(R.id.progressBar);
        btnAddNotice = findViewById(R.id.btnAddNotice);
        btnBack = findViewById(R.id.btnBack);

        btnAddNotice.setOnClickListener(v -> showAddNoticeDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new NoticeAdminAdapter(noticeList, new NoticeAdminAdapter.OnNoticeActionListener() {
            @Override
            public void onEdit(Notice notice) {
                showEditDialog(notice);
            }

            @Override
            public void onDelete(Notice notice) {
                deleteNotice(notice);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadNotices() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("notice")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    noticeList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Notice notice = document.toObject(Notice.class);
                        notice.setId(document.getId());
                        noticeList.add(notice);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading notices", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void showAddNoticeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_notice, null);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etTerm = dialogView.findViewById(R.id.etTerm);
        EditText etYear = dialogView.findViewById(R.id.etYear);

        new AlertDialog.Builder(this)
                .setTitle("Add New Notice")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String termStr = etTerm.getText().toString().trim();
                    String yearStr = etYear.getText().toString().trim();

                    if (title.isEmpty() || description.isEmpty() || termStr.isEmpty() || yearStr.isEmpty()) {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss 'UTC'XXX", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
                    String currentDate = sdf.format(new Date());

                    Map<String, Object> noticeData = new HashMap<>();
                    noticeData.put("title", title);
                    noticeData.put("description", description);
                    noticeData.put("date", currentDate);
                    noticeData.put("term", Integer.parseInt(termStr));
                    noticeData.put("year", Integer.parseInt(yearStr));

                    db.collection("notice")
                            .add(noticeData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Notice published", Toast.LENGTH_SHORT).show();
                                loadNotices();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error publishing notice", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Notice notice) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_notice, null);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etTerm = dialogView.findViewById(R.id.etTerm);
        EditText etYear = dialogView.findViewById(R.id.etYear);

        etTitle.setText(notice.getTitle());
        etDescription.setText(notice.getDescription());
        etTerm.setText(String.valueOf(notice.getTerm()));
        etYear.setText(String.valueOf(notice.getYear()));

        new AlertDialog.Builder(this)
                .setTitle("Edit Notice")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String termStr = etTerm.getText().toString().trim();
                    String yearStr = etYear.getText().toString().trim();

                    if (title.isEmpty() || description.isEmpty() || termStr.isEmpty() || yearStr.isEmpty()) {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("title", title);
                    updates.put("description", description);
                    updates.put("term", Integer.parseInt(termStr));
                    updates.put("year", Integer.parseInt(yearStr));

                    db.collection("notice")
                            .document(notice.getId())
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Notice updated", Toast.LENGTH_SHORT).show();
                                loadNotices();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error updating notice", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNotice(Notice notice) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notice")
                .setMessage("Are you sure you want to delete this notice?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.collection("notice")
                            .document(notice.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Notice deleted", Toast.LENGTH_SHORT).show();
                                loadNotices();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error deleting notice", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

