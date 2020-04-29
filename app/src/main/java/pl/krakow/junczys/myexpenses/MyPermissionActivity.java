package pl.krakow.junczys.myexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class MyPermissionActivity extends AppCompatActivity {

    final static String TAG = "   #MyPermissions";
    final static int MY_PERMISSIONS_REQUEST = 100;

    // obiekty dostępne w całej klasie, members of class
    ProgressBar m_progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_permission);
        m_progressBar = findViewById(R.id.id_pb_progress_bar);
        m_progressBar.setVisibility(View.VISIBLE);


        if (sprawdzUprawnienia()) {

            // po sprawdzeniu uprawnień, uruchamiam głóną aktyność, zwykle MainActivity.class
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            // skoro wystartowła już główna aktywność, to tą mogę spokojnie zakończyć
            finish();

        }
    }


    /**
     * @return true jeżeli wszystkie potrzebne uprawnienia (permissions) zostały przyznane,
     * jeżeli brakuje chociaż jednego, metoda zwraca false
     */
    private boolean sprawdzUprawnienia() {

        // domyślnie true, jeżeli choć jedno wymagalne uprawnienie nie zostanie przydzielone zmienna przyjmie wartość false
        boolean permissionGranted = true;

        // Lista zezwoleń które są mi potrzebne w aplikacji
        List<String> permissionsList = new ArrayList<>();

        // UWAGA! zezwolenia muszą również być przydzielone w manifeście
        // przykładowy wpis w pliku AndroidManifest jeżeli potrzebne są uprawnienia do przechywtywania i czytania smsów.
        // w przypadku innych uprawnień należy się posłużyć dokumentacją androida

        /*
        <uses-permission android:name="android.permission.RECEIVE_SMS" />
        <uses-permission android:name="android.permission.READ_SMS" />
        */

        // -- dokumentacja androida dotycząca uprawnień
        // https://developer.android.com/guide/topics/permissions/requesting.html


        // uzupełnienie listy potrzebnych zezwoleń
        permissionsList.add( Manifest.permission.READ_SMS );





        // permissionsList.add( Manifest.permission.BROADCAST_SMS );

        /*
        permissionsList.add( Manifest.permission.//tu dopisz kolejne potrzebne uprawnienie );
        */

        ////////////////////////////////////////////////////////////////////////////////////////////
        // dokumentacja Androida:
        // https://developer.android.com/guide/topics/permissions/requesting.html
        // Gdyby były potrzebne inne uprawnienia, szczegóły w:
        // Table 1. Dangerous permissions and permission groups.


        // po sprawdzeniu faktycznie przydzielonych zezwoleń będę wiedział czego jeszcze potrzebuje
        List<String> permissionsNeeded = new ArrayList<>();

        // sprawdzam czy mam pozwolenia wprowadzone do listy uprawnień
        for (String permision : permissionsList) {

            if (ContextCompat.checkSelfPermission(this, permision) != PackageManager.PERMISSION_GRANTED) {
                permissionGranted = false;
                permissionsNeeded.add(permision);
            }

        }

        // Log INFORMACYJNY, o tym jakich ewentualnnie uprawnień brakuje
        for (String permision : permissionsNeeded) {
            Log.i(TAG, "Potrzebuje: " + permision);
        }

        // jeżeli potrzebuje jakiś zezwoleń to wysyłam prośbę o zezwolnie
        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
        }

        // metoda zwróci true tylko wtedy jak wszystkie niezbędne uprawnienia są przydzielone
        return permissionGranted;
    }


    @Override
    /**
     * Metoda wywoływana po użyciu prośby o udzielenie uprawnień ActivityCompat.requestPermissions
     * @see <a href="https://developer.android.com/reference/android/support/v4/app/ActivityCompat.OnRequestPermissionsResultCallback.htmls}">dokumentacja Androida</a>
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if( requestCode != MY_PERMISSIONS_REQUEST ) { // to nie było moje pytanie
            return;
        }

        /*
         *  Każde uprawnienie ma swój opis dla użytkownika, wyjasniający dlaczego jest ono ważne dla działania aplikacji.
         *  Jeżeli uprawnienie nie zostało przydzielone, to opisy te łączą się w jeden wyjaśniający komunikat dal użytkownika:
         *  "Dlaczego aplikacja nie będzie mogła działać!"
         *  wszystkie opisy są sumowane w zmiennej sumaOpisow podczas sprawdzania permissions i grantResults
         */
        String sumaOpisow = "";


        // Sprawdzam pary elemetnów w listach permissions i grantResults
        int i = 0;
        for (String permision : permissions) {

            if (grantResults[i++] == PackageManager.PERMISSION_GRANTED) { // permission was granted

                // Zapis do logu informacyjny
                debug( "uprawnienia przydzielone");

            } else { // permission denied

                // uzupwłnij / stwórz komunikat dla użytkownika
                sumaOpisow += opisZezwolenia(permision);
                sumaOpisow += "\n\n";

            }
        }

        // Komunikat dla użytkownika wyświetlony w oknie dialogowym / informacyjnym
        if ( !sumaOpisow.isEmpty() ) {

            sumaOpisow += "\n";
            sumaOpisow += getResources().getString(R.string.str_i_respect_your_choice);

            // ustawimy to wszystko w oknie dialogowym, tak wydaje mi się będzie najsensowniej
            AlertDialog.Builder builder = new AlertDialog.Builder(MyPermissionActivity.this);

            // builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
            builder.setMessage(sumaOpisow).setTitle("Nie wyraziłaś(eś) zgody na:");

            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {  // Mam wszystkie niezbędne uprawnienia, moge uruchomić moją główną aplikacje

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    /**
     *
     * @param permision Androdiowa nazwa uprawnienia
     * @return Wyjaśnienie dla użytkownika dlaczego to uprawnienie jest ważne dla działania aplikacji
     */
    private String opisZezwolenia( String permision ) {

        String opis = "";

        switch (permision) {


            case Manifest.permission.READ_SMS:
                opis = getResources().getString(R.string.s_read_sms); // tu wskaż opis z zasobów stringu opisującego wyjaśnienie do tego uprawnienia );
                break;

            /*
            case android.Manifest.permission.// tu dopisz kolejne uprawnienie:
                opis = getResources().getString(R.string. // tu wskaż opis z zasobów stringu opisującego wyjaśnienie do tego uprawnienia );
                break;
            */

        }

        return opis;
    }

    void debug( String message ){
        if( BuildConfig.DEBUG ){
            Log.d(TAG, message );
        }
    }
}

