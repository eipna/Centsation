package com.eipna.centsation.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.eipna.centsation.databinding.ActivityArchiveBinding;
import com.eipna.centsation.ui.adapters.SavingAdapter;
import com.eipna.centsation.ui.adapters.TransactionAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.util.ArrayList;

public class ArchiveActivity extends BaseActivity implements SavingListener {

    private ActivityArchiveBinding binding;
    private SavingRepository savingRepository;
    private TransactionRepository transactionRepository;
    private SavingAdapter savingAdapter;
    private ArrayList<Saving> savings;

    private final ActivityResultLauncher<Intent> editSavingLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshList();
                }
            });

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

        savings = new ArrayList<>(savingRepository.get(Saving.IS_ARCHIVE));
        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);

        savingAdapter = new SavingAdapter(this, this, savings);
        binding.savingList.setLayoutManager(new LinearLayoutManager(this));
        binding.savingList.setAdapter(savingAdapter);
    }

    private void unarchiveSaving(Saving selectedSaving) {
        selectedSaving.setIsArchived(Saving.NOT_ARCHIVE);
        savingRepository.edit(selectedSaving);
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

    private void showDeleteDialog(String savingID) {
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
        savings.addAll(savingRepository.get(Saving.IS_ARCHIVE));
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
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra("id", selectedSaving.getID());
        editIntent.putExtra("name", selectedSaving.getName());
        editIntent.putExtra("current_saving", selectedSaving.getCurrentSaving());
        editIntent.putExtra("goal", selectedSaving.getGoal());
        editIntent.putExtra("notes", selectedSaving.getNotes());
        editIntent.putExtra("deadline", selectedSaving.getDeadline());
        editIntent.putExtra("is_archive", selectedSaving.getIsArchived());
        editSavingLauncher.launch(editIntent);
    }

    @Override
    public void OnOperationClick(SavingOperation operation, int position) {
        Saving selectedSaving = savings.get(position);
        if (operation.equals(SavingOperation.UNARCHIVE)) unarchiveSaving(selectedSaving);
        if (operation.equals(SavingOperation.SHARE)) showShareIntent(selectedSaving.getNotes());
        if (operation.equals(SavingOperation.DELETE)) showDeleteDialog(selectedSaving.getID());
        if (operation.equals(SavingOperation.HISTORY)) showHistoryDialog(selectedSaving);
    }
}