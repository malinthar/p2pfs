package io.viro.p2pfs.telnet;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.Node;
import io.viro.p2pfs.Util;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;
import io.viro.p2pfs.telnet.message.send.HeartbeatResponseSent;
import io.viro.p2pfs.telnet.message.send.HeartbeatSent;
import io.viro.p2pfs.telnet.message.send.JoinRequestSent;
import io.viro.p2pfs.telnet.message.send.JoinResponseSent;
import io.viro.p2pfs.telnet.message.send.LeaveGracefullyResponseSent;
import io.viro.p2pfs.telnet.message.send.LeaveRequestSent;
import io.viro.p2pfs.telnet.message.send.Message;
import io.viro.p2pfs.telnet.message.send.RegisterRequest;
import io.viro.p2pfs.telnet.message.send.SearchRequest;
import io.viro.p2pfs.telnet.message.send.SearchResponseSent;
import io.viro.p2pfs.telnet.processor.P2PFSMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Communication client for each node.
 */
public class P2PFSClient implements Runnable {
    private DatagramSocket socket;
    private Node node;
    private long lastHeartbeatTime;
    private ArrayList<NodeCredentials> heartbeatList;
    NodeCredentials bootstrapServer;
    P2PFSMessageProcessor processor;
    Boolean isRegistered = false;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private static final Logger logger = LoggerFactory.getLogger(P2PFSClient.class);

    public P2PFSClient(Node node, NodeCredentials bootstrapServer) {
        this.node = node;
        this.bootstrapServer = bootstrapServer;
        this.processor = new P2PFSMessageProcessor(this);
        init();
    }

    public void run() {
        try {
            Util.print("New node created at " + node.getCredentials().getHost() +
                    ":" + node.getCredentials().getPort() + ". Waiting for incoming data...");
            String message;
            while (running.get()) {
                //Massage Receiver
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);
                byte[] data = incoming.getData();
                message = new String(data, 0, incoming.getLength());
                this.processor.processMessage(message,
                        new NodeCredentials(incoming.getAddress().getHostAddress(), incoming.getPort()));

                //HeartBeatings
//                if (System.currentTimeMillis() - lastHeartbeatTime > 60 * 1000) {
//                    for (NodeCredentials nodeCredentials : this.node.getRoutingTable()) {
//                        if (!heartbeatList.contains(nodeCredentials)) {
//                            heartbeatList.add(nodeCredentials);
//                            nodeAlive(new HeartbeatSent(this.node.getCredentials(), nodeCredentials));
//                        } else {
//                            //ungracefully departure
//                            this.node.removeNeighbour(nodeCredentials);
//                        }
//                    }
//                    lastHeartbeatTime = System.currentTimeMillis();
//                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void init() {
        try {
            this.heartbeatList = new ArrayList<>();
            this.lastHeartbeatTime = 0;
            this.socket = new DatagramSocket(this.node.getCredentials().getPort());
            this.running.set(true);
            new Thread(this).start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void stop() {
        running.set(false);
    }

    public void sendMessage(Message message) {
        try {
            String messageString = String.format("%04d", message.getMessage().length() + 5) +
                    Constant.SEPARATOR + message.getMessage();
            DatagramPacket datagramPacket = new DatagramPacket(messageString.getBytes(),
                    messageString.getBytes().length,
                    InetAddress.getByName(message.getReceiver().getHost()), message.getReceiver().getPort());
            socket.send(datagramPacket);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void registerNode() {
        Message message = new RegisterRequest(this.node.getCredentials(), bootstrapServer);
        sendMessage(message);
    }

    public void join(JoinRequestSent message) {
        sendMessage(message);
    }

    public void joinOK(JoinResponseSent message) {
        sendMessage(message);
    }

    public void search(SearchRequest message) {
        sendMessage(message);
    }

    public void searchOk(SearchResponseSent response) {
        sendMessage(response);
    }

    //search functionality.
    public void triggerSearch(SearchRequestDTO searchRequestDto) {
        //search local files
        List<String> searchResults = node.searchLocally(searchRequestDto.getKeyword());
        //pass to neighbors
        if (searchResults.isEmpty()) {
            Util.print("No hits found for " + searchRequestDto.getKeyword() + " locally");
            List<NodeCredentials> nextNodes = node.searchCache(searchRequestDto.getKeyword());
            if (nextNodes.isEmpty()) {
                nextNodes = node.getRoutingTable();
            }
            nextNodes.forEach((neighbor) -> {
                if (!neighbor.getHost().equals(searchRequestDto.getRequestNodeCredentials().getHost()) &&
                        neighbor.getPort() != searchRequestDto.getRequestNodeCredentials().getPort() &&
                        searchRequestDto.getHopCount() < Constant.MAX_HOP_COUNT) {
                    Util.print("Forward the search request to neighbor" + " : " + neighbor.getHost());
                    SearchRequest searchRequest = new SearchRequest(searchRequestDto, neighbor);
                    searchRequest.incrementHopCountByOne();
                    search(searchRequest);
                }
            });
            return;
        } else {
            if (searchRequestDto.getRequestNodeCredentials().equals(this.node.getCredentials())) {
                Util.print("__________Following results were found locally for keywords " +
                        searchRequestDto.getKeyword() + "__________");
                searchResults.forEach(result -> {
                    Util.print(result);
                });
                return;
            } else {
                Util.print("Hits found for keywords " +
                        searchRequestDto.getKeyword() +
                        "! send SEROK response to search query originator");
                SearchResponseSent response = new SearchResponseSent(searchRequestDto.getId(),
                        searchRequestDto.getKeyword(),
                        searchRequestDto.getRequestNodeCredentials(),
                        this.node.getCredentials(),
                        searchRequestDto.getHopCount(), searchResults);
                searchOk(response);
            }
        }
        //file found todo://hits are not exactly matching though, might not get the best results
    }

    public void initNewSearch(String query) {
        SearchRequestDTO searchRequestDTO = node.initNewSearch(query);
        Util.print("Triggered search request for " + searchRequestDTO.getKeyword());
        this.triggerSearch(searchRequestDTO);
    }

    public Node getNode() {
        return node;
    }

    public void setIsRegistered(Boolean status) {
        this.isRegistered = status;
    }

    public Boolean getIsRegistered() {
        return isRegistered;
    }

    public void nodeAlive(HeartbeatSent message) {
        sendMessage(message);
    }

    public void nodeOK(HeartbeatResponseSent message) {
        sendMessage(message);
    }

    public void removeNodeFromHeartBeatList(NodeCredentials nodeCredentials) {
        this.heartbeatList.remove(nodeCredentials);
    }

    public void leaveOK(LeaveGracefullyResponseSent message) {
        sendMessage(message);
    }

    public void leave() {
        for (NodeCredentials nodeCredentials : this.getNode().getRoutingTable()) {
            sendMessage(new LeaveRequestSent(this.getNode().getCredentials(),
                    nodeCredentials));
        }
        this.stop();
    }

}
