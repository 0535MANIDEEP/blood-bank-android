package com.manideep.bloodbank.ui.donors;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manideep.bloodbank.BloodBankApp;
import com.manideep.bloodbank.R;
import com.manideep.bloodbank.data.Donor;
import com.manideep.bloodbank.ui.RowAdapter;

import java.util.ArrayList;
import java.util.List;

/** Registered donors, with a toggle to mark someone available or unavailable. */
public class DonorsFragment extends Fragment {

    private RowAdapter adapter;
    private List<Donor> donors = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView list = view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new RowAdapter(this::onToggle);
        list.setAdapter(adapter);

        com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton fab =
                view.findViewById(R.id.fab);
        fab.setText(R.string.title_register_donor);
        fab.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), RegisterDonorActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void onToggle(int position) {
        if (position < 0 || position >= donors.size()) {
            return;
        }
        Donor donor = donors.get(position);
        var db = ((BloodBankApp) requireActivity().getApplication()).db();
        db.setDonorAvailable(donor.id, !donor.available);
        refresh();
    }

    private void refresh() {
        if (getView() == null) {
            return;
        }
        var db = ((BloodBankApp) requireActivity().getApplication()).db();
        donors = db.allDonors();

        List<RowAdapter.Row> rows = new ArrayList<>();
        for (Donor d : donors) {
            String meta = d.phone
                    + (d.city == null || d.city.isEmpty() ? "" : "  " + d.city)
                    + (d.available ? "  Available" : "  Unavailable");
            rows.add(new RowAdapter.Row(
                    d.bloodGroup,
                    d.name,
                    meta,
                    d.available ? "Pause" : "Activate",
                    true));
        }
        adapter.submit(rows);

        TextView empty = getView().findViewById(R.id.empty);
        empty.setText(R.string.no_donors);
        empty.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
