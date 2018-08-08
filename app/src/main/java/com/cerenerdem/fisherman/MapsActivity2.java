package com.cerenerdem.fisherman;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng KullaniciLokasyonu;

    String old_SecilenBalik = "";
    String old_Aciklama = "";
    TextView txv_SecilenBalik;
    LatLng old_location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txv_SecilenBalik = (TextView) findViewById(R.id.txv_SecilenBalik);
        txv_SecilenBalik.setText("");

        Intent intent = getIntent();
        old_SecilenBalik = intent.getStringExtra("secilenbalik");
        old_Aciklama = intent.getStringExtra("aciklama");

        int position = intent.getIntExtra("position", 0);
        old_location = new LatLng(MainActivity.locations.get(position).latitude, MainActivity.locations.get(position).longitude);


        if (old_Aciklama.equals("")){

            txv_SecilenBalik.setText(old_SecilenBalik);

        } else {

            txv_SecilenBalik.setText(old_SecilenBalik + " -- " + old_Aciklama);
        }
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

                if (old_location != null) {


                    int height = 200;
                    int width = 200;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.appimages);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                    MarkerOptions markerOptions = new MarkerOptions().position(old_location)
                            .title(old_SecilenBalik)
                            .snippet(old_Aciklama)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(old_location));


                    mMap.addMarker(new MarkerOptions().position(KullaniciLokasyonu).title(getString(R.string.KullanıcıBurada).toString()));





                } else {

                    mMap.addMarker(new MarkerOptions().position(KullaniciLokasyonu).title(getString(R.string.KullanıcıBurada).toString()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(KullaniciLokasyonu));
                }


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


        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
            }

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
        }


        LatLng Location_MyHouse = new LatLng(40.727083, 29.798093);
        mMap.addMarker(new MarkerOptions().position(Location_MyHouse).title(getString(R.string.BenimEvim).toString()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Location_MyHouse, 13));

    }

}
