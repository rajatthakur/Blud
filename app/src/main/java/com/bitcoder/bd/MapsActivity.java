package com.bitcoder.bd;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker gps;
    private ArrayList<DonorNearby> nearbyDonors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        4422);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        gps = new GPSTracker(MapsActivity.this);
        nearbyDonors = (ArrayList<DonorNearby>) getIntent().getSerializableExtra("donor");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myLoc ;
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            myLoc = new LatLng(latitude,longitude);
            Log.d("he","kan");

        }else{
            myLoc = new LatLng(0,0);
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        mMap.addMarker(new MarkerOptions().position(myLoc).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        int len = nearbyDonors.size();
        Log.d("NearbyDonor2 ",nearbyDonors.get(1).getName());
        for(int i = 0;i < len; i++){
            DonorNearby donorNearby = nearbyDonors.get(i);
            Log.d("Marker",Integer.toString(i));
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(donorNearby.getLatitude(),donorNearby.getLongitude())).
                    title(donorNearby.getName()).snippet("Requires "+donorNearby.getBloodGroup()));
            marker.setTag(i);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

//                final Integer integerMarker = (Integer) marker.getTag();
                if(marker.getTitle().equals("My Location")){
                    return false;
                }
                int len = nearbyDonors.size();
                int i;
                for(i = 0;i < len; i++){
                    if(nearbyDonors.get(i).getName().equals(marker.getTitle())){
                        break;
                    }
                }
                final Integer integerMarker = i;
                Log.d("str:",marker.getTitle());
                DonorNearby donorNearby = nearbyDonors.get(integerMarker.intValue());
                String[] testArray = new String[] {"Blood Req. : "+donorNearby.getBloodGroup(),"Address : "+donorNearby.getAddress(),"City : "+donorNearby.getCity()
                        ,"Details : "+donorNearby.getDetails()};
                MaterialDialog.Builder builder = new MaterialDialog.Builder(MapsActivity.this).title(nearbyDonors.get(integerMarker.intValue()).getName())
                        .items(testArray).positiveText("Call").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DonorNearby donorNearby = nearbyDonors.get(integerMarker.intValue());
                        Log.d("Call ",donorNearby.getName());
                        String mobnew = donorNearby.getPhoneno();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+mobnew));

                        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(mobnew, ": PermError " );
                            //return;
                            if(ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.CALL_PHONE)){

                            }else{
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        123);
                            }
                            return;
                        }
                        startActivity(callIntent);
                    }
                });
                MaterialDialog dialog = builder.build();
                dialog.show();
                return false;
            }
        });
    }
}
