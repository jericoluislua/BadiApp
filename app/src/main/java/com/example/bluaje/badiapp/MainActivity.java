package com.example.bluaje.badiapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter badiliste;
    //private final static String AARBERG = "Schwimmbad Aarberg (BE)";
    //private final static String ADELBODEN = "Schwimmbad Gruebi Adelboden (BE)";
    //private final static String BERN = "Stadtberner Baeder Bern (BE)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //Internetverbindung besteht
            setContentView(R.layout.activity_main);
            //ImageView img = (ImageView) findViewById(R.id.badilogo);
            //img.setImageResource(R.drawable.badi);
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            TextView text = (TextView) findViewById(R.id.badikantone);
            text.setText("Kantone");
            addBadisToList();
        }
        else {
            //keine Internetverbindung
            setContentView(R.layout.activity_internet_error);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();


        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                badiliste.clear();
                addBadisToList();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
                //builder.setMessage("Kein" + s + " vorhanden.").setTitle(R.string.errordialog_title);
                //if(s == )
                badiliste.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return true;
            }
        });
        searchItem.expandActionView();


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void addBadisToList(){

        Set<String> hs = new TreeSet<>();
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        for (ArrayList<String> b : allBadis) {
            //b.get(6) = kanton
            //b.get(5) = stadt
            hs.add(b.get(6)/*+" - "+b.get(1)*/);
        }
        badiliste.addAll(hs);

        badis.setAdapter(badiliste);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiOrtschaftenActivity.class);
                String selected = parent.getItemAtPosition(position).toString();
                //Kleine Infobox anzeigen
                Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", selected);
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);

    }


}
