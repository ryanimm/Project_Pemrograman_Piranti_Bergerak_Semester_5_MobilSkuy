package com.example.a412020007_ryanimmanuel.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.example.a412020007_ryanimmanuel.Utils;
import com.example.a412020007_ryanimmanuel.databinding.ActivityLoginBinding;
import com.example.a412020007_ryanimmanuel.home.HomeActivity;
import com.example.a412020007_ryanimmanuel.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(view -> login());
        binding.btnRegister.setOnClickListener(view -> Utils.move(this, RegisterActivity.class));
    }

    private void login() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(!binding.etEmail.getText().toString().isEmpty() &&
                !binding.etPassword.getText().toString().isEmpty()) {
            binding.btnLogin.setEnabled(false);

            auth.signInWithEmailAndPassword(
                    binding.etEmail.getText().toString(),
                    binding.etPassword.getText().toString()
            ).addOnSuccessListener(authResult -> {
                Utils.moveClearBackstack(LoginActivity.this, HomeActivity.class);
                finish();
            }).addOnFailureListener(e -> {
                binding.btnLogin.setEnabled(true);
                String msg = e.getMessage();

                if(msg.toLowerCase().contains("there is no user") || msg.toLowerCase().contains("the password is invalid")) {
                    Utils.showToast(LoginActivity.this, "Invalid email/password");
                } else {
                    Utils.showToast(LoginActivity.this, e.getMessage());
                }
            });
        }
    }
}