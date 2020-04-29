package pl.krakow.junczys.myexpenses;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class SaveListOfStringsToCsv {
    private String TAG = "SaveListOfStringsToCsv: ";
    private Context context;
    private File file;


    SaveListOfStringsToCsv(Context context, String fileName){
        this.context = context;
        file = new File( this.context.getExternalFilesDir(null), fileName);

    }

    private void CreatedFile(){
        // Creates a file in the primary external storage space of the
        // current application.

        Log.d(TAG, file.toString());

        try {
//            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ReadWriteFile", "Unable to write to the TestFile.txt file.");
        }
    }

    String getFile(){
        return file.toString();
    }

    boolean fileExist(){
        return file.exists();
    }

    void WriteListOfStrings(List<String> listOfString ){

        CreatedFile();

        // Adds a line to the file
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));

            writer.write("Date,Account balance\n");
            for (String s : listOfString)
            {
                writer.write(s+'\n');
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RefreshData();
    }

    void AppendListOfStrings(List<String> listOfString ){

        // Append lines to the file
        BufferedWriter writer;

        try {

            writer = new BufferedWriter(new FileWriter(file, true ));

            for (String s : listOfString)
            {
                writer.write(s+'\n');
            }

            writer.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

        RefreshData();

    }

    private void RefreshData() {
        // Refresh the data so it can seen when the device is plugged in a
        // computer. You may have to unplug and replug the device to see the
        // latest changes. This is not necessary if the user should not modify
        // the files.
        MediaScannerConnection.scanFile(context,
                new String[]{file.toString()},
                null,
                null);
    }


}
