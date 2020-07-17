package pl.krakow.junczys.myexpenses;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyExpenses {
    static final String TAG = "MyExpenses";

    Context context;
    File csvFile;
    ArrayList<AccountBalance> accountBalances = new ArrayList<AccountBalance>();

    public MyExpenses(Context context){
        this.context = context;
    }

    public void setFile(String fileName){
        csvFile = new File( this.context.getExternalFilesDir(null), fileName);
    }

    int readFile(){


        try {
            CSVReader reader = new CSVReader(new FileReader(csvFile.getAbsolutePath()));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {


                // nextLine[] is an array of values from the line
                Log.d(TAG, nextLine[0] + " " +  nextLine[1] );

                AccountBalance accountBalance = new AccountBalance();


                // parse date from nextLine[0]
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy");
                try {
                    Date date = format.parse(nextLine[0]);
                    accountBalance.date = date;
                    Log.d(TAG, date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // parse value from nextLine[1]

                try {
                    float value = Float.parseFloat(nextLine[1]);
                    accountBalance.value = value;
                } catch(NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                accountBalances.add(accountBalance);


            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }

        return accountBalances.size();

    }


    // It means the last known.
    float getCurrentAccountBalance(){
        int last = accountBalances.size()-1;
        float value = accountBalances.get(last).value;
        return value;
    }

    Date getLastUpdate(){
        int last = accountBalances.size()-1;
        Date date = accountBalances.get(last).date;
        return date;
    }


    long getDaysToPayday(int dayOfMonth){

        long daysDiff;
        Date today;
        Calendar calendar;
        Date dayOfPayday;
        long msDiff;

        // Today
        today = Calendar.getInstance().getTime();


        calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), dayOfMonth);
        dayOfPayday = calendar.getTime();

        // how many days in ms
        msDiff = dayOfPayday.getTime() - today.getTime();

        // how many days in days
        daysDiff = TimeUnit.DAYS.convert(msDiff, TimeUnit.MILLISECONDS);

        if( daysDiff == 0 ){

            daysDiff = 30;

        } else if ( daysDiff < 0 ){

            // today is after pay_day then add one month 
            calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), dayOfMonth);
            calendar.add(Calendar.MONTH, 1);
            dayOfPayday = calendar.getTime();

            // how many days
            msDiff = dayOfPayday .getTime() - today.getTime();
            daysDiff = TimeUnit.DAYS.convert(msDiff, TimeUnit.MILLISECONDS);

        }

        return daysDiff;
    }

}
