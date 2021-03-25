package io.viro.p2pfs.telnet;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.Node;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.message.Message;
import io.viro.p2pfs.telnet.message.RegisterRequest;
import io.viro.p2pfs.telnet.processor.P2PFSMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Communication client for each node.
 */
public class P2PFSClient implements Runnable {
    private DatagramSocket socket;
    private Node node;
    NodeCredentials bootstrapServer;
    P2PFSMessageProcessor processor;

    private static final Logger logger = LoggerFactory.getLogger(P2PFSClient.class);

    public P2PFSClient(Node node, NodeCredentials bootstrapServer) {
        this.node = node;
        this.bootstrapServer = bootstrapServer;
        this.processor = new P2PFSMessageProcessor(this);
        init();
    }

    public void run() {
        try {
            logger.info("New node created at" + node.getCredentials().getPort() + ". Waiting for incoming data...");
            String messege;
            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);
                byte[] data = incoming.getData();
                messege = new String(data, 0, incoming.getLength());
                logger.info(incoming.getAddress().getHostAddress() + " : " +
                        incoming.getPort() + " - " + messege);
                this.processor.processMessage(messege,
                        new NodeCredentials(incoming.getAddress().getHostAddress(), incoming.getPort()));

            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void init() {
        try {
            socket = new DatagramSocket(this.node.getCredentials().getPort());
            new Thread(this).start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void registerNode() {
        try {
            Message message = new RegisterRequest(this.node.getCredentials().getHost(),
                    this.node.getCredentials().getPort(), this.node.getCredentials().getUserName());
            String messageString = String.format("%04d", message.getMessage().length() + 5) +
                    Constant.SEPARATOR + message.getMessage();
            DatagramPacket datagramPacket = new DatagramPacket(messageString.getBytes(),
                    messageString.getBytes().length,
                    InetAddress.getByName(this.bootstrapServer.getHost()), this.bootstrapServer.getPort());
            socket.send(datagramPacket);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void join() {

    }
}
