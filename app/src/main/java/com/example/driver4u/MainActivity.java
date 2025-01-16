package com.example.driver4u;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private Button sendOtp;
    private int defaultButtonColor;
    private int keyboardHeight = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            adjustButtonPosition(keyboardHeight);
            return insets;
        });

        phoneNumber = findViewById(R.id.phone_number);
        sendOtp = findViewById(R.id.send_otp);

        //To enable button only when 10 digit phone number is entered

        defaultButtonColor = ContextCompat.getColor(this, R.color.black);
        setButtonState(phoneNumber.getText().toString().isEmpty(), phoneNumber.getText().length() == 10);

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButtonState(s == null || s.length() == 0, s != null && s.length() == 10);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Not needed in this case
            }
        });
        sendOtp.setOnClickListener(v -> {nextActivity();});
    }

    private void setButtonState(boolean isEmpty, boolean hasTenDigits) {
        if (isEmpty) {
            sendOtp.setBackgroundColor(Color.parseColor("#C3C3C3")); // Gray color
            sendOtp.setEnabled(false);
        } else if (hasTenDigits) {
            sendOtp.setBackgroundColor(defaultButtonColor); // Black color
            sendOtp.setEnabled(true);
        }
        else {
            sendOtp.setBackgroundColor(Color.parseColor("#C3C3C3"));
            sendOtp.setEnabled(false);
        }
    }
    private void adjustButtonPosition(int keyboardHeight) {
        sendOtp.setTranslationY(-keyboardHeight);
    }

    void nextActivity(){
        Intent intent = new Intent(this, Verify_Number.class);
        String phoneNumber = this.phoneNumber.getText().toString();
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }


}