package io.sapl.demo.pil;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class RequestSender extends AsyncTask<Void, Void, String> {
    private static final String SSL_HANDSHAKE_FAIL = "Expected %s but found %s";
    private static final String UNABLE_REACH_SERVER = "Unable to reach PEP-Server.";
    private static final int PORT = 5699;
    private AsyncResponse delegate = null;
    private final String url;
    private final String request;
    private final CertificateManager certificateManager;

    RequestSender(String address, String req, CertificateManager certMan, AsyncResponse asyncResp) {
        url = address;
        request = req;
        delegate = asyncResp;
        certificateManager = certMan;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(certificateManager.getKeyManager(), certificateManager.getTrustManager(), null);
            try (SSLSocket socket = (SSLSocket) context.getSocketFactory().createSocket(url, PORT)) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                SSLSession s = socket.getSession();

                if (!hv.verify(url, s)) {
                    throw new SSLHandshakeException(String.format(SSL_HANDSHAKE_FAIL, url, s.getPeerPrincipal()));
                }

                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true);
                out.println(request);
                out.close();

                Scanner in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8.name());
                String answer = in.nextLine();

                in.close();
                return answer;
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            return UNABLE_REACH_SERVER;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
