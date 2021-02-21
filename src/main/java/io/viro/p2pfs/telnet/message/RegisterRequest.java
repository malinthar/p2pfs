package io.viro.p2pfs.telnet.message;

import io.viro.p2pfs.Constant;

/**
 * Register Request.
 */
public class RegisterRequest extends Message {

    private String sourceIP;
    private int sourcePORT;
    private String username;

    public RegisterRequest(String sourceIP, int sourcePORT, String username) {
        super(sourceIP, sourcePORT);
        this.sourceIP = sourceIP;
        this.sourcePORT = sourcePORT;
        this.username = username;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(Constant.REG_COM);
        message.append(Constant.SEPARATOR).append(this.sourceIP).append(Constant.SEPARATOR)
                .append(this.sourcePORT).append(Constant.SEPARATOR).append(username);
        return message.toString();
    }
}
