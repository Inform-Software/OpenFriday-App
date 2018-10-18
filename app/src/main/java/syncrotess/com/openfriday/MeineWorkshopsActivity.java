package syncrotess.com.openfriday;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeineWorkshopsActivity extends AppCompatActivity {
    Activity activity;
    TableLayout l1;
    String status = " Server offline";
    TextView et;

    Mgr mgr = new Mgr();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meineworkshops);
        l1 = findViewById(R.id.LinearLayout01);

        activity = this;
        et = (TextView) findViewById(R.id.tv_status);
        et.setText(status);
        new GetStatus().execute();
        mgr.addWorks(((OpenfridayApplication) activity.getApplication()).getMeineworks());
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

        if (mgr.getSize() != 0) {
            int size = mgr.getSize();
            Button[] bl = new Button[size];
            Button[] bb = new Button[size];
            TextView[] tv = new TextView[size];

            TableRow[] l2 = new TableRow[size];
            for (int i = 0; i < size; i++) {

                bb[i] = new Button(activity);
                mgr.put(bb[i], mgr.getWorkByIndex(i));
                bb[i].setText("Bearbeiten");
                bl[i] = new Button(activity);
                mgr.put(bl[i], mgr.getWorkByIndex(i));
                bl[i].setText("Löschen");
                l2[i] = new TableRow(activity);
                l2[i].setOrientation(LinearLayout.HORIZONTAL);
                tv[i] = new TextView(activity);


                tv[i].setText( mgr.getWorkByIndex(i).getThema());
                tv[i].setTextSize(20);

                l2[i].addView(tv[i]);

                l2[i].addView(bl[i]);
                l2[i].addView(bb[i]);
                l1.addView(l2[i]);

                bb[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View button) {

                        Workshop workshop = mgr.get(button);
                        ((OpenfridayApplication) activity.getApplication()).setWorkshop(workshop);
                        Intent intent = new Intent(MeineWorkshopsActivity.this, WorkshopBearbeitenActivity.class);
                        startActivity(intent);
                    }
                });
                bl[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View button) {
                        Workshop workshop = mgr.get(button);

                        ((OpenfridayApplication) activity.getApplication()).setWorkshop(workshop);
                        new delWorkshop().execute();
                    }


//                    int xi=view.getId();
//                       xi= xi-works.length;
//                        ((OpenfridayApplication)activity.getApplication()).setWorkshop(works[xy[xi]]);
//                        new delWorkshop().execute();                   }
                });

            }
        }

    }

    private class delWorkshop extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            try {
                String uri = new String(getString(R.string.url) + "workshop/" + ((OpenfridayApplication) activity.getApplication()).getWorkshop().getId());

                RestTemplate restTemplate = new RestTemplate(true);

                restTemplate.delete(uri);

            } catch (Exception e) {
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

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

            Intent intent = activity.getIntent();
            activity.finish();
            startActivity(intent);
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

    private class Mgr {
        private Map<View, Workshop> map = new HashMap<>();

        List<Workshop> works = new ArrayList<>();

        public void put(View view, Workshop work) {
            map.put(view, work);
        }

        public Workshop get(View view) {
            return map.get(view);
        }

        public void addWorks(Workshop[] meineworks) {
            for (Workshop work : meineworks) {
                works.add(work);
            }
        }

        public int getSize() {
            return works.size();
        }

        public Workshop getWorkByIndex(int i) {
            return works.get(i);
        }
    }
}
