package com.example.driver4u;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth instance BEFORE using it
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomePage.class));
            finish();
        }

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        Button signInButton = findViewById(R.id.signInButton);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Attempt to sign in with email and password
            signInWithEmailAndPassword(email, password);
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        // Step 2: Try signing in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, signInTask -> {
                    if (signInTask.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("LoginActivity", "signInWithEmail:success");
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Redirect to MainActivity after successful login
                        Intent intent = new Intent(LoginActivity.this, HomePage.class);
                        intent.putExtra("userEmail", email);
                        startActivity(intent);
                    } else {
                        // If sign in fails, check the error message in the exception
                        // Handle other authentication errors
                        Toast.makeText(LoginActivity.this, "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}