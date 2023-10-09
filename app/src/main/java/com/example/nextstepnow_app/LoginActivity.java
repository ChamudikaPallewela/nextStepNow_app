package com.example.nextstepnow_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpView, forgotPasswordTextView;
    private FirebaseAuth mAuth;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    private GoogleApiClient mGoogleApiClient;

    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        TextView textView = findViewById(R.id.textView6);
        ImageView imageView = findViewById(R.id.imageView4);
        TextView textView1 = findViewById(R.id.textView7);
        emailEditText=findViewById(R.id.textView11);
        passwordEditText = findViewById(R.id.textView12);
        TextView textView2 = findViewById(R.id.textView13);
        TextView textView3 = findViewById(R.id.textView15);
        loginButton = findViewById(R.id.button2);
        signUpView = findViewById(R.id.textView16);
        forgotPasswordTextView = findViewById(R.id.textView14);
        // Initialize the GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Initialize the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set the onClick listener for the sign-in button
        signInButton = findViewById(R.id.btn_google_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.login_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            textView.setBackgroundColor(Color.parseColor("#46946F"));
            textView1.setTextColor(Color.parseColor("#FFFFFF"));
            textView2.setTextColor(Color.parseColor("#FFFFFF"));
            textView3.setTextColor(Color.parseColor("#FFFFFF"));
            forgotPasswordTextView.setTextColor(Color.parseColor("#FFFFFF"));
            imageView.setImageResource(R.drawable.img2_dark);

        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            imageView.setImageResource(R.drawable.img2_light);
            textView1.setTextColor(Color.parseColor("#000000"));
            textView2.setTextColor(Color.parseColor("#000000"));
            textView3.setTextColor(Color.parseColor("#000000"));
            forgotPasswordTextView.setTextColor(Color.parseColor("#000000"));
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, FogottenPasswordActivity.class));
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e(TAG, "Google Sign-In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                            // Send the user to the home activity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish(); // Optional: Finish the current activity to prevent the user from going
                            // You can access the user's information here
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Sign-in failed, handle the error
                        }
                    }
                });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter an email address");
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter a password");
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                        //Redirect to the home page
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Login Filed: " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}