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
public class Node {
    NodeCredentials credentials;
    List<String> files;
    List<NodeCredentials> routingTable; //routing table
    List<NodeCredentials> secondaryNeighbors; //Limit=5,
    Map<NodeCredentials, List<String>> cache;

    // for search
    HashMap<Integer, SearchRequestDTO> activeSearchDetails = new HashMap<Integer, SearchRequestDTO>();
    private int nextSearchId = 0;

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
        updateRoutingTable();
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
        return this.files;
    }

    public List<String> searchLocally(String keyword) {
        String[] words = keyword.split("_");
        List<String> results = new ArrayList<>();
        for (String file : this.files) {
            int count = 0;
            for (int i = 0; i < words.length; i++) {
                if (file.contains(words[i])) {
                    count += 1;
                }
            }
            if (count == words.length) {
                results.add(file.replace(" ", "_"));
            }
        }
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
        this.nextSearchId++;
        activeSearchDetails.put(searchRequestDTO.getId(), searchRequestDTO);
        return searchRequestDTO;
    }

    public void addToCache(NodeCredentials credentials, String keyword) {
        cache.entrySet().forEach(entry -> {
            if (entry.getKey().getHost().equals(credentials.getHost()) &&
                    entry.getKey().getPort() == credentials.getPort()) {
                if (!entry.getValue().contains(keyword)) {
                    entry.getValue().add(keyword);
                }
                return;
            }
        });
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add(keyword);
        this.cache.put(credentials, keywords);
    }

    public void removeNeighbour(NodeCredentials sender) {
        int i = 0;
        int index = -1;
        for (NodeCredentials credentials : routingTable) {
            if (sender.getHost().equals(credentials.getHost()) &&
                    sender.getPort() == credentials.getPort()) {
                index = i;
                break;
            }
            i++;
        }
        if (index >= 0) {
            routingTable.remove(index);
        }
        updateRoutingTable();
    }

    public void updateRoutingTable() {
        Util.println("___________Routing Table_______________");
        routingTable.forEach(node -> {
            Util.print(node.getHost() + " : " + node.getPort());
        });
        Util.print("_______________________________________");
    }

    public void addToActiveSearch(int id, SearchRequestDTO dto) {
        activeSearchDetails.put(id, dto);
    }

    public void removeFromActiveSearch(int id) {
        activeSearchDetails.remove(id);
    }

    public boolean isActiveSearch(int id) {
        return activeSearchDetails.keySet().contains(id);
    }
}
