package io.viro.p2pfs.telnet.credentials;

/**
 * Node credentials.
 */
public class NodeCredentials {
    private String host;
    private int port;
    private String userName;

    public NodeCredentials(String host, int port, String userName) {
        this.host = host;
        this.port = port;
        this.userName = userName;
    }

    public NodeCredentials(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUserName() {
        return userName;
    }
}
