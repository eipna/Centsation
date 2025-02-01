package com.eipna.centsation;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.eipna.centsation.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;

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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title_add_saving)
                .setIcon(R.drawable.ic_add)
                .setNegativeButton(R.string.dialog_button_close, null)
                .setPositiveButton(R.string.dialog_button_add, null);

        Dialog dialog = builder.create();
        dialog.show();
    }
}