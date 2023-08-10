package com.example.a412020007_ryanimmanuel_admin.addAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityAddAdminBinding;
import java.util.HashMap;
import java.util.Map;

public class AddAdminActivity extends AppCompatActivity {
    private ActivityAddAdminBinding binding;
    private Boolean isUpdate = false;
    private Map<String, Object> admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        binding.btnAdd.setOnClickListener(view -> {
            if(!binding.etEmail.getText().toString().isEmpty() ||
                    !binding.etPassword.getText().toString().isEmpty()) {
                if(isUpdate) {
                    updateData();
                } else {
                    addData();
                }
            }
        });

        Gson gson = new Gson();
        if(getIntent().getStringExtra("data") != null) {
            admin = gson.fromJson(getIntent().getStringExtra("data"), Map.class);
            initUpdateUi(admin);
            isUpdate = true;
        }
    }

    private void initUpdateUi(Map<String, Object> admin) {
        binding.etEmail.setText(admin.get("email").toString());
        binding.etPassword.setText(admin.get("password").toString());
        binding.btnAdd.setText("Update");
    }

    private void addData() {
        binding.btnAdd.setEnabled(false);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Query untuk mengecek apakah email sudah ada dalam koleksi "admins"
        firestore.collection("admins").whereEqualTo("email", binding.etEmail.getText().toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Jika email sudah ada dalam koleksi, tampilkan pesan error
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Utils.showToast(AddAdminActivity.this, "Email is already registered as admin!");
                        binding.btnAdd.setEnabled(true);
                    } else {
                        // Jika email belum ada dalam koleksi, tambahkan data baru
                        firestore.collection("admins").add(new HashMap<String, String>() {{
                            put("email", binding.etEmail.getText().toString());
                            put("password", binding.etPassword.getText().toString());
                        }}).addOnSuccessListener(documentReference -> {
                            finish();
                            Utils.showToast(AddAdminActivity.this, "Data added!");
                        }).addOnFailureListener(e -> {
                            binding.btnAdd.setEnabled(true);
                            Utils.showToast(AddAdminActivity.this, e.getMessage());
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    binding.btnAdd.setEnabled(true);
                    Utils.showToast(AddAdminActivity.this, e.getMessage());
                });
    }

    private void updateData() {
        binding.btnAdd.setEnabled(false);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<String, Object>() {{
            put("email", binding.etEmail.getText().toString());
            put("password", binding.etPassword.getText().toString());
        }};

        // Query untuk mengecek apakah email sudah ada dalam koleksi "admins"
        firestore.collection("admins").whereEqualTo("email", binding.etEmail.getText().toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Jika email sudah ada dalam koleksi, tampilkan pesan error
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Utils.showToast(AddAdminActivity.this, "Email is already registered as admin!");
                        binding.btnAdd.setEnabled(true);
                    } else {
                        // Jika email belum ada dalam koleksi, update data
                        firestore.collection("admins").document((String) admin.get("id")).update(data).addOnSuccessListener(documentReference -> {
                            finish();
                            Utils.showToast(AddAdminActivity.this, "Data updated!");
                        }).addOnFailureListener(e -> {
                            binding.btnAdd.setEnabled(true);
                            Utils.showToast(AddAdminActivity.this, e.getMessage());
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    binding.btnAdd.setEnabled(true);
                    Utils.showToast(AddAdminActivity.this, e.getMessage());
                });
    }
                }