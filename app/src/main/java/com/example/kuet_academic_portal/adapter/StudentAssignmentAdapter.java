package com.example.kuet_academic_portal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.R;
import com.example.kuet_academic_portal.model.Assignment;

import java.util.List;

public class StudentAssignmentAdapter extends RecyclerView.Adapter<StudentAssignmentAdapter.ViewHolder> {

    private List<Assignment> assignmentList;

    public StudentAssignmentAdapter(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_student_assignment, parent, false);
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
        TextView tvTitle, tvCourse, tvDepartment, tvDescription, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCourse = itemView.findViewById(R.id.tvCourse);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        void bind(Assignment assignment) {
            tvTitle.setText(assignment.getTitle());
            tvCourse.setText("Course: " + assignment.getCourse());
            tvDepartment.setText("Dept: " + assignment.getDepartment() + " | Term: " + assignment.getTerm() + " | Year: " + assignment.getYear());
            tvDescription.setText(assignment.getDescription());
            tvDate.setText("Due: " + assignment.getDueDate());
        }
    }
}

