package com.alexandruc.pomodoro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexandruc.pomodoro.R;
import com.alexandruc.pomodoro.data.entities.Tasks;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private List<Tasks> tasksList = new ArrayList<>();

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(Tasks task);
        void onTaskCheck(Tasks task, boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Tasks currentTask = tasksList.get(position);

        holder.textViewDescription.setText(currentTask.getDescription());

        //delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    listener.onDeleteClick(currentTask);
            }
        });

        //checkbox
        holder.checkBoxCompleted.setChecked(currentTask.isCompleted());
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskCheck(currentTask, isChecked);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public void setTasks(List<Tasks> tasks) {
        this.tasksList = tasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDescription;
        private final CheckBox checkBoxCompleted;
        private final MaterialButton deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.taskDescription);
            checkBoxCompleted = itemView.findViewById(R.id.taskCheckBox);
            deleteButton = itemView.findViewById(R.id.deleteTaskButton);
        }
    }
}