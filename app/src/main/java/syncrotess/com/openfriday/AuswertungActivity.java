package syncrotess.com.openfriday;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.se.omapi.Session;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.StackView;
import android.widget.TableRow;
import android.widget.TextView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.java_websocket.WebSocket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class AuswertungActivity extends AppCompatActivity {
    String status = " Server offline";
    TextView et;
    Activity activity;
    Workshop[] workshops;
    String[] raeume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_auswertung);
        et = (TextView) findViewById(R.id.tv_status);
        et.setText(status);
        new GetStatus().execute();

        Button zurueck = findViewById(R.id.button_back);
        zurueck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == R.id.button_back) {
                    activity.finish();
                }
            }
        });

        buildplan();
    }

    private void buildplan() {
        workshops = ((OpenfridayApplication) this.getApplication()).getMeineworks();
        raeume = ((OpenfridayApplication) this.getApplication()).getStatus().getRäume();
        for (int i = 0; i < workshops.length; i++) {
            boolean contains = false;
            for (int x = 0; x < raeume.length; x++) {
                if ((workshops[i].getRaum()) != null&&workshops[i].isSelected()) {
                    if (raeume[x].equals(workshops[i].getRaum())) {
                        contains = true;
                    }
                }

            }
            if (contains) {
                String[] strar = new String[raeume.length + 1];
                for (int x = 0; x < raeume.length; x++) {
                    strar[x] = raeume[x];
                }
                strar[raeume.length] = workshops[i].getRaum();
                raeume = strar;
            }
        }

        TableRow tableRowRaum1 = findViewById(R.id.tablerow_session_1_raum);
        TableRow tableRowRaum2 = findViewById(R.id.tablerow_session_2_raum);
        TableRow tableRowRaum3 = findViewById(R.id.tablerow_session_3_raum);
        TextView[] textViewsRaeume1 = new TextView[raeume.length];
        TextView[] textViewsRaeume2 = new TextView[raeume.length];
        TextView[] textViewsRaeume3 = new TextView[raeume.length];
        for (int i = 0; i < raeume.length; i++) {
            textViewsRaeume1[i] = new TextView(activity);
            textViewsRaeume1[i].setText(raeume[i] + "   ");
            tableRowRaum1.addView(textViewsRaeume1[i]);
            textViewsRaeume2[i] = new TextView(activity);
            textViewsRaeume2[i].setText(raeume[i] + "   ");
            tableRowRaum2.addView(textViewsRaeume2[i]);
            textViewsRaeume3[i] = new TextView(activity);
            textViewsRaeume3[i].setText(raeume[i] + "   ");
            tableRowRaum3.addView(textViewsRaeume3[i]);
        }
        TableRow tableRowWork1 = findViewById(R.id.tablerow_session_1_work);
        TableRow tableRowWork2 = findViewById(R.id.tablerow_session_2_work);
        TableRow tableRowWork3 = findViewById(R.id.tablerow_session_3_work);
        Workshop[] session1 = new Workshop[raeume.length];
        Workshop[] session2 = new Workshop[raeume.length];
        Workshop[] session3 = new Workshop[raeume.length];
        for (int i = 0; i < workshops.length; i++) {
            if (workshops[i].isSelected()) {
                if (1 == workshops[i].getSession()) {
                    for (int x = 0; x < raeume.length; x++) {
                        if (raeume.equals(workshops[i].getRaum())) {
                            session1[x] = workshops[i];
                        }
                    }
                } else if (2 == workshops[i].getSession()) {
                    for (int x = 0; x < raeume.length; x++) {
                        if (raeume.equals(workshops[i].getRaum())) {
                            session2[x] = workshops[i];
                        }
                    }
                } else if (3 == workshops[i].getSession()) {
                    for (int x = 0; x < raeume.length; x++) {
                        if (raeume.equals(workshops[i].getRaum())) {
                            session3[x] = workshops[i];
                        }
                    }
                }
            }
        }
        TextView[] textViewsWork1 = new TextView[raeume.length];
        TextView[] textViewsWork2 = new TextView[raeume.length];
        TextView[] textViewsWork3 = new TextView[raeume.length];
        for (int i = 0; i < session1.length; i++) {
            textViewsWork1[i] = new TextView(activity);
            if (session1[i] != null) {
                textViewsWork1[i].setText(session1[i].getThema() + "   ");
            } else {
                textViewsWork1[i].setText("Kein Workshop   ");
            }
            tableRowWork1.addView(textViewsWork1[i]);
            textViewsWork2[i] = new TextView(activity);
            if (session2[i] != null) {
                textViewsWork2[i].setText(session2[i].getThema() + "   ");
            } else {
                textViewsWork2[i].setText("Kein Workshop   ");
            }
            tableRowWork2.addView(textViewsWork2[i]);
            textViewsWork3[i] = new TextView(activity);
            if (session3[i] != null) {
                textViewsWork3[i].setText(session3[i].getThema() + "   ");
            } else {
                textViewsWork3[i].setText("Kein Workshop   ");
            }
            tableRowWork3.addView(textViewsWork3[i]);
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
