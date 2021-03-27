package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import java.util.List;

/**
 * Register response.
 */
public class RegisterResponse extends Response {
    private List<NodeCredentials> neighboringNodes;
    public int errorCode;

    public RegisterResponse(List<NodeCredentials> neighboringNodes) {
        this.neighboringNodes = neighboringNodes;
    }

    public RegisterResponse(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<NodeCredentials> getNeighboringNodes() {
        return neighboringNodes;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

