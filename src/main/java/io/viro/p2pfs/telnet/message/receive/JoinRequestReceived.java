package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Received join request.
 */
public class JoinRequestReceived extends ReceivedMessage {
    private NodeCredentials sender;

    public JoinRequestReceived(String ip, int port) {
        sender = new NodeCredentials(ip, port, null);
    }

    public NodeCredentials getSender() {
        return sender;
    }
}
