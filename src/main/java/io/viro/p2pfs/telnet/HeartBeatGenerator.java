package io.viro.p2pfs.telnet;

import io.viro.p2pfs.Util;
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
    private List<NodeCredentials> removeRoutineList;
    private List<NodeCredentials> removeCachingList;
    private List<NodeCredentials> removeList;
    int heartBeatCount = 0;

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatGenerator.class);

    public HeartBeatGenerator(P2PFSClient client) {
        this.client = client;
        init();
    }

    public void run() {

        logger.info(" Heartbeat Generator started on " +
                this.client.getNode().getCredentials().getHost() + " Sending Heartbeats...");

        while (this.client.isRegistered) {
            try {
                Thread.sleep(1000 * 30);
            } catch (InterruptedException e) {
                logger.info(e.getMessage());
            }
            //HeartBeatings for routine
            for (NodeCredentials nodeCredentials : this.client.getNode().getRoutingTable()) {
                logger.info("Node " + this.client.getNode().getCredentials().getHost() + " sent heartbeat.");
                heartBeatCount++;

                Util.println("--------------------------performance_Sent_HeartBeats--------------------------");
                Util.println("Sent a HeartBeat, total count is : " + heartBeatCount);
                Util.println("-------------------------------------------------------------------------------");

                if (!this.client.getHeartBeatList().contains(nodeCredentials)) {
                    this.client.addHeartbeatNode(nodeCredentials);
                    this.client.nodeAlive(new HeartbeatSent(this.client.getNode().getCredentials(), nodeCredentials));
                    logger.info("Node " + nodeCredentials.getHost() + " sent heartbeat routine node.");
                } else {
                    //ungracefully departure
                    this.removeRoutineList.add(nodeCredentials);
                    logger.info("Node " + nodeCredentials.getHost() + "has ungracefully left!");
                }
            }
            for (NodeCredentials nodeCredentials : this.removeRoutineList) {
                this.client.getNode().removeNeighbour(nodeCredentials);
            }
            this.removeRoutineList = new ArrayList<>();

            //HeartBeatings for Caching
            for (NodeCredentials nodeCredentials : this.client.getNode().getCacheTable().keySet()) {
                if (!this.client.getHeartBeatList().contains(nodeCredentials)) {
                    this.client.addHeartbeatNode(nodeCredentials);
                    this.client.nodeAlive(new HeartbeatSent(this.client.getNode().getCredentials(), nodeCredentials));
                    logger.info("Node " + nodeCredentials.getHost() + " sent heartbeat to cache node.");
                } else {
                    //ungracefully departure
                    this.removeCachingList.add(nodeCredentials);
                    logger.info("Node " + nodeCredentials.getHost() + "has ungracefully left!");
                }
            }
            for (NodeCredentials nodeCredentials : this.removeCachingList) {
                this.client.getNode().removeCache(nodeCredentials);
            }
            this.removeCachingList = new ArrayList<>();

        }
    }

    public void init() {
        try {
            removeRoutineList = new ArrayList<>();
            removeCachingList = new ArrayList<>();
            new Thread(this).start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


}
