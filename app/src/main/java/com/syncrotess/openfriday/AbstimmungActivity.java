package com.syncrotess.openfriday;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.syncrotess.openfriday.R.*;


public class AbstimmungActivity extends AppCompatActivity {
    Activity activity;
    Workshop[] works;
    String status = " Server offline";
    TextView et;
    TableLayout l1;
    Mgr mgr = new Mgr();
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_abstimmung);

        activity = this;
        et = (TextView) findViewById(id.tv_status);
        et.setText(status);
        new GetStatus().execute();
        l1 = findViewById(id.LinearLayout01);

        activity = this;
        et = (TextView) findViewById(id.tv_status);
        et.setText(status);
        new GetStatus().execute();
        mgr.addWorks(((OpenfridayApplication) activity.getApplication()).getMeineworks());
        Button zurueck = findViewById(id.button_back);
        zurueck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == id.button_back) {
                    activity.finish();
                }
            }
        });
        Button bestaetigen = findViewById(id.button_bestaestigen);
        bestaetigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ce = view.getId();

                if (ce == id.button_bestaestigen) {
                    Stack<Workshop> stack = mgr.getAuswahl();
                    Long[] auswahl = new Long[stack.size()];
                    int stackszize = stack.size();
                    for (int i = 0; i < stackszize; i++) {
                        auswahl[i] = stack.pop().getId();
                    }
                    ((OpenfridayApplication) activity.getApplication()).getUser().setWorkids(auswahl);
                    new PutUser().execute();
                    new PutWorkshop().execute();
                }

            }
        });

        if (mgr.getSize() != 0) {
            int size = mgr.getSize();
            ToggleButton[] bb = new ToggleButton[size];
            Button[] bl = new Button[size];
            TextView[] tv = new TextView[size];
            Long[] workids = ((OpenfridayApplication) activity.getApplication()).getUser().getWorkids();
            TableRow[] l2 = new TableRow[size];
            for (int i = 0; i < size; i++) {

                bl[i] = new Button(activity);
                Workshop workshop = mgr.getWorkByIndex(i);
                mgr.put(bl[i], mgr.getWorkByIndex(i));
                bb[i] = new ToggleButton(activity);
                bb[i].setText("nicht Ausgewählt");
                bb[i].setTextOff("nicht Ausgewählt");
                bb[i].setTextOn("Ausgewählt");
                mgr.put(bb[i], mgr.getWorkByIndex(i));
                if (workids != null) {
                    if (Arrays.asList(workids).contains(workshop.getId())) {
                        bb[i].setChecked(true);
                        mgr.setCountVotes(mgr.getCountVotes() + 1);
                        mgr.putAuswahl(bb[i], workshop);
                    }
                }
                bl[i].setText("Details");
                l2[i] = new TableRow(activity);
                l2[i].setOrientation(LinearLayout.HORIZONTAL);
                tv[i] = new TextView(activity);


                tv[i].setText("" + mgr.getWorkByIndex(i).getThema());
                tv[i].setTextSize(20);

                l2[i].addView(tv[i]);

                l2[i].addView(bl[i]);
                l2[i].addView(bb[i]);
                l1.addView(l2[i]);

                bl[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View button) {

                        Workshop workshop = mgr.get(button);
                        View popupView = getLayoutInflater().inflate(layout.popup_details, null);

                        PopupWindow popupWindow = new PopupWindow(popupView,
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView tv_thema = popupView.findViewById(id.tv_thema);
                        tv_thema.setText(workshop.getThema());
                        TextView tv_name = popupView.findViewById(id.tv_name);
                        tv_name.setText(workshop.getName());
                        TextView tv_beschreibung = popupView.findViewById(id.tv_beschreibung);
                        tv_beschreibung.setText(workshop.getBeschreibung());
                        popupWindow.setFocusable(true);

                        popupWindow.setBackgroundDrawable(new ColorDrawable());


                        int location[] = new int[2];


                        button.getLocationOnScreen(location);

                        // Using location, the PopupWindow will be displayed right under anchorView
                        popupWindow.showAtLocation(button, Gravity.NO_GRAVITY,
                                location[0], location[1] + button.getHeight());
                    }
                });

                bb[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                        int countVotes = mgr.getCountVotes();
                        if (isChecked) {
                            if (countVotes < 5) {
                                mgr.setCountVotes(countVotes + 1);
                                Workshop workshop = mgr.get((View) compoundButton);
                                workshop.setCountvotes((workshop.getCountvotes() + 1));
                                mgr.putAuswahl((View) compoundButton, workshop);
                            } else {
                                Toast.makeText(getApplicationContext(), "Sie können nur 5 auswählen!", Toast.LENGTH_SHORT).show();
                                compoundButton.setChecked(false);
                            }
                        } else {
                            mgr.setCountVotes(countVotes - 1);
                            mgr.deleteAuswahl((View) compoundButton);
                            Workshop workshop = mgr.get((View) compoundButton);
                            workshop.setCountvotes((workshop.getCountvotes() - 1));
                        }

                    }
                });

            }
        }
    }

    private class GetStatus extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            try {
                String url = new String(getString(string.url) + "status");

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

    private class Mgr {
        Map<View, Workshop> map = new HashMap<>();
        private Map<View, Workshop> auswahl = new HashMap<>();
        private int countVotes;
        List<Workshop> works = new ArrayList<>();

        public void put(View view, Workshop work) {
            map.put(view, work);
        }

        public void putAuswahl(View view, Workshop work) {
            auswahl.put(view, work);
        }

        public Stack<Workshop> getAuswahl() {
            Iterator myVeryOwnIterator = auswahl.keySet().iterator();
            Stack<Workshop> stack = new Stack<>();
            while (myVeryOwnIterator.hasNext()) {
                View key = (View) myVeryOwnIterator.next();
                stack.add((Workshop) auswahl.get(key));
            }
            return stack;
        }

        public Workshop deleteAuswahl(View view) {
            return auswahl.remove(view);
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

        public int getCountVotes() {
            return countVotes;
        }

        public void setCountVotes(int countVotes) {
            this.countVotes = countVotes;
        }
    }

    private class PutUser extends AsyncTask<Void, Void, User> {


        @Override
        protected User doInBackground(Void... params) {

            try {

                String url = new String(getString(string.url) + "user/") + ((OpenfridayApplication) activity.getApplication()).getUser().getId();
                User user = ((OpenfridayApplication) activity.getApplication()).getUser();
                HttpEntity<User> entity = new HttpEntity<User>(user);
                RestTemplate restTemplate = new RestTemplate(true);
                ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);

                ((OpenfridayApplication) activity.getApplication()).setUser(response.getBody());
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (i == 0) {
                activity.finish();
            }
            i = 0;
        }

    }

    private class PutWorkshop extends AsyncTask<Void, Void, User> {


        @Override
        protected User doInBackground(Void... params) {

            try {
                Workshop[] workshops;
                String url = new String(getString(string.url) + "workshop");
                Collection<Workshop> hallo= mgr.map.values();
                workshops= hallo.toArray(new Workshop[mgr.map.size()]);
                HttpEntity<Workshop[]> entity = new HttpEntity<Workshop[]>(workshops);
                RestTemplate restTemplate = new RestTemplate(true);
                ResponseEntity<Workshop[]> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Workshop[].class);

                ((OpenfridayApplication) activity.getApplication()).setMeineworks(response.getBody());
            } catch (Exception e) {
                System.out.println("Rip");
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (i == 0) {
                activity.finish();
            }
            i = 0;
        }
    }

    private void loadFragment(android.app.Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit(); // save the changes
    }

}
