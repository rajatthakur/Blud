package com.bitcoder.bd;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {
    private EditText name,email,password,mobile;
    private Button signUpButton;
    private TextView textView;
    private GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = (EditText) findViewById(R.id.signUpName);
        email = (EditText) findViewById(R.id.signUpEmail);
        password = (EditText) findViewById(R.id.signUpPassword);
        mobile = (EditText) findViewById(R.id.signUpMobile);
        textView = (TextView) findViewById(R.id.link_login);
        signUpButton = (Button) findViewById(R.id.btn_signup);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()){
                    Snackbar.make(findViewById(android.R.id.content), "Registration Failed", Snackbar.LENGTH_LONG).show();
                    return;
                }
                gps = new GPSTracker(SignUpActivity.this);
                double latitude,longitude;
                latitude = longitude = 0;
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                } else {
                    gps.showSettingsAlert();

                }
                new SignUpRequest().execute(email.getText().toString(),password.getText().toString(),mobile.getText().toString(),name.getText().toString(),new Double(latitude).toString(),new Double(longitude).toString());
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }
        });

    }
    public boolean validate() {
        boolean valid = true;

        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String name = this.name.getText().toString();
        String phone = this.mobile.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Enter a valid email address");
            valid = false;
        } else {
            this.email.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            this.password.setError("At least 6 characters");
            valid = false;
        } else {
            this.password.setError(null);
        }
        if(name.isEmpty()||name.length() < 3){
            valid = false;
            this.name.setError("At least 3 characters");
        }else{
            this.name.setError(null);
        }
        if(phone.isEmpty()||phone.length() < 6){
            valid = false;
            this.mobile.setError("Enter a valid number");
        }else{
            this.mobile.setError(null);
        }
        return valid;
    }

    public class SignUpRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            try {
                String latitude, longitude;
                URL url = new URL("http://donateblood.southindia.cloudapp.azure.com/register/");
                String email,password,name,mobile;
                email = arg0[0];
                password = arg0[1];
                name = arg0[3];
                mobile = arg0[2];
                latitude = arg0[4];
                longitude = arg0[5];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String postParams =
                        "name="+name + "&email="+email+"&phone="+mobile+"&latitude=" +
                        latitude + "&longitude=" + longitude
                        + "&password="+password;

                //name=Rj&email=abcd@pq.com&phone=8899&latitude=78.8&longitude=80.2&password=qwerty
                OutputStream os = conn.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                Log.d("RE",postParams);
                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
                    Snackbar.make(findViewById(android.R.id.content), "Failed : Email Already In Use", Snackbar.LENGTH_LONG).show();
                }
                Log.d("Resp ",Integer.toString(responseCode));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader ina = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer stringBuffer = new StringBuffer("");
                    String line = "";
                    while ((line = ina.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    ina.close();
                    return stringBuffer.toString();
                }
                return null;
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if(result==null){
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equals("0")){
                    Snackbar.make(findViewById(android.R.id.content), "Failed : Email Already In Use", Snackbar.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(findViewById(android.R.id.content), "Registration Success", Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);

                startActivity(intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
