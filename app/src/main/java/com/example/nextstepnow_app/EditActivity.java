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
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private EditText editTextDate;
    private Button buttonUpdateTask;
    private DatabaseReference databaseRef;
    private String taskId;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ImageView imageView = findViewById(R.id.imageView10);
        TextView textView1 = findViewById(R.id.TaskName);
        TextView textView2 = findViewById(R.id.textView5);
        TextView textView3 = findViewById(R.id.textView6);
        TextView textView4 = findViewById(R.id.textView7);
        TextView textView5 = findViewById(R.id.textView8);
        TextView textView6 = findViewById(R.id.textView9);
        TextView textView7 = findViewById(R.id.textView10);
        TextView textView8 = findViewById(R.id.textView4);
        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.edit_layout);

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
        //bottomNavigationView.setSelectedItemId(R.id.menu_item_add_task);
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
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login page
                Intent intent = new Intent(EditActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        NetworkConnectivityUtil networkConnectivityUtil = new NetworkConnectivityUtil(this);
        networkConnectivityUtil.startNetworkMonitoring();
        // Set click listener on the calendar ImageView
        ImageView calendarImageView = findViewById(R.id.imageView8);
        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        // Get the task ID passed from the previous activity
        taskId = getIntent().getStringExtra("taskId");

        // Get a reference to the Firebase Realtime Database
        if (taskId != null) {
            // Get a reference to the Firebase Realtime Database
            databaseRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId);
        } else {
            // Handle the case when taskId is null (e.g., show an error message)
            Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // Initialize views
        editTextTitle = findViewById(R.id.textView3);
        editTextDate = findViewById(R.id.textView10);
        buttonUpdateTask = findViewById(R.id.button4);

        // Initialize the dateFormat object
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Set click listener on the "Update Task" button
        buttonUpdateTask.setOnClickListener(v -> updateTask());
    }

    private void showDatePickerDialog() {
        calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String selectedDate = dateFormat.format(calendar.getTime());
                        editTextDate.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private void updateTask() {
        String title = editTextTitle.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty()) {
            // Display a toast message if the title or date is empty
            Toast.makeText(this, "Please enter a title and date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the task title and date in the database
        databaseRef.child("title").setValue(title);
        databaseRef.child("date").setValue(date)
                .addOnSuccessListener(aVoid -> {
                    // Display a success message and finish the activity
                    Toast.makeText(EditActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();

                    // Send the user to the HomeActivity
                    Intent intent = new Intent(EditActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Display a failure message if the task could not be updated
                    Toast.makeText(EditActivity.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                });
    }
}
