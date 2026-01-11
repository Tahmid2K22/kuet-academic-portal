package com.example.kuet_academic_portal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.R;
import com.example.kuet_academic_portal.model.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> studentList;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public interface OnEditClickListener {
        void onEditClick(Student student);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Student student);
    }

    public StudentAdapter(List<Student> studentList, OnEditClickListener editListener,
                         OnDeleteClickListener deleteListener) {
        this.studentList = studentList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoll, tvName, tvDepartment, tvSemester;
        Button btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvRoll = itemView.findViewById(R.id.tvRoll);
            tvName = itemView.findViewById(R.id.tvName);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Student student) {
            tvRoll.setText("Roll: " + student.getRoll());
            tvName.setText(student.getName());
            tvDepartment.setText(student.getDepartment());
            tvSemester.setText("Term: " + student.getTerm() + " | Year: " + student.getYear());

            btnEdit.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onEditClick(student);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(student);
                }
            });
        }
    }
}

