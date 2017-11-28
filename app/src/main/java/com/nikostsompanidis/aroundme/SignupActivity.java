package com.nikostsompanidis.aroundme;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;

    Button _signupButton;
    TextView _loginLink;
    String json_string="";




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        _nameText=(EditText)findViewById(R.id.input_name);
        _emailText=(EditText)findViewById(R.id.input_email);
        _passwordText=(EditText)findViewById(R.id.input_password);
        _signupButton=(Button)findViewById(R.id.btn_signup);
        _loginLink=(TextView)findViewById(R.id.link_login) ;


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        SignupActivity.BackgroundTask backgroundTask=new BackgroundTask(SignupActivity.this);
                        try {
                            String result =backgroundTask.execute("register",name,email,password).get();
                            JSONObject json = new JSONObject(result);
                            String error = json.getString("error");
                            Log.i("success",error);
                            if(error.equals("false")) {
                                onSignupSuccess();
                            }else
                                onSignupFailed();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 4000);
    }


    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Successful Sign Up", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign Up failed , the email is used ", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 20) {
            _passwordText.setError("between 6 and 20 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private class BackgroundTask extends AsyncTask<String,Void,String> {

        Context ctx;
        BackgroundTask(Context ctx){

            this.ctx=ctx;
        }
        protected String doInBackground(String... params) {

            String reg_url="http://aroundme-application.000webhostapp.com/register.php";
            String method= params[0];
            if(method.equals("register")){


                String name=params[1];
                String email=params[2];
                String password=params[3];

                try {
                    URL url= new URL(reg_url);
                    HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream os= httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

                    String data= URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"+
                            URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                            URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    os.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((json_string = bufferedReader.readLine()) != null) {
                        stringBuilder.append(json_string + "\n");
                    }

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    Log.i("json",stringBuilder.toString().trim());
                    return stringBuilder.toString().trim();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }


    }

}