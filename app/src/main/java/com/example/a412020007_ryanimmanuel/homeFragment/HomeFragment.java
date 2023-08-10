package com.example.a412020007_ryanimmanuel.homeFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.example.a412020007_ryanimmanuel.DetailActivity;
import com.example.a412020007_ryanimmanuel.Utils;
import com.example.a412020007_ryanimmanuel.databinding.FragmentHomeBinding;
import com.example.a412020007_ryanimmanuel.models.Car;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new HomeAdapter(requireContext(), car -> {
            Gson gson = new Gson();
            Utils.moveWithExtra(
                    requireContext(),
                    DetailActivity.class,
                    "car",
                    gson.toJson(car));
        });

        binding.rvCars.setAdapter(adapter);
        binding.rvCars.setLayoutManager(new LinearLayoutManager(requireContext()));
        getCars();
    }

    private void getCars() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("cars").addSnapshotListener((value, error) -> {
            if(error != null) {
                Utils.showToast(requireContext(), error.getMessage());
            } else if(value != null) {
                List<Car> cars = new ArrayList<>();
                for (DocumentChange change: value.getDocumentChanges()) {
                    Car car = change.getDocument().toObject(Car.class);
                    car.setId(change.getDocument().getId());
                    cars.add(car);
                }

                // Menampilkan semua data yang ada di koleksi "cars"
                firestore.collection("cars").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Car> allCars = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Car car = document.toObject(Car.class);
                                car.setId(document.getId());
                                allCars.add(car);
                            }

                            adapter.submitList(allCars);
                            if(allCars.isEmpty()) {
                                binding.empty.setVisibility(View.VISIBLE);
                                binding.rvCars.setVisibility(View.GONE);
                            } else {
                                binding.empty.setVisibility(View.GONE);
                                binding.rvCars.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Utils.showToast(requireContext(), task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }

}