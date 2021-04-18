package io.viro.p2pfs.telnet.message.receive;
/**
 * Receiving a leave gracefully response.
 */
public class LeaveGracefullyResponse extends ReceivedMessage {
    private int code;

    public LeaveGracefullyResponse(int code) {
        super();
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
