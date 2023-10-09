package com.example.nextstepnow_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private EditText editTextTitle;
    private Button buttonAddTask;
    private EditText dueDateEditText;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize views
        editTextTitle = findViewById(R.id.textView3);
        dueDateEditText = findViewById(R.id.textView10);
        buttonAddTask = findViewById(R.id.button2);
        TextView textView1 = findViewById(R.id.TaskName);
        TextView textView2 = findViewById(R.id.textView5);
        TextView textView3 = findViewById(R.id.textView6);
        TextView textView4 = findViewById(R.id.textView7);
        TextView textView5 = findViewById(R.id.textView8);
        TextView textView6 = findViewById(R.id.textView9);
        TextView textView7 = findViewById(R.id.textView10);
        ImageView imageView =findViewById(R.id.imageView10);
        TextView textView8 = findViewById(R.id.textView4);

        NetworkConnectivityUtil networkConnectivityUtil = new NetworkConnectivityUtil(this);
        networkConnectivityUtil.startNetworkMonitoring();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login page
                Intent intent = new Intent(AddTaskActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // Get a reference to the Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference("tasks");

        // Set click listener on the calendar ImageView
        ImageView calendarImageView = findViewById(R.id.imageView8);
        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set initial date format
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.add_task_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            textView1.setTextColor(Color.parseColor("#FFFFFF"));
            textView2.setTextColor(Color.parseColor("#FFFFFF"));
            textView3.setTextColor(Color.parseColor("#FFFFFF"));
            textView4.setTextColor(Color.parseColor("#FFFFFF"));
            textView5.setTextColor(Color.parseColor("#FFFFFF"));
            textView6.setTextColor(Color.parseColor("#FFFFFF"));
            textView7.setTextColor(Color.parseColor("#FFFFFF"));
            textView8.setTextColor(Color.parseColor("#FFFFFF"));

        } else {
            // Dark mode is not enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
            textView1.setTextColor(Color.parseColor("#000000"));
            textView2.setTextColor(Color.parseColor("#000000"));
            textView3.setTextColor(Color.parseColor("#000000"));
            textView4.setTextColor(Color.parseColor("#000000"));
            textView5.setTextColor(Color.parseColor("#000000"));
            textView6.setTextColor(Color.parseColor("#000000"));
            textView7.setTextColor(Color.parseColor("#000000"));
            textView8.setTextColor(Color.parseColor("#000000"));
        }

        // Set up bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_item_add_task);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.menu_item_home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0, 0);
                    finish();

                    return true;
                case R.id.menu_item_add_task:
                    overridePendingTransition(0, 0);

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
        });


        // Set click listener on the "Add Task" button
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    // Method to add a task to the Firebase Realtime Database
    private void addTask() {
        String taskId = databaseRef.push().getKey();
        String taskTitle = editTextTitle.getText().toString().trim();
        String taskDate = dueDateEditText.getText().toString().trim();

        if (taskTitle.isEmpty() || taskDate.isEmpty()) {
            // Display a toast message if the title or date is empty
            Toast.makeText(this, "Please enter a title and date", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(taskId, taskTitle, taskDate);

        // Store the task object in the database
        databaseRef.child(taskId).setValue(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Display a success message and navigate back to the Home page
                        Toast.makeText(AddTaskActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddTaskActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Display a failure message if the task could not be added
                        Toast.makeText(AddTaskActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to show the date picker dialog
    private void showDatePickerDialog() {
        calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTaskActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String selectedDate = dateFormat.format(calendar.getTime());
                        dueDateEditText.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}
