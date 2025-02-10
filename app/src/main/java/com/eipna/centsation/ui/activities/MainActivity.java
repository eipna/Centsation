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

import com.eipna.centsation.R;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingListener;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.databinding.ActivityMainBinding;
import com.eipna.centsation.ui.adapters.SavingAdapter;
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
        savings = new ArrayList<>(savingRepository.getSavings(false));

        savingAdapter = new SavingAdapter(this, this, savings);
        binding.savingList.setLayoutManager(new LinearLayoutManager(this));
        binding.savingList.setAdapter(savingAdapter);

        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);
        binding.addSaving.setOnClickListener(view -> showAddSavingDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
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

    private void showAddSavingDialog() {
        @SuppressLint("InflateParams")
        View addSavingDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_saving, null);

        TextInputLayout savingNameLayout = addSavingDialogView.findViewById(R.id.field_saving_name_layout);
        TextInputLayout savingCurrentAmountLayout = addSavingDialogView.findViewById(R.id.field_saving_current_amount_layout);
        TextInputLayout savingGoalLayout = addSavingDialogView.findViewById(R.id.field_saving_goal_layout);
        TextInputLayout savingNotesLayout = addSavingDialogView.findViewById(R.id.field_saving_notes_layout);

        TextInputEditText savingNameInput = addSavingDialogView.findViewById(R.id.field_saving_name_text);
        TextInputEditText savingCurrentAmountInput = addSavingDialogView.findViewById(R.id.field_saving_current_amount_text);
        TextInputEditText savingGoalInput = addSavingDialogView.findViewById(R.id.field_saving_goal_text);
        TextInputEditText savingNotesInput = addSavingDialogView.findViewById(R.id.field_saving_notes_text);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_add_saving)
                .setView(addSavingDialogView)
                .setIcon(R.drawable.ic_add)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_add, null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String savingNameString = Objects.requireNonNull(savingNameInput.getText()).toString();
            String savingCurrentAmountString = Objects.requireNonNull(savingCurrentAmountInput.getText()).toString();
            String savingGoalString = Objects.requireNonNull(savingGoalInput.getText()).toString();
            String savingNotesString = Objects.requireNonNull(savingNotesInput.getText()).toString();

            if (!savingNameString.isEmpty() && !savingCurrentAmountString.isEmpty() && !savingGoalString.isEmpty()) {
                double savingCurrentAmount = Double.parseDouble(savingCurrentAmountString);
                double savingGoal = Double.parseDouble(savingGoalString);

                if (savingCurrentAmount > savingGoal) {
                    savingGoalLayout.setError(getString(R.string.field_error_lower_goal));
                    return;
                }
                createSaving(savingNameString, savingCurrentAmount, savingGoal, savingNotesString);
                dialog.dismiss();
            }

            savingNameLayout.setError(savingNameString.isEmpty() ? getString(R.string.field_error_required) : null);
            savingCurrentAmountLayout.setError(savingNameString.isEmpty() ? getString(R.string.field_error_lower_goal) : null);
            savingGoalLayout.setError(savingGoalString.isEmpty() ? getString(R.string.field_error_required) : null);
        }));
        dialog.show();
    }

    private void createSaving(String name, double amount, double goal, String notes) {
        Saving createdSaving = new Saving();
        createdSaving.setName(name);
        createdSaving.setCurrentAmount(amount);
        createdSaving.setGoal(goal);
        createdSaving.setNotes(notes);
        createdSaving.setArchived(false);

        savingRepository.create(createdSaving);
        savings = new ArrayList<>(savingRepository.getSavings(false));
        savingAdapter.update(savings);
        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public void OnUpdateClick(int position) {

    }

    @Override
    public void OnHistoryClick(int position) {

    }

    @Override
    public void OnArchiveClick(int position) {

    }

    @Override
    public void OnDeleteClick(int position) {

    }
}