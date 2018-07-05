package com.example.bluaje.badiapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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

public class BadiOrtschaftenActivity extends AppCompatActivity {

    ArrayAdapter badiliste;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_badi_ortschaften);
        Intent intent = getIntent();

        //ImageView img = (ImageView) findViewById(R.id.badilogo);
        //img.setImageResource(R.drawable.badi);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        addBadisToList();
        TextView text = (TextView) findViewById(R.id.badistaedte);
        name = intent.getStringExtra("name");
        text.setText(name + "(Kanton)");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                /*if(s == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(BadiOrtschaftenActivity.this);
                    builder.setMessage("Kein \"" + s + "\" vorhanden.").setTitle(R.string.errordialog_title);
                    AlertDialog dialog = builder.create();
                }*/
                badiliste.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                badiliste.clear();
                addBadisToList();
                return true;
            }
        });



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

    private void addBadisToList() {
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        Set<String> orte = new TreeSet<>();
        for (ArrayList<String> b : allBadis) {
            if(b.get(6).equals(getIntent().getExtras().getString("name"))){
                orte.add(b.get(5));

            }
        }
        badiliste.addAll(orte);
        badis.setAdapter(badiliste);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiTypenActivity.class);
                String selected = parent.getItemAtPosition(position).toString();
                //kleine Infobox anzeigne
                Toast.makeText(BadiOrtschaftenActivity.this, selected, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", selected);
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);
    }
}