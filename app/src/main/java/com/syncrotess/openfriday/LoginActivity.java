package com.syncrotess.openfriday;

import android.app.Activity;
import android.os.Environment;
import android.content.Intent;

import android.os.AsyncTask;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;


public class LoginActivity extends AppCompatActivity {
    Button confirmButton;
    EditText editText;
    File folder;
    int i = 1;
    TextView statusText;
    String status = " Server offline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        statusText = findViewById(R.id.tv_status);
        statusText.setText(status);
        new GetStatus().execute();

        folder = new File(Environment.getExternalStorageDirectory(), "Name");
        confirmButton = findViewById(R.id.confirm_login);
        editText = findViewById(R.id.editText);


        confirmButton.setOnClickListener(view -> {
            int ce = view.getId();
            if (ce == R.id.confirm_login) {
                if (editText.getText().length() > 0) {

                    new LoginPost().execute();


                } else {
                    Toast.makeText(getApplicationContext(), "Bitte gib Deinen Namen ein!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editText.setOnEditorActionListener((view, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editText.getText().length() > 0) {

                    new LoginPost().execute();


                } else {
                    Toast.makeText(getApplicationContext(), "Bitte gib Deinen Namen ein!", Toast.LENGTH_SHORT).show();
                }
            }

            return handled;
        });


    }

    private class LoginPost extends AsyncTask<Void, Void, User> {


        @Override
        protected User doInBackground(Void... params) {
            User user = new User();
            try {
                String name = editText.getText().toString();

                user.setName(name);
                String url = new String(getString(R.string.url) + "user");
                HttpEntity<User> entity = new HttpEntity<User>(user);
                RestTemplate restTemplate = new RestTemplate(true);
                //restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
                ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.POST, entity, User.class);
                user = response.getBody();
                ((OpenfridayApplication) getApplication()).setUser(response.getBody());
            } catch (Exception e) {
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user.isAdmin()) {
                Intent intent = new Intent(LoginActivity.this, AdminMenueActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, MenueActivity.class);
                startActivity(intent);
            }
        }
    }

    private class GetStatus extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            try {
                String url = new String(getString(R.string.url) + "status");
                Log.i("Cleanup", "URL: " + url);
                RestTemplate restTemplate = new RestTemplate(true);
                ResponseEntity<com.syncrotess.openfriday.Status> response = restTemplate.getForEntity(url, com.syncrotess.openfriday.Status.class);

                ((OpenfridayApplication) getApplication()).setStatus(response.getBody());
                status = response.getBody().getKommentar();
                return status;
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            statusText.setText("Status: " + status);
        }
    }

}