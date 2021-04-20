package io.viro.p2pfs;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node.
 */
public class Node {
    private NodeCredentials credentials;
    private List<String> files;
    private List<NodeCredentials> routingTable; //routing table
    private List<NodeCredentials> secondaryNeighbors; //Limit=5,
    private Map<NodeCredentials, List<String>> cache;

    private int receivedSearchRequestCount = 0;
    private int forwardedSearchRequestCount = 0;
    private int answeredRequestCount = 0;

    //for performance
    HashMap<Integer, LocalDateTime> searchInitTimeStamps = new HashMap<Integer, LocalDateTime>();

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

    public int getReceivedSearchRequestCount() {
        return receivedSearchRequestCount;
    }

    public void setReceivedSearchRequestCount(int receivedSearchRequestCount) {
        this.receivedSearchRequestCount = receivedSearchRequestCount;
    }

    public void incReceivedSearchRequestCount() {
        this.receivedSearchRequestCount++;
    }

    public int getForwardedSearchRequestCount() {
        return forwardedSearchRequestCount;
    }

    public void setForwardedSearchRequestCount(int forwardedSearchRequestCount) {
        this.forwardedSearchRequestCount = forwardedSearchRequestCount;
    }

    public void incForwardedSearchRequestCount() {
        this.forwardedSearchRequestCount++;
    }

    public int getAnsweredRequestCount() {
        return answeredRequestCount;
    }

    public void setAnsweredRequestCount(int answeredRequestCount) {
        this.answeredRequestCount = answeredRequestCount;
    }

    public void incAnsweredRequestCount() {
        this.answeredRequestCount++;
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

    public synchronized void addNeighbor(NodeCredentials neighbor) {
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

    public void removeFromTimeStampMap(int searchId) {
        if (searchInitTimeStamps.containsKey(searchId)) {
            searchInitTimeStamps.remove(searchId);
        }
    }

    public double calculateLatency(int searchId, LocalDateTime receivedTime) {
        if (!searchInitTimeStamps.containsKey(searchId)) {
            return 0;
        }

        LocalDateTime initTime = searchInitTimeStamps.get(searchId);
        int diffNano = (int) ChronoUnit.NANOS.between(initTime, receivedTime);
        double diffMillis = (diffNano / 1000000.0);
        return diffMillis;
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
        LocalDateTime now = LocalDateTime.now();
        searchInitTimeStamps.put(searchRequestDTO.getId(), now);
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

    public synchronized void removeNeighbour(NodeCredentials sender) {
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

    public List<NodeCredentials> getTwoThirdsOfRT() {
        if (this.routingTable.size() > 2) {
            List<NodeCredentials> randomNodes = new ArrayList<>();
            int upperBound = (int) (2.0 * routingTable.size() / 3);
            for (int i = 0; i < upperBound; i++) {
                randomNodes.add(routingTable.get(i));
            }
            return randomNodes;
        } else {
            return this.routingTable;
        }
    }
}
