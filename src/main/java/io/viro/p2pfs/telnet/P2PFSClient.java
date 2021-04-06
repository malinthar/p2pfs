package io.viro.p2pfs.telnet;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.Node;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;
import io.viro.p2pfs.telnet.message.receive.SearchResponse;
import io.viro.p2pfs.telnet.message.send.JoinRequestSent;
import io.viro.p2pfs.telnet.message.send.JoinResponseSent;
import io.viro.p2pfs.telnet.message.send.Message;
import io.viro.p2pfs.telnet.message.send.RegisterRequest;
import io.viro.p2pfs.telnet.message.send.SearchRequest;
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

    public Node getNode() {
        return node;
    }

    public void setIsRegistered(Boolean status) {
        this.isRegistered = status;
    }

    public Boolean getIsRegistered() {
        return isRegistered;
    }

    public void triggerSearch(SearchRequestDTO searchRequestDto) {
        List<String> searchResults = node.searchLocally(searchRequestDto.getKeyword());
        if (searchResults.isEmpty()) {
            System.out.println("File does not exists in " + node.getCredentials().getUserName());
            List<NodeCredentials> neighbors = node.getNeighbors();
            neighbors.forEach((neighbor) -> {
                search(neighbor, searchRequestDto);
                System.out.println("Forward SEARCH to neighbor" + " : " + neighbor.getUserName());
            });
            return;
        }

        System.out.println(
                "File is available at " + node.getCredentials().getUserName() + " , " +
                        node.getCredentials().getHost() + " : " + node.getCredentials().getPort());
        if (node.isEqual(searchRequestDto.getRequestNodeCredentials())) {
            System.out.println(
                    "File is available locally at " + node.getCredentials().getUserName() + " , " +
                            node.getCredentials().getHost() + " : " + node.getCredentials().getPort());
            return;
        }

        System.out.println("Send SEARCHOK response to search request originator");
        SearchResponse response =
                new SearchResponse(searchRequestDto.getId(), searchRequestDto.getRequestNodeCredentials(),
                        searchResults);
        searchOk(response);
    }

    public void initNewSearch(String query) {
        SearchRequestDTO searchRequestDTO = node.initNewSearch(query);
        System.out.println("\nTriggered search request for " + searchRequestDTO.getKeyword());
        this.triggerSearch(searchRequestDTO);
    }

    public void search(NodeCredentials neighborCredentials, SearchRequestDTO searchRequestDto) {

        SearchRequest searchRequest = new SearchRequest(searchRequestDto, neighborCredentials);
        String message = searchRequest.getMessage();
        try {
            socket.send(new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getByName(neighborCredentials.getHost()), neighborCredentials.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchOk(SearchResponse response) {
        String msg = response.getMessage();
        try {
            socket.send(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

