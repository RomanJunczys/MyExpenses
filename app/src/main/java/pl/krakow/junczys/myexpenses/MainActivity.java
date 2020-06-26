package pl.krakow.junczys.myexpenses;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
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
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TextView tv_text_view = findViewById(R.id.id_tv_text_view);
//        ProgressBar pb_load_data = findViewById(R.id.id_pb_load_data);


//        tv_text_view.setVisibility(View.INVISIBLE);
//        pb_load_data.setVisibility(View.VISIBLE);

        // Values
        int howManyRecords = valuesToFile();
        if(  howManyRecords > 0  ){


            // Report
            if( howManyRecords == 1 ) {

                verySimpleReportVer2();

            } else {

                // TODO if user wants to show more complatated report then do it if there are more records in file
                verySimpleReportVer2();
                // stringBuilder = verySimpleReport();
                // stringBuilder = simpleReport();

            }


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                tv_text_view.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_COMPACT));
//            } else {
//                tv_text_view.setText(Html.fromHtml(stringBuilder.toString()));
//            }
//
//            tv_text_view.setVisibility(View.VISIBLE);
//            pb_load_data.setVisibility(View.INVISIBLE);

        }else{

//            tv_text_view.setText("I do not find any mesage form Your Bank with account balance");
//            tv_text_view.setVisibility(View.VISIBLE);
//            pb_load_data.setVisibility(View.INVISIBLE);


        }

    }


    int valuesToFile(){

        int howManyMessages = 0;

        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        /////////////////////////////////////////////////////////////////////////
        // Add new values to file or make new file with all values from sms inbox
        SaveListOfStringsToCsv saveListOfStringsToCsv = new SaveListOfStringsToCsv(getApplicationContext(),"my_expenses.csv");


        // bank name read from preferences, setting in SettingAcitivity
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String str_in_address = sharedPref.getString("key_bank_name_preference", "Alior Bank");

        MessagesInBox messagesInBox = new MessagesInBox( getApplicationContext(), Uri.parse("content://sms/inbox"), str_in_address );

        if( saveListOfStringsToCsv.fileExist() ){

            // Add new value for dates after last date in file
            Python python = Python.getInstance();
            PyObject my_expenses = python.getModule("my_expenses");
            my_expenses.callAttr("set_file_name",saveListOfStringsToCsv.getFile());
            PyObject obj = my_expenses.callAttr("get_last_date_in_file");

            // Now create a SimpleDateFormat object.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // Now create a start time for this date in order to setup the filter.
            Date dateStart = null;
            try {
                dateStart = formatter.parse(obj.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }


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

            obj = my_expenses.callAttr("get_how_many_records_in_file");
            howManyMessages = obj.toInt();


        } else {

            // Make new file and read all values from inbox and write file

            List<String> messagesFromBank = messagesInBox.getListOfMessages();

            if( !messagesFromBank.isEmpty() ){

                saveListOfStringsToCsv.WriteListOfStrings(messagesFromBank);
                Python python = Python.getInstance();
                PyObject my_expenses = python.getModule("my_expenses");
                my_expenses.callAttr("set_file_name",saveListOfStringsToCsv.getFile());
                PyObject obj = my_expenses.callAttr("get_how_many_records_in_file");
                howManyMessages = obj.toInt();

            } else {

                // Message Box There is no message from Bank

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Info");
                alertDialog.setMessage("There is no message from your Bank with account balance");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                howManyMessages = 0;
            }

        }
        Log.d(TAG, "how Many Massages"+howManyMessages);
        return howManyMessages;
    }


    void verySimpleReportVer2(){

        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        SaveListOfStringsToCsv saveListOfStringsToCsv = new SaveListOfStringsToCsv(getApplicationContext(),"my_expenses.csv");

        Python python = Python.getInstance();
        PyObject my_expenses = python.getModule("my_expenses");


        my_expenses.callAttr("set_file_name", saveListOfStringsToCsv.getFile());

        PyObject obj;



        // ACCOUNT BALANCE
        TextView tv_account_balance_value = findViewById(R.id.id_tv_account_balance);

        // Format currency
//        NumberFormat format = NumberFormat.getCurrencyInstance();
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setMaximumFractionDigits(0);

        obj = my_expenses.callAttr("get_current_account_balance");
        float f_account_balance = obj.toFloat();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.str_account_balance));
        stringBuilder.append(": ");
        stringBuilder.append(format.format(obj.toFloat()));
        tv_account_balance_value.setText(stringBuilder);


        // LAST PENNY account_balace + recources - costs
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String str_resources = sharedPref.getString("key_resources_preference", "0");
        float f_resources = Float.parseFloat(str_resources);

        String str_costs = sharedPref.getString("key_costs_preference", "0");
        float f_costs = Float.parseFloat(str_costs);

        float f_last_penny = f_account_balance + f_resources - f_costs;

        TextView tv_last_penny = findViewById(R.id.id_tv_last_penny_value);
        tv_last_penny.setText(format.format(f_last_penny));




        // Payday read from preferences
        String str_payday = sharedPref.getString("key_payday_preference", "26");

        Integer int_payday;
        try {
            int_payday = Integer.parseInt(str_payday);
        } catch (NumberFormatException e) {
            // TODO use such a graphical interface to set this preferences to make sure it is integer form 1 to 31
            int_payday = 26;
        }

        TextView tv_days_to_payday_value = findViewById(R.id.id_tv_days_to_payday_value);

        obj = my_expenses.callAttr("get_days_to_payday", int_payday);
        int int_days_to_payday = obj.toInt();
        tv_days_to_payday_value.setText( String.valueOf(obj.toInt()) );


        // DAILY BUDGET
        TextView tv_daily_budget_value = findViewById(R.id.id_tv_daily_budget_value);
        tv_daily_budget_value.setText(format.format(f_last_penny/int_days_to_payday));

        TextView tv_last_updated = findViewById(R.id.id_tv_last_updated);
        stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.str_last_update));
        stringBuilder.append(": ");

        obj = my_expenses.callAttr("get_str_today");
        stringBuilder.append(obj.toString());

        tv_last_updated.setText(stringBuilder);


        TextView tv_resources = findViewById(R.id.id_tv_resources);
        tv_resources.setText(format.format(f_resources));

        tv_resources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("which_settings", "resources");  // pass your values and retrieve them in the other Activity using AnyKeyName
                startActivity(intent);


            }
        });


        TextView tv_costs = findViewById(R.id.id_tv_costs);
        tv_costs.setText(format.format(f_costs));

        tv_costs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("which_settings", "costs");  // pass your values and retrieve them in the other Activity using AnyKeyName
                startActivity(intent);

            }
        });





//        StringBuilder simpleReport = new StringBuilder();
//
//        simpleReport.append("<h1>").append(getString(R.string.str_report_on));
//
//        PyObject obj = my_expenses.callAttr("get_str_today");
//
//        obj = my_expenses.callAttr("get_str_today");
//        simpleReport.append(" ").append(obj.toString()).append("\n</h1>");
//        simpleReport.append("<br>");
//
//
//        simpleReport.append("<h2>").append(getString(R.string.str_current_account_balance));
//
//        // Format currency
//        NumberFormat format = NumberFormat.getCurrencyInstance();
//        format.setMaximumFractionDigits(0);
//
//        obj = my_expenses.callAttr("get_current_account_balance");
//        simpleReport.append(" ").append(format.format(obj.toFloat())).append("</h2>");
//
//        simpleReport.append("<h2>").append(getString(R.string.str_up_to_the_payday));
//
//        // Payday read from preferences
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        String str_payday = sharedPref.getString("key_payday_preference", "26");
//        Integer int_payday;
//        try {
//            int_payday = Integer.parseInt(str_payday);
//        } catch (NumberFormatException e) {
//            int_payday = 26;
//        }
//
//        obj = my_expenses.callAttr("get_days_to_payday", int_payday);
//        simpleReport.append(" ").append(obj.toInt()).append("</h2>");
//
//
//        simpleReport.append("<h2>").append(getString(R.string.str_average_budget_per_day));
//        obj = my_expenses.callAttr("get_average_budget_per_day", int_payday);
//        simpleReport.append(" ").append(format.format(obj.toFloat())).append("</h2>");
//
//        // TODO later lyout in .xml
//        simpleReport.append("<br>");
//        simpleReport.append("<br>");
//        simpleReport.append("<br>");
//        simpleReport.append("<br>");
//        simpleReport.append("<br>");
//        simpleReport.append("<br>");
//        simpleReport.append("<br>");
//
//        return simpleReport;
    }




    StringBuilder verySimpleReport(){

        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        SaveListOfStringsToCsv saveListOfStringsToCsv = new SaveListOfStringsToCsv(getApplicationContext(),"my_expenses.csv");

        Python python = Python.getInstance();
        PyObject my_expenses = python.getModule("my_expenses");
        my_expenses.callAttr("set_file_name",saveListOfStringsToCsv.getFile());

        StringBuilder simpleReport = new StringBuilder();

        simpleReport.append("<h1>").append(getString(R.string.str_report_on));

        PyObject obj = my_expenses.callAttr("get_str_today");

        obj = my_expenses.callAttr("get_str_today");
        simpleReport.append(" ").append(obj.toString()).append("\n</h1>");
        simpleReport.append("<br>");


        simpleReport.append("<h2>").append(getString(R.string.str_current_account_balance));

        // Format currency
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);

        obj = my_expenses.callAttr("get_current_account_balance");
        simpleReport.append(" ").append(format.format(obj.toFloat())).append("</h2>");

        simpleReport.append("<h2>").append(getString(R.string.str_up_to_the_payday));

        // Payday read from preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String str_payday = sharedPref.getString("key_payday_preference", "26");
        Integer int_payday;
        try {
            int_payday = Integer.parseInt(str_payday);
        } catch (NumberFormatException e) {
            int_payday = 26;
        }

        obj = my_expenses.callAttr("get_days_to_payday", int_payday);
        simpleReport.append(" ").append(obj.toInt()).append("</h2>");


        simpleReport.append("<h2>").append(getString(R.string.str_average_budget_per_day));
        obj = my_expenses.callAttr("get_average_budget_per_day", int_payday);
        simpleReport.append(" ").append(format.format(obj.toFloat())).append("</h2>");

        // TODO later lyout in .xml
        simpleReport.append("<br>");
        simpleReport.append("<br>");
        simpleReport.append("<br>");
        simpleReport.append("<br>");
        simpleReport.append("<br>");
        simpleReport.append("<br>");
        simpleReport.append("<br>");

        return simpleReport;
    }


    StringBuilder simpleReport(){

        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        SaveListOfStringsToCsv saveListOfStringsToCsv = new SaveListOfStringsToCsv(getApplicationContext(),"my_expenses.csv");

        Python python = Python.getInstance();
        PyObject my_expenses = python.getModule("my_expenses");
        my_expenses.callAttr("set_file_name",saveListOfStringsToCsv.getFile());

        StringBuilder simpleReport = new StringBuilder();

        simpleReport.append("<h1>").append(getString(R.string.str_report_on));

        PyObject obj = my_expenses.callAttr("get_str_today");

        obj = my_expenses.callAttr("get_str_today");
        simpleReport.append(" ").append(obj.toString()).append("\n</h1>");
        simpleReport.append("<br>");


        simpleReport.append("<h2>").append(getString(R.string.str_current_account_balance));

        obj = my_expenses.callAttr("get_current_account_balance");
        simpleReport.append(obj.toFloat()).append("</h2>");

        simpleReport.append("<h2>").append(getString(R.string.str_up_to_the_payday));

        // Payday read from preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String str_payday = sharedPref.getString("key_payday_preference", "26");
        Integer int_payday;
        try {
            int_payday = Integer.parseInt(str_payday);
        } catch (NumberFormatException e) {
            int_payday = 26;
        }

        obj = my_expenses.callAttr("get_days_to_payday", int_payday);
        simpleReport.append(" ").append(obj.toInt()).append("</h2>");


        simpleReport.append("<h2>").append(getString(R.string.str_average_budget_per_day));
        obj = my_expenses.callAttr("get_average_budget_per_day", int_payday);
        DecimalFormat df = new DecimalFormat("#.##");
        simpleReport.append(" ").append(df.format(obj.toFloat())).append("</h2>");
        simpleReport.append("<br>");

        // Construct sentence like this
        // You have spent 7000 during the last 7 days, i.e. 1000 on average.
        simpleReport.append("<h3>").append(getString(R.string.str_you_have_spent));
        obj = my_expenses.callAttr("get_expenses_from_last_seven_days");
        simpleReport.append(" ").append(df.format(obj.toFloat()));
        simpleReport.append(" ").append(getString(R.string.str_during_the_last_7_days_ie));
        simpleReport.append(" ").append(df.format(obj.toFloat()/7.0));
        simpleReport.append(" ").append(getString(R.string.str_on_average));
        simpleReport.append("</h2>");

        return simpleReport;
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
