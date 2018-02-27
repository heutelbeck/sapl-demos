package io.sapl.demo.pil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import lombok.Getter;

@Getter
class CertificateManager {
    private static final String CLIENT_CERT_PW = "saplgeo";
    private static final String CERT_ERR = "Unable to load certificates into Key-/Truststore. {}";
    private static final Logger LOGGER = Logger.getLogger(CertificateManager.class.getName());

    private KeyManager[] keyManager;
    private TrustManager[] trustManager;

    CertificateManager(InputStream key, InputStream trust) {
        try {
            initKeyManagers(key);
            initTrustManagers(trust);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            LOGGER.log(Level.FINE, CERT_ERR, e.getMessage());
        }
    }

    private void initTrustManagers(InputStream res) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException {
        // Create TrustStore for server certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput = new BufferedInputStream(res);
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry(CLIENT_CERT_PW, ca);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        trustManager = tmf.getTrustManagers();
    }

    private void initKeyManagers(InputStream res) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        // Create KeyStore for client authentication
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream is = new BufferedInputStream(res);
        keyStore.load(is, CLIENT_CERT_PW.toCharArray());
        is.close();

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, CLIENT_CERT_PW.toCharArray());

        keyManager = kmf.getKeyManagers();
    }
}
