package com.example.kuet_academic_portal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.R;
import com.example.kuet_academic_portal.model.Assignment;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private List<Assignment> assignmentList;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public interface OnEditClickListener {
        void onEditClick(Assignment assignment);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Assignment assignment);
    }

    public AssignmentAdapter(List<Assignment> assignmentList, OnEditClickListener editListener, OnDeleteClickListener deleteListener) {
        this.assignmentList = assignmentList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCourse, tvDepartment, tvDate;
        Button btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCourse = itemView.findViewById(R.id.tvCourse);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Assignment assignment) {
            tvTitle.setText(assignment.getTitle());
            tvCourse.setText("Course: " + assignment.getCourse());
            tvDepartment.setText("Dept: " + assignment.getDepartment() + " | Term: " + assignment.getTerm() + " | Year: " + assignment.getYear());
            tvDate.setText("Due: " + assignment.getDueDate());

            btnEdit.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onEditClick(assignment);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(assignment);
                }
            });
        }
    }
}

