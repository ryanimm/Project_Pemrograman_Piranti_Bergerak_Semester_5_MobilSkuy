package com.example.a412020007_ryanimmanuel_admin;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.a412020007_ryanimmanuel_admin.home.HomeActivity;
import com.example.a412020007_ryanimmanuel_admin.login.LoginActivity;


public class MainActivity extends AppCompatActivity {
    private static final String PREF_NAME = "login_preferences";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Mengambil SharedPreferences
        SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // Cek apakah user sudah login
        boolean isLoggedIn = pref.getBoolean(KEY_IS_LOGGED_IN, false);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isLoggedIn) {
                // Jika sudah login, dialihkan ke HomeActivity
                Utils.moveClearBackstack(this, HomeActivity.class);
            } else {
                Utils.moveClearBackstack(this, LoginActivity.class);
            }
            finish();
        }, 1000);
    }
}
