package io.viro.p2pfs.telnet.message.send;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;
import java.util.List;

/**
 * Search Request
 */

public class SearchRequest extends Message {

    int id;
    NodeCredentials requestNode;
    List<String> keywords;

    public SearchRequest(SearchRequestDTO searchRequestDTO, NodeCredentials receiver) {
        super(receiver);
        this.id = searchRequestDTO.getId();
        this.requestNode = searchRequestDTO.getRequestNodeCredentials();
        this.keywords = searchRequestDTO.getKeywords();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NodeCredentials getRequestNode() {
        return requestNode;
    }

    public void setRequestNode(NodeCredentials requestNode) {
        this.requestNode = requestNode;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String getMessage() {
        return "To be implement";
    }
}
