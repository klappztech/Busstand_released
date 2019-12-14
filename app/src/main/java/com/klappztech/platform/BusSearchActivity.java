package com.klappztech.platform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.IOException;
import java.util.List;

public class BusSearchActivity extends AppCompatActivity {

    AutoCompleteTextView autocomplete_bus;
    String bus_no;
    private String[] BUSES;
    DataBaseHelper myDbHelper;
    private static final String KEY_ROWID = "_id", DATABASE_TABLE = "routes_table" ;
    public static final String KEY_BUS_NUM = "bus_no",KEY_DEST = "dest",KEY_PLAT = "platform";
    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_BUS_NUM, KEY_DEST, KEY_PLAT};

    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search);

        // database
        initDB();
        openDB();

        //admob
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); //TODO: test
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //admon ENDS

        BUSES = getAllItemsFromDb(KEY_BUS_NUM);//get all bus number from DB



        autocomplete_bus = findViewById(R.id.autocomplete_bus_no);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,BUSES);
        autocomplete_bus.setAdapter(adapter);
        autocomplete_bus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "id clicked: " + id, Toast.LENGTH_SHORT).show();
                searchBus(view);
            }
        });

        autocomplete_bus.requestFocus();

    }

    //////////// DB functions //////////

    private void openDB() {
        myDbHelper.myDataBase = myDbHelper.myDataBase;
    }

    private void initDB() {

        myDbHelper = new DataBaseHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
    }

    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	myDbHelper.myDataBase.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	myDbHelper.myDataBase.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public String[] getItemsFromDb(String searchTerm){

        // add items on the array dynamically
        List<BusDatabaseObject> products = myDbHelper.read(searchTerm);
        int rowCount = products.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (BusDatabaseObject record : products) {

            item[x] = record.busNo;
            x++;
        }

        return item;
    }

    public String[] getAllItemsFromDb(String fieldName){

        // add items on the array dynamically
        List<BusDatabaseObject> busDatabaseObject = myDbHelper.read_field(fieldName);
        int rowCount = busDatabaseObject.size();

        String[] bus_no = new String[rowCount];
        int x = 0;

        for (BusDatabaseObject record : busDatabaseObject) {

            bus_no[x] = record.busNo;
            x++;
        }

        return bus_no;
    }



    /////////// END of DB functions //////////

    //onclick function for button
    public void searchBus(View view) {
        bus_no = autocomplete_bus.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("BUS_NO",bus_no);

        startActivity(intent);

    }



    public String GetPlatformFromBus( String bus_no){
        BUSES = getItemsFromDb(bus_no);//get all bus number from DB
        return BUSES[0];
    }

    public void dontKnowBusNo2(View view) {
        Toast.makeText(getApplicationContext(),"Find Bus Number using Maps", Toast.LENGTH_LONG).show();
// Create a Uri from an intent string. Use the result to create an Intent.
        //Uri gmmIntentUri =Uri.parse("geo:0,0?q=Kempegowda Bus Station");
        Uri gmmIntentUri = Uri.parse("google.navigation:q=Connaught+Place,+New+Delhi,Delhi&mode=l");

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }

    public void dontKnowBusNo(View view) {
        Toast.makeText(getApplicationContext(),"Find Bus Number using Google Maps and come back", Toast.LENGTH_LONG).show();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&origin=Kempegowda+Bus+Station&travelmode=transit"));
        startActivity(browserIntent);
    }
}
