package com.example.kuet_academic_portal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.R;
import com.example.kuet_academic_portal.model.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceMarkAdapter extends RecyclerView.Adapter<AttendanceMarkAdapter.ViewHolder> {

    private List<Student> studentList;
    private Map<String, String> attendanceStatus;

    public AttendanceMarkAdapter(List<Student> studentList) {
        this.studentList = studentList;
        this.attendanceStatus = new HashMap<>();
    }

    public Map<String, String> getAttendanceStatus() {
        return attendanceStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_mark_attendance, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoll, tvName;
        RadioGroup radioGroup;
        RadioButton rbPresent, rbAbsent;

        ViewHolder(View itemView) {
            super(itemView);
            tvRoll = itemView.findViewById(R.id.tvRoll);
            tvName = itemView.findViewById(R.id.tvName);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            rbPresent = itemView.findViewById(R.id.rbPresent);
            rbAbsent = itemView.findViewById(R.id.rbAbsent);
        }

        void bind(Student student) {
            tvRoll.setText(student.getRoll());
            tvName.setText(student.getName());

            
            radioGroup.setOnCheckedChangeListener(null);

            String savedStatus = attendanceStatus.get(student.getRoll());
            if ("Present".equals(savedStatus)) {
                rbPresent.setChecked(true);
            } else if ("Absent".equals(savedStatus)) {
                rbAbsent.setChecked(true);
            } else {
                radioGroup.clearCheck();
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbPresent) {
                    attendanceStatus.put(student.getRoll(), "Present");
                } else if (checkedId == R.id.rbAbsent) {
                    attendanceStatus.put(student.getRoll(), "Absent");
                }
            });
        }
    }
}

