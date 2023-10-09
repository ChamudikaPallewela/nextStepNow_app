package com.example.nextstepnow_app;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE =3;
    private BottomNavigationView bottomNavigationView;
    private static final String THEME_PREFERENCE = "themePreference";
    private static final String THEME_MODE_KEY = "themeMode";
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final int CAMERA_REQUEST_CODE = 2;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String userUid;
    private EditText tvEmail;
    private Button logout;
    //private FirebaseAuth auth;

    private Switch themeSwitch;

    private SharedPreferences themePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView textView1 = findViewById(R.id.textView22);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);

        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a confirmation dialog
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Sign out the user
                            firebaseAuth.signOut();

                            // Navigate to the login screen
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            // Show a logout success message
                            Toast.makeText(SettingsActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        Switch DarkSwitch = findViewById(R.id.switchDarkMode);
        ImageView imageView1 = findViewById(R.id.imageView11);
        imageView = findViewById(R.id.imageView);
        Button btnAddImage = findViewById(R.id.btnAddImage);
        Button btnChangeImage = findViewById(R.id.btnChangeImage);
        //Button logout=findViewById(R.id.logout);
        tvEmail = findViewById(R.id.tvEmail);
        //auth=FirebaseAuth.getInstance();


//        logout.setOnClickListener(view->{
//            auth.signOut();
//        });

        NetworkConnectivityUtil networkConnectivityUtil = new NetworkConnectivityUtil(this);
        networkConnectivityUtil.startNetworkMonitoring();
        //FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userUid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            storageReference = FirebaseStorage.getInstance().getReference("Images").child(userUid);
            tvEmail.setText(user.getEmail());

            // Retrieve the image URL from the database and load the image
            databaseReference.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String imageUrl = dataSnapshot.child("imageUri").getValue(String.class);
                        if (imageUrl != null) {
                            loadImage(Uri.parse(imageUrl));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that occurred while retrieving the image URL
                    Log.e("SettingsActivity", "Failed to retrieve image URL: " + databaseError.getMessage());
                }
            });
        }
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login page
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // Get the main layout
        ConstraintLayout mainLayout = findViewById(R.id.settings_layout);

        // Check if dark mode is enabled and set the background color accordingly
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.dark_background));
            textView1.setTextColor(Color.parseColor("#FFFFFF"));
            tvEmail.setTextColor(Color.parseColor("#FFFFFF"));
            DarkSwitch.setTextColor(Color.parseColor("#FFFFFF"));

        } else {
            // Dark mode is not enabled
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background));
            textView1.setTextColor(Color.parseColor("#000000"));
            tvEmail.setTextColor(Color.parseColor("#000000"));
            DarkSwitch.setTextColor(Color.parseColor("#000000"));

        }

        // Set up bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_item_settings);
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

        //Switch Night & Light Part 1

        themeSwitch = findViewById(R.id.switchDarkMode);
        themePreferences = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE);
        boolean isDarkModeEnabled = themePreferences.getBoolean(THEME_MODE_KEY, false);
        themeSwitch.setChecked(isDarkModeEnabled);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableDarkMode();
            } else {
                enableLightMode();
            }

        });

        if (isDarkModeEnabled) {
            enableDarkMode();
        } else {
            enableLightMode();
        }


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, open the camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            // Permission is not granted, request the camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }


    private void saveImageToStorage(Uri imageUri) {
        if (storageReference != null) {
            String uniqueFilename = System.currentTimeMillis() + ".jpg";
            StorageReference imageRef = storageReference.child(uniqueFilename);
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                databaseReference.child(userUid).child("imageUri").setValue(imageUrl)
                                        .addOnSuccessListener(aVoid -> {
                                            // Image URL saved successfully
                                            // Add the image to the device's gallery
                                            saveImageToGallery(imageUri);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle any errors that occurred while saving the image URL
                                            Log.e("SettingsActivity", "Failed to save image URL: " + e.getMessage());
                                        });
                            }))
                    .addOnFailureListener(e -> {
                        // Handle any error that occurred while uploading the image
                        Log.e("SettingsActivity", "Failed to upload image: " + e.getMessage());
                    });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            saveImageToStorage(imageUri);
            loadImage(imageUri);
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                Uri imageUri = saveImageToExternalStorage(imageBitmap);
                if (imageUri != null) {
                    saveImageToStorage(imageUri);
                    loadImage(imageUri);
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri saveImageToExternalStorage(Bitmap imageBitmap) {
        try {
            // Get the external storage directory
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir != null) {
                // Create a file to save the image
                File imageFile = new File(storageDir, "temp_image.jpg");

                // Compress the image and save it to the file
                OutputStream outputStream = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                // Return the file URI
                return Uri.fromFile(imageFile);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with saving the image to the gallery
            } else {
                // Permission denied, show a message or handle the situation accordingly
                Toast.makeText(this, "Write external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToGallery(Uri imageUri) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "Image");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        ContentResolver contentResolver = getContentResolver();
        Uri imageUriGallery = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUriGallery != null) {
            try {
                OutputStream outputStream = contentResolver.openOutputStream(imageUriGallery);
                if (outputStream != null) {
                    InputStream inputStream = contentResolver.openInputStream(imageUri);
                    if (inputStream != null) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        outputStream.close();
                        Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save image to gallery", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to create image in gallery", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadImage(Uri imageUri) {
        Picasso.get().load(imageUri).into(imageView);
    }

    private void enableLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        themePreferences.edit().putBoolean(THEME_MODE_KEY, false).apply();

    }

    private void enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        themePreferences.edit().putBoolean(THEME_MODE_KEY, true).apply();

    }
}
