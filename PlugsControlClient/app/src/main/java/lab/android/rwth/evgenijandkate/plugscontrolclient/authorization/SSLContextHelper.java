package lab.android.rwth.evgenijandkate.plugscontrolclient.authorization;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import lab.android.rwth.evgenijandkate.plugscontrolclient.R;

/**
 * Created by evgenijavstein on 09/06/15.
 */
public class SSLContextHelper {

    public static SSLContext initSSLContext(Context currentContext) {
        SSLContext context = null;
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");

            // our server self signed certificate
            //InputStream caInput = null;
            InputStream ins = currentContext.getResources().openRawResource(R.raw.cert);
            // caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));

            Certificate ca;

            ca = cf.generateCertificate(ins);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            ins.close();

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = null;

            keyStore = KeyStore.getInstance(keyStoreType);

            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return context;
    }

    public static HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //HostnameVerifier hv =
                //HttpsURLConnection.getDefaultHostnameVerifier();
                //return hv.verify("example.com", session);

                //NOTE: IN PRODUCTION MODE WE NEED TO PUT THE HOSTNAME HERE WE ARE GOING TO TRUST
                return true;
            }
        };
        return hostnameVerifier;
    }
}
