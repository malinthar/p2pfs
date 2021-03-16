package io.viro.p2pfs.telnet;

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
        logger.info(parser.parseMessage(message));
        logger.info(client.getClass().toString()); //dummay to avoid findbugs
    }
}
