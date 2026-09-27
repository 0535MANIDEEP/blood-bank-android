package com.manideep.bloodbank.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manideep.bloodbank.R;
import com.manideep.bloodbank.ui.donors.DonorsFragment;
import com.manideep.bloodbank.ui.overview.DashboardFragment;
import com.manideep.bloodbank.ui.requests.RequestsFragment;

/** Three tabs: overview, donors, requests. */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_TAB = "selected_tab";

    private int selectedTab = R.id.nav_overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemBars.pad(findViewById(R.id.root));

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(KEY_TAB, R.id.nav_overview);
        }

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(this::onTabSelected);

        if (savedInstanceState == null) {
            nav.setSelectedItemId(selectedTab);
        } else {
            showTab(selectedTab);
        }
    }

    private boolean onTabSelected(@NonNull MenuItem item) {
        showTab(item.getItemId());
        return true;
    }

    private void showTab(int itemId) {
        selectedTab = itemId;
        Fragment fragment;
        if (itemId == R.id.nav_donors) {
            fragment = new DonorsFragment();
        } else if (itemId == R.id.nav_requests) {
            fragment = new RequestsFragment();
        } else {
            fragment = new DashboardFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TAB, selectedTab);
    }
}
