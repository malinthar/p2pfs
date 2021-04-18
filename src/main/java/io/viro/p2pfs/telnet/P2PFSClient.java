package io.viro.p2pfs.telnet;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.Node;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;
import io.viro.p2pfs.telnet.message.send.*;
import io.viro.p2pfs.telnet.processor.P2PFSMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;


/**
 * Communication client for each node.
 */
public class P2PFSClient implements Runnable {
    private DatagramSocket socket;
    private Node node;
    private long lastHeartbeatTime;
    NodeCredentials bootstrapServer;
    P2PFSMessageProcessor processor;
    Boolean isRegistered = false;

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
                //Massage Receiver
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);
                byte[] data = incoming.getData();
                messege = new String(data, 0, incoming.getLength());
                logger.info(incoming.getAddress().getHostAddress() + " : " +
                        incoming.getPort() + " - " + messege);
                this.processor.processMessage(messege,
                        new NodeCredentials(incoming.getAddress().getHostAddress(), incoming.getPort()));

                //HeartBeatings
                if(System.currentTimeMillis()-lastHeartbeatTime>60*1000){
                    for (NodeCredentials nodeCredentials:this.node.getNeighbors()){
                        nodeAlive(new HeartbeatSent(this.node.getCredentials(),nodeCredentials));
                    }
                    lastHeartbeatTime=System.currentTimeMillis();
                }


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
            List<NodeCredentials> nextNodes = node.searchCache(searchRequestDto.getKeyword());
            if (nextNodes.isEmpty()) {
                nextNodes = node.getRoutingTable();
            }
            logger.info("File does not exist in " + node.getCredentials().getUserName());
            nextNodes.forEach((neighbor) -> {
                logger.info("Forward SEARCH to neighbor" + " : " + neighbor.getUserName());
                SearchRequest searchRequest = new SearchRequest(searchRequestDto, neighbor);
                searchRequest.incrementHopCountByOne();
                search(searchRequest);
            });
            return;
        }

        //file found todo://hits are not exactly matching though, might not get the best results
        logger.info("Hits found! send SEARCHOK response to search query originator");
        SearchResponseSent response = new SearchResponseSent(searchRequestDto.getId(),
                searchRequestDto.getKeyword(),
                searchRequestDto.getRequestNodeCredentials(),
                this.node.getCredentials(),
                searchRequestDto.getHopCount(), searchResults);
        searchOk(response);
    }

    public void initNewSearch(String query) {
        SearchRequestDTO searchRequestDTO = node.initNewSearch(query);
        logger.info("\nTriggered search request for " + searchRequestDTO.getKeyword());
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

    public void leaveOK(LeaveGracefullyResponseSent message) {
        sendMessage(message);
    }
}
