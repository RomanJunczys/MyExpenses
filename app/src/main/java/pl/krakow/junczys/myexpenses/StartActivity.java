package pl.krakow.junczys.myexpenses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Check if there is some settings in preferences. If so start application.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String str_choose_bank = sharedPref.getString("key_choose_bank_list_preference", "none");

        if( !(str_choose_bank != null && str_choose_bank.equals("none"))){

            Intent intent = new Intent(StartActivity.this, MyPermissionActivity.class);
            startActivity(intent);
            finish();

        } else {

            Button b_i_get_text_messages;
            Button b_i_dont_get_messages;

            b_i_get_text_messages = findViewById(R.id.id_b_i_get_text_messages);
            b_i_dont_get_messages = findViewById(R.id.id_b_i_dont_get_text_messages);

            b_i_get_text_messages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(StartActivity.this, SettingsActivity.class);
                    // Inform SettingsActivity it must set up bank settings and payday
                    intent.putExtra("which_settings", "start_settings");
                    startActivity(intent);
                    finish();
                }
            });

            b_i_dont_get_messages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://junczys.krakow.pl/index.php/22-moje-wydatki"));
                    startActivity(browserIntent);
                    finish();
                }
            });

        }
    }
}
