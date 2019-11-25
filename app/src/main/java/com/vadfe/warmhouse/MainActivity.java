package com.vadfe.warmhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttClient ;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ActiveMQ";
    public static final String clientId = "vadafe@gmail.com";
    public static final String serverURI = "tcp://mqtt.dioty.co:1883"; //replace with your ip
    public static final String publishTopic = "/vadafe@gmail.com/in";
    public static final String subscribeTopic = "/vadafe@gmail.com/out";

    MqttClient client;

    private Button publishBtn;
    private EditText messageEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        try {
            connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        publishBtn = (Button) findViewById(R.id.publish);
        messageEt = (EditText) findViewById(R.id.ntrMessage);
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishMessage(messageEt.getText().toString());
            }
        });
        publishBtn.setEnabled(false);
    }

    private void connect() throws MqttException{
        try {
            String url = serverURI;
            String password =  "";
            String username = "vadafe@gmail.com";
            Log.d(TAG,"Opening MQTT connection: '{}'");
            //LOGGER.info("properties: {}", mqttProperties);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName(username);
            connectOptions.setPassword(password.toCharArray());
            connectOptions.setCleanSession(false);
            client = new MqttClient(url, clientId, new MemoryPersistence());
            //client.setCallback(onMessageArrived);
            client.connect(connectOptions);
            if(client.isConnected()){
                publishBtn.setEnabled(true);
                subscribe();
            }
            Log.d(TAG,"Opening MQTT connection: "+String.valueOf(client.isConnected()));
            //client.subscribe(mqttProperties.getTopic());

        } catch (MqttException e) {
            e.printStackTrace();
            throw e;
        }
    }


/*
    private void connect() {

        String password =  "d5b3e103";
        String username = "vadafe@gmail.com";
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        if (false) {
            SocketFactory sf = SSLSocketFactory.getDefault();
            connectOptions.setSocketFactory(sf);
        }

        connectOptions.setAutomaticReconnect(true);
        connectOptions.setCleanSession(true);
        connectOptions.setConnectionTimeout(100);
        connectOptions.setKeepAliveInterval(300);
        connectOptions.setUserName(username);
        connectOptions.setPassword(password.toCharArray());


        client = new MqttClient(this, serverURI, clientId);
        try {
            client.connect(connectOptions, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    publishBtn.setEnabled(true);
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    private void subscribe() {
        try {
            client.subscribe(subscribeTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String message) {
        MqttMessage msg = new MqttMessage();
        msg.setPayload(message.getBytes());
        try {
            client.publish(publishTopic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
