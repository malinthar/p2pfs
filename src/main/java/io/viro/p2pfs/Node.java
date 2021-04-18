package io.viro.p2pfs;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node.
 */
public class    Node {
    NodeCredentials credentials;
    List<String> files;
    List<NodeCredentials> routingTable; //routing table
    List<NodeCredentials> secondaryNeighbors; //Limit=5,
    Map<NodeCredentials, List<String>> cache;

    // for search
    HashMap<Integer, SearchRequestDTO> activeSearchDetails = new HashMap<Integer, SearchRequestDTO>();
    private static int nextSearchId = 0;

    Node(NodeCredentials credentials, List<String> files) {
        this.credentials = credentials;
        routingTable = new ArrayList<>();
        secondaryNeighbors = new ArrayList<>();
        cache = new HashMap<>();
        this.files = files;
    }

    public boolean isEqual(NodeCredentials thatCredentials) {
        NodeCredentials thisCredentials = this.getCredentials();
        if (thisCredentials.getUserName().equals(thatCredentials.getUserName()) &&
                thisCredentials.getHost().equals(thatCredentials.getHost()) &&
                thisCredentials.getPort() == thatCredentials.getPort()) {
            return true;
        }
        return false;
    }

    public boolean isSearchActive(int searchId) {
        if (this.activeSearchDetails.containsKey(searchId)) {
            return true;
        }
        return false;
    }

    public void addNeighbor(NodeCredentials neighbor) {
        this.routingTable.add(neighbor);
    }

    public void addSecondaryNeighbor(NodeCredentials neighbor) {
        secondaryNeighbors.add(neighbor);
    }

    public int getNeighborCount() {
        return routingTable.size();
    }

    public List<NodeCredentials> getRoutingTable() {
        return this.routingTable;
    }

    public NodeCredentials getCredentials() {
        return credentials;
    }

    public List<String> getFiles() {
        return files;
    }

    public List<String> searchLocally(String keyword) {
        List<String> results = new ArrayList<>();
        files.forEach(file -> {
            if (file.contains(keyword)) {
                results.add(file);
            }
        });
        return results;
    }

    public List<NodeCredentials> searchCache(String keyword) {
        List<NodeCredentials> results = new ArrayList<>();
        cache.entrySet().forEach(entry -> {
            if (entry.getValue().contains(keyword)) {
                results.add(entry.getKey());
            }
        });
        return results;
    }

    public SearchRequestDTO initNewSearch(String query) {
//        List<String> keywordList = Arrays.asList(keywords.split(","));
        SearchRequestDTO searchRequestDTO = new SearchRequestDTO(this.nextSearchId, this.credentials, query, 0);
        activeSearchDetails.put(searchRequestDTO.getId(), searchRequestDTO);
        return searchRequestDTO;
    }

    public void addToCache(NodeCredentials credentials, String keyword) {
        if (this.cache.containsKey(credentials)) {
            this.cache.get(credentials).add(keyword);
            return;
        }

        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add(keyword);
        this.cache.put(credentials, keywords);
    }
}
