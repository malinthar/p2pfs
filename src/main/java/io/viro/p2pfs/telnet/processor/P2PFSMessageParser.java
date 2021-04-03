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
        String userName;

        switch (command) {
            case "REG":
                ip = st.nextToken();
                port = Integer.parseInt(st.nextToken());
                userName = st.nextToken();
                Credential userCredentials = new Credential(ip, port, username);
                return new RegisterRequest(userCredentials);

            case "REGOK":
                int nodeCount = Integer.parseInt(st.nextToken());
                List<Credential> nodes = new ArrayList<>();
                if (!(numOfNodes == Constant.Codes.Register.ERROR_CANNOT_REGISTER || numOfNodes == Constant.Codes.Register.ERROR_DUPLICATE_IP ||
                        numOfNodes == Constant.Codes.Register.ERROR_ALREADY_REGISTERED || numOfNodes == Constant.Codes.Register.ERROR_COMMAND)) {
                    for (int i = 0; i < numOfNodes; i++) {
                        ip = st.nextToken();
                        port = Integer.parseInt(st.nextToken());
                        nodes.add(new Credential(ip, port, null));
                    }
                }
                RegisterResponse registerResponse = new RegisterResponse(numOfNodes, nodes);
                return registerResponse;

            case "UNREG":
                ip = st.nextToken();
                port = Integer.parseInt(st.nextToken());
                userName = st.nextToken();
                Credential unregUserCredentials = new Credential(ip, port, userName);
                return new UnregisterRequest(unregUserCredentials);

            case "UNREGOK":
                int res = Integer.parseInt(st.nextToken());
                return new UnregisterResponse(res);

            case "LEAVE":
                ip = st.nextToken();
                port = Integer.parseInt(st.nextToken());
                Credential crd = new Credential(ip, port, null);
                return new LeaveRequest(crd);

            case "JOIN":
                ip = st.nextToken();
                port = Integer.parseInt(st.nextToken());
                Credential joinerCredentials = new Credential(ip, port, null);
                return new JoinRequest(joinerCredentials);

            case "JOINOK":
                int res = Integer.parseInt(st.nextToken());
                return new JoinResponse(res, senderCredential);
            case "LEAVEOK":
                int res = Integer.parseInt(st.nextToken());
                return new LeaveResponse(res);
            case "SEARCH":
                break;
            case "SEARCHOK":
                break;
            default:
                break;
        }

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
