package com.example.campus_navigation_helper.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;
import com.example.campus_navigation_helper.models.NavigationStep;

import java.util.List;

public class NavigationStepAdapter extends RecyclerView.Adapter<NavigationStepAdapter.ViewHolder> {

    private List<NavigationStep> steps;

    public NavigationStepAdapter(List<NavigationStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_navigation_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NavigationStep step = steps.get(position);
        holder.tvNum.setText(String.valueOf(step.getStepNumber()));
        holder.tvDesc.setText(step.getStepDescription());
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNum, tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNum = itemView.findViewById(R.id.tvStepNum);
            tvDesc = itemView.findViewById(R.id.tvStepDesc);
        }
    }
}
