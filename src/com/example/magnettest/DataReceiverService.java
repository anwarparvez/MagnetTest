
package com.example.magnettest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class DataReceiverService extends Service {

    private static final String PUBLIC_CHANNEL_NAME = "MagnetManager.PUBLIC_CHANNEL";
     static final String  REMOTE_COMMAND_RECORDER_ON  = "REMOTE_COMMAND_RECORDER_ON";
     static final String  REMOTE_COMMAND_RECORDER_OFF  = "REMOTE_COMMAND_RECORDER_OFF";
     static final String  REMOTE_COMMAND_SEND_FILE  = "REMOTE_COMMAND_SEND_FILE";
     static final String  REMOTE_COMMAND_ALARM_PLAY  = "REMOTE_COMMAND_ALARM_PLAY";

    MagnetManager mMagnet = null;

    IMagnetChannel channelInst;

    List<Integer> getAvailableInterfaceTypes() {
        List<Integer> interfaces = mMagnet.getAvailableInterfaceTypes();
        return interfaces;
    }

    void initMagnet() {
        // #1. get instance
        mMagnet = MagnetManager.getInstance(this);
        String mMyTempDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audioTest";
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
                
                 //IMagnetChannel c;
                 channelInst.acceptFile(arg5, 3000, 4, arg6);
               //  channelInst.acceptFile(arg0, arg1, arg2, arg3);//File(arg5);
               // c.acceptFile(arg0, arg1, arg2, arg3)
               //  acceptFile(arg5, 3000, 4, 100);
                Toast.makeText(DataReceiverService.this, "onFileWillReceive" + arg0 + ":" + arg2,
                        300).show();

            }

            @Override
            public void onFileSent(String arg0, String arg1, String arg2, String arg3, String arg4,
                    String arg5) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFileReceived(String fromNode, String fileName, String hash,
                    String fileType, String exchangeId, String arg5, long fileSize,
                    String tmpFilePath) {
                Toast.makeText(DataReceiverService.this,
                        "onFileReceived" + tmpFilePath + ":" + arg5, 300).show();
                playtReceivedFile(tmpFilePath) ;

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
                    if (string.equals("play")) {
                        playAlarm();
                    }
                    if(string.equals(REMOTE_COMMAND_RECORDER_ON))
                        startRecord();
                    else if(string.equals(REMOTE_COMMAND_RECORDER_OFF))
                        stopRecord();
                    else if(string.equals(REMOTE_COMMAND_SEND_FILE))
                    {
                        sendRecordedFile();
                    }
                    // playAlarm();
                }

                // TODO Auto-generated method stub

            }
        });

    }
    
    void sendCommand(String userName, String data) {
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

    void sendRecordedFile() {
        if (channelInst != null) {

            List<String> nodeList = channelInst.getJoinedNodeList();
            if (nodeList != null) {

                for (String node : nodeList) {
                    channelInst.sendFile(node, PUBLIC_CHANNEL_NAME, recordingFile.getPath(),
                            60 * 60 * 1000);
                }

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

    void startRecord() {

        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/audioTest");
        path.mkdirs();
        try {
            recordingFile = File.createTempFile("recording", ".pcm", path);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create file on SD card", e);
        }
        RecordThread td = new RecordThread();
        td.start();
        Toast.makeText(this, "Start Record", 300).show();
    }

    void stopRecord() {

        isRecording = false;

    }

    void playRecord() {
        PlayThread td = new PlayThread();
        td.start();
        Toast.makeText(this, "Play Record", 300).show();

    }

    void playtReceivedFile(String tempPath) {

        File path = new File(tempPath);
        receivedFile = path;
        PlayReceivedFileThread td = new PlayReceivedFileThread();
        td.start();
        Toast.makeText(this, "PlayReceivedFileThread", 300).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // td.start();
        // playAlarm();

        return super.onStartCommand(intent, flags, startId);
    }

    int frequency = 11025, channelConfiguration = AudioFormat.CHANNEL_IN_MONO;

    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    File recordingFile;

    File receivedFile;

    private boolean isRecording;

    private boolean isPlaying;

    private class PlayReceivedFileThread extends Thread {
        @Override
        public void run() {
            super.run();
            isPlaying = true;
            int channelConfiguration = AudioFormat.CHANNEL_OUT_MONO;
            int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration,
                    audioEncoding);

            short[] audiodata = new short[bufferSize / 4];

            try {
                DataInputStream dis = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(receivedFile)));
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize, AudioTrack.MODE_STREAM);

                audioTrack.play();
                while (isPlaying && dis.available() > 0) {
                    int i = 0;
                    while (dis.available() > 0 && i < audiodata.length) {
                        audiodata[i] = dis.readShort();
                        i++;
                    }
                    audioTrack.write(audiodata, 0, audiodata.length);
                }
                dis.close();

            } catch (Throwable t) {
                Log.e("AudioTrack", "Playback Failed");
            }

        }
    }

    private class PlayThread extends Thread {
        @Override
        public void run() {
            super.run();
            isPlaying = true;
            int channelConfiguration = AudioFormat.CHANNEL_OUT_MONO;
            int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration,
                    audioEncoding);

            short[] audiodata = new short[bufferSize / 4];

            try {
                DataInputStream dis = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(recordingFile)));
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize, AudioTrack.MODE_STREAM);

                audioTrack.play();
                while (isPlaying && dis.available() > 0) {
                    int i = 0;
                    while (dis.available() > 0 && i < audiodata.length) {
                        audiodata[i] = dis.readShort();
                        i++;
                    }
                    audioTrack.write(audiodata, 0, audiodata.length);
                }
                dis.close();

            } catch (Throwable t) {
                Log.e("AudioTrack", "Playback Failed");
            }

        }
    }

    private class RecordThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {

                isRecording = true;
                try {
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
                            new FileOutputStream(recordingFile)));
                    int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,
                            audioEncoding);
                    AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                            frequency, channelConfiguration, audioEncoding, bufferSize);

                    short[] buffer = new short[bufferSize];
                    audioRecord.startRecording();
                    int r = 0;
                    while (isRecording) {
                        int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                        for (int i = 0; i < bufferReadResult; i++) {
                            dos.writeShort(buffer[i]);
                        }
                        // publishProgress(new Integer(r));
                        r++;
                    }
                    audioRecord.stop();
                    Toast.makeText(getApplicationContext(), "Stop Record", 300).show();
                    Log.e("AudioRecord", "Recording Stop");
                    dos.close();
                } catch (Throwable t) {
                    Log.e("AudioRecord", "Recording Failed");
                }

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

    void playAlarm() {
        // Create an offset from the current time in which the alarm will go
        // off.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        // Create a new PendingIntent and add it to the AlarmManager
        Intent intent = new Intent(this, AlarmReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 12345, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

    }

}
