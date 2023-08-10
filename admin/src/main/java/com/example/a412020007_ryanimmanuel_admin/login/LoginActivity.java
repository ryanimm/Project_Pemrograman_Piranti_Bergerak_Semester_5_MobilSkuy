package com.example.a412020007_ryanimmanuel_admin.login;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityLoginBinding;
import com.example.a412020007_ryanimmanuel_admin.home.HomeActivity;
import com.example.a412020007_ryanimmanuel_admin.register.RegisterActivity;
import android.content.SharedPreferences;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private static final String PREF_NAME = "login_preferences";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADMIN_ID = "admin_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // cek apakah pengguna sudah login sebelumnya
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            // jika sudah, langsung pindah ke HomeActivity
            Utils.moveClearBackstack(LoginActivity.this, HomeActivity.class);
        } else {
            // jika belum, tampilkan form login
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            binding.btnLogin.setOnClickListener(view -> {
                if (!binding.etEmail.getText().toString().isEmpty() ||
                        !binding.etPassword.getText().toString().isEmpty()) {
                    login();
                }
            });
            binding.btnRegister.setOnClickListener(view -> Utils.move(this, RegisterActivity.class));
        }
    }

    private void login() {
        loading(true);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("admins")
                .whereEqualTo("email", binding.etEmail.getText().toString())
                .whereEqualTo("password", binding.etPassword.getText().toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    loading(false);
                    if (queryDocumentSnapshots.getDocumentChanges().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Invalid email/password!", Toast.LENGTH_SHORT).show();
                    } else {
                        // simpan informasi login di shared preferences
                        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(KEY_IS_LOGGED_IN, true);
                        editor.putString(KEY_EMAIL, binding.etEmail.getText().toString());
                        editor.putString(KEY_ADMIN_ID, queryDocumentSnapshots.getDocuments().get(0).getId());
                        editor.apply();
                        // pindah ke HomeActivity
                        Utils.moveClearBackstack(LoginActivity.this, HomeActivity.class);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    Utils.showToast(LoginActivity.this, e.getMessage());
                });
    }


    private void loading(Boolean value) {
        if(value) {
            binding.pbLogin.setVisibility(View.VISIBLE);
            binding.btnLogin.setVisibility(View.GONE);
        } else {
            binding.pbLogin.setVisibility(View.GONE);
            binding.btnLogin.setVisibility(View.VISIBLE);
        }
    }
}