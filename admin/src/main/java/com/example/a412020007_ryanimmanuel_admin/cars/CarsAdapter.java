package com.example.a412020007_ryanimmanuel_admin.cars;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.a412020007_ryanimmanuel_admin.databinding.ListItemCarBinding;
import com.example.a412020007_ryanimmanuel_admin.models.Car;
import java.util.ArrayList;
import java.util.List;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarsHolder> {
    private final List<Car> cars = new ArrayList<>();
    private final Context context;
    public final OnCarClick onCarClick;

    CarsAdapter(Context context, OnCarClick onCarClick) {
        this.context = context;
        this.onCarClick = onCarClick;
    }

    @NonNull
    @Override
    public CarsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemCarBinding binding = ListItemCarBinding.inflate(inflater, parent, false);
        return new CarsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarsHolder holder, int position) {
        holder.bind(context, cars.get(position));
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    protected class CarsHolder extends RecyclerView.ViewHolder {
        ListItemCarBinding binding;

        public CarsHolder(@NonNull ListItemCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Context context, Car car) {
            binding.tvCar.setText(car.name);
            binding.tvYear.setText(car.year);

            String status = "";
            if(car.withDriver) {
                status += "With driver, ";
            }

            if(car.notWIthDriver) {
                status += "Without driver";
            }

            binding.tvStatus.setText(status);
            Glide.with(context).load(car.imageUrl).into(binding.ivCar);
            binding.btnEdit.setOnClickListener(view -> onCarClick.onClick(car));
            binding.btnDelete.setOnClickListener(view -> onCarClick.onDelete(car));
        }
    }

    public void setCars(List<Car> cars) {
        this.cars.clear();
        this.cars.addAll(cars);
        notifyDataSetChanged();
    }
}
