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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {
    private BufferedReader reader;
    private PrintWriter writer;
    private JTextArea incomingMessage;
    private JTextField outgoingMessage;


    public void startClient(){
        setupConnection();

        JScrollPane scrollPane = createScrollableTextArea();
        outgoingMessage = new JTextField(20);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(a -> sendMessage());

        JPanel mainPanel = new JPanel();
        mainPanel.add(scrollPane);
        mainPanel.add(outgoingMessage);
        mainPanel.add(sendButton);

        JFrame mainFrame = new JFrame("NavChatter");
        mainFrame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        mainFrame.setSize(500, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        ExecutorService incomingMessageExecutors = Executors.newSingleThreadExecutor();
        incomingMessageExecutors.execute(new IncomingMessagesReader());
    }
    private void setupConnection(){
        try {
            SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 5000);
            SocketChannel channel = SocketChannel.open(socketAddress);

            reader = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8));
            writer = new PrintWriter(Channels.newWriter(channel, StandardCharsets.UTF_8));
            System.out.println("Connection Established with Server at time: " + System.currentTimeMillis());
        } catch (IOException e){
            System.out.println("Error in connecting to the server");
        }
    }

    private void sendMessage(){
        writer.println(outgoingMessage.getText());
        writer.flush();
        outgoingMessage.setText("");
        outgoingMessage.requestFocus();
    }

    private JScrollPane createScrollableTextArea(){
        incomingMessage = new JTextArea(15, 30);
        incomingMessage.setLineWrap(true);
        incomingMessage.setWrapStyleWord(true);
        incomingMessage.setEditable(false);

        JScrollPane scroll = new JScrollPane(incomingMessage);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        return scroll;
    }

    public class IncomingMessagesReader implements Runnable{

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null){
                    System.out.println(message);
                    incomingMessage.append(message + "\n");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        new ChatClient().startClient();
    }
}
