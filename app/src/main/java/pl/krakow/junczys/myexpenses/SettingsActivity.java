package pl.krakow.junczys.myexpenses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
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


    void setPreferencesByDefoult(String str_chose_bank){

        switch( str_chose_bank ){

            case "alior_bank":

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("key_bank_name_preference", "Alior Bank");
                editor.putString("key_word_before_value_preference", "wynosi ");
                editor.putString("key_word_after_value_preference", " PLN");
                editor.commit();

                break;

            case "mbank":
                break;

            default:
                finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            // If set bank
            // bank name read from preferences, setting in SettingAcitivity
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String str_choose_bank = sharedPref.getString("key_choose_bank_list_preference", "Alior Bank");

            if( str_choose_bank.equals("none") ){

                finish();

            } else {

                setPreferencesByDefoult( str_choose_bank );

                // In that place I have to choose the bank.
                Intent intent = new Intent(SettingsActivity.this, MyPermissionActivity.class);
                startActivity(intent);
            }




           // NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}