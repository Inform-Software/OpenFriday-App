package syncrotess.com.openfriday;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
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


public class WorkshoperstellenActivity extends AppCompatActivity {


    Button zurueck;
    Button bestaetigen;
    EditText thema, name, beschreibung, raum;
    CheckBox session_1, session_2, session_3;
    Workshop work;
    Activity activity;
    String status = " Server offline";
    TextView et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshop_erstellen);
        activity = this;
        bestaetigen = findViewById(R.id.button_bestaestigen);
        zurueck = findViewById(R.id.button_zurueck);
        thema = findViewById(R.id.edit_thema);
        name = findViewById(R.id.edit_Name);
        raum = findViewById(R.id.edit_raum);
        session_1 = findViewById(R.id.check_session_1);
        session_2 = findViewById(R.id.check_session_2);
        session_3 = findViewById(R.id.check_session_3);
        beschreibung = findViewById(R.id.edit_beschreibung);
        name.setText(((OpenfridayApplication) activity.getApplication()).getUser().getName());
        work = new Workshop();
        activity = this;
        et = (TextView) findViewById(R.id.tv_status);
        et.setText(status);
        new GetStatus().execute();

        zurueck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == R.id.button_zurueck) {
                    activity.finish();
                    ;
                }
            }
        });
        bestaetigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == R.id.button_bestaestigen) {

                    if (thema.getText().length() > 0) {
                        work.setThema((thema.getText()).toString());
                        if (name.getText().length() > 0) {
                            work.setName((name.getText()).toString());
                            if (beschreibung.getText().length() > 0) {
                                work.setBeschreibung((beschreibung.getText()).toString());
                                work.setPersid(((OpenfridayApplication) activity.getApplication()).getUser().getId());
                                if (raum != null) {
                                    work.setRaum((raum.getText()).toString());
                                }
                                if (session_1.isChecked()) {
                                    boolean[] bar = work.getNotime();
                                    bar[0] = false;
                                    work.setNotime(bar);
                                } else {
                                    boolean[] bar = work.getNotime();
                                    bar[0] = true;
                                    work.setNotime(bar);
                                }

                                if (session_2.isChecked()) {
                                    boolean[] bar = work.getNotime();
                                    bar[1] = false;
                                    work.setNotime(bar);
                                } else {
                                    boolean[] bar = work.getNotime();
                                    bar[1] = true;
                                    work.setNotime(bar);
                                }
                                if (session_3.isChecked()) {
                                    boolean[] bar = work.getNotime();
                                    bar[2] = false;
                                    work.setNotime(bar);
                                } else {
                                    boolean[] bar = work.getNotime();
                                    bar[2] = true;
                                    work.setNotime(bar);
                                }
                                if (!(work.getNotime()[0])) {
                                    if (!(work.getNotime()[1])) {
                                        if (!(work.getNotime()[2])) {
                                            Toast.makeText(getApplicationContext(), "Sie müssen mindestens zu einer Session gehen können", Toast.LENGTH_SHORT).show();
                                        } else {
                                            new PostWorkshop().execute();
                                        }
                                    } else {
                                        new PostWorkshop().execute();
                                    }
                                } else {
                                    new PostWorkshop().execute();
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

        });
    }

    private class PostWorkshop extends AsyncTask<Void, Void, Workshop> {


        @Override
        protected Workshop doInBackground(Void... params) {

            try {
                String uri = new String(getString(R.string.url) + "workshop");
                HttpEntity<Workshop> requestEntity = new HttpEntity<Workshop>(work);
                RestTemplate restTemplate = new RestTemplate(true);

                ResponseEntity<Workshop> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,
                        Workshop.class);
                return response.getBody();
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Workshop user) {
            ((OpenfridayApplication) activity.getApplication()).setWorkshop(user);
            activity.finish();
        }


    }

    private class GetStatus extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            try {
                String url = new String(getString(R.string.url) + "status");

                RestTemplate restTemplate = new RestTemplate(true);
                ResponseEntity<syncrotess.com.openfriday.Status> response = restTemplate.getForEntity(url, syncrotess.com.openfriday.Status.class);

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




