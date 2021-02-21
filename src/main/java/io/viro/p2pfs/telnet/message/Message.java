package io.viro.p2pfs.telnet.message;


/**
 * Message abstract class.
 */
public abstract class Message {
    private String sourceIP;
    private int sourcePORT;

    public Message(String sourceIP, int sourcePORT) {
        this.sourceIP = sourceIP;
        this.sourcePORT = sourcePORT;
    }

    public abstract String getMessage();

    public int getSourcePORT() {
        return sourcePORT;
    }

    public String getSourceIP() {
        return sourceIP;
    }
}
