package com.eipna.centsation.ui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eipna.centsation.R;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingListener;
import com.eipna.centsation.data.saving.SavingOperation;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.databinding.ActivityArchiveBinding;
import com.eipna.centsation.ui.adapters.SavingAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class ArchiveActivity extends BaseActivity implements SavingListener {

    private ActivityArchiveBinding binding;
    private SavingRepository savingRepository;
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
        savings = new ArrayList<>(savingRepository.getSavings(Saving.ARCHIVE_TRUE));
        binding.emptyIndicator.setVisibility(savings.isEmpty() ? View.VISIBLE : View.GONE);

        savingAdapter = new SavingAdapter(this, this, savings);
        binding.savingList.setLayoutManager(new LinearLayoutManager(this));
        binding.savingList.setAdapter(savingAdapter);
    }

    private void unarchiveSaving(Saving selectedSaving) {
        selectedSaving.setIsArchived(Saving.ARCHIVE_FALSE);
        savingRepository.update(selectedSaving);
        updateSavingsList();
    }

    private void updateSavingsList() {
        savings = new ArrayList<>(savingRepository.getSavings(Saving.ARCHIVE_TRUE));
        savingAdapter.update(savings);
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
        showEditSavingDialog(selectedSaving);
    }

    private void showEditSavingDialog(Saving selectedSaving) {
        View savingDialog = LayoutInflater.from(this).inflate(R.layout.dialog_saving, null, false);

        TextInputLayout savingNameLayout = savingDialog.findViewById(R.id.field_saving_name_layout);
        TextInputLayout savingValueLayout = savingDialog.findViewById(R.id.field_saving_value_layout);
        TextInputLayout savingGoalLayout = savingDialog.findViewById(R.id.field_saving_goal_layout);
        TextInputLayout savingNotesLayout = savingDialog.findViewById(R.id.field_saving_notes_layout);

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
        });
        dialog.show();
    }

    @Override
    public void OnOperationClick(SavingOperation operation, int position) {
        Saving selectedSaving = savings.get(position);
        if (operation.equals(SavingOperation.UNARCHIVE)) unarchiveSaving(selectedSaving);
    }
}