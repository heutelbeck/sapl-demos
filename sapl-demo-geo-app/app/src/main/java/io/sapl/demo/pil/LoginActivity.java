package io.sapl.demo.pil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {
    public static final String USERNAME = "io.sapl.demo.USERNAME";
    public static final String CREDENTIALS = "io.sapl.demo.CREDENTIALS";
    private static final String USER_PW_COMBINATION = "%s:%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        Intent intent = new Intent(this, FlightSelectionActivity.class);
        EditText usernameInput = findViewById(R.id.username);
        EditText passwordInput = findViewById(R.id.password);

        String base64EncodedCredentials = Base64.encodeToString(String.format(USER_PW_COMBINATION, usernameInput.getText().toString(), passwordInput.getText().toString()).getBytes(), Base64.NO_WRAP);
        intent.putExtra(USERNAME, usernameInput.getText().toString());
        intent.putExtra(CREDENTIALS, base64EncodedCredentials);
        startActivity(intent);
    }
}
