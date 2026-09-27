package com.manideep.bloodbank.ui.requests;

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
import com.manideep.bloodbank.data.BloodRequest;
import com.manideep.bloodbank.data.Donor;
import com.manideep.bloodbank.logic.MatchingEngine;
import com.manideep.bloodbank.ui.MatchesActivity;
import com.manideep.bloodbank.ui.RowAdapter;

import java.util.ArrayList;
import java.util.List;

/** Open requests. Tapping one opens the ranked donor list. */
public class RequestsFragment extends Fragment {

    private RowAdapter adapter;
    private List<BloodRequest> requests = new ArrayList<>();

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

        adapter = new RowAdapter(this::onRowAction);
        list.setAdapter(adapter);

        // The list layout is shared with the donors tab, so the action label
        // has to be set per screen rather than baked into the layout.
        com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton fab =
                view.findViewById(R.id.fab);
        fab.setText(R.string.title_create_request);
        fab.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CreateRequestActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void onRowAction(int position) {
        if (position < 0 || position >= requests.size()) {
            return;
        }
        BloodRequest request = requests.get(position);
        var db = ((BloodBankApp) requireActivity().getApplication()).db();

        if (request.status.equals(BloodRequest.STATUS_OPEN)) {
            startActivity(MatchesActivity.intentFor(requireContext(), request.id));
        } else {
            db.setRequestStatus(request.id, BloodRequest.STATUS_OPEN);
            refresh();
        }
    }

    private void refresh() {
        if (getView() == null) {
            return;
        }
        var db = ((BloodBankApp) requireActivity().getApplication()).db();
        requests = db.allRequests();
        List<Donor> allDonors = db.allDonors();

        List<RowAdapter.Row> rows = new ArrayList<>();
        for (BloodRequest r : requests) {
            int compatible = MatchingEngine.compatibleCount(r, allDonors);
            String meta = r.unitsNeeded + " unit" + (r.unitsNeeded == 1 ? "" : "s")
                    + "  " + r.hospital
                    + "  " + compatible + " compatible donor" + (compatible == 1 ? "" : "s");

            boolean open = r.status.equals(BloodRequest.STATUS_OPEN);
            rows.add(new RowAdapter.Row(
                    r.bloodGroup,
                    r.hospital,
                    meta,
                    open ? "Matches" : "Reopen",
                    true));
        }
        adapter.submit(rows);

        TextView empty = getView().findViewById(R.id.empty);
        empty.setText(R.string.no_requests);
        empty.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
