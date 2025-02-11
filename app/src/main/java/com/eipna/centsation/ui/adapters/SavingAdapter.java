package com.eipna.centsation.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.centsation.R;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingListener;
import com.eipna.centsation.data.saving.SavingOperation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.ViewHolder> {

    private final Context context;
    private final SavingListener savingListener;
    private ArrayList<Saving> savings;

    public SavingAdapter(Context context, SavingListener savingListener, ArrayList<Saving> savings) {
        this.context = context;
        this.savingListener = savingListener;
        this.savings = savings;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(ArrayList<Saving> updatedSavings) {
        savings.clear();
        savings.addAll(updatedSavings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View savingView = LayoutInflater.from(context).inflate(R.layout.recycler_saving, parent, false);
        return new ViewHolder(savingView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Saving currentSaving = savings.get(position);
        holder.bind(currentSaving);

        holder.itemView.setOnClickListener(view -> savingListener.OnClick(position));
        holder.delete.setOnClickListener(view -> savingListener.OnOperationClick(SavingOperation.DELETE, position));
        holder.copyNotes.setOnClickListener(view -> savingListener.OnOperationClick(SavingOperation.COPY_NOTES, position));
    }

    @Override
    public int getItemCount() {
        return savings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView name, amount, goal, percent;
        MaterialButton update, history, archive, delete, copyNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.saving_name);
            amount = itemView.findViewById(R.id.saving_amount);
            goal = itemView.findViewById(R.id.saving_goal);
            percent = itemView.findViewById(R.id.saving_percent);

            update = itemView.findViewById(R.id.saving_update);
            history = itemView.findViewById(R.id.saving_history);
            archive = itemView.findViewById(R.id.archive);
            delete = itemView.findViewById(R.id.saving_delete);

            copyNotes = itemView.findViewById(R.id.saving_copy_notes);
        }

        public void bind(Saving currentSaving) {
            name.setText(currentSaving.getName());
            amount.setText(String.valueOf(currentSaving.getCurrentAmount()));
            goal.setText(String.valueOf(currentSaving.getGoal()));
        }
    }
}