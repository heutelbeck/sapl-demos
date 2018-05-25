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
    private static final String ACCESS_DENIED = "Not allowed, %s";
    public static final String PIL_DATA = "PIL_DATA";
    static final String DEP_AP = "dep";
    static final String ARR_AP = "dest";
    static final String FLT_NO = "fltNo";
    static final String DATE = "date";
    static final String CLASSIFICATION = "classification";
    static final String RECURRENT = "recurrent";
    static final String TYPE = "type";

    private static final int META = 0;
    private static final int RESTRICTED = 1;
    private static final int CONFIDENTIAL = 2;
    private static final String DATE_PICKER = "datePicker";

    private final Calendar myCalendar = Calendar.getInstance();
    private String username;
    private String base64EncodedCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_selection);
        Intent intent = getIntent();

        // User-ID in bottomline
        username = intent.getStringExtra(LoginActivity.USERNAME);
        base64EncodedCredentials = intent.getStringExtra(LoginActivity.CREDENTIALS);
        ((TextView) findViewById(R.id.userDisplay)).setText(username);

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
        new RestRequestSender(getRequestParameter(META), base64EncodedCredentials, this).execute();
        setButtonState(false);
    }

    public void showReducedPil(View view) {
        new RestRequestSender(getRequestParameter(RESTRICTED), base64EncodedCredentials, this).execute();
        setButtonState(false);
    }

    public void showFullPil(View view) throws IOException {
        new RestRequestSender(getRequestParameter(CONFIDENTIAL), base64EncodedCredentials, this).execute();
        setButtonState(false);
    }

    private Map<String, String> getRequestParameter(int classification) {
        Map<String, String> param = new HashMap<>();
        param.put(FLT_NO, ((EditText) findViewById(R.id.selFltNo)).getText().toString());
        param.put(DEP_AP, ((EditText) findViewById(R.id.selDepAp)).getText().toString());
        param.put(ARR_AP, ((EditText) findViewById(R.id.selArrAp)).getText().toString());
        param.put(DATE, ((EditText) findViewById(R.id.selDate)).getText().toString());
        param.put(CLASSIFICATION, String.valueOf(classification));
        return param;
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
            intent.putExtra(LoginActivity.CREDENTIALS, base64EncodedCredentials);
            intent.putExtra(LoginActivity.USERNAME, username);
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), String.format(ACCESS_DENIED, output), Toast.LENGTH_LONG).show();
        }
    }
}
