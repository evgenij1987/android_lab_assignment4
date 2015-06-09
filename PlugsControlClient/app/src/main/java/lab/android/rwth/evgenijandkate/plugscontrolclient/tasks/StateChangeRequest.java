package lab.android.rwth.evgenijandkate.plugscontrolclient.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.LogInFragment;
import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.SSLContextHelper;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.IListItem;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.StateEnum;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.User;

/**
 * Created by ekaterina on 04.06.2015.
 *
 * A class for sending an http request for changing the state of the plug.
 * When the user with any rights changes the state of the plug, a GET request is being sent to the server.
 */
public class StateChangeRequest {
    private OnResponseListener onResponseListener;
    private IListItem item;
    private Context context;

    public StateChangeRequest(IListItem item, Context context) {
        this.context = context;
        this.item = item;
    }

    /**
     * Executes a task to change the state of a plug (ON or OFF) in the plugs list on the server.
     * When the user with any rights changes the state of the plug, a GET request is being sent to the server.
     */
    public void send() {
        new HttpGetChangeStateTask().execute(item);
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    private class HttpGetChangeStateTask extends AsyncTask<IListItem, Void, Boolean> {

        @Override
        protected Boolean doInBackground(IListItem... params) {
            HttpsURLConnection conn = null;
            User connectedUser = LogInFragment.getConnectedUser();
            if (connectedUser == null) return false;
            String path = StateEnum.ON.equals(params[0].getState()) ? "/turnON/" : "/turnOFF/";
            try {
                URL url = new URL("https://" + connectedUser.getIpValue() + ":" + connectedUser.getPortValue() + "/api/plugs" + path + params[0].getListItemId());
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
                        return true;
                }
            } catch (MalformedURLException e) {
                onResponseListener.onError(e.getMessage());
            } catch (IOException e) {
                onResponseListener.onError(e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean statusChangedSuccessfully) {
            super.onPostExecute(statusChangedSuccessfully);
            onResponseListener.onResponse(statusChangedSuccessfully);
        }
    }
}
