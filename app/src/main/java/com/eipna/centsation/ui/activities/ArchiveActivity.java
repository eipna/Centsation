package com.eipna.centsation.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.centsation.R;
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
import java.util.Objects;

public class ArchiveActivity extends BaseActivity implements SavingListener {

    private ActivityArchiveBinding binding;
    private SavingRepository savingRepository;
    private TransactionRepository transactionRepository;
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

        SavingAdapter savingAdapter = new SavingAdapter(this, this, savings);
        binding.savingList.setLayoutManager(new LinearLayoutManager(this));
        binding.savingList.setAdapter(savingAdapter);
    }

    private void unarchiveSaving(Saving selectedSaving) {
        selectedSaving.setIsArchived(Saving.NOT_ARCHIVE);
        savingRepository.update(selectedSaving);
        refreshList();
    }

    private void showShareIntent(String notes) {
        if (notes.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_saving_empty_notes), Toast.LENGTH_SHORT).show();
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, notes);

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
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

    private void refreshList() {
        savings.clear();
        savings.addAll(savingRepository.getSavings(Saving.IS_ARCHIVE));
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

    private void showUpdateDialog(Saving selectedSaving) {
        View updateSavingValue = LayoutInflater.from(this).inflate(R.layout.dialog_saving_transaction, null, false);

        TextInputLayout savingValueLayout = updateSavingValue.findViewById(R.id.field_saving_update_layout);
        TextInputEditText savingValueInput = updateSavingValue.findViewById(R.id.field_saving_update_text);

        MaterialButton depositButton = updateSavingValue.findViewById(R.id.button_saving_deposit);
        MaterialButton withdrawButton = updateSavingValue.findViewById(R.id.button_saving_withdraw);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_add_transaction)
                .setIcon(R.drawable.ic_add_circle)
                .setView(updateSavingValue);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            depositButton.setOnClickListener(view -> {
                String savingValueString = Objects.requireNonNull(savingValueInput.getText()).toString();
                if (savingValueString.isEmpty()) {
                    savingValueLayout.setError(getString(R.string.field_error_empty_value));
                    return;
                }

                double addedValue = selectedSaving.getValue() + Double.parseDouble(savingValueString);
                Transaction transaction = new Transaction();
                transaction.setSavingID(selectedSaving.getID());
                transaction.setAmount(Math.abs(addedValue - selectedSaving.getValue()));
                transaction.setType(TransactionType.DEPOSIT.VALUE);
                transactionRepository.create(transaction);

                selectedSaving.setValue(addedValue);
                savingRepository.update(selectedSaving);
                refreshList();
                dialog.dismiss();
            });

            withdrawButton.setOnClickListener(view -> {
                String savingValueString = Objects.requireNonNull(savingValueInput.getText()).toString();
                if (savingValueString.isEmpty()) {
                    savingValueLayout.setError(getString(R.string.field_error_empty_value));
                    return;
                }

                double deductedValue = selectedSaving.getValue() - Double.parseDouble(savingValueString);
                if (deductedValue < 0) {
                    savingValueLayout.setError(getString(R.string.field_error_negative_value));
                    return;
                }

                Transaction transaction = new Transaction();
                transaction.setSavingID(selectedSaving.getID());
                transaction.setAmount(Math.abs(deductedValue - selectedSaving.getValue()));
                transaction.setType(TransactionType.WITHDRAW.VALUE);
                transactionRepository.create(transaction);

                selectedSaving.setValue(deductedValue);
                savingRepository.update(selectedSaving);
                refreshList();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    private void showEditDialog(Saving selectedSaving) {
        View savingDialog = LayoutInflater.from(this).inflate(R.layout.dialog_saving_info, null, false);

        TextInputLayout savingNameLayout = savingDialog.findViewById(R.id.field_saving_name_layout);
        TextInputLayout savingValueLayout = savingDialog.findViewById(R.id.field_saving_value_layout);
        TextInputLayout savingGoalLayout = savingDialog.findViewById(R.id.field_saving_goal_layout);

        TextInputEditText savingNameInput = savingDialog.findViewById(R.id.field_saving_name_text);
        TextInputEditText savingValueInput = savingDialog.findViewById(R.id.field_saving_value_text);
        TextInputEditText savingGoalInput = savingDialog.findViewById(R.id.field_saving_goal_text);
        TextInputEditText savingNotesInput = savingDialog.findViewById(R.id.field_saving_notes_text);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_edit_saving)
                .setIcon(R.drawable.ic_edit)
                .setView(savingDialog)
                .setPositiveButton(R.string.dialog_button_edit, null)
                .setNegativeButton(R.string.dialog_button_cancel, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            savingNameInput.setText(selectedSaving.getName());
            savingValueInput.setText(String.valueOf(selectedSaving.getValue()));
            savingGoalInput.setText(String.valueOf(selectedSaving.getGoal()));
            savingNotesInput.setText(selectedSaving.getNotes());

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String savingNameString = Objects.requireNonNull(savingNameInput.getText()).toString();
                String savingValueString = Objects.requireNonNull(savingValueInput.getText()).toString();
                String savingGoalString = Objects.requireNonNull(savingGoalInput.getText()).toString();
                String savingNotesString = Objects.requireNonNull(savingNotesInput.getText()).toString();

                if (!savingNameString.isEmpty() && !savingValueString.isEmpty() && !savingGoalString.isEmpty()) {
                    double savingValue = Double.parseDouble(savingValueString);
                    double savingGoal = Double.parseDouble(savingGoalString);

                    if (savingValue > savingGoal) {
                        savingGoalLayout.setError(getString(R.string.field_error_lower_goal));
                        return;
                    }

                    if (savingValue != selectedSaving.getValue()) {
                        Transaction transaction = new Transaction();
                        transaction.setSavingID(selectedSaving.getID());
                        transaction.setAmount(Math.abs(savingValue - selectedSaving.getValue()));

                        if (savingValue > selectedSaving.getValue()) {
                            transaction.setType(TransactionType.DEPOSIT.VALUE);
                        }

                        if (savingValue < selectedSaving.getValue()) {
                            transaction.setType(TransactionType.WITHDRAW.VALUE);
                        }
                        transactionRepository.create(transaction);
                    }

                    Saving editedSaving = new Saving();
                    editedSaving.setID(selectedSaving.getID());
                    editedSaving.setName(savingNameString);
                    editedSaving.setValue(savingValue);
                    editedSaving.setGoal(savingGoal);
                    editedSaving.setNotes(savingNotesString);
                    editedSaving.setIsArchived(selectedSaving.getIsArchived());
                    savingRepository.update(editedSaving);

                    refreshList();
                    dialog.dismiss();
                }

                savingNameLayout.setError(savingNameString.isEmpty() ? getString(R.string.field_error_required) : null);
                savingValueLayout.setError(savingValueString.isEmpty() ? getString(R.string.field_error_required) : null);
                savingGoalLayout.setError(savingGoalString.isEmpty() ? getString(R.string.field_error_required) : null);
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
        if (operation.equals(SavingOperation.UPDATE)) showUpdateDialog(selectedSaving);
    }
}