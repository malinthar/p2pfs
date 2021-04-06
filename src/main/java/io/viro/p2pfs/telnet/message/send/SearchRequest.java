package io.viro.p2pfs.telnet.message.send;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;

/**
 * Search Request
 */

public class SearchRequest extends Message {

    int id;
    NodeCredentials requestNode;
    String keyword;

    public SearchRequest(SearchRequestDTO searchRequestDTO, NodeCredentials receiver) {
        super(receiver);
        this.id = searchRequestDTO.getId();
        this.requestNode = searchRequestDTO.getRequestNodeCredentials();
        this.keyword = searchRequestDTO.getKeyword();
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeywords(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getMessage() {
        String message = Constant.SEARCH;
        message += " " + this.id + " " + this.requestNode.getHost() + " " + this.requestNode.getPort() + " " +
                this.keyword;
//                + " " + this.getHops();
        return message;
    }
}
