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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    private EditText password;
    private EditText email;
    private Button button;
    private TextView textView;
    private GPSTracker gps;
    private ImageView imageView;
    public static int imagesLog[] = {
            R.drawable.graphic1,
            R.drawable.graphic2,
            R.drawable.graphic3,
            R.drawable.graphic4,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password = (EditText) findViewById(R.id.LoginInputPassword);
        email = (EditText) findViewById(R.id.input_email);
        textView = (TextView) findViewById(R.id.link_signup);
        button = (Button) findViewById(R.id.btn_login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()){
                    Snackbar.make(findViewById(android.R.id.content), "Login Failed", Snackbar.LENGTH_LONG).show();
                    return;
                }
                double latitude, longitude;
                latitude = longitude = 0;
                gps = new GPSTracker(LoginActivity.this);
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d("he", "kan");

                } else {
                    gps.showSettingsAlert();

                }
                new LoginRequest().execute(email.getText().toString(),password.getText().toString(),Double.toString(latitude),Double.toString(longitude));


            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
        imageView = (ImageView) findViewById(R.id.imageViewLogin);
        Random rnd = new Random(System.currentTimeMillis());
        imageView.setImageResource(imagesLog[rnd.nextInt(4)]);

    }
    public boolean validate() {
        boolean valid = true;

        String emailInp = email.getText().toString();
        String passwordInp = password.getText().toString();

        if (emailInp.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInp).matches()) {
            email.setError("Enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (passwordInp.isEmpty() || passwordInp.length() < 6) {
            password.setError("At least 6 Characters");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }

    public class LoginRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://donateblood.southindia.cloudapp.azure.com/login/");
                String email,password;
                email = arg0[0];
                password = arg0[1];

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String postParams = "email="+email+"&password="+password+
                        "&latitude=" + arg0[2] + "&longitude=" + arg0[3];
                OutputStream os = conn.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                Log.d("posted",postParams);
                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
                    Snackbar.make(findViewById(android.R.id.content), "Login Failed", Snackbar.LENGTH_LONG).show();
                }
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader ina = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer stringBuffer = new StringBuffer("");
                    String line = "";
                    while ((line = ina.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    ina.close();
                    return stringBuffer.toString();
                }else{
                    Snackbar.make(findViewById(android.R.id.content), "Login Failed", Snackbar.LENGTH_LONG).show();
                }
                return null;
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("result--",result);
                if(result==null){
                    return;
                }
            try {
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equals("0")){
                    Snackbar.make(findViewById(android.R.id.content), "Login Failed", Snackbar.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("phone",jsonObject.getString("phone"));
                intent.putExtra("name",jsonObject.getString("name"));
                intent.putExtra("email",jsonObject.getString("email"));
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
