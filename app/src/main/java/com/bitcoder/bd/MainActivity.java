package com.bitcoder.bd;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private EditText bloodGroupET,addressET,cityET,detailsET;
    private Spinner spinner;
    private GPSTracker gps;
    private FloatingActionButton floatingActionButton;
    public static int imagesNav[] = {
            R.drawable.graphic1a,
            R.drawable.graphic2a,
            R.drawable.graphic3a,
            R.drawable.graphic4a,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Button button = (Button) findViewById(R.id.buttonCreateRequest);

        //bloodGroupET = (EditText) findViewById(R.id.inputBloodGroup);
        addressET = (EditText) findViewById(R.id.inputAddress);
        cityET = (EditText) findViewById(R.id.inputCity);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        detailsET = (EditText) findViewById(R.id.inputDetails);


        View hView =  navigationView.getHeaderView(0);
        TextView tvEmail = (TextView)hView.findViewById(R.id.navHeaderEmail);
        TextView tvName = (TextView) hView.findViewById(R.id.navHeaderName);
        tvEmail.setText(getIntent().getStringExtra("email"));
        tvName.setText(getIntent().getStringExtra("name"));
        ImageView imageView = (ImageView) hView.findViewById(R.id.imageViewNavHeader);
        Random rnd = new Random(System.currentTimeMillis());
        imageView.setImageResource(imagesNav[(rnd.nextInt(4))]);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt("Blood Group".toString());
        // Spinner click listener
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();

        categories.add("A+");
        categories.add("A-");
        categories.add("B+");
        categories.add("B-");
        categories.add("AB+");
        categories.add("AB-");
        categories.add("O+");
        categories.add("O-");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        final DonorNearby donorNearby = new DonorNearby();
        donorNearby.setBloodGroup("A+");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Log.d("sp",item);
                donorNearby.setBloodGroup(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(dataAdapter);
        //Log.d("Bundle",bundle.getString("item"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validate())
                    return;
                String detText;
                if(detailsET.getText()!=null){
                    detText = detailsET.getText().toString();
                }else{
                    detText = "N/A";
                }
                double latitude ,longitude;
                latitude = longitude = 0;
                if(gps.canGetLocation()){
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d("he","kan");
                }else{
                    gps.showSettingsAlert();
                }

                new SendPostRequest().execute(donorNearby.getBloodGroup(),addressET.getText().toString(),cityET.getText().toString(),detText,new Double(latitude).toString(),new Double(longitude).toString());
            }
        });
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        4422);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        gps = new GPSTracker(MainActivity.this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude,longitude;
                latitude = longitude = 0;
                if(gps.canGetLocation()){

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                }else{
                    gps.showSettingsAlert();

                }
                new TestLocal().execute();
//                new FindDonors().execute(new Double(latitude).toString(),new Double(longitude).toString());
            }
        });

    }
    public boolean validate() {
        boolean valid = true;
        //String bloodGroup = bloodGroupET.getText().toString();
        String address = addressET.getText().toString();
        String city = cityET.getText().toString();
        String otherdetails = detailsET.getText().toString();

//        if (bloodGroup.isEmpty()) {
//            this.bloodGroupET.setError("Enter a valid email address");
//            valid = false;
//        } else {
//            this.bloodGroupET.setError(null);
//        }

        if (address.isEmpty()) {
            this.addressET.setError("Enter a valid address");
            valid = false;
        } else {
            this.addressET.setError(null);
        }
        if(city.isEmpty()){
            valid = false;
            this.cityET.setError("Enter a valid city");
        }else{
            this.cityET.setError(null);
        }

        return valid;
    }


    public class SendGetRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {
                int keyno = 4;
                String postParams = "pk="+Integer.toString(keyno);
                URL url = new URL("http://donateblood.southindia.cloudapp.azure.com"+"/detail/"+Integer.toString(keyno)+"/"); // here is your URL path

                String name = "abc";

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");


                OutputStream os = conn.getOutputStream();
                //os.write(postParams.getBytes());
                os.flush();
                os.close();
                Log.d("Sent :",postParams);
                int responseCode=conn.getResponseCode();
                Log.d("Code-",Integer.toString(responseCode));
                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader ina=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sba = new StringBuffer("");
                    String linea="";

                    while((linea = ina.readLine()) != null) {
                        sba.append(linea);
                    }
                    Log.d("Response(rt)",sba.toString());
                    ina.close();
                    return sba.toString();

                }

                return  null;
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Result :",result);

        }
    }

    public class FindDonors extends AsyncTask<String, String, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://donateblood.southindia.cloudapp.azure.com/nearby/"); // here is your URL path

                String latitude,longitude;
                latitude = arg0[0];
                longitude = arg0[1];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String postParams = "latitude="+latitude+"&longitude="+longitude;
                OutputStream os = conn.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                Log.d("Sent :",postParams);
                int responseCode=conn.getResponseCode();
                Log.d("Code-",Integer.toString(responseCode));
                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader ina=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sba = new StringBuffer("");
                    String linea="";

                    while((linea = ina.readLine()) != null) {
                        sba.append(linea);
                    }
                    Log.d("Response(near)",sba.toString());
                    ina.close();
                    return sba.toString();
                }

                return  null;
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(jsonArray==null){
                return;
            }
            ArrayList<DonorNearby> nearbyDonors = new ArrayList<>();

            int length = jsonArray.length();
            try {
                for(int i = 0;i < length; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    DonorNearby donorNearby = new DonorNearby(jsonObject.getString("address"),
                            jsonObject.getString("blood_group"),jsonObject.getString("city"),
                            jsonObject.getString("details"),Double.parseDouble(jsonObject.getString("latitude")),
                            Double.parseDouble(jsonObject.getString("longitude")),jsonObject.getString("name")
                            ,jsonObject.getString("phone"),jsonObject.getString("requested_on")
                            );
                    nearbyDonors.add(donorNearby);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(MainActivity.this,MapsActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtra("donor",nearbyDonors);
            startActivity(intent);

        }
    }
    public class TestLocal extends AsyncTask<String, String, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://10.0.2.2:8000/stocks/"); // here is your URL path

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String postParams = "ticker=FB";
                OutputStream os = conn.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                Log.d("Sent :",postParams);
                int responseCode=conn.getResponseCode();
                Log.d("Code-",Integer.toString(responseCode));
                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader ina=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sba = new StringBuffer("");
                    String linea="";

                    while((linea = ina.readLine()) != null) {
                        sba.append(linea);
                    }
                    Log.d("Response(near)",sba.toString());
                    ina.close();
                    return sba.toString();
                }
                return  "null";
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Rec : ",result);
        }
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("http://donateblood.southindia.cloudapp.azure.com"); // here is your URL path
                String phoneNo = getIntent().getStringExtra("phone");
                String bloodGroup,details,city,address;
                address = arg0[1];
                bloodGroup = arg0[0].replace("+","%2B");
                details = arg0[3];
                if(details==null){
                    details = "N/A";
                }
                city = arg0[2];
                double latitude,longitude;
                latitude = longitude = 0;

                String name = getIntent().getStringExtra("name");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String lati = arg0[4];
                if(lati.length()>5){
                    lati = lati.substring(0,5);
                }
                String longi = arg0[5];
                if(longi.length()>5){
                    longi = longi.substring(0,5);
                }
                String postParams = "name="+name+"&blood_group="+bloodGroup+"&phone=" + phoneNo +"&details="+details+"&address="+address+"&city="+city+
                                "&latitude="+lati+"&longitude="+longi;
                OutputStream os = conn.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                Log.d("Sent :",postParams);
                int responseCode=conn.getResponseCode();
                Log.d("Code-",Integer.toString(responseCode));
                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader ina=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));
                    StringBuffer sba = new StringBuffer("");
                    String linea="";
                    while((linea = ina.readLine()) != null) {
                        sba.append(linea);
                    }
                    Log.d("Response(rt)",sba.toString());
                    Snackbar.make(findViewById(android.R.id.content), "Your Request Has Been Recorded", Snackbar.LENGTH_LONG).show();
                    ina.close();
                    return sba.toString();
                }
                return  null;
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        } else if (id == R.id.nav_share) {
            String shareBody = "Download our app";
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

        } else if (id == R.id.nav_about) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this).title("About Us").content("This app was developed by Deepak Kar,Aakash And Rajat P Thakur for Code Fun Do 2017");
            MaterialDialog dialog = builder.build();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
