package com.example.a412020007_ryanimmanuel.home;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.example.a412020007_ryanimmanuel.R;
import com.example.a412020007_ryanimmanuel.databinding.ActivityHomeBinding;
import com.example.a412020007_ryanimmanuel.devFragment.DevFragment;
import com.example.a412020007_ryanimmanuel.historyFragment.HistoryFragment;
import com.example.a412020007_ryanimmanuel.homeFragment.HomeFragment;
import com.example.a412020007_ryanimmanuel.profileFragment.ProfileFragment;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        moveFragment(new HomeFragment(), "Home");
        binding.botnav.setOnItemReselectedListener(item -> {});
        binding.botnav.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_home) {
                moveFragment(new HomeFragment(), "Home");
            } else if(item.getItemId() == R.id.menu_history) {
                moveFragment(new HistoryFragment(), "History");
            } else if(item.getItemId() == R.id.menu_profile) {
                moveFragment(new ProfileFragment(), "Profile");
            } else {
                moveFragment(new DevFragment(), "Developer Information");
            }

            return true;
        });
    }

    private void moveFragment(Fragment fragment, String title) {
        getSupportActionBar().setTitle(title);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}