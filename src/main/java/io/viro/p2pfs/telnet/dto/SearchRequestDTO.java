package io.viro.p2pfs.telnet.dto;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import java.util.List;

/**
 * Search request DTO class.
 */

public class SearchRequestDTO {
    int id;
    NodeCredentials requestNode;
    List<String> keywords;

    public SearchRequestDTO(int id, NodeCredentials requestNode, List<String> keywords) {
        super();
        this.id = id;
        this.requestNode = requestNode;
        this.keywords = keywords;
    }

    public int getId() {
        return id;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public NodeCredentials getRequestNodeCredentials() {
        return requestNode;
    }
}
