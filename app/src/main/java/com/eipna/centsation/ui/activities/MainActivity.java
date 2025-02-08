package com.eipna.centsation.ui.activities;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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
        TextInputLayout savingTitleLayout = addSavingDialogView.findViewById(R.id.field_saving_name_layout);
        TextInputLayout savingCurrentAmountLayout = addSavingDialogView.findViewById(R.id.field_saving_current_amount_layout);
        TextInputEditText savingTitleInput = addSavingDialogView.findViewById(R.id.field_saving_name_text);
        TextInputEditText savingCurrentAmountInput = addSavingDialogView.findViewById(R.id.field_saving_current_amount_text);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_add_saving)
                .setView(addSavingDialogView)
                .setIcon(R.drawable.ic_add)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_add, null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String savingTitle = Objects.requireNonNull(savingTitleInput.getText()).toString();
            String savingCurrentAmount = Objects.requireNonNull(savingCurrentAmountInput.getText()).toString();

            if (!savingTitle.isEmpty() && !savingCurrentAmount.isEmpty()) {
                dialog.dismiss();
            }

            if (savingTitle.isEmpty()) {
                savingTitleLayout.setError(getString(R.string.field_error_required));
            }

            if (savingCurrentAmount.isEmpty()) {
                savingCurrentAmountLayout.setError(getString(R.string.field_error_required));
            }
        }));
        dialog.show();
    }
}