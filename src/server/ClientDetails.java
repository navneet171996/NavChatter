package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.channels.SocketChannel;

public class ClientDetails {

    private String username;
    private SocketChannel socketChannel;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
