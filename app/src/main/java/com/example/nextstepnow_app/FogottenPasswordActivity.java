package com.example.nextstepnow_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FogottenPasswordActivity extends AppCompatActivity {
    private EditText emailEditText, newPasswordEditText, retypePasswordEditText;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogotten_password);
        TextView textView = findViewById(R.id.textView19);
        ImageView imageView = findViewById(R.id.imageView6);
        TextView textView1 = findViewById(R.id.textView20);
        mAuth=FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.textView21);
        newPasswordEditText = findViewById(R.id.textView23);
        retypePasswordEditText = findViewById(R.id.textView24);
        resetPasswordButton = findViewById(R.id.button8);
        ImageView imageView3 =findViewById(R.id.imageView15);

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login page
                Intent intent = new Intent(FogottenPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.forgotton_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            textView.setBackgroundColor(Color.parseColor("#46946F"));
            textView1.setTextColor(Color.parseColor("#FFFFFF"));
            imageView.setImageResource(R.drawable.img4_dark);

        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            imageView.setImageResource(R.drawable.img4_light);
            textView1.setTextColor(Color.parseColor("#000000"));
        }

//        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetPassword();
//            }
//        });


    }

//    private void resetPassword() {
//        String email = emailEditText.getText().toString().trim();
//        String newPassword = newPasswordEditText.getText().toString().trim();
//        String retypePassword = retypePasswordEditText.getText().toString().trim();
//
//        if (TextUtils.isEmpty(email)) {
//            emailEditText.setError("Enter your email address");
//            return;
//        }
//
//        if (TextUtils.isEmpty(newPassword)) {
//            newPasswordEditText.setError("Enter a new password");
//            return;
//        }
//
//        if (newPassword.length() < 6) {
//            newPasswordEditText.setError("Password must be at least 6 characters");
//            return;
//        }
//        if (!newPassword.equals(retypePassword)) {
//            retypePasswordEditText.setError("Passwords do not match");
//            return;
//        }
//
//        // Update password using Firebase Authentication
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null){
//            user.updatePassword(newPassword).addOnCompleteListener(task ->{
//                if (task.isSuccessful()) {
//                    Toast.makeText(FogottenPasswordActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
//                    //Redirect to the home page
//                    startActivity(new Intent(FogottenPasswordActivity.this, LoginActivity.class));
//                    finish();
//                }
//                else {
//                    Toast.makeText(FogottenPasswordActivity.this,"Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//
//            });
//        } else {
//            Toast.makeText(FogottenPasswordActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
//        }
//    }
}
