package com.eipna.centsation.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eipna.centsation.R;
import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.databinding.ActivityCreateBinding;
import com.eipna.centsation.util.AlarmUtil;
import com.eipna.centsation.util.DateUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private SavingRepository savingRepository;
    private long selectedDeadline;

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showDeadlineDialog();
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        savingRepository = new SavingRepository(this);
        selectedDeadline = 0;

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.fieldSavingDeadlineLayout.setEndIconVisible(false);
        binding.fieldSavingDeadlineText.setOnClickListener(v -> hasNotificationPermission());
        binding.fieldSavingDeadlineLayout.setEndIconOnClickListener(v -> {
            binding.fieldSavingDeadlineText.setText("");
            binding.fieldSavingDeadlineLayout.setEndIconVisible(false);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId() == R.id.save) createSaving();
        return true;
    }

    private void createSaving() {
        String nameText = Objects.requireNonNull(binding.fieldSavingNameText.getText()).toString();
        String currentSavingText = Objects.requireNonNull(binding.fieldSavingCurrentSavingText.getText()).toString();
        String goalText = Objects.requireNonNull(binding.fieldSavingGoalText.getText()).toString();
        String notesText = Objects.requireNonNull(binding.fieldSavingNotesText.getText()).toString();
        String deadlineText = Objects.requireNonNull(binding.fieldSavingDeadlineText.getText()).toString();

        if (!nameText.isEmpty() && !currentSavingText.isEmpty() && !goalText.isEmpty()) {
            double currentSaving = Double.parseDouble(currentSavingText);
            double goal = Double.parseDouble(goalText);

            if (currentSaving > goal) {
                binding.fieldSavingGoalLayout.setError(getString(R.string.field_error_lower_goal));
                return;
            }

            Saving createdSaving = new Saving();
            createdSaving.setName(nameText);
            createdSaving.setCurrentSaving(currentSaving);
            createdSaving.setGoal(goal);
            createdSaving.setNotes(notesText);
            createdSaving.setIsArchived(Saving.NOT_ARCHIVE);
            createdSaving.setDeadline(selectedDeadline);

            if (!deadlineText.isEmpty()) {
                AlarmUtil.set(this, createdSaving);
            }

            savingRepository.create(createdSaving);
            setResult(RESULT_OK);
            finish();
        }

        binding.fieldSavingNameLayout.setError(nameText.isEmpty() ? getString(R.string.field_error_required) : null);
        binding.fieldSavingCurrentSavingLayout.setError(currentSavingText.isEmpty() ? getString(R.string.field_error_required) : null);
        binding.fieldSavingGoalLayout.setError(goalText.isEmpty() ? getString(R.string.field_error_required) : null);
    }

    private void hasNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            showDeadlineDialog();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Snackbar.make(binding.getRoot(), "This application requires notification permission for this feature to work.", Snackbar.LENGTH_SHORT)
                    .setAction("Grant", v -> {
                        Intent intent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        } else {
                            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                        }
                        startActivity(intent);
                    }).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
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