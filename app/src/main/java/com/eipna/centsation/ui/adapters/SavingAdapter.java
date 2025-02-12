package com.eipna.centsation.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.centsation.R;
import com.eipna.centsation.data.Currency;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingListener;
import com.eipna.centsation.data.saving.SavingOperation;
import com.eipna.centsation.util.PreferenceUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.ViewHolder> {

    private final Context context;
    private final SavingListener savingListener;
    private final PreferenceUtil preferences;
    private final ArrayList<Saving> savings;

    public SavingAdapter(Context context, SavingListener savingListener, ArrayList<Saving> savings) {
        this.context = context;
        this.savingListener = savingListener;
        this.savings = savings;
        this.preferences = new PreferenceUtil(context);
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
        holder.bind(currentSaving, preferences);

        holder.itemView.setOnClickListener(view -> savingListener.OnClick(position));
        holder.delete.setOnClickListener(view -> savingListener.OnOperationClick(SavingOperation.DELETE, position));
        holder.copyNotes.setOnClickListener(view -> savingListener.OnOperationClick(SavingOperation.COPY_NOTES, position));
        holder.update.setOnClickListener(view -> savingListener.OnOperationClick(SavingOperation.UPDATE, position));
    }

    @Override
    public int getItemCount() {
        return savings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView parent;
        MaterialTextView name, value, goal, percent;
        MaterialButton update, history, archive, delete, copyNotes;

        LinearLayout description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.saving_parent);
            name = itemView.findViewById(R.id.saving_name);
            value = itemView.findViewById(R.id.saving_value);
            goal = itemView.findViewById(R.id.saving_goal);
            percent = itemView.findViewById(R.id.saving_percent);
            description = itemView.findViewById(R.id.saving_description);

            update = itemView.findViewById(R.id.saving_update);
            history = itemView.findViewById(R.id.saving_history);
            archive = itemView.findViewById(R.id.archive);
            delete = itemView.findViewById(R.id.saving_delete);

            copyNotes = itemView.findViewById(R.id.saving_copy_notes);
        }

        public void bind(Saving currentSaving, PreferenceUtil preferences) {
            String currencySymbol = Currency.getSymbol(preferences.getCurrency());

            if (Currency.isRTLCurrency(preferences.getCurrency())) {
                description.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                value.setTextDirection(View.TEXT_DIRECTION_RTL);
                goal.setTextDirection(View.TEXT_DIRECTION_RTL);
            } else {
                description.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                value.setTextDirection(View.TEXT_DIRECTION_LTR);
                goal.setTextDirection(View.TEXT_DIRECTION_LTR);
            }

            if (currentSaving.getNotes().isEmpty()) {
                copyNotes.setVisibility(View.GONE);
            }

            parent.setChecked(currentSaving.getValue() >= currentSaving.getGoal());
            name.setText(currentSaving.getName());
            percent.setText(getPercent(currentSaving.getValue(), currentSaving.getGoal()));
            value.setText(String.format("%s%s", currencySymbol, currentSaving.getValue()));
            goal.setText(String.format("%s%s", currencySymbol, currentSaving.getGoal()));
        }

        public String getPercent(double value, double goal) {
            double percent = (value / goal) * 100;
            return String.format(" (%s%c)", (int) percent, '%');
        }
    }
}