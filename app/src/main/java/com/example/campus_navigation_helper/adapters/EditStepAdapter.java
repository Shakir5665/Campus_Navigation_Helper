package com.example.campus_navigation_helper.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_navigation_helper.R;

import java.util.List;

public class EditStepAdapter extends RecyclerView.Adapter<EditStepAdapter.ViewHolder> {

    private Context context;
    private List<String> stepsList;

    public EditStepAdapter(Context context, List<String> stepsList) {
        this.context = context;
        this.stepsList = stepsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_edit_step, parent, false);
        return new ViewHolder(view, new CustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvStepNumber.setText((position + 1) + ".");

        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.etDescription.setText(stepsList.get(holder.getAdapterPosition()));

        holder.ivRemove.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                stepsList.remove(currentPos);
                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, stepsList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    private class CustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (position != RecyclerView.NO_POSITION && position < stepsList.size()) {
                stepsList.set(position, charSequence.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber;
        EditText etDescription;
        ImageView ivRemove;
        CustomEditTextListener myCustomEditTextListener;

        public ViewHolder(@NonNull View itemView, CustomEditTextListener listener) {
            super(itemView);
            tvStepNumber = itemView.findViewById(R.id.tvStepNumber);
            etDescription = itemView.findViewById(R.id.etStepDescription);
            ivRemove = itemView.findViewById(R.id.btnRemoveStep);
            myCustomEditTextListener = listener;
            etDescription.addTextChangedListener(myCustomEditTextListener);
        }
    }
}
