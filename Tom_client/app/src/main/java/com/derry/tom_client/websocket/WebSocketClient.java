package com.derry.tom_client.websocket;

import android.util.Log;

import com.derry.tom_client.model.Message;
import com.google.gson.Gson;

//import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private static final String WS_URL = "ws://10.70.57.162:8088/ws";
    
    private org.java_websocket.client.WebSocketClient client;
    private WebSocketListener listener;
    private Gson gson = new Gson();
    
    public interface WebSocketListener {
        void onConnect();
        void onMessageReceived(Message message);
        void onDisconnect(int code, String reason);
        void onError(Exception ex);
    }
    
    public WebSocketClient(WebSocketListener listener) {
        this.listener = listener;
    }
    
    public void connect() {
        try {
            client = new org.java_websocket.client.WebSocketClient(new URI(WS_URL)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG, "WebSocket连接成功");
                    if (listener != null) {
                        listener.onConnect();
                    }
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "收到消息: " + message);
                    if (listener != null) {
                        try {
                            Message msg = gson.fromJson(message, Message.class);
                            listener.onMessageReceived(msg);
                        } catch (Exception e) {
                            Log.e(TAG, "解析消息失败", e);
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocket连接关闭: " + reason);
                    if (listener != null) {
                        listener.onDisconnect(code, reason);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "WebSocket错误", ex);
                    if (listener != null) {
                        listener.onError(ex);
                    }
                }
            };
            client.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "WebSocket URI语法错误", e);
        }
    }
    
    public void sendMessage(Message message) {
        if (client != null && client.isOpen()) {
            String json = gson.toJson(message);
            client.send(json);
        } else {
            Log.e(TAG, "WebSocket未连接，无法发送消息");
        }
    }
    
    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }
    
    public boolean isConnected() {
        return client != null && client.isOpen();
    }
}