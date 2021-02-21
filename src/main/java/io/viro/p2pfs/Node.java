package io.viro.p2pfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a node.
 */
public class Node {
    private String username;
    private String ip;
    private int port;
    List<String> files;
    List<Map<String, String>> neighbors;

    Node(String username, String ip, int port) {
        neighbors = new ArrayList<>();
        files = new ArrayList<>();
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public void addNeighbors(List<Map<String, String>> neighbors) {
        this.neighbors.addAll(neighbors);
    }

    public List<Map<String, String>> getNeighbors() {
        return this.neighbors;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getFiles() {
        return files;
    }
}
