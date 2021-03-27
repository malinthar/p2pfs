package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
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
     * Returns a sort of command. Look up the commands in the helping code.(chanuka)
     *
     * @param message
     * @return
     */
    private static final Logger logger = LoggerFactory.getLogger(P2PFSMessageParser.class);

    public Response parseMessage(String message) {
        StringTokenizer tokenizer = new StringTokenizer(message, " ");
        String length = tokenizer.nextToken();
        logger.info("Message length :", length);
        String command = tokenizer.nextToken();

        if (command.equals(Constant.REGOK)) {
            int numOfNodes = Integer.parseInt(tokenizer.nextToken());
            if (Constant.REG_ERROR_CODES.contains(numOfNodes)) {
                Response response = new RegisterResponse(numOfNodes);
            } else {
                String ip;
                int port;
                List<NodeCredentials> neighboringNodes = new ArrayList<>();
                for (int i = 0; i < numOfNodes; i++) {
                    ip = tokenizer.nextToken();
                    port = Integer.parseInt(tokenizer.nextToken());
                    neighboringNodes.add(new NodeCredentials(ip, port));
                    logger.info(ip, port);
                }
                Response response = new RegisterResponse(neighboringNodes);
                return response;
            }
        }
        return null;
    }
}
