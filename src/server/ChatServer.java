package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    List<PrintWriter> clientWriters = new ArrayList<>();

    public void startListening(){
        ExecutorService threadPool = Executors.newCachedThreadPool();
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(5000));

            System.out.println("Server is listening:");

            while(serverSocketChannel.isOpen()){
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                PrintWriter writer = new PrintWriter(Channels.newWriter(clientSocketChannel, StandardCharsets.UTF_8));
                clientWriters.add(writer);
                threadPool.submit(new ClientHandler(clientSocketChannel));
                System.out.println("Successfully made a connection with the client");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void read(SocketChannel clientSocket){
        String message;
        BufferedReader reader = new BufferedReader(Channels.newReader(clientSocket, StandardCharsets.UTF_8));
        try {
            while((message = reader.readLine()) != null){
                System.out.println(message);
                write();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void write(){
        for (PrintWriter writer:clientWriters){
            writer.println("Message received");
            writer.flush();
        }
    }

    public static void main(String[] args){
        ChatServer chatServer = new ChatServer();
        chatServer.startListening();

    }

    public class ClientHandler implements Runnable{
        BufferedReader reader;
        SocketChannel channel;

        public ClientHandler(SocketChannel socketChannel){
            channel = socketChannel;
            reader = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8));
        }
        @Override
        public void run() {
            String message;
            try {
                while((message = reader.readLine()) != null){
                    System.out.println(message);
                    write();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
