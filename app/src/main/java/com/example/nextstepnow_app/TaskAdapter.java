package com.example.nextstepnow_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private PreferenceManager preferenceManager;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.preferenceManager = new PreferenceManager(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTitle.setText(task.getTitle());
        holder.textViewDate.setText(task.getDate());

        // Set a listener for the checkbox
        boolean isCompleted = preferenceManager.getTaskCompletionStatus(task.getId());
        holder.checkBoxCompleted.setOnCheckedChangeListener(null); // Clear any existing listener
        holder.checkBoxCompleted.setChecked(isCompleted);
        holder.checkBoxCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferenceManager.setTaskCompletionStatus(task.getId(), isChecked);
                notifyDataSetChanged();
                task.sendToCompletedTaskPage(task, buttonView.getContext());
            }
        });
        // Set click listener for the task item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click action for the selected task
                // Start a new activity or show a dialog for the timer page
                Intent intent = new Intent(v.getContext(), TimerPage.class);
                intent.putExtra("taskId", task.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTitle;
        public TextView textViewDate;
        public CheckBox checkBoxCompleted;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.mcheckbox);
            textViewDate = itemView.findViewById(R.id.due_date_tv);
            checkBoxCompleted = itemView.findViewById(R.id.mcheckbox);
        }
    }
}

