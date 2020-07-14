package pl.krakow.junczys.myexpenses;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

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


    int getDaysToPayday(int dayOfMonth){
        int days=0;

        return days;
    }
}
