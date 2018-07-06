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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class BadisActivity extends AppCompatActivity {

    ArrayAdapter badiliste;
    private String name;
    private ArrayList<String> badiId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
        setContentView(R.layout.activity_badis);
        Intent intent = getIntent();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        TextView text = (TextView) findViewById(R.id.badis);
        name = intent.getStringExtra("name");
        text.setText(name);
        addBadisToList();
        }
        else {
            //keine Internetverbindung
            setContentView(R.layout.activity_internet_error);
        }
    }

    private void addBadisToList() {
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        Set<String> badi = new TreeSet<>();
        Set<String> tsbadiId = new TreeSet<>();
        for (ArrayList<String> b : allBadis) {
            if((b.get(9).equals(getIntent().getExtras().getString("name"))) && (b.get(5).equals(getIntent().getExtras().getString("ort")))){
                if(b.get(2).isEmpty() || b.get(2).equals("")){
                    badi.add(b.get(1) + " - " + b.get(3));
                    tsbadiId.add(b.get(0));
                }
                else{
                    badi.add(b.get(1) + " - " + b.get(2));
                    tsbadiId.add(b.get(0));
                }
            }
        }
        badiliste.addAll(badi);
        badis.setAdapter(badiliste);
        badiId.addAll(tsbadiId);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailsActivity.class);
                String selected = parent.getItemAtPosition(position).toString();
                //kleine Infobox anzeigen
                Toast.makeText(BadisActivity.this, selected, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", selected);
                intent.putExtra("id", badiId.get(position));
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);
    }
}