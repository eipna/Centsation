package com.eipna.centsation.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.centsation.R;
import com.eipna.centsation.data.Currency;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingListener;
import com.eipna.centsation.data.saving.SavingOperation;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.data.transaction.Transaction;
import com.eipna.centsation.data.transaction.TransactionRepository;
import com.eipna.centsation.data.transaction.TransactionType;
import com.eipna.centsation.databinding.ActivityArchiveBinding;
import com.eipna.centsation.ui.adapters.SavingAdapter;
import com.eipna.centsation.ui.adapters.TransactionAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ArchiveActivity extends BaseActivity implements SavingListener {

    private ActivityArchiveBinding binding;
    private SavingRepository savingRepository;
    private TransactionRepository transactionRepository;
    private SavingAdapter savingAdapter;
    private ArrayList<Saving> savings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityArchiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Drawable drawable = MaterialShapeDrawable.createWithElevationOverlay(this);
        binding.appBar.setStatusBarForeground(drawable);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        savingRepository = new SavingRepository(this);
        transactionRepository = new TransactionRepository(this);

        savings = new ArrayList<>(savingRepository.getSavings(Saving.IS_ARCHIVE));
        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);

        savingAdapter = new SavingAdapter(this, this, savings);
        binding.savingList.setLayoutManager(new LinearLayoutManager(this));
        binding.savingList.setAdapter(savingAdapter);
    }

    private void unarchiveSaving(Saving selectedSaving) {
        selectedSaving.setIsArchived(Saving.NOT_ARCHIVE);
        savingRepository.update(selectedSaving);
        refreshList();
    }

    private void showShareIntent(String notes) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, notes);

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void showDeleteDialog(int savingID) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_saving)
                .setIcon(R.drawable.ic_delete_forever)
                .setMessage(R.string.dialog_message_delete_saving)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_delete, (dialogInterface, i) -> {
                    savingRepository.delete(savingID);
                    refreshList();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showHistoryDialog(Saving selectedSaving) {
        View transactionDialog = LayoutInflater.from(this).inflate(R.layout.dialog_saving_history, null, false);

        ArrayList<Transaction> transactions = transactionRepository.getTransactions(selectedSaving.getID());
        TransactionAdapter adapter = new TransactionAdapter(this, transactions);

        RecyclerView transactionList = transactionDialog.findViewById(R.id.transaction_list);
        transactionList.setLayoutManager(new LinearLayoutManager(this));
        transactionList.setAdapter(adapter);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_history_saving)
                .setIcon(R.drawable.ic_history)
                .setView(transactionDialog)
                .setPositiveButton(R.string.dialog_button_close, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshList() {
        savings.clear();
        savings.addAll(savingRepository.getSavings(Saving.IS_ARCHIVE));
        savingAdapter.notifyDataSetChanged();
        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void OnClick(int position) {
        Saving selectedSaving = savings.get(position);
        showEditDialog(selectedSaving);
    }

    private void showTransactionDialog(Saving selectedSaving) {
        View transactionDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_saving_transaction, null, false);

        TextInputLayout amountLayout = transactionDialogView.findViewById(R.id.field_saving_amount_layout);
        TextInputEditText amountInput = transactionDialogView.findViewById(R.id.field_saving_amount_text);

        MaterialButton depositButton = transactionDialogView.findViewById(R.id.button_saving_deposit);
        MaterialButton withdrawButton = transactionDialogView.findViewById(R.id.button_saving_withdraw);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_create_transaction)
                .setIcon(R.drawable.ic_add_circle)
                .setView(transactionDialogView);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            depositButton.setOnClickListener(view -> {
                String amountText = Objects.requireNonNull(amountInput.getText()).toString();

                if (amountText.isEmpty() || Double.parseDouble(amountText) == 0) {
                    amountLayout.setError(getString(R.string.field_error_empty_saving));
                    return;
                }

                double addedSaving = selectedSaving.getCurrentSaving() + Double.parseDouble(amountText);
                double amount = Double.parseDouble(amountText);

                selectedSaving.setCurrentSaving(addedSaving);
                savingRepository.makeTransaction(selectedSaving, amount, TransactionType.DEPOSIT);

                refreshList();
                dialog.dismiss();
            });

            withdrawButton.setOnClickListener(view -> {
                String amountText = Objects.requireNonNull(amountInput.getText()).toString();

                if (amountText.isEmpty() || Double.parseDouble(amountText) == 0) {
                    amountLayout.setError(getString(R.string.field_error_empty_saving));
                    return;
                }

                double deductedSaving = selectedSaving.getCurrentSaving() - Double.parseDouble(amountText);
                double amount = Double.parseDouble(amountText);
                if (deductedSaving < 0) {
                    amountLayout.setError(getString(R.string.field_error_negative_saving));
                    return;
                }

                selectedSaving.setCurrentSaving(deductedSaving);
                savingRepository.makeTransaction(selectedSaving, amount, TransactionType.WITHDRAW);

                refreshList();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    private void showEditDialog(Saving selectedSaving) {
        View editDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_saving_edit, null, false);
        String currentCurrencySymbol = Currency.getSymbol(preferences.getCurrency());

        TextInputLayout nameLayout = editDialogView.findViewById(R.id.field_saving_name_layout);
        TextInputLayout goalLayout = editDialogView.findViewById(R.id.field_saving_goal_layout);

        TextInputEditText nameInput = editDialogView.findViewById(R.id.field_saving_name_text);
        TextInputEditText goalInput = editDialogView.findViewById(R.id.field_saving_goal_text);
        TextInputEditText notesInput = editDialogView.findViewById(R.id.field_saving_notes_text);

        goalLayout.setPrefixText(currentCurrencySymbol);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_edit_saving)
                .setIcon(R.drawable.ic_edit)
                .setView(editDialogView)
                .setPositiveButton(R.string.dialog_button_edit, null)
                .setNegativeButton(R.string.dialog_button_cancel, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            nameInput.setText(selectedSaving.getName());
            goalInput.setText(String.format(Locale.getDefault(), "%.2f", selectedSaving.getGoal()));
            notesInput.setText(selectedSaving.getNotes());

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String nameText = Objects.requireNonNull(nameInput.getText()).toString();
                String goalText = Objects.requireNonNull(goalInput.getText()).toString();
                String notesText = Objects.requireNonNull(notesInput.getText()).toString();

                if (!nameText.isEmpty() && !goalText.isEmpty()) {
                    double savingGoal = Double.parseDouble(goalText);

                    Saving editedSaving = new Saving();
                    editedSaving.setID(selectedSaving.getID());
                    editedSaving.setName(nameText);
                    editedSaving.setCurrentSaving(selectedSaving.getCurrentSaving());
                    editedSaving.setGoal(savingGoal);
                    editedSaving.setNotes(notesText);
                    editedSaving.setIsArchived(selectedSaving.getIsArchived());
                    savingRepository.update(editedSaving);

                    refreshList();
                    dialog.dismiss();
                }
                nameLayout.setError(nameText.isEmpty() ? getString(R.string.field_error_required) : null);
                goalLayout.setError(nameText.isEmpty() ? getString(R.string.field_error_required) : null);
            });
        });
        dialog.show();
    }

    @Override
    public void OnOperationClick(SavingOperation operation, int position) {
        Saving selectedSaving = savings.get(position);
        if (operation.equals(SavingOperation.UNARCHIVE)) unarchiveSaving(selectedSaving);
        if (operation.equals(SavingOperation.SHARE)) showShareIntent(selectedSaving.getNotes());
        if (operation.equals(SavingOperation.DELETE)) showDeleteDialog(selectedSaving.getID());
        if (operation.equals(SavingOperation.HISTORY)) showHistoryDialog(selectedSaving);
        if (operation.equals(SavingOperation.TRANSACTION)) showTransactionDialog(selectedSaving);
    }
}