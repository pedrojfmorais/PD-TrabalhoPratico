package utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ListNI
{
    public static void main(String[] args) throws SocketException
    {
        Enumeration<NetworkInterface> allNIs = NetworkInterface.getNetworkInterfaces();
        while(allNIs.hasMoreElements())
        {
            NetworkInterface networkInterface = allNIs.nextElement();
            System.out.println(networkInterface.getName());

            Enumeration<InetAddress> allIPs = networkInterface.getInetAddresses();
            while (allIPs.hasMoreElements())
                System.out.println(allIPs.nextElement().getHostAddress());

            System.out.println();
        }
    }
}
