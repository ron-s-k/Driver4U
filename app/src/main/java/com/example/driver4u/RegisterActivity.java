package com.example.driver4u;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, password, phone;
    Button register, login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        phone = findViewById(R.id.editTextPhone);
        register = findViewById(R.id.buttonRegister);
        login = findViewById(R.id.buttonLogin);

        login.setOnClickListener(view -> {
            // Create an Intent to start the LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

            // Start the LoginActivity
            startActivity(intent);
        });

        register.setOnClickListener(view -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            String userName = username.getText().toString().trim();
            String userPhone = phone.getText().toString().trim();

            if (userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty() || userPhone.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d("RegisterActivity", "User registration started");


            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Get the current user's UID
                            // Create a map to store user data
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", userName);
                            user.put("phone", userPhone);
                            user.put("email", userEmail);

                            // Store the user data in FireStore using the user's UID as the document ID
                            db.collection("users")
                                    .document(userEmail)  // Use UID as document ID
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "User data saved.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show());

                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                            // Redirect to LoginActivity after successful registration
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Optional: close RegisterActivity so user can't go back to it
                        } else {
                            // If registration fails, display a message to the user
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                                Log.d("RegisterActivity", "Email already in use");
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                Log.d("RegisterActivity", "Registration failed: " + task.getException());
                            }
                        }
                    });
        });
    }
}