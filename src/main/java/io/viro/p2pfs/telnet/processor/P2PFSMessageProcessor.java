package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.P2PFSClient;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process messages.
 */
public class P2PFSMessageProcessor {

    private P2PFSMessageParser parser;
    private P2PFSClient client;
    private static final Logger logger = LoggerFactory.getLogger(P2PFSMessageProcessor.class);

    public P2PFSMessageProcessor(P2PFSClient client) {
        this.parser = new P2PFSMessageParser();
        this.client = client;
    }

    /**
     * To be completed malintha.
     *
     * @param message
     * @param sender
     */
    public void processMessage(String message, NodeCredentials sender) {
        Response response = parser.parseMessage(message);
        logger.info(response.toString()); //dummy to avoid findbugs
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
            } else {
                //add neighbors.
            }
        }
    }


}
