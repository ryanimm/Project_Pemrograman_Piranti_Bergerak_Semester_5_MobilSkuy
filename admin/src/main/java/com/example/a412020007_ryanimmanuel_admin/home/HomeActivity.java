package com.example.a412020007_ryanimmanuel_admin.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.a412020007_ryanimmanuel_admin.DeveloperCreditActivity;
import com.example.a412020007_ryanimmanuel_admin.R;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.admins.AdminActivity;
import com.example.a412020007_ryanimmanuel_admin.cars.CarsActivity;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityHomeBinding;
import com.example.a412020007_ryanimmanuel_admin.history.HistoryActivity;
import com.example.a412020007_ryanimmanuel_admin.login.LoginActivity;
import com.example.a412020007_ryanimmanuel_admin.orderDetail.OrderDetailActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private HomeAdapter adapter;
    private static final String PREF_NAME = "login_preferences";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);


        binding.cardCar.setOnClickListener(view -> Utils.move(this, CarsActivity.class));
        binding.cardAdmin.setOnClickListener(view -> Utils.move(this, AdminActivity.class));
        binding.cardRevenue.setOnClickListener(view -> Utils.move(this, HistoryActivity.class));

        adapter = new HomeAdapter(this, data ->
                Utils.moveWithExtra(this, OrderDetailActivity.class, "data", data));
        binding.rvPending.setAdapter(adapter);
        binding.rvPending.setLayoutManager(new LinearLayoutManager(this));
        getDatas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void logout() {
        // hapus informasi login dari shared preferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_IS_LOGGED_IN);
        editor.apply();
        // pindah ke LoginActivity
        Utils.moveClearBackstack(HomeActivity.this, LoginActivity.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_history) {
            Utils.move(this, HistoryActivity.class);
        } else if (item.getItemId() == R.id.menu_developer) {
            Utils.move(this, DeveloperCreditActivity.class);
        }else {
            logout();
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void getDatas() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("cars").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Utils.showToast(HomeActivity.this, e.getMessage());
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    // Tampilkan jumlah total document setelah perubahan terjadi
                    binding.tvAdminCount.setText(queryDocumentSnapshots.size() + "");
                }
            }
        });

        firestore.collection("admins").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Utils.showToast(HomeActivity.this, e.getMessage());
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    // Tampilkan jumlah total document setelah perubahan terjadi
                    binding.tvCarCount.setText(queryDocumentSnapshots.size() + "");
                }
            }
        });



        firestore.collection("orders")
                .whereEqualTo("status", "pending")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        Utils.showToast(HomeActivity.this, error.getMessage());
                    } else if(value != null) {
                        List<Map<String, Object>> datas = new ArrayList<>();

                        for (DocumentSnapshot snapshot: value.getDocuments()) {
                            Map<String, Object> data = snapshot.getData();
                            data.put("id", snapshot.getId());
                            datas.add(data);
                        }


                        adapter.submitData(datas);

                        if(datas.isEmpty()) {
                            binding.empty.setVisibility(View.VISIBLE);
                            binding.rvPending.setVisibility(View.GONE);
                        } else {
                            binding.empty.setVisibility(View.GONE);
                            binding.rvPending.setVisibility(View.VISIBLE);
                        }

                        binding.tvPendingCount.setText(String.valueOf(datas.size()));
                    }
                });
        firestore.collection("orders")
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int totalRevenue = 0;
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = snapshot.getData();
                            Log.d("TAG", data.get("orderData") + "");
                            Log.d("TAG2", ((Map) data.get("orderData")).get("price") + "");
                            int price = Integer.valueOf((String) ((Map) data.get("orderData")).get("price"));
                            totalRevenue += price;
                        }
                        binding.tvRevenue.setText("Rp" + totalRevenue);
                    }
                });
        firestore.collection("orders")
                .whereEqualTo("status", "accepted")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }

                        int totalRevenue = 0;
                        for (DocumentSnapshot snapshot : value) {
                            Map<String, Object> data = snapshot.getData();
                            Log.d("TAG", data.get("orderData") + "");
                            Log.d("TAG2", ((Map) data.get("orderData")).get("price") + "");
                            int price = Integer.valueOf((String) ((Map) data.get("orderData")).get("price"));
                            totalRevenue += price;
                        }
                        binding.tvRevenue.setText("Rp" + totalRevenue);
                    }
                });
    }
}