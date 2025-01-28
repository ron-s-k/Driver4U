package com.example.driver4u;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class Verify_Number extends AppCompatActivity {
        private int defaultButtonColor;
        private int keyboardHeight = 0;
        private TextView sentMessage, resendOtp;
        private EditText inputOtp;
        private Button confirmOtp;
        String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_number);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            adjustButtonPosition(keyboardHeight);
            return insets;
        });
        sentMessage = findViewById(R.id.sentMessage);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sentMessage.setText("Enter OTP sent to  +91 "+phoneNumber);
        inputOtp = findViewById(R.id.inputOtp);
        resendOtp = findViewById(R.id.resendOtp);
        confirmOtp = findViewById(R.id.confirmOtp);

        //TO enable button only when 6 digit OTP is entered

        defaultButtonColor = ContextCompat.getColor(this, R.color.black);
        setButtonState(phoneNumber.isEmpty(), phoneNumber.length() == 6);

        inputOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButtonState(s == null || s.length() == 0, s != null && s.length() == 6);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Not needed in this case
            }
        });

        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Verify_Number.this, HomePage.class);
                startActivity(intent);
            }
        });

    }
    private void setButtonState(boolean isEmpty, boolean hasTenDigits) {
        if (isEmpty) {
            confirmOtp.setBackgroundColor(Color.parseColor("#C3C3C3")); // Gray color
            confirmOtp.setEnabled(false);
        } else if (hasTenDigits) {
            confirmOtp.setBackgroundColor(defaultButtonColor); // Black color
            confirmOtp.setEnabled(true);
        }
        else {
            confirmOtp.setBackgroundColor(Color.parseColor("#C3C3C3"));
            confirmOtp.setEnabled(false);
        }
    }
    private void adjustButtonPosition(int keyboardHeight) {
        confirmOtp.setTranslationY(-keyboardHeight);

    }
}