
package com.example.magnettest;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.samsung.magnet.IMagnetChannel;
import com.samsung.magnet.IMagnetChannelListener;
import com.samsung.magnet.IMagnetManagerListener;
import com.samsung.magnet.MagnetManager;

public class MagnetTestActivity extends Activity implements OnClickListener {

    MagnetManager mMagnet = null;

    private Intent intent;

    IMagnetChannel channelInst;

    ArrayAdapter<String> adapter2;

    EditText searchEditText;

    final String PUBLIC_CHANNEL_NAME = "MagnetManager.PUBLIC_CHANNEL";

    final String CHANNEL_NAME = "com.samsung.test.TEST_CHANNEL";

    final String TAG = "MagnetTestActivity";

    void startDataReceiverService() {

        intent = new Intent(this, DataReceiverService.class);
        startService(intent);
        if (intent != null) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        } else {
            Toast.makeText(MagnetTestActivity.this, "First Satrt service to bind.",
                    Toast.LENGTH_LONG).show();
        }
        if (localservice != null) {
           // showNetworkInterFace();
            localservice.joinChannel();
            Toast.makeText(MagnetTestActivity.this, "localservice start.",
                    Toast.LENGTH_LONG).show();
        }
        else{
            
        }
    }

    void showHostAddress() {
        try {
            // String address=InetAddress.getLocalHost().getHostAddress();
            String address = NetworkHelper.getLocalAddress().getHostAddress().toString();
            if (address != null)
                Toast.makeText(getApplicationContext(), address.toString(), Toast.LENGTH_LONG)
                        .show();
            adapter2.add("IP Network: " + address.toString() + " ");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void showNetworkInterFace() {
        List<Integer> interfaces = localservice.getAvailableInterfaceTypes();
        for (Integer netInterface : interfaces) {
            switch (netInterface) {
                case MagnetManager.INTERFACE_TYPE_WIFI:
                    adapter2.add("INTERFACE_TYPE_WIFI");
                    Log.i(TAG, "getAvailableInterfaceTypes()  MagnetManager.INTERFACE_TYPE_WIFI");
                    break;
                case MagnetManager.INTERFACE_TYPE_WIFIAP:
                    adapter2.add("INTERFACE_TYPE_WIFIAP");
                    Log.i(TAG, "getAvailableInterfaceTypes  MagnetManager.INTERFACE_TYPE_WIFIAP");
                    break;
                case MagnetManager.INTERFACE_TYPE_WIFIP2P:
                    adapter2.add("INTERFACE_TYPE_WIFIP2P");
                    Log.i(TAG, "getAvailableInterfaceTypes  MagnetManager.INTERFACE_TYPE_WIFIP2P");
                    break;
                default:
                    Log.i(TAG, "getAvailableInterfaceTypes  MagnetManager.INTERFACE_TYPE_UNKNOWN");
                    break;
            }

        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_magnet_test);

        ListView listView = (ListView)findViewById(R.id.listview);
        ArrayList<String> list = new ArrayList<String>();

        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter2);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setStackFromBottom(true);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.bind).setOnClickListener(this);
        findViewById(R.id.unbind).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.play).setOnClickListener(this);
        findViewById(R.id.send_file).setOnClickListener(this);

        startDataReceiverService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.magnet_test, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:

                localservice.startRecord();
                break;
            case R.id.stop:
                localservice.stopRecord();
               // intent = new Intent(this, DataReceiverService.class);
               // stopService(intent);
                break;
            case R.id.button1:
                // adapter2.add(localservice.getService());
                localservice.joinChannel();
                Toast.makeText(MagnetTestActivity.this, "localservice start.",
                        Toast.LENGTH_LONG).show();

                break;
            case R.id.button2:
                
                EditText editText = (EditText)findViewById(R.id.editText1); // name
                String data = editText.getText().toString(); // Quiz
                localservice.sendMessage("User", data);
                editText.setText("");

                break;
            case R.id.bind:

                break;
            case R.id.unbind:
                if (intent != null) {
                    unbindService(serviceConnection);
                    // txt_view.setText("DisConnected !!!!! bind it for updating UI...";
                } else {
                    Toast.makeText(MagnetTestActivity.this, "First Satrt bind to UnBind.",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.play:
                localservice.playRecord();
                break;
            case R.id.send_file:
                localservice.sendRecordedFile();
                break;
        }

    }

    DataReceiverService localservice;

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // txt_view.setText("DisConnected !!!!! bind it for updating UI...";
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder SERVICE) {
            localservice = ((DataReceiverService.LocalBinder)SERVICE).getBinder();
            try {
                // txt_view.setText(String.valueOf(localservice.STR));
                // localservice.STR = "String changes from Activit"
            } catch (Exception e) {
            }
        }

    };

}
