package com.klappztech.platform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

public class MainActivity extends AppCompatActivity {

    DataBaseHelper myDbHelper;
    private static final String KEY_ROWID = "_id", DATABASE_TABLE = "routes_table" ;
    public static final String KEY_BUS_NUM = "bus_no",KEY_DEST = "dest",KEY_PLAT = "platform";
    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_BUS_NUM, KEY_DEST, KEY_PLAT};

    private String[] RESULT_STRING_ARRAY;
    private String platform_no;

    private ListView listView;
    private ListViewAdapter adapter;
    private List<String> OtherBusNo = new ArrayList<>();
    private List<String> OtherBusDest = new ArrayList<>();

    private RatingBar ratingBar;

    private NativeExpressAdView nativeExpressAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // database
        initDB();
        openDB();

        //admob

        //admob END

        Intent intent = getIntent();
        String bus_no = intent.getStringExtra("BUS_NO");

        TextView txtBusNo = findViewById(R.id.txt_bus_no);
        txtBusNo.setText(bus_no);

        /* 1. Get and fill platform number */

        RESULT_STRING_ARRAY = getItemsFromDb(bus_no);//get all platform numbers from DB
        platform_no = RESULT_STRING_ARRAY[0];

        TextView txtPlatformNo = findViewById(R.id.txt_platform_no);
        if(RESULT_STRING_ARRAY.length>0) {
            txtPlatformNo.setText(platform_no);
        } else {
            txtPlatformNo.setText("X");

            new AlertDialog.Builder(this)
                    .setTitle("Wrong Bus Number")
                    .setMessage("No bus found with No:"+bus_no)

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Go back to BusSearch
                            startActivity(new Intent(getApplicationContext(), BusSearchActivity.class));
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    //.setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        /* 2. Get and fill buses in the same route */

        List<BusDatabaseObject> db_result = new ArrayList<>();

        db_result = getOtherBusRoutesFromDb(platform_no);



        for (BusDatabaseObject record : db_result) {

            OtherBusNo.add(record.busNo);
            OtherBusDest.add(record.destination);


        }

        listView = findViewById(R.id.otherBuslistView);
        adapter = new ListViewAdapter(OtherBusNo, OtherBusDest, this);
        listView.setAdapter(adapter);


        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if(rating >= 3.5) {
                    Toast.makeText(getApplicationContext(),"Please give your rating in Play Store", Toast.LENGTH_LONG).show();
                    rateThisApp();
                } else {
                    Toast.makeText(getApplicationContext(),"Thank for your rating", Toast.LENGTH_LONG).show();
                }

            }
        });
    } //OnCreate

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

            item[x] = record.platform;
            x++;
        }

        return item;
    }

    public List<BusDatabaseObject> getOtherBusRoutesFromDb(String platform_number){

        // add items on the array dynamically
        List<BusDatabaseObject> products = myDbHelper.select_where("platform",platform_number,"0,10");

        return products;
    }

    public String[] getAllItemsFromDb(String fieldName){

        // add items on the array dynamically
        List<BusDatabaseObject> products = myDbHelper.read_field(fieldName);
        int rowCount = products.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (BusDatabaseObject record : products) {

            item[x] = record.busNo;
            x++;
        }

        return item;
    }



    /////////// END of DB functions //////////

    //onclick function for button
    public void shareThisApp(View view) {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share with"));
        } catch(Exception e) {
            //e.toString();
        }

    }

    public void rateThisApp() {
        Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +  BuildConfig.APPLICATION_ID)));
        }
    }
}
