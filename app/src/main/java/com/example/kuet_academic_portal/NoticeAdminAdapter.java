package com.example.kuet_academic_portal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.model.Notice;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class NoticeAdminAdapter extends RecyclerView.Adapter<NoticeAdminAdapter.NoticeViewHolder> {
    private List<Notice> noticeList;
    private OnNoticeActionListener listener;

    public interface OnNoticeActionListener {
        void onEdit(Notice notice);
        void onDelete(Notice notice);
    }

    public NoticeAdminAdapter(List<Notice> noticeList, OnNoticeActionListener listener) {
        this.noticeList = noticeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice_admin, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = noticeList.get(position);
        holder.tvTitle.setText(notice.getTitle());
        holder.tvDescription.setText(notice.getDescription());
        holder.tvDate.setText(notice.getDate());
        holder.tvTermYear.setText("Term: " + notice.getTerm() + " | Year: " + notice.getYear());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(notice));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(notice));
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate, tvTermYear;
        MaterialButton btnEdit, btnDelete;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTermYear = itemView.findViewById(R.id.tvTermYear);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

