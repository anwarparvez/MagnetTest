
package com.example.magnettest;

import java.util.Calendar;
import java.util.List;

import com.samsung.magnet.IMagnetChannel;
import com.samsung.magnet.IMagnetChannelListener;
import com.samsung.magnet.IMagnetManagerListener;
import com.samsung.magnet.MagnetManager;

import android.R.integer;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class DataReceiverService extends Service {

    private static final String PUBLIC_CHANNEL_NAME = "MagnetManager.PUBLIC_CHANNEL";

    MagnetManager mMagnet = null;

    IMagnetChannel channelInst;

    List<Integer> getAvailableInterfaceTypes() {
        List<Integer> interfaces = mMagnet.getAvailableInterfaceTypes();
        return interfaces;
    }

    void initMagnet() {
        // #1. get instance
        mMagnet = MagnetManager.getInstance(this);
        String mMyTempDirectory = "/data/anr/";
        // #2. set some values before start
        mMagnet.setTempDirectory(mMyTempDirectory);
        mMagnet.setHandleEventLooper(getMainLooper());
        // showNetworkInterFace();
        // #3. select network interface
        List<Integer> interfaces = mMagnet.getAvailableInterfaceTypes();

        for (final Integer integer : interfaces) {

            Toast.makeText(getApplicationContext(), "Abailable Network: " + integer + " ",
                    Toast.LENGTH_LONG).show();
            // adapter2.add("Abailable Network: " + integer + " ");
            // #4. Start magnet
            mMagnet.start(integer, new IMagnetManagerListener() {

                @Override
                public void onError(int arg0) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNetworkDisconnected() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "onNetworkDisconnected",
                            Toast.LENGTH_LONG).show();

                }

                @Override
                public void onStarted(String arg0, int arg1) {
                    // TODO Auto-generated method stub
                    final int a = integer;
                    Toast.makeText(getApplicationContext(),
                            "onStarted: agr0- " + arg0 + " agr1-" + arg1 + " InterFace-" + a + " ",
                            Toast.LENGTH_LONG).show();
                    // adapter2.add("onStarted: agr0- " + arg0 + " agr1-" + arg1
                    // + " InterFace-" + a + " ");

                }

            });
        }
    }

    void joinChannel() {
        channelInst = mMagnet.joinChannel(PUBLIC_CHANNEL_NAME, new IMagnetChannelListener() {

            @Override
            public void onNodeLeft(String arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNodeJoined(String arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFileWillReceive(String arg0, String arg1, String arg2, String arg3,
                    String arg4, String arg5, long arg6) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFileSent(String arg0, String arg1, String arg2, String arg3, String arg4,
                    String arg5) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFileReceived(String arg0, String arg1, String arg2, String arg3,
                    String arg4, String arg5, long arg6, String arg7) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFileFailed(String arg0, String arg1, String arg2, String arg3,
                    String arg4, int arg5) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFileChunkSent(String arg0, String arg1, String arg2, String arg3,
                    String arg4, String arg5, long arg6, long arg7, long arg8) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFileChunkReceived(String arg0, String arg1, String arg2, String arg3,
                    String arg4, String arg5, long arg6, long arg7) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataReceived(String arg0, String arg1, String arg2, byte[][] data) {
                if (data != null && data.length == 2) {
                    String name = new String(data[0]);
                    String string = new String(data[1]);
                    // adapter2.add(name + ":" + string);
                    Toast.makeText(DataReceiverService.this, name + ":" + string, 300).show();
                    if(string.equals("play"))
                    {
                        playAlarm();
                    }
                    //playAlarm();
                }

                // TODO Auto-generated method stub

            }
        });

    }

    void sendMessage(String userName, String data) {
        if (channelInst != null) {

            List<String> nodeList = channelInst.getJoinedNodeList();
            if (nodeList != null) {

                // #1. SEND QUIZ

                byte[][] payload = new byte[2][1];
                payload[0] = userName.getBytes(); // Sender’s friendly
                                                  // name
                payload[1] = data.getBytes(); // Quiz
                channelInst.sendDataToAll(PUBLIC_CHANNEL_NAME, payload);
                // editText.setText("");

                // setEditTextFocus(false);

            } else {
                Toast.makeText(getApplicationContext(), "nodeList==null  ", Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "channelInst==null  ", Toast.LENGTH_LONG)
                    .show();
        }

    }

    void destroyMagnet() {
        // #2. leave channel
        mMagnet.leaveChannel(PUBLIC_CHANNEL_NAME);
        // #6. Stop magnet
        mMagnet.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return iBinder;
    }

    private IBinder iBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        DataReceiverService getBinder() {
            return DataReceiverService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service Created", 300).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroy", 300).show();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Toast.makeText(this, "Service LowMemory", 300).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // super.onStart(intent, startId);

        Toast.makeText(this, "Service start", 300).show();
        initMagnet();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "task perform in service", 300).show();
        ThreadDemo td = new ThreadDemo();
        td.start();
        //playAlarm();
        return super.onStartCommand(intent, flags, startId);
    }

    private class ThreadDemo extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                sleep(70 * 1000);
                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // showAppNotification();
        }
    };

    String getService() {
        return "getService";
    }
    
    void playAlarm(){
        //Create an offset from the current time in which the alarm will go off.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
 
        //Create a new PendingIntent and add it to the AlarmManager
        Intent intent = new Intent(this, AlarmReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
            12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = 
            (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                pendingIntent);
       
    }

}
