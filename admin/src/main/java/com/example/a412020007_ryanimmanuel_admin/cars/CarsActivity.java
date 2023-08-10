package com.example.a412020007_ryanimmanuel_admin.cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.example.a412020007_ryanimmanuel_admin.Utils;
import com.example.a412020007_ryanimmanuel_admin.addCar.AddCarActivity;
import com.example.a412020007_ryanimmanuel_admin.databinding.ActivityCarsBinding;
import com.example.a412020007_ryanimmanuel_admin.models.Car;
import java.util.ArrayList;
import java.util.List;

public class CarsActivity extends AppCompatActivity {
    private ActivityCarsBinding binding;
    private CarsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCarsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);

        adapter = new CarsAdapter(this, new OnCarClick() {
            @Override
            public void onClick(Car car) {
                Intent i = new Intent(CarsActivity.this, AddCarActivity.class);
                Gson gson = new Gson();
                i.putExtra("car", gson.toJson(car));
                startActivity(i);
            }

            @Override
            public void onDelete(Car car) {
                deleteData(car.id);
            }
        });

        binding.rvCars.setAdapter(adapter);
        binding.rvCars.setLayoutManager(new LinearLayoutManager(this));

        binding.btnAdd.setOnClickListener(view -> Utils.move(this, AddCarActivity.class));
        getCars();
    }

    private void getCars() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("cars").addSnapshotListener((value, error) -> {
            if(error != null) {
                Toast.makeText(
                        CarsActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT
                ).show();
            } else if(value != null) {
                List<Car> cars = new ArrayList<>();
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    Car car = snapshot.toObject(Car.class);
                    car.setId(snapshot.getId());
                    cars.add(car);
                }
                adapter.setCars(cars);
                if(cars.isEmpty()) {
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.rvCars.setVisibility(View.GONE);
                } else {
                    binding.empty.setVisibility(View.GONE);
                    binding.rvCars.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void deleteData(String id) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        firestore.collection("cars").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageUrl = documentSnapshot.getString("imageUrl");
                StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);
                firestore.collection("cars").document(id).delete().addOnSuccessListener(unused -> {
                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                            Toast.makeText(CarsActivity.this, "Success delete data!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(CarsActivity.this, "Failed to delete image file", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(CarsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}