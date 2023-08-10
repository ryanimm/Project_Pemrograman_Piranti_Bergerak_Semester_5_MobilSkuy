package com.example.a412020007_ryanimmanuel_admin.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.a412020007_ryanimmanuel_admin.databinding.ListItemOrderBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HistoryHolder> {
    private final List<Map<String, Object>> datas = new ArrayList<>();
    private OnOrderClick onOrderClick;

    public HomeAdapter(Context context, OnOrderClick onOrderClick) {
        this.onOrderClick = onOrderClick;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemOrderBinding binding = ListItemOrderBinding.inflate(inflater, parent, false);
        return new HistoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        holder.bind(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    protected class HistoryHolder extends RecyclerView.ViewHolder {
        private final ListItemOrderBinding binding;

        public HistoryHolder(@NonNull ListItemOrderBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(Map<String, Object> data) {
            Map<String, Object> orderData = (Map<String, Object>) data.get("orderData");
            Map<String, Object> tenantData = (Map<String, Object>) data.get("tenantData");
            binding.tvName.setText((String) tenantData.get("name"));
            binding.tvCar.setText((String) orderData.get("carName"));
            binding.tvDate.setText(orderData.get("startDate") + " - " + orderData.get("endDate"));

            String status = (String) data.get("status");
            binding.tvStatus.setText(status);

            itemView.setOnClickListener(view -> onOrderClick.onClick((HashMap<String, Object>) data));
        }
    }

    public void submitData(List<Map<String, Object>> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }
}

interface OnOrderClick {
    void onClick(HashMap<String, Object> data);
}
