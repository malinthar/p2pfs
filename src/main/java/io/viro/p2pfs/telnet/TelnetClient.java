package io.viro.p2pfs.telnet;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.Node;
import io.viro.p2pfs.telnet.message.Message;
import io.viro.p2pfs.telnet.message.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Communication client for each node.
 */
public class TelnetClient implements Runnable {
    private DatagramSocket socket;
    private Node node;
    private String bootstrapServerIP;
    private int bootstrapServerPort;

    private static final Logger logger = LoggerFactory.getLogger(TelnetClient.class);

    public TelnetClient(Node node, String bootstrapServerIp, int bootstrapServerPort) {
        this.node = node;
        this.bootstrapServerIP = bootstrapServerIp;
        this.bootstrapServerPort = bootstrapServerPort;
        init();
    }

    public void run() {
        try {
            logger.info("New node created at" + node.getPort() + ". Waiting for incoming data...");
            String messege;
            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);
                byte[] data = incoming.getData();
                messege = new String(data, 0, incoming.getLength());
                logger.info(incoming.getAddress().getHostAddress() + " : " +
                        incoming.getPort() + " - " + messege);
            }
        } catch (IOException e) {
            //echo(e.getMessage());
        }
    }

    public void init() {
        try {
            socket = new DatagramSocket(this.node.getPort());
        } catch (Exception e) {
            new Thread(this).start();
        }
    }

    public void registerNode() {
        try {
            Message message = new RegisterRequest(this.node.getIp(), this.node.getPort(),
                    this.node.getUsername());
            String messageString = String.format("%04d", message.getMessage().length() + 5) +
                    Constant.SEPARATOR + message.getMessage();
            DatagramPacket datagramPacket = new DatagramPacket(messageString.getBytes(),
                    messageString.getBytes().length,
                    InetAddress.getByName(this.bootstrapServerIP), this.bootstrapServerPort);
            socket.send(datagramPacket);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
