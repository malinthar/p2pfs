package io.viro.p2pfs;

import io.viro.p2pfs.telnet.TelnetClient;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            Node node = new Node(nodeUserName, nodeIp, nodePort);
            TelnetClient client = new TelnetClient(node, bootstrapServerIp, bootstrapServerPort);
            client.registerNode();
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }

        } else {
            logger.error("The number of inputs are incorrect");
        }
    }
}
