package io.sapl.demo.pil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LoginActivity extends AppCompatActivity {
    public static final String USERNAME = "io.sapl.demo.USERNAME";
    private static final String INVALID_CREDENTIALS = "The username/password combination provided is not correct.";
    private static final String PW_DB_NOT_FOUND = "The database containing the credentials could not be accessed.";
    private static final String SEPARATOR = " ";
    private static final int USERNAME_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        Intent intent = new Intent(this, FlightSelectionActivity.class);
        EditText usernameInput = findViewById(R.id.username);
        EditText passwordInput = findViewById(R.id.password);

        String user = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        try {
            if (checkCredentials(user, password)) {
                intent.putExtra(USERNAME, user);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), INVALID_CREDENTIALS, Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), PW_DB_NOT_FOUND, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkCredentials(String username, String password) throws IOException {
        String str;
        InputStream is = getResources().openRawResource(R.raw.credentials);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while ((str = reader.readLine()) != null) {
            String[] credential = str.split(SEPARATOR);
            if (credential[USERNAME_INDEX].equals(username) && credential[PASSWORD_INDEX].equals(Base64.encodeToString(password.getBytes(), Base64.NO_WRAP))) {
                is.close();
                return true;
            }
        }
        is.close();
        return false;
    }
}
