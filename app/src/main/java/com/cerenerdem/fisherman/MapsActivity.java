package com.cerenerdem.fisherman;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static LocationManager locationManager;
    public static LocationListener locationListener;

    EditText edt_Aciklama;
    Button btn_AddLocation;
    Button btn_Other;
    Button btn_Whiting;
    Button btn_HorseMackerel;

    String Aciklama;
    String coord1 = "";
    String coord2 = "";
    String DevamDurumu = "basla";
    String TarihSaat = "";
    public static String SecilenBalik = "";
    LatLng KullaniciLokasyonu;
    static  SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationadd);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        edt_Aciklama = (EditText) findViewById(R.id.edt_Aciklama);
        btn_AddLocation = (Button) findViewById(R.id.btn_AddLocation);
        btn_Other = (Button) findViewById(R.id.btn_Other);
        btn_HorseMackerel = (Button) findViewById(R.id.btn_HorseMackerel);
        btn_Whiting = (Button) findViewById(R.id.btn_Whiting);


        btn_HorseMackerel.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_HorseMackerel.setTextColor(getResources().getColor(R.color.BtnTextColor));
        btn_Whiting.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_Whiting.setTextColor(getResources().getColor(R.color.BtnTextColor));
        btn_Other.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_Other.setTextColor(getResources().getColor(R.color.BtnTextColor));

        btn_AddLocation.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_AddLocation.setTextColor(getResources().getColor(R.color.BtnTextColor));

    }



    


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mMap.clear();
                KullaniciLokasyonu = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(KullaniciLokasyonu).title(getString(R.string.KullanıcıBurada).toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KullaniciLokasyonu, 15));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //Lokasyon izni alınır.
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                onBackPressed();

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
            }

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
        }


        LatLng Location_MyHouse = new LatLng(40.727083, 29.798093);
        mMap.addMarker(new MarkerOptions().position(Location_MyHouse).title(getString(R.string.BenimEvim).toString()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Location_MyHouse, 15));





    }


    public void click_SaveLocations(View v) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            TarihSaat = sdf.format(new Date());

        try {

            if (SecilenBalik == "") {

                DevamDurumu = "baliksecilmedi"; //Balık Seçilmedi.
                Toast.makeText(getApplicationContext(), getString(R.string.BalikSecilmedi).toString() + " Hata:101", Toast.LENGTH_SHORT).show();


            } else if (SecilenBalik != "basla") {

                DevamDurumu = "baliksecildi"; //Balık Seçildi.

            }


            if (DevamDurumu == "baliksecilmedi") {

                Toast.makeText(getApplicationContext(), getString(R.string.BalikSecilmedi).toString() + " Hata:102", Toast.LENGTH_SHORT).show();

            } else if (DevamDurumu == "baliksecildi") {

                try {

                    Double l1 = KullaniciLokasyonu.latitude;
                    Double l2 = KullaniciLokasyonu.longitude;

                    coord1 = l1.toString();
                    coord2 = l2.toString();

                    DevamDurumu = "baliksecildi_lokasyonalindi";// Balık Seçildi - Lokasyon Alındı

                } catch (Exception e) {

                    DevamDurumu = "baliksecildi_lokasyonalinamadi";// Balık Seçildi - Lokasyon Alınamadı
                    Toast.makeText(getApplicationContext(), getString(R.string.Uyari_SagliksizKonum).toString(), Toast.LENGTH_SHORT).show();

                }
            }


            if (DevamDurumu == "baliksecildi_lokasyonalindi") {


                try {

                    Aciklama = String.valueOf(edt_Aciklama.getText());

                    database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS places (secilenbalik VARCHAR, aciklama VARCHAR, latitude VARCHAR, longitude VARCHAR)");
                    String RunCommand_Insert = "INSERT INTO places (secilenbalik, aciklama, latitude, longitude) values (?, ?, ?, ?)";


                    SQLiteStatement sqLiteStatement = database.compileStatement(RunCommand_Insert);
                    //sqLiteStatement.bindString(1, TarihSaat);
                    sqLiteStatement.bindString(1, SecilenBalik + " -- " + TarihSaat);
                    sqLiteStatement.bindString(2, Aciklama);
                    sqLiteStatement.bindString(3, coord1);
                    sqLiteStatement.bindString(4, coord2);

                    sqLiteStatement.execute();

                    Toast.makeText(getApplicationContext(), getString(R.string.KonumKaydedildi).toString(), Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(this, MainActivity.class);
                    startActivityForResult(i, 1);

                } catch (Exception e) {

                    e.toString();
                    Toast.makeText(getApplicationContext(), getString(R.string.Hata_KayitSirasi).toString() + " Hata:103", Toast.LENGTH_SHORT).show();

                }


            } else if (DevamDurumu == "baliksecildi_lokasyonalinamadi") {

                Toast.makeText(getApplicationContext(), getString(R.string.Uyari_SagliksizKonum).toString() + " Hata:104", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                TarihSaat = "";
                SecilenBalik = "";
                Aciklama = "";
                coord1 = "";
                coord2 = "";
                DevamDurumu = "kayitolumsuzbitti";

            }
        } catch (Exception e){

            Toast.makeText(getApplicationContext(), getString(R.string.Hata).toString() + " Hata:105", Toast.LENGTH_SHORT).show();

        }
    }


    public void click_Other(View v) {
        btn_HorseMackerel.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_HorseMackerel.setTextColor(getResources().getColor(R.color.BtnTextColor));
        btn_Whiting.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_Whiting.setTextColor(getResources().getColor(R.color.BtnTextColor));

        btn_Other.setBackgroundColor(getResources().getColor(R.color.SelectBackground));
        btn_Other.setTextColor(getResources().getColor(R.color.SelectTextColor));

        SecilenBalik = "Diğer";
    }

    public void click_HorseMackerel(View v) {
        btn_HorseMackerel.setBackgroundColor(getResources().getColor(R.color.SelectBackground));
        btn_HorseMackerel.setTextColor(getResources().getColor(R.color.SelectTextColor));

        btn_Whiting.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_Whiting.setTextColor(getResources().getColor(R.color.BtnTextColor));
        btn_Other.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_Other.setTextColor(getResources().getColor(R.color.BtnTextColor));

        SecilenBalik = "İstavrit";

    }
    public void click_Whiting(View v) {
        btn_HorseMackerel.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_HorseMackerel.setTextColor(getResources().getColor(R.color.BtnTextColor));
        btn_Other.setBackgroundColor(getResources().getColor(R.color.BtnBackground));
        btn_Other.setTextColor(getResources().getColor(R.color.BtnTextColor));

        btn_Whiting.setBackgroundColor(getResources().getColor(R.color.SelectBackground));
        btn_Whiting.setTextColor(getResources().getColor(R.color.SelectTextColor));

        SecilenBalik = "Mezgit";

    }

}

