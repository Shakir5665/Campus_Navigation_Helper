package com.example.campus_navigation_helper.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.models.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Location> locations;
    private List<Location> locationsFull;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Location location);
    }

    public LocationAdapter(List<Location> locations, OnItemClickListener listener) {
        this.locations = locations;
        this.locationsFull = new ArrayList<>(locations);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.tvName.setText(location.getName());
        holder.tvBlock.setText(location.getBlock());
        holder.tvDesc.setText(location.getShortDescription());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(location));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void filter(String text) {
        locations.clear();
        if (text.isEmpty()) {
            locations.addAll(locationsFull);
        } else {
            text = text.toLowerCase();
            for (Location item : locationsFull) {
                if (item.getName().toLowerCase().contains(text) || item.getBlock().toLowerCase().contains(text)) {
                    locations.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBlock, tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvLocName);
            tvBlock = itemView.findViewById(R.id.tvLocBlock);
            tvDesc = itemView.findViewById(R.id.tvLocShortDesc);
        }
    }
}
