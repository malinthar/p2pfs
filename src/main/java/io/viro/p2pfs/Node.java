package io.viro.p2pfs;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import io.viro.p2pfs.telnet.dto.SearchRequestDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a node.
 */
public class Node {
    NodeCredentials credentials;
    List<String> files;
    List<NodeCredentials> neighbors;
    List<NodeCredentials> secondaryNeighbors;

    // for search
    HashMap<Integer, SearchRequestDTO> activeSearchDetails = new HashMap<Integer, SearchRequestDTO>();
    private static int nextSearchId = 0;

    Node(NodeCredentials credentials) {
        this.credentials = credentials;
        neighbors = new ArrayList<>();
        secondaryNeighbors = new ArrayList<>();
        files = new ArrayList<>();
    }

    public boolean isEqual(NodeCredentials thatCredentials) {
        NodeCredentials thisCredentials = this.getCredentials();
        if (thisCredentials.getUserName() == thatCredentials.getUserName() &&
                thisCredentials.getHost() == thatCredentials.getHost() &&
                thisCredentials.getPort() == thatCredentials.getPort()) {
            return true;
        }
        return false;
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

    public List<String> searchLocally(String keyword) {
        // to be implement
        return new ArrayList<>();
    }

    public SearchRequestDTO initNewSearch(String keyword) {
//        List<String> keywordList = Arrays.asList(keywords.split(","));
        SearchRequestDTO searchRequestDTO = new SearchRequestDTO(this.nextSearchId, this.credentials, keyword);
        activeSearchDetails.put(searchRequestDTO.getId(), searchRequestDTO);
        return searchRequestDTO;
    }
}
