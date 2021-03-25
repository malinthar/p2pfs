package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Parse messages.
 */
public class P2PFSMessageParser {

    /**
     * Returns a sort of command. Look up the commands in the helping code.(chanuka)
     *
     * @param message
     * @return
     */
    public Response parseMessage(String message) {
        StringTokenizer st = new StringTokenizer(message, " ");

        String length = st.nextToken();
        String command = st.nextToken();
        int numOfNodes = Integer.parseInt(st.nextToken());
        String ip;
        int port;
        List<NodeCredentials> neighboringNodes = new ArrayList<>();
        for (int i = 0; i < numOfNodes; i++) {
            ip = st.nextToken();
            port = Integer.parseInt(st.nextToken());
            neighboringNodes.add(new NodeCredentials(ip, port));
        }
        Response response = new RegisterResponse(neighboringNodes);
        return response;
    }
}
