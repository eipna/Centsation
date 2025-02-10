package com.eipna.centsation.ui.activities;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuCompat;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.eipna.centsation.R;
import com.eipna.centsation.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Drawable drawable = MaterialShapeDrawable.createWithElevationOverlay(this);
        binding.appBar.setStatusBarForeground(drawable);

        setSupportActionBar(binding.toolbar);

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
        if (item.getItemId() == R.id.archive) {
            Intent archiveIntent = new Intent(getApplicationContext(), ArchiveActivity.class);
            startActivity(archiveIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }

        if (item.getItemId() == R.id.settings) {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
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
                dialog.dismiss();
            }

            savingNameLayout.setError(savingNameString.isEmpty() ? getString(R.string.field_error_required) : null);
            savingCurrentAmountLayout.setError(savingNameString.isEmpty() ? getString(R.string.field_error_lower_goal) : null);
            savingGoalLayout.setError(savingGoalString.isEmpty() ? getString(R.string.field_error_required) : null);
        }));
        dialog.show();
    }
}