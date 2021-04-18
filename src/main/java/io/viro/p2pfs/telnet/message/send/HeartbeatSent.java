package io.viro.p2pfs.telnet.message.send;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Join request.
 */
public class HeartbeatSent extends Message {

    private NodeCredentials sender;

    public HeartbeatSent(NodeCredentials sender, NodeCredentials receiver) {
        super(receiver);
        this.sender = sender;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(Constant.CHECK_NODE);
        message.append(Constant.SEPARATOR).append(sender.getHost()).append(Constant.SEPARATOR)
                .append(sender.getPort());
        return message.toString();
    }

}
