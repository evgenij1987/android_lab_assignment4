package lab.android.rwth.evgenijandkate.plugscontrolclient;

import android.app.Activity;
import android.util.Log;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;


import java.util.HashMap;
import java.util.Map;


import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.LogInFragment;
import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.SSLContextHelper;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.IListItem;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.User;
import lab.android.rwth.evgenijandkate.plugscontrolclient.tasks.JSONPlugsParser;

/**
 * Created by evgenijavstein on 09/06/15.
 * A helper wrapper around <code>org.java_websocket.client.WebSocketClient</code> to just receive a plug object
 * if a plug was turned on/off by other users.
 */
public class PlugUpdateClient {

    interface OnPlugUpdateListener {

        void onUpdate(IListItem updatedItem);

        void onError(String message);
    }

    //message to subscribe for events on websocket server
    private static final String SUBSCRIBE = "subscribe";
    //message to unsubscribe to not receive updates about plugs turned off/on
    private static final String UNSUBSCRIBE = "unsubscribe";
    //successful response on unsubscribe message
    private static final String SUCCESS_UNSUBSCRIBE = "success_unsubscribe";
    //successful response on subscribe message
    private static final String SUCCESS_SUBSCRIBE = "success_subscribe";
    //unsuccessful response if a not authenticated user is trying to communicate with the web socket server
    private static final String NOT_AUTHENTICATED = "not authenticated!";
    private WebSocketClient mWebSocketClient;
    private OnPlugUpdateListener onPlugUpdateListener;
    private Activity hostActivity;

    public PlugUpdateClient(Activity hostActivity) {
        this.hostActivity = hostActivity;
    }

    public void subscribe() {
        User connectedUser = LogInFragment.getConnectedUser();
        if (connectedUser == null) {
            onPlugUpdateListener.onError("not logged in");
            return;
        }
        URI uri;
        try {
            uri = new URI("wss://" + connectedUser.getIpValue() + ":" + (Integer.parseInt(connectedUser.getPortValue()) + 1));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        //set Authorization header to authenticate the web socket handshake
        //otherwise connection will be rejected by server
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", LogInFragment.getB64Auth(connectedUser.getUserAccountName(), connectedUser.getPassword()));

        //use draft no 10: as defined in https://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-10
        mWebSocketClient = new WebSocketClient(uri, new Draft_10(), headers, 0) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");

                //the only to messages we send to notification web server: SUBSCRIBE, UNSUBSCRIBE
                mWebSocketClient.send(SUBSCRIBE);
            }

            @Override
            public void onMessage(final String message) {
                //we want to update the UI, so we need to run on UI here
                hostActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Websocket", "Respose:" + message);
                        //since we have only 4 kinds of messages, we just filter out subscription response and authentication
                        //not successfull message
                        if (!message.equals(SUCCESS_SUBSCRIBE) && !message.equals(SUCCESS_UNSUBSCRIBE) &&
                                !message.equals(NOT_AUTHENTICATED)) {
                            try {
                                IListItem updatedItem = JSONPlugsParser.parseItem(message);
                                onPlugUpdateListener.onUpdate(updatedItem);
                            } catch (JSONException e) {
                                onPlugUpdateListener.onError(e.getMessage());
                            }
                        }
                        if (message.equals(NOT_AUTHENTICATED))
                            onPlugUpdateListener.onError(message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                onPlugUpdateListener.onError(e.getMessage());
            }
        };
        mWebSocketClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(SSLContextHelper.initSSLContext(hostActivity)));
        mWebSocketClient.connect();
    }

    public void unsubscribe() {
        mWebSocketClient.send(UNSUBSCRIBE);
    }

    public void setOnPlugUpdateListener(OnPlugUpdateListener onPlugUpdateListener) {
        this.onPlugUpdateListener = onPlugUpdateListener;
    }

}
