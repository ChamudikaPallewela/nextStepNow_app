package com.example.nextstepnow_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {
    private Button button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        TextView textView = findViewById(R.id.textView2);
        ImageView imageView = findViewById(R.id.imageView9);
        TextView textView1 = findViewById(R.id.textView5);

        button3 = findViewById(R.id.button3);
        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.start_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            textView.setBackgroundColor(Color.parseColor("#46946F"));
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            imageView.setImageResource(R.drawable.img_dark);

        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            imageView.setImageResource(R.drawable.img_light);
            textView1.setTextColor(Color.parseColor("#000000"));
        }
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the login activity
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}