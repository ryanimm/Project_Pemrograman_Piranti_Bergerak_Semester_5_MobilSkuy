package com.example.a412020007_ryanimmanuel.orderForm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.example.a412020007_ryanimmanuel.Utils;
import com.example.a412020007_ryanimmanuel.databinding.ActivityOrderFormBinding;
import com.example.a412020007_ryanimmanuel.home.HomeActivity;
import java.util.Date;
import java.util.HashMap;


public class OrderFormActivity extends AppCompatActivity {
    private ActivityOrderFormBinding binding;
    private HashMap<String, Object> detailData;
    private Uri ktpUri;
    private Uri receiptUri;

    ActivityResultLauncher<Intent> ktpIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData().getData() != null) {
                    ktpUri = result.getData().getData();
                    Bitmap imageBitmap = handleImage(ktpUri);
                    binding.ibKtp.setImageBitmap(imageBitmap);
                }
            });

    ActivityResultLauncher<Intent> receiptIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData().getData() != null) {
                    receiptUri = result.getData().getData();
                    Bitmap imageBitmap = handleImage(receiptUri);
                    binding.ibReceipt.setImageBitmap(imageBitmap);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Please fill this form");

        detailData = (HashMap<String, Object>) getIntent().getSerializableExtra("data");
        if(detailData == null) {
            finish();
            Utils.showToast(this, "Something happened!");
        }

        binding.ibKtp.setOnClickListener(view -> ImagePicker.with(this)
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(480, 480)    //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent(intent -> {
                    ktpIntent.launch(intent);
                    return null;
                }));
        binding.ibReceipt.setOnClickListener(view -> ImagePicker.with(this)
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(480, 480)    //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent(intent -> {
                    receiptIntent.launch(intent);
                    return null;
                }));
        binding.btnRegister.setOnClickListener(view -> order());
    }

    private void order() {
//        binding.btnRegister.setEnabled(false);

        // Tambahkan proses loading
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Currently processing order...");
        progressDialog.show();


        String name = binding.etName.getText().toString();
        String address = binding.etAddress.getText().toString();
        String email = binding.etEmail.getText().toString();
        String phone = binding.etPhone.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentId = auth.getCurrentUser().getUid();

        HashMap<String, Object> requestBody = new HashMap<String, Object>(){{
            put("name", name);
            put("address", address);
            put("email", email);
            put("phone", phone);
        }};

        detailData.put("userId", currentId);
        detailData.put("tenantData", requestBody);
        detailData.put("time", new Date().getTime());
        detailData.put("status", "pending");

        if(!email.isEmpty() &&
                !address.isEmpty() &&
                !name.isEmpty() &&
                !phone.isEmpty() &&
                ktpUri != null &&
                receiptUri != null) {

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            firebaseStorage.getReference().child(String.valueOf(new Date().getTime())).putFile(ktpUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri ->
                                detailData.put("ktpUrl", uri));

                        firebaseStorage.getReference().child(String.valueOf(new Date().getTime())).putFile(receiptUri)
                                .addOnSuccessListener(taskSnapshot1 ->
                                        taskSnapshot1.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                            detailData.put("receiptUtl", uri);
                                            storeData();
                                            // Tutup proses loading
                                            progressDialog.dismiss();
                                        }))
                                .addOnFailureListener(e -> {
                                    Utils.showToast(OrderFormActivity.this, e.getMessage());

                                    // Tutup proses loading
                                    progressDialog.dismiss();
                                });
                    }).addOnFailureListener(e -> {
                        Utils.showToast(OrderFormActivity.this, e.getMessage());

                        // Tutup proses loading
                        progressDialog.dismiss();
                    });
        }
        else{
            Utils.showToast(OrderFormActivity.this, "Please Fill All The Form!");
            progressDialog.dismiss();
        }
    }

    private void storeData() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("orders").add(detailData).addOnSuccessListener(documentReference -> {
            binding.btnRegister.setEnabled(true);
            Utils.moveClearBackstack(OrderFormActivity.this, HomeActivity.class);
            Utils.showToast(OrderFormActivity.this, "Car are ordered!");
        }).addOnFailureListener(e -> {
            Utils.showToast(OrderFormActivity.this, e.getMessage());
        });
    }

    private Bitmap handleImage(Uri uri) {
        String path = uri.getPath();
        Matrix matrix = new Matrix();
        matrix.postRotate(Utils.getImageOrientation(path));

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);

            return rotatedBitmap;
        } catch (Exception e) {
            return null;
        }
    }
}