package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.message.receive.HeartbeatRequestReceived;
import io.viro.p2pfs.telnet.message.receive.HeartbeatResponse;
import io.viro.p2pfs.telnet.message.receive.JoinRequestReceived;
import io.viro.p2pfs.telnet.message.receive.JoinResponseReceived;
import io.viro.p2pfs.telnet.message.receive.LeaveGracefullyRequestReceived;
import io.viro.p2pfs.telnet.message.receive.LeaveGracefullyResponse;
import io.viro.p2pfs.telnet.message.receive.ReceivedMessage;
import io.viro.p2pfs.telnet.message.receive.RegisterResponse;
import io.viro.p2pfs.telnet.message.receive.SearchRequestReceived;
import io.viro.p2pfs.telnet.message.receive.SearchResponseReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Parse messages.
 */
public class P2PFSMessageParser {

    /**
     * Returns a sort of command. Look up the commands in the helping code.
     *
     * @param message
     * @return
     */
    private static final Logger logger = LoggerFactory.getLogger(P2PFSMessageParser.class);

    public ReceivedMessage parseMessage(String message) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(message, " ");
            String length = tokenizer.nextToken();
            if (Integer.parseInt(length) <= 0) {
                logger.info("Message length is invalid");
                return null;
            }
            String command = tokenizer.nextToken();
            if (command.equals(Constant.REGOK)) {
                int numOfNodes = Integer.parseInt(tokenizer.nextToken());
                if (Constant.getRegErrorCodes().contains(numOfNodes)) {
                    ReceivedMessage response = new RegisterResponse(numOfNodes);
                    return response;
                } else {
                    String ip;
                    int port;
                    List<NodeCredentials> neighboringNodes = new ArrayList<>();
                    for (int i = 0; i < numOfNodes; i++) {
                        ip = tokenizer.nextToken();
                        port = Integer.parseInt(tokenizer.nextToken());
                        neighboringNodes.add(new NodeCredentials(ip, port));
                    }
                    ReceivedMessage response = new RegisterResponse(neighboringNodes);
                    return response;
                }
            }
            if (command.equals(Constant.JOINOK)) {
                String ip = tokenizer.nextToken();
                int port = Integer.parseInt(tokenizer.nextToken());
                NodeCredentials sender = new NodeCredentials(ip, port, null);
                int code = Integer.parseInt(tokenizer.nextToken());
                return new JoinResponseReceived(sender, code);
            }
            if (command.equals(Constant.JOIN)) {
                String ip = tokenizer.nextToken();
                int port = Integer.parseInt(tokenizer.nextToken());
                return new JoinRequestReceived(ip, port);
            }
            if (command.equals(Constant.SEARCH)) {
                int searchId = Integer.parseInt(tokenizer.nextToken());
                String host = tokenizer.nextToken();
                int port = Integer.parseInt(tokenizer.nextToken());
                String keyword = tokenizer.nextToken();
                int hops = Integer.parseInt(tokenizer.nextToken());
                NodeCredentials requestOwner = new NodeCredentials(host, port, null);
                SearchRequestReceived searchRequestReceived =
                        new SearchRequestReceived(searchId, requestOwner, keyword, hops);
                return searchRequestReceived;
            }
            if (command.equals(Constant.CHECK_NODE)) {
                return new HeartbeatRequestReceived();
            }
            if (command.equals(Constant.NODEOK)) {
                return new HeartbeatResponse();
            }
            if (command.equals(Constant.LEAVE)) {
                String ip = tokenizer.nextToken();
                int port = Integer.parseInt(tokenizer.nextToken());
                NodeCredentials sender = new NodeCredentials(ip, port, null);
                return new LeaveGracefullyRequestReceived(sender);
            }
            if (command.equals(Constant.LEAVEOK)) {
                int code = Integer.parseInt(tokenizer.nextToken());
                return new LeaveGracefullyResponse(code);
            }
            if (command.equals(Constant.SEARCHOK)) {
                int searchId = Integer.parseInt(tokenizer.nextToken());
                String keyword = tokenizer.nextToken();
                int numResults = Integer.parseInt(tokenizer.nextToken());
                String ip = tokenizer.nextToken();
                int port = Integer.parseInt(tokenizer.nextToken());
                int hops = Integer.parseInt(tokenizer.nextToken());

                List<String> results = new ArrayList<>();
                NodeCredentials sentNodeCredentials = new NodeCredentials(ip, port, null);

                if (numResults == 0 || numResults == Constant.SEARCH_ERROR) {
                    return new SearchResponseReceived(searchId, keyword, sentNodeCredentials, hops, results);
                }

                for (int i = 0; i < numResults; i++) {
                    results.add(tokenizer.nextToken());
                }

                return new SearchResponseReceived(searchId, keyword, sentNodeCredentials, hops, results);
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
