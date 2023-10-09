package com.example.nextstepnow_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TimerPage extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Chronometer chronometer;
    private Button startButton;
    private Button stopButton;
    private Button resumeButton;

    private long elapsedTime = 0;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_page);
        // Initialize UI components
        chronometer = findViewById(R.id.chronometer);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resumeButton = findViewById(R.id.resumeButton);
        ImageView imageView =findViewById(R.id.imageView13);
        NetworkConnectivityUtil networkConnectivityUtil = new NetworkConnectivityUtil(this);
        networkConnectivityUtil.startNetworkMonitoring();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login page
                Intent intent = new Intent(TimerPage.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.timer_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
        } else {
            // Dark mode is not enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
        }

        // Set up the bottom navigation view and handle item selection
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.menu_item_home:
                    // Navigate to the Home page
                    startActivity(new Intent(TimerPage.this, HomeActivity.class));
                    overridePendingTransition(0, 0);
                    finish(); // Optional: Close the current activity
                    return true;
                case R.id.menu_item_add_task:
                    // Navigate to the Add Task page
                    startActivity(new Intent(TimerPage.this, AddTaskActivity.class));
                    overridePendingTransition(0, 0);
                    finish(); // Optional: Close the current activity
                    return true;
                case R.id.menu_item_completed_task:
                    // Navigate to the Completed Task page
                    startActivity(new Intent(TimerPage.this, CompletedTaskActivity.class));
                    overridePendingTransition(0, 0);
                    finish(); // Optional: Close the current activity
                    return true;
                case R.id.menu_item_settings:
                    // Navigate to the Settings page
                    startActivity(new Intent(TimerPage.this, SettingsActivity.class));
                    overridePendingTransition(0, 0);
                    finish(); // Optional: Close the current activity
                    return true;
            }
            return false;
        });
        // Set click listeners for buttons
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeTimer();
            }
        });
    }

    private void resumeTimer() {
        if (!isRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
            chronometer.start();
            isRunning = true;
        }
    }

    private void stopTimer() {
        if (isRunning) {
            chronometer.stop();
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            isRunning = false;
        }
    }

    private void startTimer() {
        if (!isRunning) {
            // Reset elapsed time to 0
            elapsedTime = 0;

            // Set chronometer base to current system time minus the elapsed time
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedTime);

            // Set time format for the chronometer
            chronometer.setFormat("00:%s");

            // Start the chronometer
            chronometer.start();
            isRunning = true;
        }


    }
}
