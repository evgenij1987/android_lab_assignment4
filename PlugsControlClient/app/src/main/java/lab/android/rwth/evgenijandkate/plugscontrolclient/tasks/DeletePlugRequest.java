package lab.android.rwth.evgenijandkate.plugscontrolclient.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.LogInFragment;
import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.SSLContextHelper;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.IListItem;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.User;

/**
 * Created by ekaterina on 07.06.2015.
 *
 * A class for sending an http request for deleting plug.
 * When the user with administrative rights removes plug, a DELETE request is being sent to the server.
 */
public class DeletePlugRequest {
    private OnResponseListener onResponseListener;
    private List<IListItem> itemsCheckedToBeDeleted;
    private Context context;

    public DeletePlugRequest(List<IListItem> items, Context context) {
        this.context = context;
        this.itemsCheckedToBeDeleted = new ArrayList<>();
        for (IListItem listItem : items) {
            if (listItem.isChecked()) {
                this.itemsCheckedToBeDeleted.add(listItem);
            }
        }
    }

    /**
     * Executes a task to delete a plug from the plugs list on the server.
     * When the user with administrative rights removes plug, a DELETE request is being sent to the server.
     */
    public void send() {
        new HttpDeletePlugTask().execute(itemsCheckedToBeDeleted);
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    private class HttpDeletePlugTask extends AsyncTask<List<IListItem>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(List<IListItem>... params) {
            for (IListItem listItem : params[0]) {
                if (!connectToDeleteItem(listItem.getListItemId())) {
                    return false;
                }
            }
            return true;
        }

        private boolean connectToDeleteItem(int listItemId) {
            HttpsURLConnection conn = null;
            User connectedUser = LogInFragment.getConnectedUser();
            if (connectedUser == null) return false;
            try {
                URL url = new URL("https://" + connectedUser.getIpValue() + ":" + connectedUser.getPortValue() + "/api/plugs/" + listItemId);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(SSLContextHelper.initSSLContext(context).getSocketFactory());
                conn.setHostnameVerifier(SSLContextHelper.getHostnameVerifier());
                conn.setRequestMethod("DELETE");
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
