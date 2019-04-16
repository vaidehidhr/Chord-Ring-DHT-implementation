package edu.buffalo.cse.cse486586.simpledht;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleDhtActivity extends Activity {

    static final String TAG =SimpleDhtActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"SimpleDHTActivity: oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_dht_main);

        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button3).setOnClickListener(
                new OnTestClickListener(tv, getContentResolver()));
        // My code
        final Button LDump = (Button)findViewById(R.id.button1);
        LDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "LDump clicked");
            }
//            return false;
        });
        final Button GDump = (Button)findViewById(R.id.button2);
        GDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "GDump clicked");
            }
//            return false;
        });
        // My code ends
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG,"SimpleDHTActivity onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_simple_dht_main, menu);
        return true;
    }

//    private class ClientTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... msgs, hashed_key, myPort, successor) {
////            for(int i = 0; i < 5; i++) {
//                try {
//                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(successor);
//                    String msgToSend = msgs[0];
//
//                    // For this section code from PA1 has been referred
//                    DataOutputStream dout_client = new DataOutputStream(socket.getOutputStream());
//                    dout_client.writeUTF(msgToSend);
//
//                    DataInputStream din_client = new DataInputStream(socket.getInputStream());
//                    if (din_client.readUTF().equals("Message received")) {
//                        dout_client.flush();
//                        socket.close();
//                    }
//
//                } catch (UnknownHostException e) {
//                    Log.e(TAG, "ClientTask UnknownHostException");
//                } catch (IOException e) {
//                    Log.e(TAG, "ClientTask socket IOException");
//                }
////            }
//            return null;
//        }
//    }
}