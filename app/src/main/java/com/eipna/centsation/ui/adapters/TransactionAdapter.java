package com.eipna.centsation.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.centsation.R;
import com.eipna.centsation.data.transaction.Transaction;
import com.eipna.centsation.data.transaction.TransactionType;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Transaction> transactions;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View transactionView = LayoutInflater.from(context).inflate(R.layout.recycler_transaction, parent, false);
        return new ViewHolder(transactionView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Transaction currentTransaction = transactions.get(position);
        holder.bind(currentTransaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView type, amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.transaction_type);
            amount = itemView.findViewById(R.id.transaction_amount);
        }

        @SuppressLint("DefaultLocale")
        public void bind(Transaction currentTransaction) {
            type.setText(currentTransaction.getType());

            if (currentTransaction.getType().equals(TransactionType.DEPOSIT.VALUE) || currentTransaction.getType().equals(TransactionType.CREATED.VALUE)) {
                amount.setTextColor(itemView.getContext().getResources().getColor(R.color.md_theme_secondary, itemView.getContext().getTheme()));
                amount.setText(String.format("%c%.2f", '+', currentTransaction.getAmount()));
            }

            if (currentTransaction.getType().equals(TransactionType.WITHDRAW.VALUE)) {
                amount.setTextColor(itemView.getContext().getResources().getColor(R.color.md_theme_error, itemView.getContext().getTheme()));
                amount.setText(String.format("%c%.2f", '-', currentTransaction.getAmount()));
            }
        }
    }
}