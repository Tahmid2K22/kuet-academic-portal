package com.example.kuet_academic_portal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.model.Notice;
import com.example.kuet_academic_portal.model.StudentSession;
import com.example.kuet_academic_portal.session.SessionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {
    private static final String TAG = "NoticeActivity";
    private NoticeAdapter adapter;
    private List<Notice> noticeList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        noticeList = new ArrayList<>();
        adapter = new NoticeAdapter(noticeList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        loadNotices();
    }

    private void loadNotices() {
        progressBar.setVisibility(View.VISIBLE);
        
        StudentSession session = sessionManager.getSession();
        if (session == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Session error - Please login again", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Session is null");
            return;
        }

        int term = session.getTerm();
        int year = session.getYear();

        Log.d(TAG, "Session data - Name: " + session.getName() + ", Email: " + session.getEmail() +
                   ", Term: " + term + ", Year: " + year + ", Role: " + session.getRole());

        
        if (term <= 0 || year <= 0) {
            Log.w(TAG, "Invalid term/year data. Loading all notices.");
            Toast.makeText(this, "Loading all notices (profile incomplete)", Toast.LENGTH_SHORT).show();
            loadAllNotices();
            return;
        }

        Log.d(TAG, "Loading notices for term: " + term + ", year: " + year);

        db.collection("notice")
                .whereEqualTo("term", term)
                .whereEqualTo("year", year)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    noticeList.clear();

                    Log.d(TAG, "Query successful. Document count: " + queryDocumentSnapshots.size());

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Notice notice = document.toObject(Notice.class);
                            noticeList.add(notice);
                            Log.d(TAG, "Added notice - Title: " + notice.getTitle() +
                                     ", Term: " + notice.getTerm() + ", Year: " + notice.getYear());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing notice document: " + document.getId(), e);
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                    if (noticeList.isEmpty()) {
                        Toast.makeText(NoticeActivity.this,
                                "No notices available for Term " + term + ", Year " + year,
                                Toast.LENGTH_LONG).show();
                        Log.w(TAG, "No notices found for term=" + term + ", year=" + year);
                    } else {
                        Toast.makeText(NoticeActivity.this,
                                "Loaded " + noticeList.size() + " notice(s)",
                                Toast.LENGTH_SHORT).show();
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NoticeActivity.this, "Error loading notices: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error loading notices", e);
                });
    }

    private void loadAllNotices() {
        db.collection("notice")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    noticeList.clear();

                    Log.d(TAG, "Loading all notices. Document count: " + queryDocumentSnapshots.size());

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Notice notice = document.toObject(Notice.class);
                            noticeList.add(notice);
                            Log.d(TAG, "Added notice - Title: " + notice.getTitle() +
                                     ", Term: " + notice.getTerm() + ", Year: " + notice.getYear());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing notice document: " + document.getId(), e);
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                    if (noticeList.isEmpty()) {
                        Toast.makeText(NoticeActivity.this, "No notices available", Toast.LENGTH_LONG).show();
                        Log.w(TAG, "No notices found in database");
                    } else {
                        Toast.makeText(NoticeActivity.this,
                                "Loaded " + noticeList.size() + " notice(s) (all terms/years)",
                                Toast.LENGTH_SHORT).show();
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NoticeActivity.this, "Error loading notices: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error loading all notices", e);
                });
    }
}
