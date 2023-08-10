package com.example.a412020007_ryanimmanuel_admin.register;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityRegisterBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAdd.setOnClickListener(view -> {
            if (!binding.etEmail.getText().toString().isEmpty() ||
                    !binding.etPassword.getText().toString().isEmpty()) {
                register();
            }
        });

    }

    private void register() {
        binding.btnAdd.setEnabled(false);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Query untuk mengecek apakah email sudah ada dalam koleksi "admins"
        firestore.collection("admins").whereEqualTo("email", binding.etEmail.getText().toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Jika email sudah ada dalam koleksi, tampilkan pesan error
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Utils.showToast(RegisterActivity.this, "Email is already registered as admin!");
                        binding.btnAdd.setEnabled(true);
                    } else {
                        // Jika email belum ada dalam koleksi, tambahkan data baru
                        firestore.collection("admins").add(new HashMap<String, String>() {{
                            put("email", binding.etEmail.getText().toString());
                            put("password", binding.etPassword.getText().toString());
                        }}).addOnSuccessListener(documentReference -> {
                            finish();
                            Utils.showToast(RegisterActivity.this, "Register Success");
                        }).addOnFailureListener(e -> {
                            binding.btnAdd.setEnabled(true);
                            Utils.showToast(RegisterActivity.this, e.getMessage());
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    binding.btnAdd.setEnabled(true);
                    Utils.showToast(RegisterActivity.this, e.getMessage());
                });
    }

}