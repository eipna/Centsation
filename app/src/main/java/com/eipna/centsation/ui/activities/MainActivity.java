package com.eipna.centsation.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eipna.centsation.R;
import com.eipna.centsation.data.Currency;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingListener;
import com.eipna.centsation.data.saving.SavingOperation;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.data.saving.SavingSort;
import com.eipna.centsation.data.transaction.Transaction;
import com.eipna.centsation.data.transaction.TransactionRepository;
import com.eipna.centsation.data.transaction.TransactionType;
import com.eipna.centsation.databinding.ActivityMainBinding;
import com.eipna.centsation.ui.adapters.SavingAdapter;
import com.eipna.centsation.ui.adapters.TransactionAdapter;
import com.eipna.centsation.util.AlarmUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class MainActivity extends BaseActivity implements SavingListener {

    private ActivityMainBinding binding;
    private SavingRepository savingRepository;
    private TransactionRepository transactionRepository;
    private SavingAdapter savingAdapter;
    private ArrayList<Saving> savings;

    private String sortCriteria;
    private boolean isSortAscending;

    private final ActivityResultLauncher<Intent> createSavingLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshList();
                }
            });

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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Drawable drawable = MaterialShapeDrawable.createWithElevationOverlay(this);
        binding.appBar.setStatusBarForeground(drawable);
        setSupportActionBar(binding.toolbar);

        savingRepository = new SavingRepository(this);
        transactionRepository = new TransactionRepository(this);

        sortCriteria = preferences.getSortCriteria();
        isSortAscending = preferences.getSortOrder();

        savings = new ArrayList<>();
        savings.addAll(savingRepository.getSavings(Saving.NOT_ARCHIVE));
        savingAdapter = new SavingAdapter(this, this, savings);
        sortSavings(sortCriteria);

        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);
        binding.savingList.setLayoutManager(new LinearLayoutManager(this));
        binding.savingList.setAdapter(savingAdapter);

        binding.createSaving.setOnClickListener(v -> createSavingLauncher.launch(new Intent(MainActivity.this, CreateActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshList() {
        savings.clear();
        savings.addAll(savingRepository.getSavings(Saving.NOT_ARCHIVE));
        sortSavings(sortCriteria);
        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);
        savingAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        loadSortingMenu(menu);
        return true;
    }

    private void loadSortingMenu(Menu menu) {
        if (sortCriteria.equals(SavingSort.NAME.SORT)) {
            menu.findItem(R.id.sort_name).setChecked(true);
        } else if (sortCriteria.equals(SavingSort.VALUE.SORT)) {
            menu.findItem(R.id.sort_value).setChecked(true);
        } else if (sortCriteria.equals(SavingSort.GOAL.SORT)) {
            menu.findItem(R.id.sort_goal).setChecked(true);
        } else if (sortCriteria.equals(SavingSort.DEADLINE.SORT)) {
            menu.findItem(R.id.sort_deadline).setChecked(true);
        }

        if (isSortAscending) {
            menu.findItem(R.id.sort_ascending).setChecked(true);
        } else {
            menu.findItem(R.id.sort_descending).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.archive) startActivity(new Intent(this, ArchiveActivity.class));
        if (item.getItemId() == R.id.settings) startActivity(new Intent(this, SettingsActivity.class));

        if (item.getItemId() == R.id.sort_name) {
            sortCriteria = SavingSort.NAME.SORT;
            item.setChecked(true);
            sortSavings(sortCriteria);
        }

        if (item.getItemId() == R.id.sort_value) {
            sortCriteria = SavingSort.VALUE.SORT;
            item.setChecked(true);
            sortSavings(sortCriteria);
        }

        if (item.getItemId() == R.id.sort_goal) {
            sortCriteria = SavingSort.GOAL.SORT;
            item.setChecked(true);
            sortSavings(sortCriteria);
        }

        if (item.getItemId() == R.id.sort_deadline) {
            sortCriteria = SavingSort.DEADLINE.SORT;
            item.setChecked(true);
            sortSavings(sortCriteria);
        }

        if (item.getItemId() == R.id.sort_ascending) {
            isSortAscending = true;
            item.setChecked(true);
            sortSavings(sortCriteria);
        }

        if (item.getItemId() == R.id.sort_descending) {
            isSortAscending = false;
            item.setChecked(true);
            sortSavings(sortCriteria);
        }
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortSavings(String criteria) {
        Comparator<Saving> savingComparator = null;

        if (criteria.equals(SavingSort.NAME.SORT)) {
            savingComparator = Saving.SORT_NAME;
        } else if (criteria.equals(SavingSort.VALUE.SORT)) {
            savingComparator = Saving.SORT_VALUE;
        } else if (criteria.equals(SavingSort.GOAL.SORT)) {
            savingComparator = Saving.SORT_GOAL;
        } else if (criteria.equals(SavingSort.DEADLINE.SORT)) {
            savingComparator = Saving.SORT_DEADLINE;
        }

        if (savingComparator != null) {
            if (!isSortAscending) {
                savingComparator = savingComparator.reversed();
            }
        }

        savings.sort(savingComparator);
        savingAdapter.notifyDataSetChanged();

        preferences.setSortCriteria(sortCriteria);
        preferences.setSortOrder(isSortAscending);
    }

    private void showHistoryDialog(Saving selectedSaving) {
        View historyDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_saving_history, null, false);

        ArrayList<Transaction> transactions = transactionRepository.getTransactions(selectedSaving.getID());
        TransactionAdapter adapter = new TransactionAdapter(this, transactions);

        RecyclerView transactionList = historyDialogView.findViewById(R.id.transaction_list);
        transactionList.setLayoutManager(new LinearLayoutManager(this));
        transactionList.setAdapter(adapter);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_history_saving)
                .setIcon(R.drawable.ic_history)
                .setView(historyDialogView)
                .setPositiveButton(R.string.dialog_button_close, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteDialog(Saving saving) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_saving)
                .setIcon(R.drawable.ic_delete_forever)
                .setMessage(R.string.dialog_message_delete_saving)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_delete, (dialogInterface, i) -> {
                    AlarmUtil.cancel(this, saving);
                    savingRepository.delete(saving.getID());
                    refreshList();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showShareIntent(String notes) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, notes);

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void showTransactionDialog(Saving selectedSaving) {
        View transactionDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_saving_transaction, null, false);
        String currentCurrencySymbol = Currency.getSymbol(preferences.getCurrency());

        TextInputLayout amountLayout = transactionDialogView.findViewById(R.id.field_saving_amount_layout);
        TextInputEditText amountInput = transactionDialogView.findViewById(R.id.field_saving_amount_text);

        TextInputLayout typeDropdownLayout = transactionDialogView.findViewById(R.id.field_transaction_type_layout);
        MaterialAutoCompleteTextView typeDropdownInput = transactionDialogView.findViewById(R.id.field_transaction_type_text);

        amountLayout.setPrefixText(currentCurrencySymbol);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_create_transaction)
                .setIcon(R.drawable.ic_add_circle)
                .setView(transactionDialogView)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_submit, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String amountText = Objects.requireNonNull(amountInput.getText()).toString();
                String typeText = typeDropdownInput.getText().toString();

                if (!amountText.isEmpty() && !typeText.isEmpty()) {
                    if (typeText.equals(TransactionType.DEPOSIT.VALUE)) {
                        double addedSaving = selectedSaving.getCurrentSaving() + Double.parseDouble(amountText);
                        double amount = Double.parseDouble(amountText);

                        selectedSaving.setCurrentSaving(addedSaving);
                        savingRepository.makeTransaction(selectedSaving, amount, TransactionType.DEPOSIT);

                        refreshList();
                        dialog.dismiss();
                    } else if (typeText.equals(TransactionType.WITHDRAW.VALUE)) {
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
                    }
                }

                amountLayout.setError(amountText.isEmpty() ? getString(R.string.field_error_required) : null);
                typeDropdownLayout.setError(typeText.isEmpty() ? getString(R.string.field_error_required) : null);
            });
        });
        dialog.show();
    }

    private void archiveSaving(Saving selectedSaving) {
        selectedSaving.setIsArchived(Saving.IS_ARCHIVE);
        selectedSaving.setDeadline(AlarmUtil.NO_ALARM);
        AlarmUtil.cancel(this, selectedSaving);
        savingRepository.update(selectedSaving);
        refreshList();
    }

    @Override
    public void OnClick(int position) {
        Saving selectedSaving = savings.get(position);
        Intent editIntent = new Intent(MainActivity.this, EditActivity.class);
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
        if (operation.equals(SavingOperation.DELETE)) showDeleteDialog(selectedSaving);
        if (operation.equals(SavingOperation.SHARE)) showShareIntent(selectedSaving.getNotes());
        if (operation.equals(SavingOperation.TRANSACTION)) showTransactionDialog(selectedSaving);
        if (operation.equals(SavingOperation.ARCHIVE)) archiveSaving(selectedSaving);
        if (operation.equals(SavingOperation.HISTORY)) showHistoryDialog(selectedSaving);
    }
}