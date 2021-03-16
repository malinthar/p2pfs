package io.viro.p2pfs;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a node.
 */
public class Node {
    NodeCredentials credentials;
    List<String> files;
    List<Map<String, String>> neighbors;

    Node(NodeCredentials credentials) {
        this.credentials = credentials;
        neighbors = new ArrayList<>();
        files = new ArrayList<>();
    }

    public void addNeighbors(List<Map<String, String>> neighbors) {
        this.neighbors.addAll(neighbors);
    }

    public List<Map<String, String>> getNeighbors() {
        return this.neighbors;
    }

    public NodeCredentials getCredentials() {
        return credentials;
    }

    public List<String> getFiles() {
        return files;
    }
}
