package com.eipna.centsation;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.eipna.centsation.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

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

    private void showAddSavingDialog() {
        @SuppressLint("InflateParams")
        View addSavingDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_saving, null);
        TextInputLayout savingTitleLayout = addSavingDialogView.findViewById(R.id.field_saving_title_layout);
        TextInputLayout savingCurrentAmountLayout = addSavingDialogView.findViewById(R.id.field_saving_current_amount_layout);
        TextInputEditText savingTitleInput = addSavingDialogView.findViewById(R.id.field_saving_title_text);
        TextInputEditText savingCurrentAmountInput = addSavingDialogView.findViewById(R.id.field_saving_current_amount_text);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_add_saving)
                .setView(addSavingDialogView)
                .setNeutralButton(R.string.dialog_button_close, null)
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