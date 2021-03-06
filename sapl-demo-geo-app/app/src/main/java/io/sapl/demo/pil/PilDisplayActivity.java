package io.sapl.demo.pil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;
import lombok.Setter;

public class PilDisplayActivity extends AppCompatActivity implements AsyncResponse {
    private static final long PERMISSION_VALIDITY_PERIOD = 20000;
    private static final String HTTP_OK = "200";
    private static final String NOT_ALLOWED = "You are not allowed to view this data anymore.";

    protected Map<String, String> requestParameter;
    private String base64EncodedCredentials;
    private Timer timerObj;
    private PilData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pil_display);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        Intent intent = getIntent();
        base64EncodedCredentials = intent.getStringExtra(LoginActivity.CREDENTIALS);
        data = (PilData) intent.getSerializableExtra(FlightSelectionActivity.PIL_DATA);

        requestParameter = createRecurrentRequest();
        fillMetaData();
        fillPaxData();
        fillPassengerList();

        // Check regularly if permission is still applicable
        timerObj = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                new RestRequestSender(requestParameter, base64EncodedCredentials, PilDisplayActivity.this).execute();
            }
        };
        timerObj.schedule(timerTaskObj, PERMISSION_VALIDITY_PERIOD, PERMISSION_VALIDITY_PERIOD);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerObj.cancel();
    }

    private Map<String, String> createRecurrentRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(FlightSelectionActivity.FLT_NO, data.getMetaData().getFltNo());
        params.put(FlightSelectionActivity.DEP_AP, data.getMetaData().getDepAp());
        params.put(FlightSelectionActivity.ARR_AP, data.getMetaData().getArrAp());
        params.put(FlightSelectionActivity.DATE, data.getMetaData().getDate());
        params.put(FlightSelectionActivity.CLASSIFICATION, String.valueOf(data.getMetaData().getClassification()));
        params.put(FlightSelectionActivity.TYPE, String.valueOf(FlightSelectionActivity.RECURRENT));

        return params;
    }

    private void fillMetaData() {
        PilMetaInf meta = data.getMetaData();
        if (meta != null) {
            String route = meta.getDepAp() + "-" + meta.getArrAp();
            ((TextView) findViewById(R.id.phDate)).setText(meta.getDate());
            ((TextView) findViewById(R.id.phAcType)).setText(meta.getAcType());
            ((TextView) findViewById(R.id.phFltNo)).setText(meta.getFltNo());
            ((TextView) findViewById(R.id.phRoute)).setText(route);
        }
    }

    private void fillPaxData() {
        PilPaxInf pax = data.getPaxData();
        if (pax != null) {
            // F: First Class
            // C: Business Class
            // E: Premium Economy Class
            // Y: Economy Class
            ((TextView) findViewById(R.id.phFAct)).setText(String.valueOf(pax.getFAct()));
            ((TextView) findViewById(R.id.phFMax)).setText(String.valueOf(pax.getFMax()));
            ((TextView) findViewById(R.id.phCAct)).setText(String.valueOf(pax.getCAct()));
            ((TextView) findViewById(R.id.phCMax)).setText(String.valueOf(pax.getCMax()));
            ((TextView) findViewById(R.id.phEAct)).setText(String.valueOf(pax.getEAct()));
            ((TextView) findViewById(R.id.phEMax)).setText(String.valueOf(pax.getEMax()));
            ((TextView) findViewById(R.id.phYAct)).setText(String.valueOf(pax.getYAct()));
            ((TextView) findViewById(R.id.phYMax)).setText(String.valueOf(pax.getYMax()));
            ((TextView) findViewById(R.id.phTtlAct)).setText(String.valueOf(pax.getFAct() + pax.getCAct() + pax.getEAct() + pax.getYAct()));
            ((TextView) findViewById(R.id.phTtlMax)).setText(String.valueOf(pax.getFMax() + pax.getCMax() + pax.getEMax() + pax.getYMax()));
        }
    }

    private void fillPassengerList() {
        if (data.getPassengers() != null) {
            final ListView listview = findViewById(R.id.paxList);
            PilPassenger[] passengers = data.getPassengers();
            final int paxCount = passengers.length;

            String[] seats = new String[paxCount];
            String[] names = new String[paxCount];
            String[] bdates = new String[paxCount];
            String[] gender = new String[paxCount];
            String[] specials = new String[paxCount];

            for (int i = 0; i < paxCount; i++) {
                seats[i] = passengers[i].getSeat();
                names[i] = passengers[i].getName();
                bdates[i] = passengers[i].getBdate();
                gender[i] = passengers[i].getGender();
                specials[i] = passengers[i].getSpecial();
            }

            final PaxInfoAdapter adapter = new PaxInfoAdapter(this, seats, names, gender, bdates, specials);
            listview.setAdapter(adapter);
        }
    }

    @Override
    public void processFinish(String output) {
        if (!output.equals(HTTP_OK)) {
            Toast.makeText(getApplicationContext(), NOT_ALLOWED, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class PaxInfoAdapter extends ArrayAdapter<String> {
        Context context;
        String[] seats;
        String[] names;
        String[] gender;
        String[] bdates;
        String[] specials;

        private PaxInfoAdapter(Context ctxtInput, String[] seatsInput, String[] namesInput,
                               String[] genderInput, String[] bdatesInput, String[] specialsInput) {
            super(ctxtInput, -1, namesInput);
            context = ctxtInput;
            seats = seatsInput;
            names = namesInput;
            gender = genderInput;
            bdates = bdatesInput;
            specials = specialsInput;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.pax_list_row, parent, false);
                holder = new ViewHolder();

                holder.setSeatView((TextView) convertView.findViewById(R.id.seatView));
                holder.setNameView((TextView) convertView.findViewById(R.id.nameView));
                holder.setGenderView((TextView) convertView.findViewById(R.id.genderView));
                holder.setBdateView((TextView) convertView.findViewById(R.id.bdateView));
                holder.setSpecialsView((TextView) convertView.findViewById(R.id.specialsView));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.getSeatView().setText(seats[position]);
            holder.getNameView().setText(names[position]);
            holder.getGenderView().setText(gender[position]);
            holder.getBdateView().setText(bdates[position]);
            holder.getSpecialsView().setText(specials[position]);
            return convertView;
        }

        @Getter
        @Setter
        private class ViewHolder {
            private TextView seatView;
            private TextView nameView;
            private TextView genderView;
            private TextView bdateView;
            private TextView specialsView;
        }
    }
}
