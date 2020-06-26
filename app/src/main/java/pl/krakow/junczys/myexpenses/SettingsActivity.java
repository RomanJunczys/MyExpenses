package pl.krakow.junczys.myexpenses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceCategory;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

import static androidx.preference.PreferenceManager.*;


public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    String TAG = "SettingsActivity: ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // new bundle
        Bundle bundle = new Bundle();
        // set up which_settings key
        bundle.putString("which_settings", Objects.requireNonNull(getIntent().getExtras()).getString("which_settings"));
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, settingsFragment )
                .commit();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupSharedPreferences();

    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

   void setPreferencesByDefault(String str_chose_bank){

        SharedPreferences sharedPref = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        switch( str_chose_bank ){

            case "alior_bank":

                editor.putString("key_bank_name_preference", "Alior Bank");
                editor.putString("key_word_before_value_preference", "wynosi ");
                editor.putString("key_word_after_value_preference", " PLN");
                editor.apply();

                break;

            case "mbank":

                editor.putString("key_bank_name_preference", "mBank");
                editor.putString("key_word_before_value_preference", "wynosi ");
                editor.putString("key_word_after_value_preference", " PLN");
                editor.apply();

                break;

            case "none":

                editor.apply();
                break;

            default:

                finish();
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {

            // If set bank
            // bank name read from preferences, setting in SettingAcitivity
            SharedPreferences sharedPref = getDefaultSharedPreferences(this);
            String str_choose_bank = sharedPref.getString("key_choose_bank_list_preference", "none");

            assert str_choose_bank != null;
            if( str_choose_bank.equals("none") ){

                finish();

            } else {

                setPreferencesByDefault( str_choose_bank );

                // In that place I have to choose the bank.
                Intent intent = new Intent(SettingsActivity.this, StartActivity.class);
                startActivity(intent);
            }

           NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d(TAG, "on shared preference changed");

//        if (key.equals("key_bank_name_preference")) {
//            Log.d(TAG, "key bank name prefernence");
//        }

        Intent intent = new Intent(this, SettingsActivity.class);
        // Inform SettingsActivity it must set up bank settings and payday
        intent.putExtra("which_settings", "after_change");
        startActivity(intent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

//            TODO when you set up first time, there is explanation for every preferences but when I chose bank it updates automatically
//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
//            boolean isSeted = sharedPref.getString("key_choose_bank_list_preference","none").equals("none") ? false : true;

            String str_which_settings;
            str_which_settings = "";

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                str_which_settings = bundle.getString("which_settings", "");
            }

            if( str_which_settings.equals("resources") ){

                // set all invisible besides resources

                PreferenceCategory preferenceCategory = findPreference("key_preference_category_name_of_bank");
                if (preferenceCategory != null) {
                    preferenceCategory.setVisible(false);
                }

                preferenceCategory = findPreference("key_preference_category_payday");
                if (preferenceCategory != null) {
                    preferenceCategory.setVisible(false);
                }

                EditTextPreference numberPreference = findPreference("key_costs_preference");
                if (numberPreference != null) {
                    numberPreference.setVisible(false);
                }

                numberPreference = findPreference("key_resources_preference");
                if (numberPreference != null) {
                    numberPreference.setOnBindEditTextListener(
                            new EditTextPreference.OnBindEditTextListener() {
                                @Override
                                public void onBindEditText(@NonNull EditText editText) {
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    editText.setSelection(editText.getText().length());
                                }
                            });
                }

            } else if (str_which_settings.equals("bank_payday_settings")){

                // set visible only the bank and the payday setting

                PreferenceCategory preferenceCategory = findPreference("key_preference_category_name_of_bank");
                if (preferenceCategory != null) {
                    preferenceCategory.setVisible(true);
                }

                preferenceCategory = findPreference("key_preference_category_payday");
                if (preferenceCategory != null) {
                    preferenceCategory.setVisible(true);
                }

                // when the dialog is shown to the user, the keyboard opens in numeric-only mode, so the user can enter only numbers into the EditText.
                EditTextPreference numberPreference = findPreference("key_payday_preference");
                if (numberPreference != null) {
                    numberPreference.setOnBindEditTextListener(
                            new EditTextPreference.OnBindEditTextListener() {
                                @Override
                                public void onBindEditText(@NonNull EditText editText) {
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    editText.setSelection(editText.getText().length());
                                }
                            });
                }

                preferenceCategory = findPreference("key_preference_category_resources_costs");
                if (preferenceCategory != null) {
                    preferenceCategory.setVisible(false);
                }

            } else if (str_which_settings.equals("after_change")) {

                ListPreference listPreference = findPreference("key_choose_bank_list_preference");
                if (listPreference != null) {
                    listPreference.setVisible(true);
                    listPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
                }

                EditTextPreference editTextPreference = findPreference("key_payday_preference");
                if (editTextPreference != null) {
                    editTextPreference.setVisible(true);
                    editTextPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
                }


                // when the dialog is shown to the user, the keyboard opens in numeric-only mode, so the user can enter only numbers into the EditText.
                EditTextPreference numberPreference = findPreference("key_payday_preference");
                if (numberPreference != null) {
                    numberPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
                    numberPreference.setOnBindEditTextListener(
                            new EditTextPreference.OnBindEditTextListener() {
                                @Override
                                public void onBindEditText(@NonNull EditText editText) {
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                }
                            });
                }

                numberPreference = findPreference("key_resources_preference");
                if (numberPreference != null) {
                    numberPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
                    numberPreference.setOnBindEditTextListener(
                            new EditTextPreference.OnBindEditTextListener() {
                                @Override
                                public void onBindEditText(@NonNull EditText editText) {
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    editText.setSelection(editText.getText().length());
                                }
                            });
                }

                numberPreference = findPreference("key_costs_preference");
                if (numberPreference != null) {
                    numberPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
                    numberPreference.setOnBindEditTextListener(
                            new EditTextPreference.OnBindEditTextListener() {
                                @Override
                                public void onBindEditText(@NonNull EditText editText) {
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    editText.setSelection(editText.getText().length());
                                }
                            });
                }
            }

        }
    }
}