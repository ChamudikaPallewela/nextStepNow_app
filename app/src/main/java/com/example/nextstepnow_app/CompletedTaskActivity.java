package com.example.nextstepnow_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CompletedTaskActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private TaskAdapter taskAdapter;
    private List<Task> completedTaskList;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);
        ImageView imageView4=findViewById(R.id.imageView4);
        ImageView imageView =findViewById(R.id.imageView4);
        TextView textView26 = findViewById(R.id.textView26);
        NetworkConnectivityUtil networkConnectivityUtil = new NetworkConnectivityUtil(this);
        networkConnectivityUtil.startNetworkMonitoring();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login page
                Intent intent = new Intent(CompletedTaskActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCompletedTasks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.completed_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            textView26.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            // Dark mode is not enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
            textView26.setTextColor(Color.parseColor("#000000"));
        }

        // Set up bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_item_completed_task);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.menu_item_home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0, 0);
                    finish();

                    return true;
                case R.id.menu_item_add_task:
                    startActivity(new Intent(getApplicationContext(), AddTaskActivity.class));
                    overridePendingTransition(0, 0);

                    return true;
                case R.id.menu_item_completed_task:


                    return true;
                case R.id.menu_item_settings:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
            }
            return false;
        });
        // Initialize completed task list and adapter
        completedTaskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(completedTaskList,this);
        recyclerView.setAdapter(taskAdapter);

        // Get reference to Firebase completed tasks
        databaseRef = FirebaseDatabase.getInstance().getReference("completed_tasks");

        // Retrieve completed tasks from Firebase
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing completed task list
                completedTaskList.clear();

                // Iterate over completed task snapshots and add them to the completed task list
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    completedTaskList.add(task);
                }

                // Notify the adapter of the data change
                taskAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Display error message if failed to load completed tasks
                Toast.makeText(CompletedTaskActivity.this, "Failed to load completed tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}