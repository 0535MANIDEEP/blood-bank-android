package com.manideep.bloodbank.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manideep.bloodbank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * One adapter for the donor, request and match lists. They share a row shape:
 * a group badge, a title, a meta line and one action.
 */
public class RowAdapter extends RecyclerView.Adapter<RowAdapter.Holder> {

    public interface OnAction {
        void onAction(int position);
    }

    public static class Row {
        public final String group;
        public final String title;
        public final String meta;
        public final String actionLabel;
        public final boolean actionEnabled;

        public Row(String group, String title, String meta, String actionLabel, boolean actionEnabled) {
            this.group = group;
            this.title = title;
            this.meta = meta;
            this.actionLabel = actionLabel;
            this.actionEnabled = actionEnabled;
        }
    }

    private final List<Row> rows = new ArrayList<>();
    private final OnAction callback;

    public RowAdapter(OnAction callback) {
        this.callback = callback;
    }

    public void submit(List<Row> newRows) {
        rows.clear();
        rows.addAll(newRows);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Row row = rows.get(position);
        holder.group.setText(row.group);
        holder.title.setText(row.title);
        holder.meta.setText(row.meta);

        if (row.actionLabel == null) {
            holder.action.setVisibility(View.GONE);
        } else {
            holder.action.setVisibility(View.VISIBLE);
            holder.action.setText(row.actionLabel);
            holder.action.setEnabled(row.actionEnabled);
            final int index = holder.getBindingAdapterPosition();
            holder.action.setOnClickListener(v -> {
                if (index != RecyclerView.NO_POSITION) {
                    callback.onAction(index);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView group;
        final TextView title;
        final TextView meta;
        final com.google.android.material.button.MaterialButton action;

        Holder(@NonNull View itemView) {
            super(itemView);
            group = itemView.findViewById(R.id.group);
            title = itemView.findViewById(R.id.name);
            meta = itemView.findViewById(R.id.meta);
            action = itemView.findViewById(R.id.action);
        }
    }
}
