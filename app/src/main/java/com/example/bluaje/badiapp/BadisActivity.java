package com.example.bluaje.badiapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class BadisActivity extends AppCompatActivity {

    ArrayAdapter badiliste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_typen);
        addBadisToList();
    }

    private void addBadisToList() {
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        Set<String> badi = new TreeSet<>();
        for (ArrayList<String> b : allBadis) {
            if((b.get(9).equals(getIntent().getExtras().getString("name"))) && (b.get(5).equals(getIntent().getExtras().getString("ort")))){

                if(b.get(2).isEmpty() || b.get(2).equals("")){
                    badi.add(b.get(1) + " - " + b.get(3));
                }
                else{
                    badi.add(b.get(1) + " - " + b.get(2));
                }
            }
        }
        badiliste.addAll(badi);
        badis.setAdapter(badiliste);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailsActivity.class);
                String selected = parent.getItemAtPosition(position).toString();
                //kleine Infobox anzeigne
                Toast.makeText(BadisActivity.this, selected, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", selected);
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);
    }
}