package io.sapl.demo.pil;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FlightSelectionActivity extends AppCompatActivity implements AsyncResponse {
    public static final String PIL_DATA = "PIL_DATA";
    public static final String PEP_SERVER = "saplgeo.pepserver.info"; // adopt for PEP server URL

    private static final int META = 0;
    private static final int RESTRICTED = 1;
    private static final int CONFIDENTIAL = 2;
    private static final String DATE_PICKER = "datePicker";

    private final Calendar myCalendar = Calendar.getInstance();
    private Map<String, Object> subject = new HashMap<>();
    private Map<String, Object> environment = new HashMap<>();
    private String username;
    private CertificateManager certManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_selection);
        Intent intent = getIntent();

        // Load certificates
        certManager = new CertificateManager(getResources().openRawResource(R.raw.saplgeo_client), getResources().openRawResource(R.raw.saplgeo_server));

        // User-ID in bottomline
        username = intent.getStringExtra(LoginActivity.USERNAME);
        ((TextView) findViewById(R.id.userDisplay)).setText(username);
        subject.put(SAPLRequest.PERS_ID, username);
        subject.put(SAPLRequest.OPS_STATUS, SAPLRequest.STD_STATUS);

        // current date
        TextView dateView = findViewById(R.id.selDate);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), DATE_PICKER);
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT, Locale.GERMAN);
        dateView.setText(sdf.format(myCalendar.getTime()));
    }

    public void showMetaPil(View view) {
        SAPLRequest req = new SAPLRequest(subject, SAPLRequest.PIL_RETRIEVE, getResourceMap(META), environment);
        new RequestSender(PEP_SERVER, req.toJsonNode(), certManager, this).execute();
        setButtonState(false);
    }

    public void showReducedPil(View view) {
        SAPLRequest req = new SAPLRequest(subject, SAPLRequest.PIL_RETRIEVE, getResourceMap(RESTRICTED), environment);
        new RequestSender(PEP_SERVER, req.toJsonNode(), certManager, this).execute();
        setButtonState(false);
    }

    public void showFullPil(View view) throws IOException {
        SAPLRequest req = new SAPLRequest(subject, SAPLRequest.PIL_RETRIEVE, getResourceMap(CONFIDENTIAL), environment);
        new RequestSender(PEP_SERVER, req.toJsonNode(), certManager, this).execute();
        setButtonState(false);
    }

    private Map<String, Object> getResourceMap(int classification) {
        Map<String, Object> resource = new HashMap<>();
        resource.put(SAPLRequest.FLT_NO, ((EditText) findViewById(R.id.selFltNo)).getText().toString());
        resource.put(SAPLRequest.DEP_AP, ((EditText) findViewById(R.id.selDepAp)).getText().toString());
        resource.put(SAPLRequest.ARR_AP, ((EditText) findViewById(R.id.selArrAp)).getText().toString());
        resource.put(SAPLRequest.DATE, ((EditText) findViewById(R.id.selDate)).getText().toString());
        resource.put(SAPLRequest.AC_REG, SAPLRequest.STD_AC);
        resource.put(SAPLRequest.CLASSIFICATION, classification);
        return resource;
    }

    private void setButtonState(boolean state) {
        findViewById(R.id.button2).setEnabled(state);
        findViewById(R.id.button3).setEnabled(state);
        findViewById(R.id.button5).setEnabled(state);
    }

    @Override
    public void processFinish(String output) {
        setButtonState(true);

        try {
            ObjectMapper mapper = new ObjectMapper();
            PilData data = mapper.readValue(output, PilData.class);
            Intent intent = new Intent(this, PilDisplayActivity.class);
            intent.putExtra(PIL_DATA, data);
            intent.putExtra(LoginActivity.USERNAME, username);
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
        }
    }
}
