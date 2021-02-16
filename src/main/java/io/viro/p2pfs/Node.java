package io.viro.p2pfs;

import java.util.List;
import java.util.Map;

public class Node {
    private String ip;
    private int port;
    List<String> files;
    List<Map<String, String>> neighbors;

    public void join() {
        
    }

    public void addNeighbors(List<Map<String, String>> neighbors) {
        this.neighbors.addAll(neighbors);
    }

    public List<Map<String,String>> getNeighbors() {
        return this.neighbors;
    }


 }
