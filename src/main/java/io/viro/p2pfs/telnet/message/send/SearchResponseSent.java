package io.viro.p2pfs.telnet.message.send;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import java.util.List;

/**
 * Response for a SearchRequest.
 */
public class SearchResponseSent extends Message {

    NodeCredentials sender;
    private int searchId;
    private String keyword;
    private int numResults;
    private int hops;
    private List<String> results;

    public SearchResponseSent(int searchId, String keyword, NodeCredentials receiver,
                              NodeCredentials sender, int hops, List<String> results) {
        super(receiver);
        this.searchId = searchId;
        this.keyword = keyword;
        this.results = results;
        this.numResults = results.size();
        this.hops = hops;
        this.sender = sender;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(Constant.SEARCHOK);
        message.append(Constant.SEPARATOR).append(searchId).append(Constant.SEPARATOR)
                .append(keyword).append(Constant.SEPARATOR)
                .append(this.numResults).append(Constant.SEPARATOR)
                .append(this.sender.getHost()).append(Constant.SEPARATOR)
                .append(this.sender.getPort()).append(Constant.SEPARATOR).append(this.hops);
        for (String result : results) {
            message.append(Constant.SEPARATOR).append(result);
        }
        return message.toString();
    }
}
