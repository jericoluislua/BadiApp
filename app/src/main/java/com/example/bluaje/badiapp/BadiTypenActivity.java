package com.example.bluaje.badiapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
public class BadiTypenActivity extends AppCompatActivity {

    ArrayAdapter badiliste;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            setContentView(R.layout.activity_badi_typen);
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            TextView text = (TextView) findViewById(R.id.baditypen);
            text.setText(name + "(Stadt)");
            addBadisToList();
        } else {
            //keine Internetverbindung
            setContentView(R.layout.activity_internet_error);
        }
    }

    private void addBadisToList() {
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        Set<String> badityp = new TreeSet<>();
        for (ArrayList<String> b : allBadis) {
            if(b.get(5).equals(getIntent().getExtras().getString("name"))){
                badityp.add(b.get(9));
            }
        }
        badiliste.addAll(badityp);
        badis.setAdapter(badiliste);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadisActivity.class);
                String selected = parent.getItemAtPosition(position).toString();
                //kleine Infobox anzeigen
                Toast.makeText(BadiTypenActivity.this, selected, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                String ortschaft = getIntent().getExtras().getString("name");
                intent.putExtra("ort", ortschaft);
                intent.putExtra("name", selected);
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);
    }
}