package com.example.a412020007_ryanimmanuel_admin.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityHistoryBinding;
import com.example.a412020007_ryanimmanuel_admin.orderDetail.OrderDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    private ActivityHistoryBinding binding;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new HistoryAdapter(this, data ->
                Utils.moveWithExtra(this, OrderDetailActivity.class, "data", data));
        binding.rvHistory.setAdapter(adapter);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));

        getAllDatas();
    }

    private void getAllDatas() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("orders").whereEqualTo("status", "accepted")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> datas = new ArrayList<>();

                    for (DocumentChange change: queryDocumentSnapshots.getDocumentChanges()) {
                        datas.add(change.getDocument().getData());
                    }

                    adapter.submitData(datas);

                    if(datas.isEmpty()) {
                        binding.empty.setVisibility(View.VISIBLE);
                        binding.rvHistory.setVisibility(View.GONE);
                    } else {
                        binding.empty.setVisibility(View.GONE);
                        binding.rvHistory.setVisibility(View.VISIBLE);
                    }

                }).addOnFailureListener(e ->
                        Utils.showToast(HistoryActivity.this, e.getMessage()));
    }

}