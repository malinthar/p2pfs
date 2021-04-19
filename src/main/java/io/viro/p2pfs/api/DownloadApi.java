package io.viro.p2pfs.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.viro.p2pfs.Util;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class DownloadApi implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String queryParam = httpExchange.getRequestURI().getQuery();
        Util.println("parameter :" + queryParam);
        String response = null;
        try {
            response = this.createFile();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    public void startListening(String host, int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
            server.createContext("/download", this);
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createFile() throws NoSuchAlgorithmException {
        int max = 10;
        int min = 2;
        Random rand = new Random();
        int randomNum = rand.nextInt(max - min + 1) + min;
        char[] mbValue = new char[randomNum * 1024 * 1024];
        String file = String.valueOf(mbValue);

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] hash = messageDigest.digest(file.getBytes(StandardCharsets.UTF_8));
        BigInteger fileString = new BigInteger(1, hash);
        String hashString = fileString.toString(16);

        Util.println("Hash for the file is :" + hashString);
        Util.println("Size of the file is :" + mbValue.length);

        return file;
    }
}
