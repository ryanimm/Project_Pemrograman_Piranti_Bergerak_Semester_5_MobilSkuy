package com.example.a412020007_ryanimmanuel_admin.admins;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.addAdmin.AddAdminActivity;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityAdminBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;
    private AdminsAdapter adapter;
    private static final String PREF_NAME = "login_preferences";
    private static final String KEY_ADMIN_ID = "admin_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);

        binding.btnAdd.setOnClickListener(view -> Utils.move(this, AddAdminActivity.class));

        adapter = new AdminsAdapter(new OnAdminClick() {
            @Override
            public void onEdit(Map<String, Object> admin) {
                Gson gson = new Gson();
                Utils.moveWithExtra(
                        AdminActivity.this,
                        AddAdminActivity.class,
                        "data",
                        gson.toJson(admin));
            }

            @Override
            public void onDelete(Map<String, Object> admin) {
                deleteData((String) admin.get("id"));
            }
        });

        binding.rvAdmin.setAdapter(adapter);
        binding.rvAdmin.setLayoutManager(new LinearLayoutManager(this));

        getAdmins();
    }

    private void getAdmins() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("admins").addSnapshotListener((value, error) -> {
            if (error != null) {
                Utils.showToast(AdminActivity.this, error.getMessage());
            }
            else if (value != null) {
                List<Map<String, Object>> admins = new ArrayList<>();
                for (DocumentSnapshot snapshot: value.getDocuments()) {
                    Map<String, Object> admin= snapshot.getData();
                    admin.put("id", snapshot.getId());
                    admins.add(admin);
                }

                if(!admins.isEmpty()) {
                    binding.empty.setVisibility(View.GONE);
                    binding.rvAdmin.setVisibility(View.VISIBLE);
                } else {
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.rvAdmin.setVisibility(View.GONE);
                }

                adapter.submitList(admins);

            }
        });
    }


    private void deleteData(String id) {
        // ambil id admin yang sedang login dari preferensi
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String loggedInAdminId = preferences.getString(KEY_ADMIN_ID, null);
        // jika id admin yang sedang login sama dengan id admin yang akan dihapus,
        // tampilkan pesan error dan jangan lakukan penghapusan
        if (loggedInAdminId != null && loggedInAdminId.equals(id)) {
            Utils.showToast(this, "Cannot delete current admin!");
            return;
        }
        // jika id admin yang sedang login berbeda dengan id admin yang akan dihapus,
        // lakukan penghapusan data admin
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("admins").document(id).delete();
        Utils.showToast(this, "Success delete data!");
        Intent intent = new Intent(AdminActivity.this, AdminActivity.class);
        startActivity(intent);
        finish();
    }
}