package com.avenueinfotech.mycitylocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Button button;
    Button butAddress;
    TextView textView;
    TextView address;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;
    private TextView altitude;
    private TextView latlng;
//    private TextView tvCoordiante;

    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1183672799205641~3514807417");
//        EventBus.getDefault().register(this);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        button = (Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textView);
        butAddress =(Button)findViewById(R.id.butAddress);
        address = (TextView)findViewById(R.id.address);
//        altitude = (TextView)findViewById(R.id.altitude);
        latlng = (TextView)findViewById(R.id.latlng);
//        altitude = (TextView)findViewById(R.id.altitude);

        callConnection();
        initLocationRequest();

        cd = new ConnectionDetector(this);

        if (cd.isConnected()) {
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Enable Internet & GPS for Information", Toast.LENGTH_LONG).show();
        }
//
//            }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                   if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                           android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                       ActivityCompat.requestPermissions(MainActivity.this,
                               new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                   }

                   } else {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try{
                        textView.setText(hereLocation(location.getLatitude(), location.getLongitude()));
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Enable GPS & Internet", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        butAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                    }

                } else {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try{
                        address.setText(addressLocation(location.getLatitude(), location.getLongitude()));
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Enable GPS & Internet", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    private void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        initLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
    }

    private void stopLocationUpdate(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try{
                            textView.setText(hereLocation(location.getLatitude(), location.getLongitude()));
                            address.setText(hereLocation(location.getLatitude(), location.getLongitude()));
                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(this, "No permsiion granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    public String hereLocation(double lat, double lon){
        String curCity = "";

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if(addressList.size() > 0){
                curCity = addressList.get(0).getAddressLine(0);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return curCity;



    }

    public String addressLocation(double lat, double lon){
        String curAddress = "";

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat, lon, 3);
            if(addressList.size() > 0){
                curAddress = addressList.get(0).getAddressLine(1);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return curAddress;



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOG", "onConnection(" + bundle + ")");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(l != null){
            Log.i("LOG", "Lat: "+l.getLatitude());
//            Log.i("LOG", "Log: "+l.getLongitude());
            Log.i("Altiude", "Log: "+l.getAltitude());
            latlng.setText("Latitude: "+l.getLatitude()+ "Longitude: "+l.getLongitude()+" | "+l.getAltitude());
//            altitude.setText((int) l.getAltitude());
            latlng.setText(Html.fromHtml("Latitude: "+l.getLatitude()+"<br />"+
            "Longitude: "+l.getLongitude()+"<br />"+
                "Bearing: "+l.getBearing()+"<br />"+
                "Altitude: "+l.getAltitude()+"<br />"+
                "Speed: "+l.getSpeed()+"<br />"+
                "Provider: "+l.getProvider()+"<br />"+
                "Accurucy: "+l.getAccuracy()+"<br />"
            ));


        }

        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
//            altitude.setText((int) +location.getAltitude());
//        tvCoordiante.setText(Html.fromHtml("Location: "+location.getLatitude()+"<br />"+
//                "Longitude: "+location.getLongitude()+"<br />"+
//                "Bearing: "+location.getBearing()+"<br />"+
//                "Altitude: "+location.getAltitude()+"<br />"+
//                "Speed: "+location.getSpeed()+"<br />"+
//                "Provider: "+location.getProvider()+"<br />"+
//                "Accurucy: "+location.getAccuracy()+"<br />"+
//                "Speed: "+ DateFormat.getTimeFormat(this).format(new Date())+"<br +>"
//        ));
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (requestCode == RESULT_OK){
//                Place place = PlacePicker.getPlace(data, this);
//                String toastMsg = String.format("Place: %s", place.getAddress());
//                Toast.makeText(this, coastMsg, Toast.LENGTH_LONG).show();
//                lat = place.getLatitude().latitude;
//                lng = place.getLogitude().longitude;
//
//            }
//        }
//    }

//    public void onEvent(final MessageEB m){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("LOG", m.getResultMessage());
//                address.setText("Data: "+m.getResultMessage());
//            }
//        });
//    }


}
