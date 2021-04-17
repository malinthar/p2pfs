package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.P2PFSClient;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.message.receive.JoinRequestReceived;
import io.viro.p2pfs.telnet.message.receive.JoinResponseReceived;
import io.viro.p2pfs.telnet.message.receive.ReceivedMessage;
import io.viro.p2pfs.telnet.message.receive.RegisterResponse;
import io.viro.p2pfs.telnet.message.receive.SearchRequestReceived;
import io.viro.p2pfs.telnet.message.receive.SearchResponseReceived;
import io.viro.p2pfs.telnet.message.send.JoinRequestSent;
import io.viro.p2pfs.telnet.message.send.JoinResponseSent;
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
                    logger.info("Registered successfully, BS server responded with 0 nodes!");
                } else {
                    //select two random nodes from the returned nodes and put join requests to them.
                    logger.info("Registered successfully, BS server responded with " + nodesList.size() + " nodes!");
                    for (int i = 0; i < 2; i++) {
                        if (nodesList.size() == 0) {
                            break;
                        } else {
                            int randomNumber = random.nextInt(nodesList.size());
                            NodeCredentials neighbor = nodesList.get(randomNumber);
                            nodesList.remove(randomNumber);
                            client.join(new JoinRequestSent(client.getNode().getCredentials(), neighbor));
                        }
                    }
                    //todo: what can we do with the remaining nodes.
                }
            }
        } else if (response instanceof JoinResponseReceived) {
            if (((JoinResponseReceived) response).getCode() == Constant.JOIN_SUCCESS) {
                //todo: Logic to be discussed.
                this.client.getNode().addNeighbor(sender);
                logger.info("Node ", sender.getHost(), "is added to routing table of Node ",
                        this.client.getNode().getCredentials().getHost(), " after JOINOK");
            }
        } else if (response instanceof JoinRequestReceived) {
            logger.info("Join request received from ", sender.getHost());
            if (this.client.getNode().getNeighborCount() < 6) {
                this.client.getNode().addNeighbor(sender);
                this.client.joinOK(new JoinResponseSent(this.client.getNode().getCredentials(),
                        sender, Constant.JOIN_SUCCESS));
            } else {
                //todo:What to do in this case. Here send the error code
                this.client.getNode().addSecondaryNeighbor(sender);
                this.client.joinOK(new JoinResponseSent(this.client.getNode().getCredentials(),
                        sender, Constant.JOIN_ERROR));
            }

        } else if (response instanceof SearchRequestReceived) {
            //todo: Complete
        } else if (response instanceof SearchResponseReceived) {
            //todo: Complete
        } else {
            logger.info("no handler");
        }
    }
}
