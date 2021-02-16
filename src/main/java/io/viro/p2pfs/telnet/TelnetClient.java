package io.viro.p2pfs.telnet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TelnetClient {
    private static TelnetClient INSTANCE = new TelnetClient();
    DatagramSocket sock = null;

    private TelnetClient() { }

    public static TelnetClient getInstance() {
        return INSTANCE;
    }

    public void init() {
        try {
            sock = new DatagramSocket(55556);
            String s;
            echo("New node created at 55556. Waiting for incoming data...");

            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);
                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());
                echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
            }
        } catch (IOException e) {
            echo(e.getMessage());
        }
    }

    public void registerNode() {
        try {
            String reply = "REG 129.82.123.45 5001 1234abcd";
            reply = String.format("%04d", reply.length() + 5) + " " + reply;
            DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length, InetAddress.getByName("localhost"), 55555);
            sock.send(dpReply);
        } catch (IOException e) {
            echo(e.getMessage());
        }
    }

    public static void echo(String msg) {
        System.out.println(msg);
    }
}
