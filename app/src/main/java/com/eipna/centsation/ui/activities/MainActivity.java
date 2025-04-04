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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends BaseActivity implements SavingListener {

    private ActivityMainBinding binding;
    private SavingRepository savingRepository;
    private TransactionRepository transactionRepository;
    private SavingAdapter savingAdapter;
    private ArrayList<Saving> savings;

    private String sortCriteria;
    private boolean isSortAscending;

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

        binding.createSaving.setOnClickListener(view -> showCreateDialog());
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

    private void showCreateDialog() {
        View createDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_saving_create, null, false);
        String currentCurrencySymbol = Currency.getSymbol(preferences.getCurrency());

        TextInputLayout nameLayout = createDialogView.findViewById(R.id.field_saving_name_layout);
        TextInputLayout currentSavingLayout = createDialogView.findViewById(R.id.field_saving_current_saving_layout);
        TextInputLayout goalLayout = createDialogView.findViewById(R.id.field_saving_goal_layout);

        TextInputEditText nameInput = createDialogView.findViewById(R.id.field_saving_name_text);
        TextInputEditText currentSavingInput = createDialogView.findViewById(R.id.field_saving_current_saving_text);
        TextInputEditText goalInput = createDialogView.findViewById(R.id.field_saving_goal_text);
        TextInputEditText notesInput = createDialogView.findViewById(R.id.field_saving_notes_text);

        currentSavingLayout.setPrefixText(currentCurrencySymbol);
        goalLayout.setPrefixText(currentCurrencySymbol);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_create_saving)
                .setIcon(R.drawable.ic_add_circle)
                .setView(createDialogView)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_create, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String nameText = Objects.requireNonNull(nameInput.getText()).toString();
            String currentSavingText = Objects.requireNonNull(currentSavingInput.getText()).toString();
            String goalText = Objects.requireNonNull(goalInput.getText()).toString();
            String notesText = Objects.requireNonNull(notesInput.getText()).toString();

            if (!nameText.isEmpty() && !currentSavingText.isEmpty() && !goalText.isEmpty()) {
                double currentSaving = Double.parseDouble(currentSavingText);
                double goal = Double.parseDouble(goalText);

                if (currentSaving > goal) {
                    goalLayout.setError(getString(R.string.field_error_lower_goal));
                    return;
                }

                Saving createdSaving = new Saving();
                createdSaving.setName(nameText);
                createdSaving.setCurrentSaving(currentSaving);
                createdSaving.setGoal(goal);
                createdSaving.setNotes(notesText);
                createdSaving.setIsArchived(Saving.NOT_ARCHIVE);
                savingRepository.create(createdSaving);

                refreshList();
                dialog.dismiss();
            }
            nameLayout.setError(nameText.isEmpty() ? getString(R.string.field_error_required) : null);
            currentSavingLayout.setError(currentSavingText.isEmpty() ? getString(R.string.field_error_required) : null);
            goalLayout.setError(goalText.isEmpty() ? getString(R.string.field_error_required) : null);
        }));
        dialog.show();
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
                    double goal = Double.parseDouble(goalText);

                    Saving editedSaving = new Saving();
                    editedSaving.setID(selectedSaving.getID());
                    editedSaving.setName(nameText);
                    editedSaving.setCurrentSaving(selectedSaving.getCurrentSaving());
                    editedSaving.setGoal(goal);
                    editedSaving.setNotes(notesText);
                    editedSaving.setIsArchived(selectedSaving.getIsArchived());
                    savingRepository.update(editedSaving);

                    refreshList();
                    dialog.dismiss();
                }
                nameLayout.setError(nameText.isEmpty() ? getString(R.string.field_error_required) : null);
                goalLayout.setError(goalText.isEmpty() ? getString(R.string.field_error_required) : null);
            });
        });
        dialog.show();
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

        MaterialButton depositButton = transactionDialogView.findViewById(R.id.button_saving_deposit);
        MaterialButton withdrawButton = transactionDialogView.findViewById(R.id.button_saving_withdraw);

        amountLayout.setPrefixText(currentCurrencySymbol);

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

    private void archiveSaving(Saving selectedSaving) {
        selectedSaving.setIsArchived(Saving.IS_ARCHIVE);
        savingRepository.update(selectedSaving);
        refreshList();
    }

    @Override
    public void OnClick(int position) {
        Saving selectedSaving = savings.get(position);
        showEditDialog(selectedSaving);
    }

    @Override
    public void OnOperationClick(SavingOperation operation, int position) {
        Saving selectedSaving = savings.get(position);
        if (operation.equals(SavingOperation.DELETE)) showDeleteDialog(selectedSaving.getID());
        if (operation.equals(SavingOperation.SHARE)) showShareIntent(selectedSaving.getNotes());
        if (operation.equals(SavingOperation.TRANSACTION)) showTransactionDialog(selectedSaving);
        if (operation.equals(SavingOperation.ARCHIVE)) archiveSaving(selectedSaving);
        if (operation.equals(SavingOperation.HISTORY)) showHistoryDialog(selectedSaving);
    }
}