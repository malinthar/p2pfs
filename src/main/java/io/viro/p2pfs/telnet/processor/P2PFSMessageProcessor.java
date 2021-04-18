package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.Util;
import io.viro.p2pfs.telnet.P2PFSClient;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;
import io.viro.p2pfs.telnet.message.receive.HeartbeatRequestReceived;
import io.viro.p2pfs.telnet.message.receive.HeartbeatResponse;
import io.viro.p2pfs.telnet.message.receive.JoinRequestReceived;
import io.viro.p2pfs.telnet.message.receive.JoinResponseReceived;
import io.viro.p2pfs.telnet.message.receive.LeaveGracefullyRequestReceived;
import io.viro.p2pfs.telnet.message.receive.LeaveGracefullyResponse;
import io.viro.p2pfs.telnet.message.receive.ReceivedMessage;
import io.viro.p2pfs.telnet.message.receive.RegisterResponse;
import io.viro.p2pfs.telnet.message.receive.SearchRequestReceived;
import io.viro.p2pfs.telnet.message.receive.SearchResponseReceived;
import io.viro.p2pfs.telnet.message.send.HeartbeatResponseSent;
import io.viro.p2pfs.telnet.message.send.JoinRequestSent;
import io.viro.p2pfs.telnet.message.send.JoinResponseSent;
import io.viro.p2pfs.telnet.message.send.LeaveGracefullyResponseSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * Process messages.
 */
public class P2PFSMessageProcessor {

    private P2PFSMessageParser parser;
    private P2PFSClient client;
    private static final Logger logger = LoggerFactory.getLogger(P2PFSMessageProcessor.class);
    private Random random;

    public P2PFSMessageProcessor(P2PFSClient client) {
        this.parser = new P2PFSMessageParser();
        this.client = client;
        this.random = new Random();
    }

    /**
     * To be completed malintha.
     *
     * @param message
     * @param sender
     */
    public void processMessage(String message, NodeCredentials sender) {
        ReceivedMessage response = parser.parseMessage(message);
        if (response instanceof RegisterResponse) {
            if (((RegisterResponse) response).getNeighboringNodes() == null) {
                int errorCode = ((RegisterResponse) response).getErrorCode();
                if (errorCode == Constant.ALREADY_REGISTERED_TO_ME) {
                    logger.error("Already registered to this host!");
                } else if (errorCode == Constant.ALREADY_REGISTERED) {
                    logger.error("Already registered to another host!");
                } else if (errorCode == Constant.BS_FULL) {
                    logger.error("Boostrap Server is full!");
                } else if (errorCode == Constant.COMMAND_ERROR) {
                    logger.error("Host error!");
                } else {
                    logger.error("Unknown error");
                }
                client.setIsRegistered(false);
            } else {
                client.setIsRegistered(true);
                List<NodeCredentials> nodesList = ((RegisterResponse) response).getNeighboringNodes();
                if (nodesList.size() == 0) {
                    Util.print("Registered successfully, BS server responded with 0 nodes!");
                } else {
                    //select two random nodes from the returned nodes and put join requests to them.
                    Util.print("Registered successfully, BS server responded with " + nodesList.size() + " nodes!");
                    for (int i = 0; i < 2; i++) {
                        if (nodesList.size() == 0) {
                            break;
                        } else {
                            int randomNumber = random.nextInt(nodesList.size());
                            NodeCredentials neighbor = nodesList.get(randomNumber);
                            nodesList.remove(randomNumber);
                            Util.print("Sent join request to " + neighbor.getHost());
                            client.join(new JoinRequestSent(client.getNode().getCredentials(), neighbor));
                        }
                    }
                    //todo: What can we do with the remaining nodes.
                }
            }
        } else if (response instanceof JoinResponseReceived) {
            if (((JoinResponseReceived) response).getCode() == Constant.JOIN_SUCCESS) {
                this.client.getNode().addNeighbor(((JoinResponseReceived) response).getSender());
                Util.print("Node " + ((JoinResponseReceived) response).getSender().getHost() +
                        " is added to routing table");
            } else {
                //todo: JoinResponse is an error what to do?
                Util.print("Error in joining with node" + sender.getHost());
            }
        } else if (response instanceof JoinRequestReceived) {
            Util.print("Join request received from " + ((JoinRequestReceived) response).getSender().getHost());
            if (this.client.getNode().getNeighborCount() < 6) {
                this.client.getNode().addNeighbor(((JoinRequestReceived) response).getSender());
                Util.print("Node " + ((JoinRequestReceived) response).getSender().getHost() +
                        " is added to routing table");
                this.client.joinOK(new JoinResponseSent(this.client.getNode().getCredentials(),
                        ((JoinRequestReceived) response).getSender(), Constant.JOIN_SUCCESS));

            } else {
                //todo:What to do in this case. Here send the error code
                this.client.getNode().addSecondaryNeighbor(sender);
                Util.print("Routing table is full, sent error code");
                this.client.joinOK(new JoinResponseSent(this.client.getNode().getCredentials(),
                        sender, Constant.JOIN_ERROR));

            }
        } else if (response instanceof SearchRequestReceived) {
            Util.print("Search request received from " + sender.getHost());
            SearchRequestReceived searchRequestReceived = (SearchRequestReceived) response;
            SearchRequestDTO searchRequestDTO = new SearchRequestDTO(searchRequestReceived);
            this.client.triggerSearch(searchRequestDTO);

        } else if (response instanceof SearchResponseReceived) {
            Util.print("Search response received from " + sender.getHost());
            SearchResponseReceived searchResultResponse = (SearchResponseReceived) response;
            if (searchResultResponse.getResults().size() != 0) {
                Util.print("Hit! the files have been found at " +
                        sender.getHost() + " files are :" + searchResultResponse.getResults().toString());
                this.client.getNode()
                        .addToCache(searchResultResponse.getCredential(), searchResultResponse.getKeyword());
            }

            //todo: Complete
        } else if (response instanceof HeartbeatRequestReceived) {
            Util.print("Heartbeat request received from " + sender.getHost());
            this.client.nodeOK(
                    new HeartbeatResponseSent(this.client.getNode().getCredentials(), sender, Constant.NODE_ALIVE));
        } else if (response instanceof HeartbeatResponse) {
            Util.print("Heartbeat Response received from " + sender.getHost());
            this.client.removeNodeFromHeartBeatList(sender);
        } else if (response instanceof LeaveGracefullyRequestReceived) {
            Util.print("Leave Gracefully request received from " + sender.getHost());
            List<NodeCredentials> neighbours = this.client.getNode().getRoutingTable();
            neighbours.remove(sender);
            logger.info("Leave Gracefully request received from ", sender.getHost());
            this.client.getNode().removeNeighbour(sender);
            this.client.leaveOK(new LeaveGracefullyResponseSent(
                    this.client.getNode().getCredentials(), sender, Constant.LEAVE_SUCCESS));
        } else if (response instanceof LeaveGracefullyResponse) {
            Util.print("Leave Gracefully response received from " + sender.getHost());
            if (((LeaveGracefullyResponse) response).getCode() == Constant.LEAVE_SUCCESS) {
                //todo: what have to do when leave
            }
        } else {
            logger.info("no handler");
        }
    }
}
