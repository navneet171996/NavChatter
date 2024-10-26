package client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    private PrintWriter writer;
    private JTextField outgoingMessage;

    public void startClient(){
        setupConnection();

        outgoingMessage = new JTextField(20);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(a -> sendMessage());

        JPanel mainPanel = new JPanel();
        mainPanel.add(outgoingMessage);
        mainPanel.add(sendButton);

        JFrame mainFrame = new JFrame("NavChatter");
        mainFrame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        mainFrame.setSize(500, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
    private void setupConnection(){
        try {
            SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 5000);
            SocketChannel channel = SocketChannel.open(socketAddress);

            writer = new PrintWriter(Channels.newWriter(channel, StandardCharsets.UTF_8));
            System.out.println("Connection Established with Server at time: " + System.currentTimeMillis());
        } catch (IOException e){
            System.out.println("Error in connecting to the server");
        }
    }
    public String read(){
//        reader = Channels.newReader(socketChannel, StandardCharsets.UTF_8);
//        bufferedReader = new BufferedReader(reader);
//        try {
//            String message = bufferedReader.readLine();
//            return message;
//        } catch (IOException e) {
//            return "ERROR reading message";
//        }
        return "";
    }

    public void write(String message){
//        writer = Channels.newWriter(socketChannel, StandardCharsets.UTF_8);
//        printWriter = new PrintWriter(writer);
//        printWriter.println(message);
    }

    private void sendMessage(){
        writer.println(outgoingMessage.getText());
        writer.flush();
        outgoingMessage.setText("");
        outgoingMessage.requestFocus();
    }

    public static void main(String[] args){
        new ChatClient().startClient();
    }
}
