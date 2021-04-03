package io.viro.p2pfs;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node.
 */
public class Node {
    NodeCredentials credentials;
    List<String> files;
    List<NodeCredentials> neighbors;
    List<NodeCredentials> secondaryNeighbors;

    Node(NodeCredentials credentials) {
        this.credentials = credentials;
        neighbors = new ArrayList<>();
        secondaryNeighbors = new ArrayList<>();
        files = new ArrayList<>();
    }

    public void addNeighbor(NodeCredentials neighbor) {
        this.neighbors.add(neighbor);
    }

    public void addSecondaryNeighbor(NodeCredentials neighbor) {
        secondaryNeighbors.add(neighbor);
    }

    public int getNeighborCount() {
        return neighbors.size();
    }

    public List<NodeCredentials> getNeighbors() {
        return this.neighbors;
    }

    public NodeCredentials getCredentials() {
        return credentials;
    }

    public List<String> getFiles() {
        return files;
    }
}
