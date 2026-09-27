package com.manideep.bloodbank.ui.overview;

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
import com.manideep.bloodbank.ui.InventoryAdapter;
import com.manideep.bloodbank.ui.requests.CreateRequestActivity;
import com.manideep.bloodbank.ui.donors.RegisterDonorActivity;

/** Counts, stock levels, and the two primary actions. */
public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView inventory = view.findViewById(R.id.inventory_list);
        inventory.setLayoutManager(new LinearLayoutManager(requireContext()));
        inventory.setAdapter(new InventoryAdapter());

        view.findViewById(R.id.btn_register).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), RegisterDonorActivity.class)));

        view.findViewById(R.id.btn_request).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CreateRequestActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        if (getView() == null) {
            return;
        }
        var db = ((BloodBankApp) requireActivity().getApplication()).db();

        ((TextView) getView().findViewById(R.id.stat_donors))
                .setText(String.valueOf(db.allDonors().size()));
        ((TextView) getView().findViewById(R.id.stat_available))
                .setText(String.valueOf(db.availableDonors().size()));
        ((TextView) getView().findViewById(R.id.stat_requests))
                .setText(String.valueOf(db.openRequests().size()));
        ((TextView) getView().findViewById(R.id.stat_units))
                .setText(String.valueOf(db.totalUnits()));

        RecyclerView list = getView().findViewById(R.id.inventory_list);
        RecyclerView.Adapter<?> adapter = list.getAdapter();
        if (adapter instanceof InventoryAdapter) {
            ((InventoryAdapter) adapter).submit(db.inventoryRows());
        }
    }
}
