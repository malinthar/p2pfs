package io.viro.p2pfs.telnet.message.send;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
/**
 * Send a Heartbeat response.
 */
public class LeaveGracefullyResponseSent extends Message {

    private NodeCredentials sender;
    private int code;

    public LeaveGracefullyResponseSent(NodeCredentials sender, NodeCredentials receiver, int code) {
        super(receiver);
        this.sender = sender;
        this.code = code;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(Constant.LEAVEOK);
        message.append(Constant.SEPARATOR).append(this.code);
        return message.toString();
    }
}
