package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Heartbeat Response.
 */
public class HeartbeatResponse extends ReceivedMessage {
    private NodeCredentials sender;
    private int code;

    public HeartbeatResponse(NodeCredentials sender, int code) {
        this.sender = sender;
        this.code = code;
    }

    public NodeCredentials getSender() {
        return sender;
    }

    public int getCode() {
        return code;
    }

}
