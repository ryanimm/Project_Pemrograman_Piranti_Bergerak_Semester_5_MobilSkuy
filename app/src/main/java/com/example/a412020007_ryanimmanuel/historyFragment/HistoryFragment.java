package com.example.a412020007_ryanimmanuel.historyFragment;

import static android.view.View.GONE;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.a412020007_ryanimmanuel.Utils;
import com.example.a412020007_ryanimmanuel.databinding.FragmentHistoryBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private HistoryAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new HistoryAdapter(requireContext());
        binding.rvCars.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCars.setAdapter(adapter);

        getHistories();
    }

    private void getHistories() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentId = auth.getCurrentUser().getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("orders")
                .whereEqualTo("userId", currentId)
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if(e != null) {
                        Utils.showToast(requireContext(), e.getMessage());
                    } else if(queryDocumentSnapshots != null) {
                        List<Map<String, Object>> datas = new ArrayList<>();

                        for (DocumentChange change: queryDocumentSnapshots.getDocumentChanges()) {
                            datas.add(change.getDocument().getData());
                        }

                        adapter.submitData(datas);
                        if(datas.isEmpty()) {
                            binding.empty.setVisibility(View.VISIBLE);
                            binding.rvCars.setVisibility(GONE);
                        } else {
                            binding.empty.setVisibility(GONE);
                            binding.rvCars.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}