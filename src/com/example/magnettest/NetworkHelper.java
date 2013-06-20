package com.example.magnettest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.util.Log;

public class NetworkHelper {
 

    static public ArrayList<InetAddress> getConnectedDevices(String YourPhoneIPAddress) {
        ArrayList<InetAddress> ret = new ArrayList<InetAddress>();

        int LoopCurrentIP = 0;

        String IPAddress = "";
        String[] myIPArray = YourPhoneIPAddress.split("\\.");
        InetAddress currentPingAddr;

        for (int i = 0; i <= 255; i++) {
            try {

                // build the next IP address
                currentPingAddr = InetAddress.getByName(myIPArray[0] + "." + myIPArray[1] + "."
                        + myIPArray[2] + "." + Integer.toString(LoopCurrentIP));

                // 50ms Timeout for the "ping"
                if (currentPingAddr.isReachable(50)) {

                    ret.add(currentPingAddr);
                }
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }

            LoopCurrentIP++;
        }
        return ret;
    }

    public static InetAddress getLocalAddress()throws IOException {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        //return inetAddress.getHostAddress().toString();
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("SALMAN", ex.toString());
        }
        return null;
    }
    
}
