package com.eipna.centsation.ui.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.eipna.centsation.R;
import com.eipna.centsation.databinding.ActivityCreateBinding;
import com.eipna.centsation.util.DateUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.fieldSavingDeadlineLayout.setEndIconVisible(false);
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
                .setTitleText("Select deadline date")
                .setCalendarConstraints(calendarConstraints.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            binding.fieldSavingDeadlineText.setText(DateUtil.getStringDate(selection, "MM/dd/yyyy"));
            binding.fieldSavingDeadlineLayout.setEndIconVisible(true);
        });
        datePicker.show(getSupportFragmentManager(), null);
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