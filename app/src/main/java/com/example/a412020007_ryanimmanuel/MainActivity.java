package com.example.a412020007_ryanimmanuel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.auth.FirebaseAuth;
import com.example.a412020007_ryanimmanuel.login.LoginActivity;
import com.example.a412020007_ryanimmanuel.home.HomeActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if(auth.getCurrentUser() != null) {
                Utils.moveClearBackstack(this, HomeActivity.class);
            } else {
                Utils.moveClearBackstack(this, LoginActivity.class);
            }
            finish();
        }, 1000);
    }
}