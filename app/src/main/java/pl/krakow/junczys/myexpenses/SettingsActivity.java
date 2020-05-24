package pl.krakow.junczys.myexpenses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

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



public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("SettingsActivity","yes");
    }

    void setPreferencesByDefault(String str_chose_bank){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
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
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String str_choose_bank = sharedPref.getString("key_choose_bank_list_preference", "none");

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


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

//            TODO when you set up first time, there is explanation for every preferences but when I chose bank it updates automatically
//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
//            boolean isSeted = sharedPref.getString("key_choose_bank_list_preference","none").equals("none") ? false : true;

            if(true) {

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

            }

            // when the dialog is shown to the user, the keyboard opens in numeric-only mode, so the user can enter only numbers into the EditText.
            EditTextPreference numberPreference = findPreference("key_payday_preference");
            if (numberPreference != null) {
                numberPreference.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            }
                        });
            }

        }
    }
}