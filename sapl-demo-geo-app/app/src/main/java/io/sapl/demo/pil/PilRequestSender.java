package io.sapl.demo.pil;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestRequestSender extends AsyncTask<Void, Void, String> {
    private static final String UNABLE_REACH_SERVER = "Unable to reach PIL-Server.";
    public static final String PIL_SERVER = "http://saplgeo.mariusmueller.info:5699/pil";
    private AsyncResponse delegate = null;
    private Map<String, String> requestParameter;
    private String base64EncodedCredentials;

    RestRequestSender(Map<String, String> params, String credentials, AsyncResponse asyncResponse) {
        delegate = asyncResponse;
        requestParameter = params;
        base64EncodedCredentials = credentials;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(PIL_SERVER).newBuilder();
            if (requestParameter.size() > 0) {
                for (Map.Entry<String, String> entry : requestParameter.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .header("Authorization", "Basic " + base64EncodedCredentials)
                    .build();
            Response response = client.newCall(request).execute();

            if (response.code() == 200) {
                return response.body().string();
            } else {
                return String.valueOf(response.code());
            }

        } catch (IOException e) {
            return UNABLE_REACH_SERVER;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
