package pt.isec.pd.a2018020733.trabalhopratico.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ListNetworkInterfaces {

    public static void main(String[] args) throws SocketException {
        Enumeration<NetworkInterface> allNIs = NetworkInterface.getNetworkInterfaces();

        while(allNIs.hasMoreElements()){
            NetworkInterface ni = allNIs.nextElement();

            System.out.println(ni.getName());

            Enumeration<InetAddress> allIPs = ni.getInetAddresses();

            while(allIPs.hasMoreElements())
                System.out.println(allIPs.nextElement().getHostAddress());

            System.out.println();
        }
    }
}
