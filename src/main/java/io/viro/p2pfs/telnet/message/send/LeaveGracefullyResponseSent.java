package io.viro.p2pfs.telnet.message.send;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;

public class LeaveGracefullyResponseSent extends Message {

    private NodeCredentials sender;
    private String msg;

    public LeaveGracefullyResponseSent(NodeCredentials sender, NodeCredentials receiver, String msg) {
        super(receiver);
        this.sender=sender;
        this.msg=msg;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(Constant.NODEOK);
        return message.toString();
    }
}
