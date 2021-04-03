package io.viro.p2pfs.telnet.message.receive;

/**
 * Receiving a join response.
 */
public class JoinResponseReceived extends ReceivedMessage {
    private int code;

    public JoinResponseReceived(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
