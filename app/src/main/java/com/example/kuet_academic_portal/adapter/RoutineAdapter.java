package com.example.kuet_academic_portal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.R;
import com.example.kuet_academic_portal.model.Routine;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private List<Routine> routineList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Routine routine);
    }

    public RoutineAdapter(List<Routine> routineList) {
        this.routineList = routineList;
    }

    public RoutineAdapter(List<Routine> routineList, OnItemClickListener listener) {
        this.routineList = routineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routineList.get(position);
        holder.tvDay.setText(routine.getDay());
        holder.tvTime.setText(routine.getStartTime() + " - " + routine.getEndTime());
        holder.tvCourseCode.setText(routine.getCourseCode());
        holder.tvTeacher.setText(routine.getTeacher());
        holder.tvRoom.setText(routine.getRoom());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(routine);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routineList.size();
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvTime, tvCourseCode, tvTeacher, tvRoom;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvRoom = itemView.findViewById(R.id.tvRoom);
        }
    }
}
