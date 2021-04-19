package io.viro.p2pfs.telnet;

import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.message.send.HeartbeatSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Commander thead.
 */
public class HeartBeatGenerator implements Runnable {
    private P2PFSClient client;
    private List<NodeCredentials> removeList;

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatGenerator.class);

    public HeartBeatGenerator(P2PFSClient client) {
        this.client = client;
        init();
    }

    public void run() {

        logger.info(" Heartbeat Generator started on " +
                this.client.getNode().getCredentials().getHost() + " Sending Heartbeats...");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
        }

        while (this.client.isRegistered) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.info(e.getMessage());
            }
            //HeartBeatings
            for (NodeCredentials nodeCredentials : this.client.getNode().getRoutingTable()) {
                logger.info("Node " + this.client.getNode().getCredentials().getHost() + " sent heartbeat.");
                if (!this.client.getHeartBeatList().contains(nodeCredentials)) {
                    this.client.getHeartBeatList().add(nodeCredentials);
                    this.client.nodeAlive(new HeartbeatSent(this.client.getNode().getCredentials(), nodeCredentials));
                    logger.info("Node " + this.client.getNode().getCredentials().getHost() + " sent heartbeat.");
                } else {
                    //ungracefully departure
                    removeList.add(nodeCredentials);
                }
            }
            for (NodeCredentials nodeCredentials : this.removeList) {
                this.client.getNode().removeNeighbour(nodeCredentials);
            }

//            logger.info("Node " + this.client.getNode().getCredentials().getHost() + "has gracefully left!");
        }
    }

    public void init() {
        try {
            removeList = new ArrayList<>();
            new Thread(this).start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


}
