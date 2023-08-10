package com.example.a412020007_ryanimmanuel.profileFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.a412020007_ryanimmanuel.Utils;
import com.example.a412020007_ryanimmanuel.databinding.FragmentProfileBinding;
import com.example.a412020007_ryanimmanuel.login.LoginActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.etBirthdate.setOnClickListener(view1 -> showDateDialog());
        binding.btnRegister.setOnClickListener(view1 -> update());
        binding.btnLogout.setOnClickListener(view1 -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();

            Utils.moveClearBackstack(requireContext(), LoginActivity.class);
        });

        getProfile();
    }

    private void showDateDialog(){
        Calendar newCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E, dd MMMM yy", Locale.getDefault());
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (DatePickerDialog.OnDateSetListener) (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            binding.etBirthdate.setText(dateFormatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void getProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentId = auth.getCurrentUser().getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(currentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> user = documentSnapshot.getData();
                    if(user != null) {
                        initUi(user);
                    }
                }).addOnFailureListener(e -> Utils.showToast(requireContext(), e.getMessage()));
    }

    private void initUi(Map<String, Object> user) {
        binding.etName.setText((CharSequence) user.get("name"));
        binding.etBirthdate.setText((CharSequence) user.get("birthDate"));
        binding.etPhone.setText((CharSequence) user.get("phone"));
        binding.etEmail.setText((CharSequence) user.get("email"));
        binding.btnRegister.setEnabled(true);
    }

    private void update() {
        binding.btnRegister.setEnabled(false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentId = auth.getCurrentUser().getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(currentId).update(new HashMap<String, Object>() {{
            put("name", binding.etName.getText().toString());
            put("birthDate", binding.etBirthdate.getText().toString());
            put("phone", binding.etPhone.getText().toString());
            put("email", binding.etEmail.getText().toString());
        }}).addOnSuccessListener(unused -> {
            auth.getCurrentUser().updateEmail(binding.etEmail.getText().toString());

            binding.btnRegister.setEnabled(true);
            Utils.showToast(requireContext(), "Profile updated!");
        }).addOnFailureListener(e -> {
            binding.btnRegister.setEnabled(true);
            Utils.showToast(requireContext(), e.getMessage());
        });
    }
}