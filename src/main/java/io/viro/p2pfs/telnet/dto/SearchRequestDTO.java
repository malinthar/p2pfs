package io.viro.p2pfs.telnet.dto;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;

/**
 * Search request DTO class.
 */

public class SearchRequestDTO {
    int id;
    NodeCredentials requestNode;
    String keyword;

    public SearchRequestDTO(int id, NodeCredentials requestNode, String keywords) {
        super();
        this.id = id;
        this.requestNode = requestNode;
        this.keyword = keyword;
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
}
