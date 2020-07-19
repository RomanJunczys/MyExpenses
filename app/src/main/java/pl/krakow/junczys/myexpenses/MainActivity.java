package pl.krakow.junczys.myexpenses;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity: ";
    Expenses expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expenses = new Expenses( getApplicationContext() );
        expenses.setFile("my_expenses.csv");


        // Update your account balance
        int howManyRecords = updateYourAccountBalance();


        if(  howManyRecords > 0  ){


            // Report
            verySimpleReportVer3();

        } else {

            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtra("which_settings", "after_change");
            startActivity(intent);
            finish();

        }

    }


    int updateYourAccountBalance(){

        int howManyMessages;

        /////////////////////////////////////////////////////////////////////////
        // Add new values to file or make new file with all values from sms inbox
        SaveListOfStringsToCsv saveListOfStringsToCsv = new SaveListOfStringsToCsv(getApplicationContext(),"my_expenses.csv");


        // bank name read from preferences, setting in SettingAcitivity
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String str_in_address = sharedPref.getString("key_bank_name_preference", "");

        MessagesInBox messagesInBox = new MessagesInBox( getApplicationContext(), Uri.parse("content://sms/inbox"), str_in_address );

        if( saveListOfStringsToCsv.fileExist() ){

            expenses.readFile();
            Date dateStart = expenses.getLastUpdate();


            // Add one day to filter query - after that day, then I have in file.
            Calendar c = Calendar.getInstance();
            c.setTime(dateStart);
            c.add(Calendar.DATE, 1);
            dateStart = c.getTime();


            // Now create the filter and query the messages.
            String filter = "date>=" + dateStart.getTime();
            List<String> messagesFromBankAfterDate = messagesInBox.getListOfMessagesAfterDate(filter);

            if( !messagesFromBankAfterDate.isEmpty() ) {
                saveListOfStringsToCsv.AppendListOfStrings(messagesFromBankAfterDate);
            }

            howManyMessages = expenses.getSize();


        } else {

            // Make new file and read all values from inbox and write file

            List<String> messagesFromBank = messagesInBox.getListOfMessages();

            if( !messagesFromBank.isEmpty() ){

                saveListOfStringsToCsv.WriteListOfStrings(messagesFromBank);

                expenses = new Expenses(getApplicationContext());
                expenses.setFile("my_expenses.csv");
                expenses.readFile();
                howManyMessages = expenses.getSize();

            } else {

                // Message Box:  There is no message from Bank
//                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//                alertDialog.setTitle("Info");
//                alertDialog.setMessage("There is no message from your Bank with account balance");
//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alertDialog.show();

                howManyMessages = 0;
            }

        }
        Log.d(TAG, "how Many Massages"+howManyMessages);
        return howManyMessages;

    }


    void verySimpleReportVer3(){


        SaveListOfStringsToCsv saveListOfStringsToCsv = new SaveListOfStringsToCsv(getApplicationContext(),"my_expenses.csv");
        saveListOfStringsToCsv.getFile();



       // Format currency
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setMaximumFractionDigits(0);

        StringBuilder stringBuilder = new StringBuilder();



        // ACCOUNT BALANCE
        TextView tv_account_balance_value = findViewById(R.id.id_tv_account_balance);

        float f_account_balance = expenses.getCurrentAccountBalance();
        stringBuilder.append(getString(R.string.str_account_balance));
        stringBuilder.append(": ");
        stringBuilder.append(format.format(f_account_balance));
        tv_account_balance_value.setText(stringBuilder);


        // LAST PENNY account_balace + recources - costs
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String str_resources = sharedPref.getString("key_resources_preference", "0");
        float f_resources = 0;
        if (str_resources != null) {
            f_resources = Float.parseFloat(str_resources);
        }

        String str_costs = sharedPref.getString("key_costs_preference", "0");
        float f_costs = 0;
        if (str_costs != null) {
            f_costs = Float.parseFloat(str_costs);
        }

        float f_last_penny = f_account_balance + f_resources - f_costs;

        TextView tv_last_penny = findViewById(R.id.id_tv_last_penny_value);
        tv_last_penny.setText(format.format(f_last_penny));




        // Payday read from preferences
        String str_payday = sharedPref.getString("key_payday_preference", "26");

        int int_payday=0;

        try {

            if (str_payday != null) {
                int_payday = Integer.parseInt(str_payday);
            }

        } catch (NumberFormatException e) {

            // TODO use such a graphical interface to set this preferences to make sure it is integer form 1 to 31
            int_payday = 28;

        }

        TextView tv_days_to_payday_value = findViewById(R.id.id_tv_days_to_payday_value);

        long daysToPayday = expenses.getDaysToPayday(int_payday);
        tv_days_to_payday_value.setText( String.valueOf(daysToPayday) );


        // DAILY BUDGET
        TextView tv_daily_budget_value = findViewById(R.id.id_tv_daily_budget_value);
        tv_daily_budget_value.setText(format.format(f_last_penny/daysToPayday));

        TextView tv_last_updated = findViewById(R.id.id_tv_last_updated);
        stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.str_last_update));
        stringBuilder.append(": ");

        Date lastUpdate = expenses.getLastUpdate();

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        stringBuilder.append(dateFormat.format(lastUpdate));

        tv_last_updated.setText(stringBuilder);


        TextView tv_resources = findViewById(R.id.id_tv_resources);
        tv_resources.setText(format.format(f_resources));

        tv_resources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("which_settings", "resources");
                startActivity(intent);
                finish();


            }
        });


        TextView tv_costs = findViewById(R.id.id_tv_costs);
        tv_costs.setText(format.format(f_costs));

        tv_costs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("which_settings", "costs");
                startActivity(intent);
                finish();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.id_action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("which_settings", "after_change");
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
