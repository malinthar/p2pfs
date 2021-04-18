package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Receiving a join response.
 */
public class JoinResponseReceived extends ReceivedMessage {
    private int code;
    private NodeCredentials sender;

    public JoinResponseReceived(NodeCredentials sender, int code) {
        this.code = code;
        this.sender = sender;
    }

    public int getCode() {
        return code;
    }

    public NodeCredentials getSender() {
        return sender;
    }
}
