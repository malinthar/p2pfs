package io.viro.p2pfs.telnet.dto;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Search request DTO class.
 */

public class SearchRequestDTO {
    int id;
    NodeCredentials requestNode;
    String keyword;
    int hopCount = 0;

    public SearchRequestDTO(int id, NodeCredentials requestNode, String keyword, int hopCount) {
        super();
        this.id = id;
        this.requestNode = requestNode;
        this.keyword = keyword;
        this.hopCount = hopCount;
    }

    public int getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public NodeCredentials getRequestNodeCredentials() {
        return requestNode;
    }

    public int getHopCount() {
        return this.hopCount;
    }

    public void setHopCount(int count) {
        this.hopCount = count;
    }

    public void incHopCountByOne() {
        this.hopCount++;
    }
}
