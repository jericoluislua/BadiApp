package com.example.bluaje.badiapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter badiliste;
    //private final static String AARBERG = "Schwimmbad Aarberg (BE)";
    //private final static String ADELBODEN = "Schwimmbad Gruebi Adelboden (BE)";
    //private final static String BERN = "Stadtberner Baeder Bern (BE)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ImageView img = (ImageView) findViewById(R.id.badilogo);
        //img.setImageResource(R.drawable.badi);
        addBadisToList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_details, menu);


        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    private void addBadisToList(){
        Intent intent = new Intent(getApplicationContext(), BadiDetailsActivity.class);
        String selected = parent.getItemAtPosition(position).toString();
        String badi = "";
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        for (ArrayList<String> b : allBadis) {
            badiliste.add(b.get(5)+" - "+b.get(1));
            if(badi.equals(selected)) {
                intent.putExtra("badi",b.get(0));
            }
        }

        //badiliste.add(getString(R.string.badaarberg));
        //badiliste.add(getString(R.string.badadelboden));
        //badiliste.add(getString(R.string.badbern));
        badis.setAdapter(badiliste);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View v, int position, long id) {

                //Kleine Infobox anzeigen
                Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", selected);
                startActivity(intent);
                //if (selected.equals(getString(R.string.badaarberg))) {
                //    intent.putExtra("badi", "71");

                //} else if (selected.equals(getString(R.string.badadelboden))) {
                //    intent.putExtra("badi", "27");

                //} else if (selected.equals(getString(R.string.badbern))) {
                //    intent.putExtra("badi", "6");

                //} else {
                //    intent.putExtra("badi", "55");

                //}
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);

    }


}
