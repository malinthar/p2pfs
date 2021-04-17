package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import java.util.List;

/**
 * Search Response
 */

public class SearchResponseReceived extends ReceivedMessage {
    private int searchId;
    private NodeCredentials credential;
    private int numResults;
    private int hops;
    private List<String> results;

    public SearchResponseReceived(int searchId, NodeCredentials credential, int hops, List<String> results) {

        this.searchId = searchId;
        this.credential = credential;
        this.results = results;
        this.numResults = results.size();
        this.hops = hops;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public NodeCredentials getCredential() {
        return credential;
    }

    public void setCredential(NodeCredentials credential) {
        this.credential = credential;
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

    public String getMessage() {
        String message = Constant.SEARCHOK;
        message += " " + searchId + " " + this.numResults + " " + this.credential.getHost() + " " +
                this.credential.getPort() + " " + this.hops;
        StringBuffer buffer = new StringBuffer(message);
        for (String result : results) {
            buffer.append(" ").append(result);
        }
        return buffer.toString();
    }
}
