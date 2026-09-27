package com.manideep.bloodbank.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manideep.bloodbank.BloodBankApp;
import com.manideep.bloodbank.R;
import com.manideep.bloodbank.data.BloodRequest;
import com.manideep.bloodbank.logic.Geo;
import com.manideep.bloodbank.logic.MatchingEngine;

import java.util.ArrayList;
import java.util.List;

/** Compatible donors for one request, nearest first. */
public class MatchesActivity extends AppCompatActivity {

    private static final String EXTRA_REQUEST_ID = "request_id";

    public static Intent intentFor(Context context, long requestId) {
        Intent intent = new Intent(context, MatchesActivity.class);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        SystemBars.pad(findViewById(android.R.id.content));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        long requestId = getIntent().getLongExtra(EXTRA_REQUEST_ID, -1);
        var db = ((BloodBankApp) getApplication()).db();
        BloodRequest request = db.requestById(requestId);

        if (request == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.summary_group))
                .setText("NEED " + request.bloodGroup + "  " + request.unitsNeeded + " UNIT"
                        + (request.unitsNeeded == 1 ? "" : "S"));
        ((TextView) findViewById(R.id.summary_hospital)).setText(request.hospital);

        List<MatchingEngine.Match> matches =
                MatchingEngine.matchesFor(request, db.allDonors());

        StringBuilder meta = new StringBuilder();
        meta.append(matches.size())
                .append(matches.size() == 1 ? " compatible donor available" : " compatible donors available");
        if (request.city != null && !request.city.isEmpty()) {
            meta.append("  ").append(request.city);
        }
        ((TextView) findViewById(R.id.summary_meta)).setText(meta.toString());

        List<RowAdapter.Row> rows = new ArrayList<>();
        for (MatchingEngine.Match match : matches) {
            String line = match.donor.phone
                    + "  " + Geo.formatKm(match.distanceKm);
            if (match.donor.city != null && !match.donor.city.isEmpty()) {
                line += "  " + match.donor.city;
            }
            rows.add(new RowAdapter.Row(
                    match.donor.bloodGroup,
                    match.donor.name,
                    line,
                    "Call",
                    true));
        }

        RowAdapter adapter = new RowAdapter(position -> {
            if (position >= 0 && position < matches.size()) {
                dial(matches.get(position).donor.phone);
            }
        });

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        adapter.submit(rows);

        TextView empty = findViewById(R.id.empty);
        empty.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void dial(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.trim())));
        } catch (Exception ignored) {
            // No dialer on the device. Nothing useful to do here.
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
