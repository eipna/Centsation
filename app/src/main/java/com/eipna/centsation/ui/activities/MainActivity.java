package com.eipna.centsation.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eipna.centsation.R;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingListener;
import com.eipna.centsation.data.saving.SavingOperation;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.databinding.ActivityMainBinding;
import com.eipna.centsation.ui.adapters.SavingAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends BaseActivity implements SavingListener {

    private ActivityMainBinding binding;
    private SavingRepository savingRepository;
    private SavingAdapter savingAdapter;
    private ArrayList<Saving> savings;

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
        savings = new ArrayList<>();
        savings.addAll(savingRepository.getSavings(0));
        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);

        savingAdapter = new SavingAdapter(this, this, savings);
        binding.savingList.setLayoutManager(new LinearLayoutManager(this));
        binding.savingList.setAdapter(savingAdapter);

        binding.addSaving.setOnClickListener(view -> showAddDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void updateSavingsList() {
        savings = new ArrayList<>(savingRepository.getSavings(0));
        savingAdapter.update(savings);
        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.archive) startActivity(new Intent(this, ArchiveActivity.class));
        if (item.getItemId() == R.id.settings) startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    private void showAddDialog() {
        View savingDialog = LayoutInflater.from(this).inflate(R.layout.dialog_saving, null, false);

        TextInputLayout savingNameLayout = savingDialog.findViewById(R.id.field_saving_name_layout);
        TextInputLayout savingValueLayout = savingDialog.findViewById(R.id.field_saving_value_layout);
        TextInputLayout savingGoalLayout = savingDialog.findViewById(R.id.field_saving_goal_layout);

        TextInputEditText savingNameInput = savingDialog.findViewById(R.id.field_saving_name_text);
        TextInputEditText savingValueInput = savingDialog.findViewById(R.id.field_saving_value_text);
        TextInputEditText savingGoalInput = savingDialog.findViewById(R.id.field_saving_goal_text);
        TextInputEditText savingNotesInput = savingDialog.findViewById(R.id.field_saving_notes_text);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_add_saving)
                .setIcon(R.drawable.ic_add)
                .setView(savingDialog)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_add, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
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

                Saving createdSaving = new Saving();
                createdSaving.setName(savingNameString);
                createdSaving.setValue(savingValue);
                createdSaving.setGoal(savingGoal);
                createdSaving.setNotes(savingNotesString);
                createdSaving.setIsArchived(0);
                savingRepository.create(createdSaving);
                updateSavingsList();
                dialog.dismiss();
            }

            savingNameLayout.setError(savingNameString.isEmpty() ? getString(R.string.field_error_required) : null);
            savingValueLayout.setError(savingNameString.isEmpty() ? getString(R.string.field_error_required) : null);
            savingGoalLayout.setError(savingGoalString.isEmpty() ? getString(R.string.field_error_required) : null);
        }));
        dialog.show();
    }

    private void showEditDialog(Saving selectedSaving) {
        View savingDialog = LayoutInflater.from(this).inflate(R.layout.dialog_saving, null, false);

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

                    Saving editedSaving = new Saving();
                    editedSaving.setID(selectedSaving.getID());
                    editedSaving.setName(savingNameString);
                    editedSaving.setValue(savingValue);
                    editedSaving.setGoal(savingGoal);
                    editedSaving.setNotes(savingNotesString);
                    editedSaving.setIsArchived(selectedSaving.getIsArchived());
                    savingRepository.update(editedSaving);
                    updateSavingsList();
                    dialog.dismiss();
                }

                savingNameLayout.setError(savingNameString.isEmpty() ? getString(R.string.field_error_required) : null);
                savingValueLayout.setError(savingValueString.isEmpty() ? getString(R.string.field_error_required) : null);
                savingGoalLayout.setError(savingGoalString.isEmpty() ? getString(R.string.field_error_required) : null);
            });
        });
        dialog.show();
    }

    private void showDeleteDialog(int savingID) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_delete_saving)
                .setMessage(R.string.dialog_message_delete_saving)
                .setIcon(R.drawable.ic_warning_outline)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_delete, (dialogInterface, i) -> {
                    savingRepository.delete(savingID);
                    updateSavingsList();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void showUpdateDialog(Saving selectedSaving) {
        View updateSavingValue = LayoutInflater.from(this).inflate(R.layout.dialog_saving_update, null, false);

        TextInputLayout savingValueLayout = updateSavingValue.findViewById(R.id.field_saving_update_layout);
        TextInputEditText savingValueInput = updateSavingValue.findViewById(R.id.field_saving_update_text);

        MaterialButton addButton = updateSavingValue.findViewById(R.id.button_saving_add);
        MaterialButton deductButton = updateSavingValue.findViewById(R.id.button_saving_deduct);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_update_value)
                .setIcon(R.drawable.ic_update)
                .setView(updateSavingValue);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void archiveSaving(Saving selectedSaving) {
        selectedSaving.setIsArchived(0);
        savingRepository.update(selectedSaving);
        updateSavingsList();
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
        if (operation.equals(SavingOperation.UPDATE)) showUpdateDialog(selectedSaving);
        if (operation.equals(SavingOperation.ARCHIVE)) archiveSaving(selectedSaving);
    }
}