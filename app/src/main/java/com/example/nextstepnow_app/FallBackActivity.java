package com.example.nextstepnow_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FallBackActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_back);
        TextView textView1 = findViewById(R.id.textView4);
        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.fall_back_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            textView1.setTextColor(Color.parseColor("#FFFFFF"));


        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
            textView1.setTextColor(Color.parseColor("#000000"));
        }

        Button retry = findViewById(R.id.button);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        // Connection is stable, proceed with normal flow
                        Intent intent = new Intent(FallBackActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close the OfflineScreen activity
                    }
                };

                NetworkRequest networkRequest = new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build();
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            }
     });


}
}