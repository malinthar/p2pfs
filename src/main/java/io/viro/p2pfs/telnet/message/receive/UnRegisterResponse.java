package io.viro.p2pfs.telnet.message.receive;

/**
 * Register response.
 */
public class UnRegisterResponse extends ReceivedMessage {
    public int errorCode;


    public UnRegisterResponse(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

