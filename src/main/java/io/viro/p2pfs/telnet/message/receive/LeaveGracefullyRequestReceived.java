package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Receiving a leave gracefully request.
 */
public class LeaveGracefullyRequestReceived extends ReceivedMessage {

    private NodeCredentials sender;

    public LeaveGracefullyRequestReceived(NodeCredentials sender) {
        this.sender = sender;
    }

    public NodeCredentials getSender() {
        return sender;
    }
}
