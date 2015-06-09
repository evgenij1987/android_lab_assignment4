package lab.android.rwth.evgenijandkate.plugscontrolclient.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.LogInFragment;
import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.SSLContextHelper;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.User;

/**
 * Created by ekaterina on 07.06.2015.
 *
 * A class for sending an http request to authenticate a user.
 * When the user logs in, a corresponding role (ADMIN or USER) is being received as JSON.
 */
public class CheckUserTask {
    private Context context;
    private OnResponseListener onResponseListener;

    public CheckUserTask(Context context) {
        this.context = context;
    }

    /**
     * Sends a GET request to authenticate a user
     *
     * @param user a user's entered data
     */
    public void send(User user) {
        new HttpCheckUserTask().execute(user);
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    private class HttpCheckUserTask extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... args) {
            User loggingInUser = args[0];

            String ipValue = loggingInUser.getIpValue();
            String portValue = loggingInUser.getPortValue();
            if (ipValue == null || ipValue.isEmpty() || portValue == null || portValue.isEmpty()) {
                onResponseListener.onError("Connection parameters are missing");
                return null;
            }

            HttpsURLConnection conn = null;
            try {
                URL url = new URL("https://" + ipValue + ":" + portValue + "/api/authenticate");
                conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(SSLContextHelper.initSSLContext(context).getSocketFactory());
                conn.setHostnameVerifier(SSLContextHelper.getHostnameVerifier());
                conn.setRequestMethod("GET");
                conn.addRequestProperty("Authorization", LogInFragment.getB64Auth(loggingInUser.getUserAccountName(), loggingInUser.getPassword()));
                conn.setDoInput(true);
                conn.connect();
                int status = conn.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        return JSONUserParser.parse(sb.toString(), loggingInUser);
                }
            } catch (MalformedURLException e) {
                onResponseListener.onError(e.getMessage());
            } catch (IOException e) {
                onResponseListener.onError(e.getMessage());
            } catch (JSONException e) {
                onResponseListener.onError(e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(User connectedUser) {
            super.onPostExecute(connectedUser);
            onResponseListener.onResponse(connectedUser);
        }
    }
}
