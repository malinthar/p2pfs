package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import java.util.List;

public class RegisterResponse extends Response {
    private List<NodeCredentials> neighboringNodes;

    public RegisterResponse(List<NodeCredentials> neighboringNodes) {
        this.neighboringNodes = neighboringNodes;
    }
}
