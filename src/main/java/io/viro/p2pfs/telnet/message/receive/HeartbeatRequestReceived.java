package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Search Response.
 */
public class HeartbeatRequestReceived extends ReceivedMessage {
    private NodeCredentials sender;

    public HeartbeatRequestReceived(NodeCredentials sender) {
        this.sender = sender;
    }

    public NodeCredentials getSender() {
        return sender;
    }
}
