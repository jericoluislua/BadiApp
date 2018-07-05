package com.example.bluaje.badiapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BadiDetailsActivity extends AppCompatActivity {

    private String badiId;
    private String name;
    private String weather;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_badi_details);
        Intent intent = getIntent();

        badiId = intent.getStringExtra("badi");
        name = intent.getStringExtra("name");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        TextView text = (TextView) findViewById(R.id.badiinfos);
        text.setText(name);
        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "Bitte warten...");
        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);

    }

    @SuppressLint("StaticFieldLeak")
    private void getBadiTemp(String url){
        //Den ArrayAdapter wollen wir später verwenden um die Temperaturen zu speichern
        //angezeigt sollen sie im Format der simple_list_item_1 werden (einem Standard Android Element)

        final ArrayAdapter temps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        //Android verlangt, dass die Datenverarbeitung von den GUI Prozessen getrennt wird.
        //Darum starten wir hier einen asynchronen Task (quasi einen Hintergrundprozess).
        new AsyncTask<String, String, String>(){
            //Der AsyncTask verlangt die implementation der Methode doInBackground.
            //Nachdem doInBackground ausgeführt wurde, startet automatisch die Methode onPostExecute
            //mit den Daten die man in der Metohde doInBackground mit return zurückgegeben hat (hier msg).

            protected String doInBackground(String[] badi){
                //In der variable msg soll die Antwort der Seite wiewarm.ch gespeichert werden.
                String msg = "";
                try {
                    URL url = new URL(badi[0]);
                    //Hier bauen wir die Verbindung auf:
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //Lesen des Antwortcodes der Webseite:
                    int code = conn.getResponseCode();
                    //Hier lesen wir die Nachricht der Webseite wiewarm.ch für Badi XY:
                    msg = IOUtils.toString(conn.getInputStream());
                    //und Loggen den Statuscode in der Konsole:
                    Log.i(getString(R.string.TAG), Integer.toString(code));
                }catch (Exception e){
                    Log.v(getString(R.string.TAG), e.toString());
                }
                return msg;
            }
            @Override
            public void onPostExecute(String result) {
                //In result werden zurückgelieferten Daten der Methode doInBackground (return msg;) übergeben.
                //Hier ist also unser Resultat der Seite z.B. http://www.wiewarm.ch/api/v1/bad.json/55
                //In einem Browser IE, Chrome usw. sieht man schön das Resulat als JSON formatiert.
                //JSON Daten können wir aber nicht direkt ausgeben, also müssen wir sie umformatieren.
                try {
                    //Zum Verarbeiten bauen wir die Methode parseBadiTemp und speichern das Resulat in einer Liste.
                    List<String> badiInfos = parseBadiTemp(result);
                    //Jetzt müssen wir nur noch alle Elemente der Liste badidetails hinzufügen.
                    //Dazu holen wir die ListView badidetails vom GUI

                    ListView badidetails = (ListView) findViewById(R.id.badidetails);
                    //und befüllen unser ArrayAdapter den wir am Anfang definiert haben (braucht es zum befüllen eines ListViews)

                    temps.addAll(badiInfos);
                    //Mit folgender Zeile fügen wir den befüllten ArrayAdapter der ListView hinzu:

                    badidetails.setAdapter(temps);
                } catch (JSONException e) {
                    Log.v(getString(R.string.TAG), e.toString());
                }
            }

            private List parseBadiTemp(String jonString)throws JSONException{
                {
                    //Wie bereits erwähnt können JSON Daten nicht direkt einem ListView übergeben werden.
                    //Darum parsen ("lesen") wir die JSON Daten und bauen eine ArrayListe, die kompatibel
                    //mit unserem ListView ist.
                    ArrayList<String> resultList = new ArrayList<String>();
                    JSONObject jsonObj = jsonObj = new JSONObject(jonString);
                    JSONObject becken = jsonObj.getJSONObject("becken");
                    //Das ist unser Pointer um aus den JSON Daten alle Datensätze herauszulesen
                    Iterator keys = becken.keys();
                    //Hier holen wir Element für Element aus dem JSON Stream:
                    //Was wo drin steckt, definiert die API der Datenquelle.
                    //Für wiewarm.ch muss man es wie folgt machen:
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        JSONObject subObj = becken.getJSONObject(key);
                        //Wenn man die Antwort der Webseite anschaut, steckt im Element "beckenname",
                        //der Name des Schwimmbeckens
                        String name = subObj.getString("beckenname");
                        //und unter temp ist die Temperatur angegeben
                        String temp = subObj.getString("temp");
                        //Sobald wir die Daten haben, fügen wir sie unserer Liste hinzu:
                        resultList.add(name + ": " + temp + " Grad Celsius");
                    }
                    return resultList;
                }
            }
        }.execute(url);
    }

    @SuppressLint("StaticFieldLeak")
    private void getWeatherTemp(String url) {
        //dieser ArrayAdapter wird später verwendet um die Temperaturen zu speichern
        final ArrayAdapter temps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        //Hier starten wir einen asynchronen Task, da Android verlangt dass die Datenbearbeitung und GUI getrennt sind
        new AsyncTask<String, String, String>() {
            //der AsyncTask verlangt die Implementation der Methode doInBackground, nach dieser wird immer die Methode onPostExecute ausgeführt
            @Override
            protected String doInBackground(String[] weather) {
                //in dieser Variable wird die Antwort der Seite wiewarm.ch gespeichert.
                String msg = "";
                try {
                    URL url = new URL(weather[0]);
                    //Hier bauen wir die Verbindung auf:
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //Lesen des Antwortcodes der Website
                    int code = conn.getResponseCode();
                    //Nun können wir den Lade Dialog wieder ausblenden
                    mDialog.dismiss();
                    //Hier lesen wir die Nachricht der Website wiewarm uns speichern es in msg
                    msg = IOUtils.toString(conn.getInputStream());
                    //und loggen den Statuscode in der Konsole
                    Log.i(getString(R.string.TAG), Integer.toString(code));
                } catch (Exception e) {
                    Log.i(getString(R.string.TAG), e.toString());
                }
                return msg;
            }

            public void onPostExecute(String result) {
                try {
                    //parseBadiTemp ist eine Methode zum verarbeiten und speichern der Daten
                    List<String> weatherInfos = parseWeatherTemp(result);
                    //Hier wird die ListView der Badidetails geholt
                    ListView weatherdetails = (ListView) findViewById(R.id.weatherdetails);
                    //wir füller nun die Daten in den ArrayAdapter
                    temps.addAll(weatherInfos);
                    //Hier wird der ArrayAdapter der ListView hinzugefügt
                    weatherdetails.setAdapter(temps);
                } catch(JSONException e) {
                    Log.i(getString(R.string.TAG), e.toString());
                }
            }

            private List parseWeatherTemp(String jonString)throws JSONException{
                ArrayList<String> resultList = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(jonString);
                JSONArray a = jsonObject.getJSONArray("weather");
                JSONObject object = a.getJSONObject(0);
                weather = object.getString("main");
                ImageView img = (ImageView) findViewById(R.id.weatherPic);
                switch (weather){
                    case "Clouds":
                        //Bild
                        img.setImageResource(R.drawable.clouds);
                        resultList.add("Bewölkt");
                        break;
                    case "Sun":
                        //Bild
                        img.setImageResource(R.drawable.sun);
                        resultList.add("Sonne");
                        break;
                    case "Rain":
                        //Bild
                        img.setImageResource(R.drawable.rain);
                        resultList.add("Regen");
                        break;
                    case "Snow":
                        //Bild
                        img.setImageResource(R.drawable.snow);
                        resultList.add("Schnee");
                        break;

                    case "Thunderstorm":
                        //Bild
                        img.setImageResource(R.drawable.thunderstorm);
                        resultList.add("Gewitter");
                        break;
                    default:
                        img.setImageResource(R.drawable.notfound);
                        resultList.add("Wetterart ist nicht definiert");
                        break;
                }

                JSONObject wetter = jsonObject.getJSONObject("main");
                Iterator keys = wetter.keys();

                double temp_k = wetter.getDouble("temp");
                double temp_c = temp_k- 273.15;
                resultList.add("Momentan"+ (float)temp_c+" °C");

                double max_k = wetter.getDouble("temp_max");
                double max_c = max_k-273.15;
                resultList.add("Max: "+(float) max_c+"°C");

                double min_k = wetter.getDouble("temp_min");
                double min_c= min_k-273.15;
                resultList.add("Min: " + (float)min_c +"°C");



                return resultList;
            }

        }.execute(url);
    }

}
