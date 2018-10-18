package syncrotess.com.openfriday;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.design.widget.CoordinatorLayout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MenueActivity extends AppCompatActivity {
    Activity activity;
    Button abstimmmung;
    Button auswertung;
    Button meineworkshops;
    Button workshoperstellen;
    Button logout;
    TextView name;
    File ordner;
    TextView et;
    String status;
    int ie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);
        et = (TextView) findViewById(R.id.tv_status);
        new GetStatus().execute();
        activity = this;
        name = (TextView) findViewById(R.id.tv_name);
        abstimmmung = (Button) findViewById(R.id.button_abstimmung);
        auswertung = (Button) findViewById(R.id.button_auswertung);
        meineworkshops = (Button) findViewById(R.id.button_meineworkshops);
        workshoperstellen = (Button) findViewById(R.id.button_workshoperstellen);
        logout = (Button) findViewById(R.id.button_logout);
        ordner = new File(Environment.getExternalStorageDirectory(), "Name");
        File myfile = new File("namen.txt");
        name = (TextView) findViewById(R.id.tv_name);
        name.setText("Name: " + (((OpenfridayApplication) this.getApplication()).getUser()).getName());

        abstimmmung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ie = view.getId();

                if (ie == R.id.button_abstimmung) {
                    new GetAllWorkshops().execute();
                }
            }
        });
        auswertung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ie = view.getId();

                if (ie == R.id.button_auswertung) {
                    new GetAllWorkshops().execute();

                }
            }
        });
        meineworkshops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == R.id.button_meineworkshops) {
                    new GetMyWorkshops().execute();
                }
            }
        });
        workshoperstellen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == R.id.button_workshoperstellen) {
                    Intent intent = new Intent(MenueActivity.this, WorkshoperstellenActivity.class);
                    startActivity(intent);
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == R.id.button_logout) {
                    activity.finish();
                }
            }
        });


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

            Intent intent = new Intent(MenueActivity.this, MeineWorkshopsActivity.class);
            startActivity(intent);
        }


    }

    private class GetAllWorkshops extends AsyncTask<Void, Void, Workshop[]> {


        @Override
        protected Workshop[] doInBackground(Void... params) {

            try {
                String uri = new String(getString(R.string.url) + "/workshop");
                RestTemplate restTemplate = new RestTemplate(true);

                ResponseEntity<Workshop[]> responseEntity = restTemplate.getForEntity(uri, Workshop[].class);
                ((OpenfridayApplication) activity.getApplication()).setMeineworks(responseEntity.getBody());
                return responseEntity.getBody();
            } catch (Exception e) {
                int i = 5;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Workshop[] workshops) {
            if (ie == R.id.button_abstimmung) {
                Intent intent = new Intent(MenueActivity.this, AbstimmungActivity.class);
                startActivity(intent);
            } else if (ie == R.id.button_auswertung) {
                Intent intent = new Intent(MenueActivity.this, AuswertungActivity.class);
                startActivity(intent);
            }
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