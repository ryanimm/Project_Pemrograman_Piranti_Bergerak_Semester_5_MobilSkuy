package com.example.a412020007_ryanimmanuel_admin.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import com.example.a412020007_ryanimmanuel_admin.R;
import com.example.a412020007_ryanimmanuel_admin.databinding.ListItemHistoryBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {
    private Context context;
    private List<Map<String, Object>> datas = new ArrayList<>();
    private OnOrderClick onOrderClick;

    public HistoryAdapter(Context context, OnOrderClick onOrderClick) {
        this.onOrderClick = onOrderClick;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemHistoryBinding binding = ListItemHistoryBinding.inflate(inflater, parent, false);
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
        private final ListItemHistoryBinding binding;

        public HistoryHolder(@NonNull ListItemHistoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(Map<String, Object> data) {
            Map<String, Object> orderData = (Map<String, Object>) data.get("orderData");
            binding.tvName.setText((String) orderData.get("carName"));
            binding.tvDate.setText(orderData.get("startDate") + " - " + orderData.get("endDate"));

            String status = (String) data.get("status");
            binding.tvStatus.setText((String) data.get("status"));

            if(Objects.equals(status, "rejected")) {
                binding.tvStatus.setBackground(AppCompatResources.getDrawable(context, R.drawable.rounded_red));
                binding.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            }

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
