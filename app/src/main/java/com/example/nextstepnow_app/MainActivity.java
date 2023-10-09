package com.example.nextstepnow_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT =3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.main_layout);

        // Check if dark mode is enabled
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            TextView textView = findViewById(R.id.textView);
            textView.setTextColor(getResources().getColor(R.color.dark_text));
        } else {
            // Dark mode is not enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
            TextView textView = findViewById(R.id.textView);
            textView.setTextColor(getResources().getColor(R.color.light_text));
        }
        new Handler().postDelayed(new Runnable() {
           @Override
            public void run() {

              Intent homeIntent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
