package com.eipna.centsation.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import com.eipna.centsation.R;
import com.eipna.centsation.data.Currency;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.databinding.ActivityEditBinding;
import com.eipna.centsation.util.AlarmUtil;
import com.eipna.centsation.util.DateUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Locale;
import java.util.Objects;

public class EditActivity extends BaseActivity {

    private ActivityEditBinding binding;
    private SavingRepository savingRepository;

    private int IDExtra, isArchiveExtra;
    private String nameExtra, notesExtra;
    private double currentSavingExtra, goalExtra;
    private long deadlineExtra, selectedDeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        savingRepository = new SavingRepository(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String currentCurrencySymbol = Currency.getSymbol(preferences.getCurrency());
        binding.fieldSavingGoalLayout.setPrefixText(currentCurrencySymbol);

        IDExtra = getIntent().getIntExtra("id", -1);
        nameExtra = getIntent().getStringExtra("name");
        currentSavingExtra = getIntent().getDoubleExtra("current_saving", -1);
        goalExtra = getIntent().getDoubleExtra("goal", -1);
        notesExtra = getIntent().getStringExtra("notes");
        deadlineExtra = getIntent().getLongExtra("deadline", AlarmUtil.NO_ALARM);
        isArchiveExtra = getIntent().getIntExtra("is_archive", Saving.NOT_ARCHIVE);

        selectedDeadline = deadlineExtra;

        binding.fieldSavingDeadlineLayout.setVisibility(isArchiveExtra == Saving.IS_ARCHIVE ? View.GONE : View.VISIBLE);
        binding.fieldSavingNameText.setText(nameExtra);
        binding.fieldSavingGoalText.setText(String.format(Locale.getDefault(), "%.2f", goalExtra));
        binding.fieldSavingNotesText.setText(notesExtra);
        binding.fieldSavingDeadlineLayout.setEndIconVisible(false);

        if (deadlineExtra != AlarmUtil.NO_ALARM) {
            binding.fieldSavingDeadlineLayout.setEndIconVisible(true);
            binding.fieldSavingDeadlineText.setText(DateUtil.getStringDate(deadlineExtra, "MM/dd/yyyy"));
        }

        binding.fieldSavingDeadlineText.setOnClickListener(v -> showDeadlineDialog());
        binding.fieldSavingDeadlineLayout.setEndIconOnClickListener(v -> {
            binding.fieldSavingDeadlineText.setText("");
            binding.fieldSavingDeadlineLayout.setEndIconVisible(false);
        });
    }

    private void showDeadlineDialog() {
        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(calendarConstraints.build())
                .setTitleText("Select deadline date")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDeadline = selection;
            binding.fieldSavingDeadlineText.setText(DateUtil.getStringDate(selection, "MM/dd/yyyy"));
            binding.fieldSavingDeadlineLayout.setEndIconVisible(true);
        });
        datePicker.show(getSupportFragmentManager(), null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId() == R.id.save) editSaving();
        return true;
    }

    private void editSaving() {
        String nameText = Objects.requireNonNull(binding.fieldSavingNameText.getText()).toString();
        String goalText = Objects.requireNonNull(binding.fieldSavingGoalText.getText()).toString();
        String notesText = Objects.requireNonNull(binding.fieldSavingNotesText.getText()).toString();
        String deadlineText = Objects.requireNonNull(binding.fieldSavingDeadlineText.getText()).toString();

        if (!nameText.isEmpty() && !goalText.isEmpty()) {
            double goal = Double.parseDouble(goalText);

            Saving editedSaving = new Saving();
            editedSaving.setID(IDExtra);
            editedSaving.setName(nameText);
            editedSaving.setCurrentSaving(currentSavingExtra);
            editedSaving.setGoal(goal);
            editedSaving.setNotes(notesText);
            editedSaving.setIsArchived(Saving.NOT_ARCHIVE);
            editedSaving.setDeadline(selectedDeadline);

            if (deadlineText.isEmpty()) {
                AlarmUtil.cancel(this, editedSaving);
                editedSaving.setDeadline(AlarmUtil.NO_ALARM);
            } else {
                AlarmUtil.set(this, editedSaving);
            }

            savingRepository.update(editedSaving);
            setResult(RESULT_OK);
            finish();
        }

        binding.fieldSavingNameLayout.setError(nameText.isEmpty() ? getString(R.string.field_error_required) : null);
        binding.fieldSavingGoalLayout.setError(goalText.isEmpty() ? getString(R.string.field_error_required) : null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_saving, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}