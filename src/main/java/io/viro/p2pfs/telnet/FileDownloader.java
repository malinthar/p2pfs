package io.viro.p2pfs.telnet;

import io.viro.p2pfs.Util;
import io.viro.p2pfs.telnet.credentials.NodeCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * File Downloader.
 */
public class FileDownloader {
    private static final Logger logger = LoggerFactory.getLogger(P2PFSClient.class);
    private static final String GET = "GET";

    public static void getFileFromNetwork(String fileHandle, NodeCredentials remote) {
        try {
            URL url = new URL(remote.getHost() + ":" + remote.getPort() + "?fileHandle=" + fileHandle);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GET);
            int code = connection.getResponseCode();
            Util.println("Remote node " + remote.getHost() + ":" +
                    remote.getPort() + "responded for file download with code " + code);
            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(new
                        InputStreamReader(connection.getInputStream()));

                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    buffer.append(line);
                }
                input.close();

                MessageDigest dg = null;
                dg = MessageDigest.getInstance("SHA-256");
                byte[] hash = dg.digest(buffer.toString().getBytes(StandardCharsets.UTF_8));
                BigInteger hashN = new BigInteger(1, hash);
                String receivedMsg = hashN.toString(16);
                Util.print("Received hash " + receivedMsg);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
