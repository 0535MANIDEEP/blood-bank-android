package com.manideep.bloodbank.ui.donors;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.manideep.bloodbank.BloodBankApp;
import com.manideep.bloodbank.R;
import com.manideep.bloodbank.ui.SystemBars;
import com.manideep.bloodbank.data.BloodType;
import com.manideep.bloodbank.data.Donor;
import com.manideep.bloodbank.logic.DeviceLocation;

/** Adds a donor to the local register. */
public class RegisterDonorActivity extends AppCompatActivity {

    private TextInputEditText name;
    private TextInputEditText phone;
    private MaterialAutoCompleteTextView group;
    private TextInputEditText city;
    private MaterialSwitch useLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_donor);
        SystemBars.pad(findViewById(android.R.id.content));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        name = findViewById(R.id.input_name);
        phone = findViewById(R.id.input_phone);
        group = findViewById(R.id.input_group);
        city = findViewById(R.id.input_city);
        useLocation = findViewById(R.id.switch_location);

        group.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, BloodType.ALL));

        findViewById(R.id.btn_save).setOnClickListener(v -> save());
    }

    private void save() {
        String donorName = text(name);
        String donorPhone = text(phone);
        String donorGroup = BloodType.normalise(text(group));
        String donorCity = text(city);

        if (donorName.isEmpty()) {
            name.setError(getString(R.string.err_name));
            return;
        }
        if (donorPhone.isEmpty()) {
            phone.setError(getString(R.string.err_phone));
            return;
        }
        if (!BloodType.isValid(donorGroup)) {
            group.setError(getString(R.string.err_group));
            return;
        }

        double lat = Double.NaN;
        double lon = Double.NaN;
        if (useLocation.isChecked()) {
            Location location = DeviceLocation.lastKnown(this);
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            } else {
                Toast.makeText(this, R.string.err_location, Toast.LENGTH_LONG).show();
            }
        }

        Donor donor = new Donor(donorName, donorPhone, donorGroup, donorCity, lat, lon, true);
        ((BloodBankApp) getApplication()).db().insertDonor(donor);

        Toast.makeText(this, donorName + " registered", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    private String text(MaterialAutoCompleteTextView field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
