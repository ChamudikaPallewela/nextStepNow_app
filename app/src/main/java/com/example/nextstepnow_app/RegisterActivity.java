package com.example.nextstepnow_app;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Source;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullNameText, emailEditText, PasswordEditText, RepeatPasswordEditText;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference(); // Initialize the userDatabase reference

        TextView textView = findViewById(R.id.textView8);
        ImageView imageView = findViewById(R.id.imageView7);
        TextView textView1 = findViewById(R.id.textView9);
        fullNameText = findViewById(R.id.textView12);
        emailEditText = findViewById(R.id.textView10);
        PasswordEditText = findViewById(R.id.textView17);
        RepeatPasswordEditText = findViewById(R.id.textView18);
        registerButton = findViewById(R.id.button6);
        ImageView imageView3 = findViewById(R.id.imageView14);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login page
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.register_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            textView.setBackgroundColor(Color.parseColor("#46946F"));
            textView1.setTextColor(Color.parseColor("#FFFFFF"));
            imageView.setImageResource(R.drawable.img3_dark);
        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            imageView.setImageResource(R.drawable.img3_light);
            textView1.setTextColor(Color.parseColor("#000000"));
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String fullName = fullNameText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = PasswordEditText.getText().toString().trim();
        String repeatPassword = RepeatPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            fullNameText.setError("Enter your full name");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter an email address");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            PasswordEditText.setError("Enter a password");
            return;
        }

        if (password.length() < 6) {
            PasswordEditText.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(repeatPassword)) {
            RepeatPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String userId = currentUser.getUid();
                        saveUserData(userId, fullName, email);

                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        // Redirect to the login page
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String fullName, String email) {
        User user = new User(userId, fullName, email);
        userDatabase.child("users").child(userId).setValue(user); // Use the userDatabase reference to save the user data
    }
}
