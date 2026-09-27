package com.manideep.bloodbank.ui.requests;

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
import com.manideep.bloodbank.data.BloodRequest;
import com.manideep.bloodbank.data.BloodType;
import com.manideep.bloodbank.logic.DeviceLocation;

/** Raises a request for units of a blood group. */
public class CreateRequestActivity extends AppCompatActivity {

    private MaterialAutoCompleteTextView group;
    private TextInputEditText units;
    private TextInputEditText hospital;
    private TextInputEditText phone;
    private MaterialSwitch useLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        SystemBars.pad(findViewById(android.R.id.content));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        group = findViewById(R.id.input_group);
        units = findViewById(R.id.input_units);
        hospital = findViewById(R.id.input_hospital);
        phone = findViewById(R.id.input_phone);
        useLocation = findViewById(R.id.switch_location);

        group.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, BloodType.ALL));

        findViewById(R.id.btn_save).setOnClickListener(v -> save());
    }

    private void save() {
        String wanted = BloodType.normalise(text(group));
        String hospitalName = text(hospital);

        if (!BloodType.isValid(wanted)) {
            group.setError(getString(R.string.err_group));
            return;
        }
        if (hospitalName.isEmpty()) {
            hospital.setError(getString(R.string.err_hospital));
            return;
        }

        int unitCount;
        try {
            unitCount = Integer.parseInt(text(units));
        } catch (NumberFormatException e) {
            units.setError(getString(R.string.err_units));
            return;
        }
        if (unitCount < 1) {
            units.setError(getString(R.string.err_units));
            return;
        }

        double lat = Double.NaN;
        double lon = Double.NaN;
        String city = "";
        if (useLocation.isChecked()) {
            Location location = DeviceLocation.lastKnown(this);
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            } else {
                Toast.makeText(this, R.string.err_location, Toast.LENGTH_LONG).show();
            }
        }

        BloodRequest request = new BloodRequest(
                wanted, unitCount, hospitalName, city, lat, lon, text(phone));

        ((BloodBankApp) getApplication()).db().insertRequest(request);
        Toast.makeText(this, "Request raised for " + wanted, Toast.LENGTH_SHORT).show();
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
