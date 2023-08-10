package com.example.a412020007_ryanimmanuel_admin.addCar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityAddCarBinding;
import com.example.a412020007_ryanimmanuel_admin.models.Car;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AddCarActivity extends AppCompatActivity {
    private ActivityAddCarBinding binding;
    private Car car;
    private Uri imageUri;
    private String transmission = "Automatic";
    private Boolean isUpdate = false;

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    Bitmap imageBitmap = handleImage(imageUri);
                    binding.ibCar.setImageBitmap(imageBitmap);
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);

        Gson gson = new Gson();

        if(getIntent().getStringExtra("car") != null) {
            car = gson.fromJson(getIntent().getStringExtra("car"), Car.class);
            initUpdateUI();
            isUpdate = true;
        }

        if (!binding.ckDriver.isChecked()) {
            binding.etPriceDaily.setEnabled(false);
            binding.etPriceDaily.setText("0");
        }

        binding.ckDriver.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked) {
                binding.etPriceDaily.setEnabled(false);
                binding.etPriceDaily.setText("0");
            } else {
                binding.etPriceDaily.setEnabled(true);
            }
        });

        binding.ibCar.setOnClickListener(view -> ImagePicker.with(this)
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(480, 480)    //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent(intent -> {
                    startActivityIntent.launch(intent);
                    return null;
                }));

        binding.btnAdd.setOnClickListener(view -> addData());
    }

    private void initUpdateUI() {
        Glide.with(this).load(car.imageUrl).into(binding.ibCar);
        binding.etName.setText(car.name);
        binding.etYear.setText(car.year);
        binding.etSeat.setText(car.totalSeat);

        if(car.withDriver) {
            binding.ckDriver.setChecked(true);
        }

        if(car.notWIthDriver) {
            binding.ckDriver.setChecked(true);
        }

        int index = 0;
        if(Objects.equals(car.transmission, "Manual")) index = 1;
        binding.spTransmission.setSelection(index);
        binding.etPriceDaily.setText(car.dailyPrice);
        binding.etPriceWithoutDriver.setText(car.dailyPriceWithoutDriver);
        binding.btnAdd.setText("Update");
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

    private ProgressDialog progressDialog;
    private void addData() {
        binding.btnAdd.setEnabled(false);

        progressDialog = new ProgressDialog(AddCarActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        if(imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReference().child(String.valueOf(new Date().getTime())).putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri ->
                            storeToDb(uri.toString()))).addOnFailureListener(e -> {
                binding.btnAdd.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(AddCarActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            });
        } else {
            storeToDb(null);
        }
    }

    private void storeToDb(String url) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if(isUpdate) {
            HashMap<String, Object> data = new HashMap<String, Object>() {{
                put("name", binding.etName.getText().toString());
                put("year", binding.etYear.getText().toString());
                put("totalSeat", binding.etSeat.getText().toString());
                put("transmission", binding.spTransmission.getSelectedItem().toString());
                put("withDriver", binding.ckDriver.isChecked());
                put("notWIthDriver", binding.ckNoDriver.isChecked());
                put("dailyPrice", binding.etPriceDaily.getText().toString());
                put("dailyPriceWithoutDriver", binding.etPriceWithoutDriver.getText().toString());
            }};

            if(url != null) {
                data.put("imageUrl", url);
            }

            firestore.collection("cars")
                    .document(car.getId())
                    .update(data)
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(AddCarActivity.this, "Success add data!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        binding.btnAdd.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(AddCarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Car car = new Car(
                    url,
                    binding.etName.getText().toString(),
                    binding.etYear.getText().toString(),
                    binding.etSeat.getText().toString(),
                    binding.spTransmission.getSelectedItem().toString(),
                    binding.ckDriver.isChecked(),
                    binding.ckNoDriver.isChecked(),
                    binding.etPriceDaily.getText().toString(),
                    binding.etPriceWithoutDriver.getText().toString()
            );


            firestore.collection("cars").add(car).addOnSuccessListener(documentReference -> {
                progressDialog.dismiss();
                finish();
                Toast.makeText(AddCarActivity.this, "Success add data!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                binding.btnAdd.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(AddCarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}