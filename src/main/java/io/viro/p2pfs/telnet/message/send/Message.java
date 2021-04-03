package io.viro.p2pfs.telnet.message.send;


import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Message abstract class.
 */
public abstract class Message {
    private NodeCredentials receiver;

    public Message(NodeCredentials receiver) {
        this.receiver = receiver;
    }

    public abstract String getMessage();

    public NodeCredentials getReceiver() {
        return receiver;
    }
}
