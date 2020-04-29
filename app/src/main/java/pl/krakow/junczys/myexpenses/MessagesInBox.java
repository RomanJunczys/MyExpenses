package pl.krakow.junczys.myexpenses;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class MessagesInBox {

    private String TAG = "MessagesInBox";

    private Context context;
    private Uri smsQueryUri;
    private String adress;

    MessagesInBox(Context in_context, Uri in_smsQueryUri, String in_adress){
        context = in_context;
        smsQueryUri = in_smsQueryUri;
        adress = in_adress;

    }

    List<String> getListOfMessages(){

        List<String> listOfMessages = new ArrayList<>();

        Cursor cursor;
        String sortOrder = "date ASC";
        cursor = context.getContentResolver().query(smsQueryUri, null, null, null, sortOrder);

        if (cursor == null) {
            Log.i(TAG, "Cursor is null. Uri: " + smsQueryUri);
        }

        boolean hasData = false;
        if (cursor != null) {
            hasData = cursor.moveToFirst();
        }

        while ( hasData ){

            if( cursor.getString(cursor.getColumnIndexOrThrow("address")).equals(adress)){

                final String body = cursor.getString( cursor.getColumnIndexOrThrow("body") );

                Date date = new Date(cursor.getLong(4));
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);


//                    final String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                int indexStart = body.indexOf("wynosi ");
                int indexStop = body.indexOf(" PLN");

                String str_value;
                if( indexStart != -1 & indexStop != -1 ){
                    str_value = body.substring(indexStart+7, indexStop);
                    str_value = str_value.replace(",",".");
                    str_value = str_value.replace(" ","");
                    float f = Float.parseFloat(str_value);
                    listOfMessages.add(formattedDate+","+f);
                }

                Log.i("Start and stop index: ", ""+indexStart+" "+indexStop);
            }
            hasData = cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }

        return listOfMessages;
    }


    List<String> getListOfMessagesAfterDate(String in_filter){

        List<String> listOfMessages = new ArrayList<>();

        Cursor cursor;
        String sortOrder = "date ASC";
        cursor = context.getContentResolver().query(smsQueryUri, null, in_filter, null, sortOrder);

        if (cursor == null) {
            Log.i(TAG, "Cursor is null. Uri: " + smsQueryUri);
        }

        boolean hasData = false;
        if (cursor != null) {
            hasData = cursor.moveToFirst();
        }

        while ( hasData ){

            if( cursor.getString(cursor.getColumnIndexOrThrow("address")).equals(adress)){

                final String body = cursor.getString( cursor.getColumnIndexOrThrow("body") );

                Date date = new Date(cursor.getLong(4));
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);


//                    final String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                int indexStart = body.indexOf("wynosi ");
                int indexStop = body.indexOf(" PLN");

                String str_value;
                if( indexStart != -1 & indexStop != -1 ){
                    str_value = body.substring(indexStart+7, indexStop);
                    str_value = str_value.replace(",",".");
                    str_value = str_value.replace(" ","");
                    float f = Float.parseFloat(str_value);
                    listOfMessages.add(formattedDate+","+f);
                }

                Log.i("Start and stop index: ", ""+indexStart+" "+indexStop);
            }
            hasData = cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }

        return listOfMessages;
    }

}
