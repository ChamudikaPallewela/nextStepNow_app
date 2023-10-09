package com.example.nextstepnow_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    // Declare variables
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        NetworkConnectivityUtil networkConnectivityUtil = new NetworkConnectivityUtil(this);
        networkConnectivityUtil.startNetworkMonitoring();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize task list and adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList,this);
        recyclerView.setAdapter(taskAdapter);

        // Get reference to Firebase tasks
        databaseRef = FirebaseDatabase.getInstance().getReference("tasks");

        // Retrieve tasks from Firebase
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing task list
                taskList.clear();

                // Iterate over task snapshots and add them to the task list
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    taskList.add(task);
                }

                // Notify the adapter of the data change
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Display error message if failed to load tasks
                Toast.makeText(HomeActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
            }
        });

        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.home_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
        }

        // Set up bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item selection
                switch (item.getItemId()) {
                    case R.id.menu_item_home:
                        overridePendingTransition(0, 0);

                        return true;
                    case R.id.menu_item_add_task:
                        startActivity(new Intent(getApplicationContext(), AddTaskActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.menu_item_completed_task:
                        startActivity(new Intent(getApplicationContext(), CompletedTaskActivity.class));
                        overridePendingTransition(0, 0);
                        finish();

                        return true;
                    case R.id.menu_item_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        // Handle swipe gestures
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private Drawable deleteIcon;
            private Drawable editIcon;
            private int deletedColor;
            private int editedColor;


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the position and task associated with the swiped item
                int position = viewHolder.getAdapterPosition();
                Task task = taskList.get(position);
                String taskId = task.getId();

                if (direction == ItemTouchHelper.RIGHT) {
                    // Handle edit task action
                    editTask(task, taskId);
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Handle delete task action
                    deleteTask(task, position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Customize the swipe gestures (e.g., change background color, display icons)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    int backgroundCornerOffset = 20; // Adjust this value to change the corner radius of the background

                    // Get the resources for delete and edit icons
                    deleteIcon = getResources().getDrawable(R.drawable.ic_delete);
                    editIcon = getResources().getDrawable(R.drawable.ic_edit);


                    // Get the bounds of the item view
                    int itemViewHeight = itemView.getBottom() - itemView.getTop();
                    int itemViewWidth = itemView.getRight() - itemView.getLeft();

                    // Set the background color and corner radius for deleted tasks
                    deletedColor = Color.RED;
                    GradientDrawable deleteBackground = new GradientDrawable();
                    deleteBackground.setColor(deletedColor);
                    deleteBackground.setCornerRadius(backgroundCornerOffset);

                    // Set the background color and corner radius for edited tasks
                    editedColor = Color.BLUE;
                    GradientDrawable editBackground = new GradientDrawable();
                    editBackground.setColor(editedColor);
                    editBackground.setCornerRadius(backgroundCornerOffset);
//dX represents the horizontal displacement or distance of the swipe action. It indicates how far the item view has been swiped horizontally.

                    if (dX > 0) {
                        // Swiping to the right (edit action)
                        int iconMargin = (itemViewHeight - editIcon.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + (itemViewHeight - editIcon.getIntrinsicHeight()) / 2;
                        int iconBottom = iconTop + editIcon.getIntrinsicHeight();

                        // Set the bounds for the edit icon
                        int iconRight = itemView.getLeft() + iconMargin + editIcon.getIntrinsicWidth();
                        int iconLeft = itemView.getLeft() + iconMargin;
                        editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        // Set the background color and icon for edited tasks
                        editBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());

                        // Draw the edit icon and background for edited tasks
                        editBackground.draw(c);
                        editIcon.draw(c);
                    } else if (dX < 0) {
                        // Swiping to the left (delete action)
                        int iconMargin = (itemViewHeight - deleteIcon.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + (itemViewHeight - deleteIcon.getIntrinsicHeight()) / 2;
                        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                        // Set the bounds for the delete icon
                        int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        // Set the background color and icon for deleted tasks
                        deleteBackground.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());

                        // Draw the delete icon and background for deleted tasks
                        deleteBackground.draw(c);
                        deleteIcon.draw(c);
                    }
                }
            }

        };

        // Attach swipe gesture listener to RecyclerView
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }



    private void editTask(Task task, String taskId) {
        // Handle the edit action for the selected task
        // Start a new activity or show a dialog for editing the task
        Intent intent = new Intent(HomeActivity.this, EditActivity.class);
        intent.putExtra("taskId", taskId);
        intent.putExtra("task", task);
        startActivity(intent);
    }

    private void deleteTask(Task task, int position) {
        // Handle the delete action for the selected task
        // For example, you can show a confirmation dialog before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform the delete operation
                        // Remove the task from the database and update the RecyclerView
                        databaseRef.child(task.getId()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (position >= 0 && position < taskList.size()) {
                                            taskList.remove(position);
                                            taskAdapter.notifyItemRemoved(position);
                                            Toast.makeText(HomeActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (taskList.isEmpty()) {
                                                Toast.makeText(HomeActivity.this, "Task list is empty", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendToCompletedTaskPage() {
        // Send user to the CompletedTaskActivity
        Intent completedTaskIntent = new Intent(HomeActivity.this, CompletedTaskActivity.class);
        startActivity(completedTaskIntent);
        finish();
    }
}
