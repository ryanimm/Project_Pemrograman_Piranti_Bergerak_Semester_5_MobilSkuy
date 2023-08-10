package com.example.a412020007_ryanimmanuel_admin.orderDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityOrderDetailBinding;

import java.util.HashMap;

public class OrderDetailActivity extends AppCompatActivity {
    ActivityOrderDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HashMap<String, Object> order = (HashMap<String, Object>) getIntent().getSerializableExtra("data");
        if(order != null) {
            initUi(order);
        } else {
            finish();
            Utils.showToast(this, "Something happened!");
        }
    }


    private static final int REQUEST_CALL_PHONE = 1;
    public void initUi(HashMap<String, Object> data) {
        HashMap<String, Object> tenantData = (HashMap<String, Object>) data.get("tenantData");
        HashMap<String, Object> orderData = (HashMap<String, Object>) data.get("orderData");

        binding.etName.setText((CharSequence) tenantData.get("name"));
        binding.etAddress.setText((CharSequence) tenantData.get("address"));
        binding.etEmail.setText((CharSequence) tenantData.get("email"));
        binding.etPhone.setText((CharSequence) tenantData.get("phone"));
        binding.etPeriod.setText(orderData.get("startDate") + " - " + orderData.get("endDate") + " (" + orderData.get("totalDays") + " hari)");
        binding.etPrice.setText((CharSequence) orderData.get("price"));

        // Jika status order "accepted" atau "rejected", maka sembunyikan kedua button
        if (data.get("status").equals("accepted") || data.get("status").equals("rejected")) {
            binding.btnAccept.setVisibility(View.GONE);
            binding.btnReject.setVisibility(View.GONE);
        } else {
            // Jika status order belum "accepted" atau "rejected", maka tampilkan kedua button
            binding.btnAccept.setOnClickListener(view -> updateStatus((String) data.get("id"), "accepted"));
            binding.btnReject.setOnClickListener(view -> updateStatus((String) data.get("id"), "rejected"));
        }

        Glide.with(this).load(data.get("ktpUrl")).into(binding.ivKtp);
        Glide.with(this).load(data.get("receiptUtl")).into(binding.ivReceipt);

        binding.etPhone.setOnClickListener(view -> {
            // Cek apakah izin telah diberikan
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // Minta izin jika belum diberikan
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
            } else {
                // Izin telah diberikan, jalankan kode untuk memanggil telepon
                String phoneNumber = binding.etPhone.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin telah diberikan, jalankan kode untuk memanggil telepon
                String phoneNumber = binding.etPhone.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            } else {
                // Izin ditolak, tampilkan pesan
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void updateStatus(String id, String status) {
        binding.btnReject.setEnabled(false);
        binding.btnReject.setEnabled(false);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("orders").document(id).update(new HashMap<String, Object>() {{
            put("status", status);
        }}).addOnSuccessListener(unused -> {
            finish();
            Utils.showToast(OrderDetailActivity.this, "Order " + status + "ed!");
        }).addOnFailureListener(e -> {
            binding.btnReject.setEnabled(true);
            binding.btnReject.setEnabled(true);
            Utils.showToast(OrderDetailActivity.this, e.getMessage());
        });
    }
}