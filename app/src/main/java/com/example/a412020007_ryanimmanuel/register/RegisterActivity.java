package com.example.a412020007_ryanimmanuel.register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.a412020007_ryanimmanuel.Utils;
import com.example.a412020007_ryanimmanuel.databinding.ActivityRegisterBinding;
import com.example.a412020007_ryanimmanuel.models.User;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etBirthdate.setOnClickListener(view -> showDateDialog());
        binding.btnRegister.setOnClickListener(view -> register());
    }

    private void showDateDialog(){
        Calendar newCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E, dd MMMM yy", Locale.getDefault());
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            binding.etBirthdate.setText(dateFormatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void register() {
        binding.btnRegister.setEnabled(false);
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();

        if(!email.isEmpty() &&
                !password.isEmpty() &&
                !binding.etEmail.getText().toString().isEmpty() &&
                !binding.etPhone.getText().toString().isEmpty() &&
                !binding.etBirthdate.getText().toString().isEmpty()) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult ->
                            storeData(authResult.getUser().getUid()))
                    .addOnFailureListener(e -> {
                        binding.btnRegister.setEnabled(true);
                        Utils.showToast(RegisterActivity.this, e.getMessage());
                    });
        }
    }

    private void storeData(String uid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        User user = new User();
        user.setName(binding.etName.getText().toString());
        user.setEmail(binding.etEmail.getText().toString());
        user.setPhone(binding.etPhone.getText().toString());
        user.setBirthDate(binding.etBirthdate.getText().toString());

        firestore.collection("users").document(uid).set(user).addOnSuccessListener(unused -> {
            finish();
            Utils.showToast(RegisterActivity.this, "Success, Please login!");
        }).addOnFailureListener(e -> {
            binding.btnRegister.setEnabled(true);
            Utils.showToast(RegisterActivity.this, e.getMessage());
        });
    }
}