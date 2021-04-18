package io.viro.p2pfs.telnet.message.send;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Leave request.
 */
public class LeaveRequestSent extends Message {

    private NodeCredentials sender;

    public LeaveRequestSent(NodeCredentials sender, NodeCredentials receiver) {
        super(receiver);
        this.sender = sender;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(Constant.LEAVE);
        message.append(Constant.SEPARATOR).append(sender.getHost()).append(Constant.SEPARATOR)
                .append(sender.getPort());
        return message.toString();
    }

}
