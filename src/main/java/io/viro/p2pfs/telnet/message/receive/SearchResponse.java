package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import java.util.List;

/**
 * Search Response
 */

public class SearchResponse {
    private int searchId;
    private NodeCredentials credential;
    private int numResults;
    //    private int hops;
    private List<String> results;

    public SearchResponse(int searchId, NodeCredentials credential, List<String> results) {
        this.searchId = searchId;
        this.credential = credential;
        this.results = results;
        this.numResults = results.size();
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

    public String getMessage() {
        String message = Constant.SEARCHOK;
        message += " " + searchId + " " + this.numResults + " " + this.credential.getHost() + " " +
                this.credential.getPort();
//                + " " + this.getHops();
        for (String result : results) {
            message += " " + result;
        }
        return message;
    }
}
