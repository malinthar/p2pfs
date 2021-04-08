package io.viro.p2pfs;

import io.viro.p2pfs.telnet.P2PFSClient;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Boostrap class for starting a Peer node in the p2p system.
 */
public class BootstrapNode {
    private static final Logger logger = LoggerFactory.getLogger(BootstrapNode.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        if (args.length == 5) {
            for (String arg : args) {
                logger.info(arg);
            }
            String nodeIp = args[0];
            int nodePort = Integer.parseInt(args[1]);
            String nodeUserName = args[2];
            String bootstrapServerIp = args[3];
            int bootstrapServerPort = Integer.parseInt(args[4]);
            NodeCredentials credentials = new NodeCredentials(nodeIp, nodePort, nodeUserName);
            NodeCredentials bootstrapServer = new NodeCredentials(bootstrapServerIp, bootstrapServerPort);
            Node node = new Node(credentials);
            P2PFSClient client = new P2PFSClient(node, bootstrapServer);
            client.registerNode();

            List<String> searchQueries =
                    Arrays.asList("Twilight", "Jack", "American_Idol", "Happy_Feet", "Twilight_saga", "Happy_Feet",
                            "Feet");
            Collections.shuffle(searchQueries);

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }

                if (!client.getIsRegistered()) {
                    continue;
                }
                //search function here
                for (int i = 0; i < searchQueries.size(); i++) {
                    String query = searchQueries.get(i);
                    client.initNewSearch(query);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        logger.info(e.getMessage());
                    }
                }
                break;
            }

        } else {
            logger.error("The number of inputs are incorrect");
        }
    }
}
