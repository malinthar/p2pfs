package io.viro.p2pfs.telnet.message.send;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Register Request.
 */
public class RegisterRequest extends Message {

    private NodeCredentials senderCredential;

    public RegisterRequest(NodeCredentials sender, NodeCredentials receiver) {
        super(receiver);
        this.senderCredential = sender;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(Constant.REG);
        message.append(Constant.SEPARATOR).append(this.senderCredential.getHost()).append(Constant.SEPARATOR)
                .append(this.senderCredential.getPort()).
                append(Constant.SEPARATOR).append(this.senderCredential.getUserName());
        return message.toString();
    }
}
