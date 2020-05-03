package pl.krakow.junczys.myexpenses;


import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.DecimalFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity: ";


    void valuesToFile(){

        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        /////////////////////////////////////////////////////////////////////////
        // Add new values to file or make new file with all values from sms inbox
        SaveListOfStringsToCsv saveListOfStringsToCsv = new SaveListOfStringsToCsv(getApplicationContext(),"my_expenses.csv");
        MessagesInBox messagesInBox = new MessagesInBox( getApplicationContext(), Uri.parse("content://sms/inbox"),"Alior Bank" );

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

        } else {

            // Make new file and read all values from inbox and write file

            List<String> messagesFromBank = messagesInBox.getListOfMessages();
            saveListOfStringsToCsv.WriteListOfStrings(messagesFromBank);

        }
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
        obj = my_expenses.callAttr("get_days_to_payday", 26);
        simpleReport.append(" ").append(obj.toInt()).append("</h2>");


        simpleReport.append("<h2>").append(getString(R.string.str_average_budget_per_day));
        obj = my_expenses.callAttr("get_average_budget_per_day", 26);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv_text_view = findViewById(R.id.id_tv_text_view);
        ProgressBar pb_load_data = findViewById(R.id.id_pb_load_data);


        tv_text_view.setVisibility(View.INVISIBLE);
        pb_load_data.setVisibility(View.VISIBLE);

        // Values
        valuesToFile();


        // Report
        StringBuilder stringBuilder;
        stringBuilder = simpleReport();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_text_view.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv_text_view.setText(Html.fromHtml(stringBuilder.toString()));
        }

        tv_text_view.setVisibility(View.VISIBLE);
        pb_load_data.setVisibility(View.INVISIBLE);

    }
}
