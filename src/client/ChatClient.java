package client;

import utils.MessageFormat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {
    private BufferedReader reader;
//    private PrintWriter writer;
    private JTextArea incomingMessage;
    private JTextField outgoingMessage;
    private SocketChannel channel;
    private String username;

    public ChatClient() {
    }

    public ChatClient(String username) {
        this.username = username;
    }

    public void startClient(){
        setupConnection();
        sendFirstMessage();

        JScrollPane scrollPane = createScrollableTextArea();
        outgoingMessage = new JTextField(20);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(a -> prepareMessageForSending());

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
            channel = SocketChannel.open(socketAddress);

            reader = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8));
//            writer = new PrintWriter(Channels.newWriter(channel, StandardCharsets.UTF_8));
            System.out.println("Connection Established with Server at time: " + System.currentTimeMillis());
        } catch (IOException e){
            System.out.println("Error in connecting to the server");
        }
    }

    private void prepareMessageForSending(){
        MessageFormat messageFormat = new MessageFormat();
        messageFormat.setMessage(outgoingMessage.getText());
    }

    private void sendFirstMessage(){
        MessageFormat message = new MessageFormat();
        message.setSender(username);
        message.setReceiver("*****");

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(message);
            objectOutputStream.flush();

            byte[] messageInBytes = byteArrayOutputStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(messageInBytes.length);
            buffer.put(messageInBytes);
            buffer.flip();

            while (buffer.hasRemaining()){
                channel.write(buffer);
            }

        }catch (IOException e){
            System.out.println("Error");
        }
    }

    private void sendMessage(MessageFormat message){
//        writer.println(outgoingMessage.getText());
//        writer.flush();

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(message);
            objectOutputStream.flush();

            byte[] messageInBytes = byteArrayOutputStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(messageInBytes.length);
            buffer.put(messageInBytes);
            buffer.flip();

            while (buffer.hasRemaining()){
                channel.write(buffer);
            }

            outgoingMessage.setText("");
            outgoingMessage.requestFocus();
        }catch (IOException e){
            System.out.println("Error");
        }

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
