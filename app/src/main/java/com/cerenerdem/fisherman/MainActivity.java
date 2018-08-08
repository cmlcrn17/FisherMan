package com.cerenerdem.fisherman;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

//AdMob için Kütüphane
//import com.google.android.gms.ads.*;
//import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;


public class MainActivity extends AppCompatActivity {

    static ArrayAdapter arrayAdapter;
    static ArrayList<String> secilenbalik = new ArrayList<String>();
    static ArrayList<String> explains = new ArrayList<String>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ListView lst_FisherManLocation = null;
    public static ArrayList<String> veriler = new ArrayList<String>();

    //AdMobs için tanımlanan değişkenler
   private AdView mAdView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.fishaddlocation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.Add_FishLocations) {

            Intent IntentMaps = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(IntentMaps);

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.appimages);
        setContentView(R.layout.activity_main);


        MobileAds.initialize(this, "ca-app-pub-1077976104717606~3050645269");
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1077976104717606~3050645269");
        //adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");


        veriler.clear();

        lst_FisherManLocation = (ListView) findViewById(R.id.lst_FisherManLocation);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, secilenbalik);
        arrayAdapter.clear();
        lst_FisherManLocation.setAdapter(arrayAdapter);

        try

        {

            MapsActivity.database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            Cursor cursor = MapsActivity.database.rawQuery("SELECT * FROM places", null);

            int selectedIx = cursor.getColumnIndex("secilenbalik");
            int explainIx = cursor.getColumnIndex("aciklama");
            int latitudeIx = cursor.getColumnIndex("latitude");
            int longitudeIx = cursor.getColumnIndex("longitude");


            while (cursor.moveToNext()) {

                String SecilenBalikFromDatabase = cursor.getString(selectedIx);
                String ExplainFromDatabase = cursor.getString(explainIx);
                String latitudeFromDatabase = cursor.getString(latitudeIx);
                String longitudeFromDatabase = cursor.getString(longitudeIx);

                secilenbalik.add(SecilenBalikFromDatabase);
                explains.add(ExplainFromDatabase);

                Double l1 = Double.parseDouble(latitudeFromDatabase);
                Double l2 = Double.parseDouble(longitudeFromDatabase);

                LatLng locationFromDatabase = new LatLng(l1, l2);
                locations.add(locationFromDatabase);

            }

            cursor.close();
            arrayAdapter.notifyDataSetChanged();

        } catch (
                Exception e)

        {
            if (lst_FisherManLocation.getCount() == 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.ListeBos).toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.Hata).toString() + " 104", Toast.LENGTH_SHORT).show();
            }
        }


        lst_FisherManLocation.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity2.class);
                intent.putExtra("secilenbalik", secilenbalik.get(position));
                intent.putExtra("aciklama", explains.get(position));
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });


    }


    //Mail Gönder Yarım Kaldı
    public void click_MailGonder(View v) {


        try {
            veriler.clear();
            //Eklenen verileri oku
            MapsActivity.database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            Cursor cursor = MapsActivity.database.rawQuery("SELECT * FROM places", null);


            while (cursor.moveToNext()) {

                veriler.add(secilenbalik.toString() + " - " + locations.toString());

            }

            cursor.close();


            //Mail Gönder
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            emailIntent.setType("plain/text");

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Konu");//Email konusu

            emailIntent.putExtra(Intent.EXTRA_TEXT, veriler.toString());//Email içeriği

            startActivity(Intent.createChooser(emailIntent, "E-Posta Göndermek için Seçiniz:"));//birden fazla email uygulaması varsa seçmek için

            String aEmailList[] = {""};//Mail gönderielecek kişi.Birden fazla ise virgülle ayırarak yazılır

            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);

            startActivity(emailIntent);


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), getString(R.string.ListeBos).toString() + " 104", Toast.LENGTH_SHORT).show();

        }

    }

    //Yeni Konum Ekle sayfasından sonra listview sayfasını güncelle işlemi yapılıyor.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


}
