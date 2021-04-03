package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import java.util.List;

/**
 * Register response.
 */
public class RegisterResponse extends ReceivedMessage {
    private List<NodeCredentials> neighboringNodes;
    public int errorCode;

    public RegisterResponse(List<NodeCredentials> neighboringNodes) {
        this.neighboringNodes = neighboringNodes;
        this.errorCode = -1;
    }

    public RegisterResponse(int errorCode) {
        this.neighboringNodes = null;
        this.errorCode = errorCode;
    }

    public List<NodeCredentials> getNeighboringNodes() {
        return neighboringNodes;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

