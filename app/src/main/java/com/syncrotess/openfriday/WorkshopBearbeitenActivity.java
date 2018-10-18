package com.syncrotess.openfriday;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class WorkshopBearbeitenActivity extends AppCompatActivity {
    Button zurueck;
    Button bestaetigen;
    EditText thema, name, beschreibung, raum;
    CheckBox session_1, session_2, session_3;
    Activity activity;
    String status = " Server offline";
    TextView et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshop_bearbeiten);
        activity = this;
        et = (TextView) findViewById(R.id.tv_status);
        et.setText(status);
        new GetStatus().execute();
        bestaetigen = findViewById(R.id.button_bestaestigen);
        zurueck = findViewById(R.id.button_zurueck);
        thema = findViewById(R.id.edit_thema);
        name = findViewById(R.id.edit_Name);
        raum = findViewById(R.id.edit_raum);
        session_1 = findViewById(R.id.check_session_1);
        session_2 = findViewById(R.id.check_session_2);
        session_3 = findViewById(R.id.check_session_3);
        beschreibung = findViewById(R.id.edit_beschreibung);
        name.setText(((OpenfridayApplication) activity.getApplication()).getWorkshop().getName());
        thema.setText(((OpenfridayApplication) activity.getApplication()).getWorkshop().getThema());
        beschreibung.setText(((OpenfridayApplication) activity.getApplication()).getWorkshop().getBeschreibung());
        raum.setText(((OpenfridayApplication) activity.getApplication()).getWorkshop().getRaum());
        if (!(((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime()[0])) {
            session_1.setChecked(true);
        }
        if (!(((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime()[1])) {
            session_2.setChecked(true);
        }
        if (!(((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime()[2])) {
            session_3.setChecked(true);
        }
        zurueck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == R.id.button_zurueck) {
                    new GetMyWorkshops().execute();
                }
            }
        });
        bestaetigen.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == R.id.button_bestaestigen) {

                    if (thema.getText().length() > 0) {
                        ((OpenfridayApplication) activity.getApplication()).getWorkshop().setThema((String.valueOf(thema.getText())));
                        if (name.getText().length() > 0) {

                            ((OpenfridayApplication) activity.getApplication()).getWorkshop().setName((String.valueOf(name.getText())));
                            if (beschreibung.getText().length() > 0) {
                                ((OpenfridayApplication) activity.getApplication()).getWorkshop().setBeschreibung((String.valueOf(beschreibung.getText())));
                                if (raum != null) {
                                    ((OpenfridayApplication) activity.getApplication()).getWorkshop().setRaum((raum.getText()).toString());
                                }
                                if (session_1.isChecked()) {
                                    boolean[] bar = ((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime();
                                    bar[0] = false;
                                    ((OpenfridayApplication) activity.getApplication()).getWorkshop().setNotime(bar);
                                } else {
                                    boolean[] bar = ((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime();
                                    bar[0] = true;
                                    ((OpenfridayApplication) activity.getApplication()).getWorkshop().setNotime(bar);
                                }
                                if (session_2.isChecked()) {
                                    boolean[] bar = ((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime();
                                    bar[1] = false;
                                    ((OpenfridayApplication) activity.getApplication()).getWorkshop().setNotime(bar);
                                } else {
                                    boolean[] bar = ((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime();
                                    bar[1] = true;
                                    ((OpenfridayApplication) activity.getApplication()).getWorkshop().setNotime(bar);
                                }
                                if (session_3.isChecked()) {
                                    boolean[] bar = ((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime();
                                    bar[2] = false;
                                    ((OpenfridayApplication) activity.getApplication()).getWorkshop().setNotime(bar);
                                } else {
                                    boolean[] bar = ((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime();
                                    bar[2] = true;
                                    ((OpenfridayApplication) activity.getApplication()).getWorkshop().setNotime(bar);
                                }
                                if (!((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime()[0]) {
                                    if (!((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime()[1]) {
                                        if (!((OpenfridayApplication) activity.getApplication()).getWorkshop().getNotime()[2]) {
                                            Toast.makeText(getApplicationContext(), "Sie müssen mindestens zu einer Session gehen können", Toast.LENGTH_SHORT).show();
                                        } else {
                                            new PutWorkshop().execute();
                                        }
                                    } else {
                                        new PutWorkshop().execute();
                                    }
                                } else {
                                    new PutWorkshop().execute();
                                }


                            } else {
                                Toast.makeText(getApplicationContext(), "Bitte geben Sie eine Beschreibung an", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Bitte gebe Sie einen Namen ein!", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Bitte geben Sie ein Thema an!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
        ));
    }

    private class PutWorkshop extends AsyncTask<Void, Void, Workshop> {


        @Override
        protected Workshop doInBackground(Void... params) {

            try {
                String uri = new String(getString(R.string.url) + "workshop/" + (((OpenfridayApplication) activity.getApplication()).getWorkshop().getId()));
                HttpEntity<Workshop> requestEntity = new HttpEntity<Workshop>(((OpenfridayApplication) activity.getApplication()).getWorkshop());
                RestTemplate restTemplate = new RestTemplate(true);

                ResponseEntity<Workshop> response = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity,
                        Workshop.class);
                return response.getBody();
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Workshop user) {
            ((OpenfridayApplication) activity.getApplication()).setWorkshop(user);
            new GetMyWorkshops().execute();
        }


    }

    private class GetMyWorkshops extends AsyncTask<Void, Void, Workshop[]> {


        @Override
        protected Workshop[] doInBackground(Void... params) {

            try {
                String uri = new String(getString(R.string.url) + "workshop/user/" + (((OpenfridayApplication) activity.getApplication()).getUser()).getId()).toString();
                RestTemplate restTemplate = new RestTemplate(true);

                ResponseEntity<Workshop[]> responseEntity = restTemplate.getForEntity(uri, Workshop[].class);
                Workshop[] work = responseEntity.getBody();
                ((OpenfridayApplication) activity.getApplication()).setMeineworks(work);
                return responseEntity.getBody();
            } catch (Exception e) {
                int i = 5;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Workshop[] workshops) {

            activity.finish();
        }


    }

    private class GetStatus extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            try {
                String url = new String(getString(R.string.url) + "status");

                RestTemplate restTemplate = new RestTemplate(true);
                ResponseEntity<com.syncrotess.openfriday.Status> response = restTemplate.getForEntity(url, com.syncrotess.openfriday.Status.class);

                ((OpenfridayApplication) activity.getApplication()).setStatus(response.getBody());
                status = response.getBody().getKommentar();
                int i;
                return status;
            } catch (Exception e) {
                int i = 6;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            et.setText("Status: " + status);
        }
    }
}
