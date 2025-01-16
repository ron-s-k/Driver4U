package com.example.driver4u.toast;

import android.content.Context;
import android.widget.Toast;

public class MakeToast {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
