package io.viro.p2pfs;

import io.viro.p2pfs.telnet.TelnetClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        TelnetClient client = TelnetClient.getInstance();
        client.init();
        client.registerNode();
    }
}
