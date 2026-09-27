package com.manideep.bloodbank.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manideep.bloodbank.R;

import java.util.ArrayList;
import java.util.List;

/** Blood bank stock rows. */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.Holder> {

    private final List<String[]> rows = new ArrayList<>();

    public void submit(List<String[]> data) {
        rows.clear();
        rows.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String[] row = rows.get(position);
        holder.group.setText(row[0]);
        holder.units.setText(row[1] + " units");
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView group;
        final TextView units;

        Holder(@NonNull View itemView) {
            super(itemView);
            group = itemView.findViewById(R.id.group);
            units = itemView.findViewById(R.id.units);
        }
    }
}
