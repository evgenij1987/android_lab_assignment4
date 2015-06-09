package lab.android.rwth.evgenijandkate.plugscontrolclient.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.LogInFragment;
import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.SSLContextHelper;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.IListItem;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.User;

/**
 * Created by ekaterina on 04.06.2015.
 *
 * A class for sending an http request for getting a list of available plugs.
 * When the user logs in and proceeds to the plug control activity, a GET request is being sent to the server.
 */
public class PlugsListGetRequest {
    private OnResponseListener onResponseListener;
    private Context context;

    /**
     * Sends a GET request to retrieve the list of available plugs
     */
    public void send() {
        new HttpGetPlugsTask().execute();
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    public PlugsListGetRequest(Context context) {
        this.context = context;
    }

    private class HttpGetPlugsTask extends AsyncTask<Void, Void, List<IListItem>> {

        @Override
        protected List<IListItem> doInBackground(Void... params) {
            HttpsURLConnection conn = null;
            User connectedUser = LogInFragment.getConnectedUser();
            if (connectedUser == null) return null;
            try {
                URL url = new URL("https://" + connectedUser.getIpValue() + ":" + connectedUser.getPortValue() + "/api/plugs");
                conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(SSLContextHelper.initSSLContext(context).getSocketFactory());
                conn.setHostnameVerifier(SSLContextHelper.getHostnameVerifier());
                conn.setRequestMethod("GET");
                conn.addRequestProperty("Authorization", LogInFragment.getB64Auth(connectedUser.getUserAccountName(), connectedUser.getPassword()));
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
                        return JSONPlugsParser.parse(sb.toString());
                }

            } catch (MalformedURLException e) {
                onResponseListener.onError(e.getMessage());
            } catch (IOException e) {
                onResponseListener.onError(e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<IListItem> items) {
            super.onPostExecute(items);
            onResponseListener.onResponse(items);
        }
    }
}
