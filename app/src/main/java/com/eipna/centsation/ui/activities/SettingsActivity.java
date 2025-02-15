package com.eipna.centsation.ui.activities;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.eipna.centsation.R;
import com.eipna.centsation.data.Currency;
import com.eipna.centsation.data.Theme;
import com.eipna.centsation.databinding.ActivitySettingsBinding;
import com.eipna.centsation.util.PreferenceUtil;
import com.eipna.centsation.util.ThemeUtil;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Drawable drawable = MaterialShapeDrawable.createWithElevationOverlay(this);
        binding.appBar.setStatusBarForeground(drawable);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        PreferenceUtil preferences;

        private ListPreference listTheme;
        private ListPreference listCurrency;

        private SwitchPreferenceCompat switchDynamicColors;

        private Preference appVersion;
        private Preference appLicense;

        private int easterEggCounter;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_main, rootKey);
            setPreferences();

            listCurrency.setEntries(Currency.getNames());
            listCurrency.setEntryValues(Currency.getCodes());
            listCurrency.setValue(preferences.getCurrency());
            listCurrency.setSummary(Currency.getName(preferences.getCurrency()));
            listCurrency.setOnPreferenceChangeListener((preference, currency) -> {
                preferences.setCurrency((String) currency);
                listCurrency.setSummary(Currency.getName((String) currency));
                return true;
            });

            switchDynamicColors.setVisible(DynamicColors.isDynamicColorAvailable());
            switchDynamicColors.setChecked(preferences.isDynamicColors());
            switchDynamicColors.setOnPreferenceChangeListener((preference, isChecked) -> {
                preferences.setDynamicColors((boolean) isChecked);
                requireActivity().recreate();
                return true;
            });

            listTheme.setEntries(Theme.getValues());
            listTheme.setEntryValues(Theme.getValues());
            listTheme.setSummary(preferences.getTheme());
            listTheme.setValue(preferences.getTheme());
            listTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedTheme = (String) newValue;
                ThemeUtil.set(selectedTheme);
                preferences.setTheme(selectedTheme);
                return true;
            });

            try {
                PackageManager packageManager = requireContext().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(requireContext().getPackageName(), 0);
                appVersion.setSummary(packageInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            appVersion.setOnPreferenceClickListener(preference -> {
                easterEggCounter++;
                if (easterEggCounter == 7) {
                    String easterEggMessage = getString(R.string.app_easter_egg);
                    Toast.makeText(requireContext(), easterEggMessage, Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            appLicense.setOnPreferenceClickListener(preference -> {
                showLicenseDialog();
                return true;
            });
        }

        private void setPreferences() {
            preferences = new PreferenceUtil(requireContext());
            listCurrency = findPreference("currency");
            listTheme = findPreference("theme");
            switchDynamicColors = findPreference("dynamic_colors");
            appVersion = findPreference("app_version");
            appLicense = findPreference("app_license");
        }

        private void showLicenseDialog() {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.preference_app_license)
                    .setMessage(readLicenseFromAssets())
                    .setPositiveButton(R.string.dialog_button_close, null);

            Dialog dialog = builder.create();
            dialog.show();
        }

        private String readLicenseFromAssets() {
            StringBuilder stringBuilder = new StringBuilder();
            AssetManager assetManager = requireContext().getAssets();

            try (InputStream inputStream = assetManager.open("license.txt")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            } catch (IOException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return stringBuilder.toString();
        }
    }
}