package com.example.a412020007_ryanimmanuel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.example.a412020007_ryanimmanuel.databinding.ActivityDetailBinding;
import com.example.a412020007_ryanimmanuel.models.Car;
import com.example.a412020007_ryanimmanuel.orderForm.OrderFormActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private Car car;
    private long totalDays = 1;
    private HashMap<String, Object> detailData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Gson gson = new Gson();
        String carStr = getIntent().getStringExtra("car");
        car = gson.fromJson(carStr, Car.class);

        if(car != null) {
            initUI(car);
        } else {
            finish();
            Utils.showToast(this, "Cannot fetch data!");
        }


        binding.etFrom.setOnClickListener(view -> showDateDialog(true));
        binding.etTo.setOnClickListener(view -> showDateDialog(false));
        binding.rbGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            int totalInt;
            if(i == R.id.rb_driver) {
                totalInt = Integer.valueOf(car.dailyPrice);
            } else {
                totalInt = Integer.valueOf(car.dailyPriceWithoutDriver);
            }
            setTotal(totalInt);
        });
        binding.btnNext.setOnClickListener(view -> {
            HashMap<String, Object> orderData = new HashMap<>();
            orderData.put("carId", car.id);
            orderData.put("carName", car.name);
            orderData.put("startDate", binding.etFrom.getText().toString());
            orderData.put("endDate", binding.etTo.getText().toString());
            orderData.put("totalDays", totalDays);
            orderData.put("price", binding.tvTotal.getText().toString().replace("Rp", ""));

            boolean withDriver = binding.rbDriver.isChecked();
            orderData.put("withDriver", withDriver);

            detailData.put("orderData", orderData);
            Utils.moveWithExtra(this, OrderFormActivity.class, "data", detailData);
        });
    }

    private void initUI(Car car) {
        binding.tvName.setText(car.name);
        binding.tvYear.setText("(" + car.year + " )");
        if (car.dailyPrice.equals("0")) {
            binding.tvPrice.setText("Rp" + car.dailyPriceWithoutDriver);
            binding.rbDriver.setEnabled(false);
        } else {
            binding.tvPrice.setText("Rp" + car.dailyPrice);
        }
        binding.tvSeat.setText(car.totalSeat);
        binding.tvTransmission.setText(car.transmission);
        binding.tvTotal.setText("Rp" + car.dailyPrice);

        binding.etFrom.setText(Utils.getCurrentFormattedDate());
        binding.etTo.setText(Utils.getCurrentFormattedDate());
        Glide.with(this).load(car.imageUrl).into(binding.imageView4);
    }

    private void showDateDialog(Boolean isFrom){
        Calendar newCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            if(isFrom) {
                binding.etFrom.setText(dateFormatter.format(newDate.getTime()));
            } else {
                binding.etTo.setText(dateFormatter.format(newDate.getTime()));
            }

            if(binding.rbDriver.isChecked()) {
                setTotal(Integer.valueOf(car.dailyPrice));
            } else {
                setTotal(Integer.valueOf(car.dailyPriceWithoutDriver));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void setTotal(int price) {
        try {
            totalDays = Utils.reduceDateString(binding.etFrom.getText().toString(), binding.etTo.getText().toString()) + 1;
            String total = String.valueOf(price * totalDays);
            binding.tvTotal.setText("Rp" + total);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}