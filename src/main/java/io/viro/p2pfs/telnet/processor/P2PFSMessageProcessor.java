package io.viro.p2pfs.telnet.processor;

import io.viro.p2pfs.Constant;
import io.viro.p2pfs.Util;
import io.viro.p2pfs.downloadapi.FileDownloader;
import io.viro.p2pfs.telnet.P2PFSClient;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import io.viro.p2pfs.telnet.dto.SearchRequestDTO;
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
import io.viro.p2pfs.telnet.message.receive.UnRegisterResponse;
import io.viro.p2pfs.telnet.message.send.HeartbeatResponseSent;
import io.viro.p2pfs.telnet.message.send.JoinRequestSent;
import io.viro.p2pfs.telnet.message.send.JoinResponseSent;
import io.viro.p2pfs.telnet.message.send.LeaveGracefullyResponseSent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


/**
 * Process messages.
 */
public class P2PFSMessageProcessor {

    private P2PFSMessageParser parser;
    private P2PFSClient client;
    private static final Logger logger = LoggerFactory.getLogger(P2PFSMessageProcessor.class);
    private Random random;

    public P2PFSMessageProcessor(P2PFSClient client) {
        this.parser = new P2PFSMessageParser();
        this.client = client;
        this.random = new Random();
    }

    /**
     * To be completed malintha.
     *
     * @param message
     * @param sender
     */
    public void processMessage(String message, NodeCredentials sender) {
        ReceivedMessage response = parser.parseMessage(message);
        if (response instanceof RegisterResponse) {
            if (((RegisterResponse) response).getNeighboringNodes() == null) {
                int errorCode = ((RegisterResponse) response).getErrorCode();
                if (errorCode == Constant.ALREADY_REGISTERED_TO_ME) {
                    Util.println("Already registered to this host!");
                } else if (errorCode == Constant.ALREADY_REGISTERED) {
                    Util.println("Already registered to another host!");
                } else if (errorCode == Constant.BS_FULL) {
                    Util.println("Boostrap Server is full!");
                } else if (errorCode == Constant.COMMAND_ERROR) {
                    Util.println("Host error!");
                } else {
                    Util.println("Unknown error");
                }
                client.setIsRegistered(false);
            } else {
                client.setIsRegistered(true);
                List<NodeCredentials> nodesList = ((RegisterResponse) response).getNeighboringNodes();
                if (nodesList.size() == 0) {
                    logger.info("Registered successfully, BS server responded with 0 nodes!");
                } else {
                    //select two random nodes from the returned nodes and put join requests to them.
                    logger.info("Registered successfully, BS server responded with " + nodesList.size() + " nodes!");
                    for (int i = 0; i < 2; i++) {
                        if (nodesList.size() == 0) {
                            break;
                        } else {
                            int randomNumber = random.nextInt(nodesList.size());
                            NodeCredentials neighbor = nodesList.get(randomNumber);
                            nodesList.remove(randomNumber);
                            //logger.debug("Sent join request to " + neighbor.getHost());
                            client.join(new JoinRequestSent(client.getNode().getCredentials(), neighbor));
                        }
                    }
                    //todo: What can we do with the remaining nodes.
                }
            }
        } else if (response instanceof JoinResponseReceived) {
            if (((JoinResponseReceived) response).getCode() == Constant.JOIN_SUCCESS) {
                this.client.getNode().addNeighbor(((JoinResponseReceived) response).getSender());
                //logger.debug("Node " +
                // ((JoinResponseReceived) response).getSender().getHost() +
                // " is added to routing table");
            } else {
                //todo: JoinResponse is an error what to do?
                //logger.debug("Error in joining with node" + sender.getHost());
            }
        } else if (response instanceof JoinRequestReceived) {
            //logger.debug("Join request received from " + ((JoinRequestReceived) response).getSender().getHost());
            if (this.client.getNode().getNeighborCount() < 5) {
                this.client.getNode().addNeighbor(((JoinRequestReceived) response).getSender());
                //logger.debug("Node " + ((JoinRequestReceived) response).getSender().getHost() +
                // " is added to routing table");
                this.client.joinOK(new JoinResponseSent(this.client.getNode().getCredentials(),
                        ((JoinRequestReceived) response).getSender(), Constant.JOIN_SUCCESS));

            } else {
                //todo:What to do in this case. Here send the error code
                this.client.getNode().addSecondaryNeighbor(sender);
                //logger.debug("Routing table is full, sent error code");
                this.client.joinOK(new JoinResponseSent(this.client.getNode().getCredentials(),
                        sender, Constant.JOIN_ERROR));

            }
        } else if (response instanceof SearchRequestReceived) {
            //logger.debug("Search request received from " + sender.getHost());

            SearchRequestReceived searchRequestReceived = (SearchRequestReceived) response;
            SearchRequestDTO searchRequestDTO = new SearchRequestDTO(searchRequestReceived);
            this.client.triggerSearch(searchRequestDTO);

            this.client.getNode().incReceivedSearchRequestCount();
            Util.println("--------------------------performance_Received_Count--------------------------");
            Util.println("Received a search Request, total count is : " +
                    this.client.getNode().getReceivedSearchRequestCount());
            Util.println("-------------------------------------------------------------------------------");

        } else if (response instanceof SearchResponseReceived) {
            //logger.debug("Search response received from " + sender.getHost());
            SearchResponseReceived searchResultResponse = (SearchResponseReceived) response;
            if (searchResultResponse.getResults().size() != 0) {
                if (this.client.getNode().isActiveSearch(searchResultResponse.getSearchId())) {

                    this.client.getNode().removeFromActiveSearch(searchResultResponse.getSearchId());

                    LocalDateTime now = LocalDateTime.now();
                    double latencyInMillis =
                            this.client.getNode().calculateLatency(searchResultResponse.getSearchId(), now);
                    this.client.getNode().removeFromTimeStampMap(searchResultResponse.getSearchId());

                    Util.println("_______________________________________________________________");
                    Util.printWUS("Hits! for \"" + ((SearchResponseReceived) response).getKeyword() + "\" from " +
                            ((SearchResponseReceived) response).getSender().getHost() +
                            " : " + ((SearchResponseReceived) response).getSender().getPort());
                    Util.print("Search ID: " + searchResultResponse.getSearchId());
                    Util.print("Number of hops: " + searchResultResponse.getHops());
                    Util.print("Number of results: " + searchResultResponse.getNumResults());
                    Util.print("Files: ");
                    Util.print("Latency (milli seconds) : " + latencyInMillis);
                    searchResultResponse.getResults().forEach(file -> {
                        Util.printWUS(file);
                    });
                    this.client.getNode()
                            .addToCache(searchResultResponse.getSender(), searchResultResponse.getKeyword());
                    this.client.getNode().removeFromActiveSearch(searchResultResponse.getSearchId());
                    searchResultResponse.getResults().forEach(file -> {
                        FileDownloader.getFileFromNetwork(file, searchResultResponse.getSender(),
                                this.client.getNode().getCredentials().getHost());
                    });

                } else {
                    return;
                }

            }

            //todo: Complete
        } else if (response instanceof HeartbeatRequestReceived) {
            HeartbeatRequestReceived res = (HeartbeatRequestReceived) response;
            //logger.debug("Heartbeat request received from " + res.getSender().getHost());
            this.client.nodeOK(new HeartbeatResponseSent(
                    this.client.getNode().getCredentials(),
                    res.getSender(),
                    Constant.NODE_ALIVE));
        } else if (response instanceof HeartbeatResponse) {
            HeartbeatResponse res = (HeartbeatResponse) response;
            logger.info("Heartbeat Response received from " + res.getSender().getHost());
            if (res.getCode() == Constant.NODE_ALIVE) {
                this.client.removeNodeFromHeartBeatList(res.getSender());
            }
        } else if (response instanceof LeaveGracefullyRequestReceived) {
            LeaveGracefullyRequestReceived res = (LeaveGracefullyRequestReceived) response;
            //logger.debug("Leave Gracefully request received from " + res.getSender().getHost());
            this.client.getNode().removeNeighbour(res.getSender());
            this.client.leaveOK(new LeaveGracefullyResponseSent(
                    this.client.getNode().getCredentials(), res.getSender(), Constant.LEAVE_SUCCESS));
        } else if (response instanceof LeaveGracefullyResponse) {
            LeaveGracefullyResponse res = (LeaveGracefullyResponse) response;
//            Util.print("Leave Gracefully response received from " + res.getSender().getHost());
            if (res.getCode() == Constant.LEAVE_SUCCESS) {
                Util.println("Good Bye!!!");
                this.client.unRegisterNode();
                Util.print("Good Bye!!!");
            } else {
                Util.println("Error leaving!");
            }
        } else if (response instanceof UnRegisterResponse) {
            UnRegisterResponse res = (UnRegisterResponse) response;
            switch (res.errorCode) {
                case Constant.UNREG_SUCCESS:
                    Util.print("Successfully Unregistered in the Bootstrap!");
                    this.client.setIsRegistered(false);
                    break;
                case Constant.UNREG_ERROR:
                    Util.print("Error while Unregistering in the Bootstrap!");
                    break;
                default:
                    logger.info("no error handler");
            }
        } else {
            logger.debug("no handler");
        }
    }
}
