package com.example.a412020007_ryanimmanuel.homeFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.a412020007_ryanimmanuel.databinding.ListItemCarBinding;
import com.example.a412020007_ryanimmanuel.models.Car;
import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    private final List<Car> cars = new ArrayList<>();
    protected OnCarClick onCarClick;
    protected Context context;

    public HomeAdapter(Context context, OnCarClick onCarClick) {
        this.context = context;
        this.onCarClick = onCarClick;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemCarBinding binding = ListItemCarBinding.inflate(inflater, parent, false);
        return new HomeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        holder.bind(cars.get(position));
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public class HomeHolder extends RecyclerView.ViewHolder {
        private final ListItemCarBinding binding;

        public HomeHolder(@NonNull ListItemCarBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(Car car) {
            binding.tvName.setText(car.name);
            if (car.dailyPrice.equals("0")) {
                binding.tvPrice.setText("Rp" + car.dailyPriceWithoutDriver + " / day");
            } else {
                binding.tvPrice.setText("Rp" + car.dailyPrice + " / day");
            }
            binding.tvTransmission.setText("Transmission: " + car.transmission);
            binding.tvStatus.setText(car.totalSeat + " seats");

            Glide.with(context).load(car.imageUrl).into(binding.imageView3);
            itemView.setOnClickListener(view -> onCarClick.onClick(car));
        }
    }

    public void submitList(List<Car> cars) {
        this.cars.clear();
        this.cars.addAll(cars);
        notifyDataSetChanged();
    }
}

interface OnCarClick {
    void onClick(Car car);
}