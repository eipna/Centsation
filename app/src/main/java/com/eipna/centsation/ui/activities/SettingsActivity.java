package com.eipna.centsation.ui.activities;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.eipna.centsation.R;
import com.eipna.centsation.data.Currency;
import com.eipna.centsation.data.Theme;
import com.eipna.centsation.databinding.ActivitySettingsBinding;
import com.eipna.centsation.util.SharedPreferencesUtil;
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

        private SharedPreferencesUtil sharedPreferencesUtil;

        private ListPreference listTheme;
        private ListPreference listCurrency;

        private SwitchPreferenceCompat switchDynamicColors;

        private Preference appVersion;
        private Preference appLicense;

        private int easterEggCounter;

        private String themePrefs;
        private String currencyPrefs;
        private boolean dynamicColorsPrefs;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_main, rootKey);
            setPreferences();

            listCurrency.setEntries(Currency.getNames());
            listCurrency.setEntryValues(Currency.getCodes());
            listCurrency.setValue(currencyPrefs);
            listCurrency.setSummary(Currency.getName(currencyPrefs));
            listCurrency.setOnPreferenceChangeListener((preference, newValue) -> {
                sharedPreferencesUtil.setString("currency", (String) newValue);
                listCurrency.setSummary(Currency.getName((String) newValue));
                return true;
            });

            switchDynamicColors.setVisible(DynamicColors.isDynamicColorAvailable());
            switchDynamicColors.setChecked(dynamicColorsPrefs);
            switchDynamicColors.setOnPreferenceChangeListener((preference, newValue) -> {
                sharedPreferencesUtil.setBoolean("dynamic_colors", (boolean) newValue);
                requireActivity().recreate();
                return true;
            });

            listTheme.setEntries(Theme.getValues());
            listTheme.setEntryValues(Theme.getValues());
            listTheme.setSummary(themePrefs);
            listTheme.setValue(themePrefs);
            listTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedTheme = (String) newValue;
                if (selectedTheme.equals(Theme.LIGHT.VALUE)) {
                    sharedPreferencesUtil.setString("theme", Theme.LIGHT.VALUE);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (selectedTheme.equals(Theme.DARK.VALUE)) {
                    sharedPreferencesUtil.setString("theme", Theme.DARK.VALUE);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (selectedTheme.equals(Theme.BATTERY.VALUE)) {
                    sharedPreferencesUtil.setString("theme", Theme.BATTERY.VALUE);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                } else if (selectedTheme.equals(Theme.SYSTEM.VALUE)) {
                    sharedPreferencesUtil.setString("theme", Theme.SYSTEM.VALUE);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
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
            sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());

            currencyPrefs = sharedPreferencesUtil.getString("currency", Currency.UNITED_STATES_DOLLAR.CODE);
            themePrefs = sharedPreferencesUtil.getString("theme", Theme.SYSTEM.VALUE);
            dynamicColorsPrefs = sharedPreferencesUtil.getBoolean("dynamic_colors", false);

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
                    .setIcon(R.drawable.ic_license)
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