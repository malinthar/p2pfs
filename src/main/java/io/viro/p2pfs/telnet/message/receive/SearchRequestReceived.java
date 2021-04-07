package io.viro.p2pfs.telnet.message.receive;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 *Received Search request.
 */

public class SearchRequestReceived extends ReceivedMessage {
    int searchId;
    NodeCredentials requestOriginator;
    String keyword;

    public SearchRequestReceived(int searchId, NodeCredentials requestOwner, String keyword) {
        super();
        this.searchId = searchId;
        this.requestOriginator = requestOwner;
        this.keyword = keyword;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public NodeCredentials getRequestOriginator() {
        return requestOriginator;
    }

    public void setRequestOriginator(NodeCredentials requestOriginator) {
        this.requestOriginator = requestOriginator;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
