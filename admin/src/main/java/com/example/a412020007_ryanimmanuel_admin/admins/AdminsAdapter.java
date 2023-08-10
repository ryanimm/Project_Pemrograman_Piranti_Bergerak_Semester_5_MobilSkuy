package com.example.a412020007_ryanimmanuel_admin.admins;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a412020007_ryanimmanuel_admin.databinding.ListItemAdminBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminsAdapter extends RecyclerView.Adapter<AdminsAdapter.AdminHolder> {
    private final OnAdminClick onAdminClick;
    private final List<Map<String, Object>> admins = new ArrayList<Map<String, Object>>();

    public AdminsAdapter(OnAdminClick onAdminClick) {
        this.onAdminClick = onAdminClick;
    }

    @NonNull
    @Override
    public AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemAdminBinding binding = ListItemAdminBinding.inflate(inflater, parent, false);
        return new AdminHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminHolder holder, int position) {
        holder.bind(admins.get(position));
    }

    @Override
    public int getItemCount() {
        return admins.size();
    }

    protected class AdminHolder extends RecyclerView.ViewHolder {
        ListItemAdminBinding binding;

        public AdminHolder(@NonNull ListItemAdminBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(Map<String, Object> admin) {
            binding.tvEmail.setText(admin.get("email").toString());
            binding.ibEdit.setOnClickListener(view -> onAdminClick.onEdit(admin));
            binding.ibDelete.setOnClickListener(view -> onAdminClick.onDelete(admin));
        }
    }

    public void submitList(List<Map<String, Object>> admins) {
        this.admins.clear();
        this.admins.addAll(admins);
        notifyDataSetChanged();
    }
}

interface OnAdminClick {
    void onEdit(Map<String, Object> admin);
    void onDelete(Map<String, Object> admin);
}
