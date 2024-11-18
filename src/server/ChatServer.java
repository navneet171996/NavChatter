package server;

import utils.MessageFormat;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private Map<String, ClientDetails> clientDetailsMap = new ConcurrentHashMap<>();

    public void startListening(){
        ExecutorService threadPool = Executors.newCachedThreadPool();
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(5000));

            System.out.println("Server is listening:");

            while(serverSocketChannel.isOpen()){
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
//                PrintWriter writer = new PrintWriter(Channels.newWriter(clientSocketChannel, StandardCharsets.UTF_8));
//                clientWriters.add(writer);

                threadPool.submit(new ClientHandler(clientSocketChannel));
                System.out.println("Successfully made a connection with the client");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public MessageFormat read(SocketChannel channel){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int bytesRead = channel.read(buffer);
            if(bytesRead > 0){
                buffer.flip();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                return (MessageFormat) objectInputStream.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new MessageFormat();
    }

    public void write(SocketChannel channel, MessageFormat message){
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(message);
            objectOutputStream.flush();

            ByteBuffer buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            channel.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public class ClientHandler implements Runnable{
        private SocketChannel channel;

        public ClientHandler(SocketChannel socketChannel){
            channel = socketChannel;
        }
        @Override
        public void run() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            try {
                while(true){
                    byteBuffer.clear();
                    int bytesRead = channel.read(byteBuffer);
                    if(bytesRead == -1)
                        break;

                    byteBuffer.flip();
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(byteBuffer.array(), 0, byteBuffer.limit());
                    ObjectInputStream objectStream = new ObjectInputStream(byteStream);
                    MessageFormat message = (MessageFormat) objectStream.readObject();
                    if(message.getReceiver().equals("*****")){
                        ClientDetails clientDetails = new ClientDetails();
                        clientDetails.setUsername(message.getSender());
                        clientDetails.setSocketChannel(channel);
                        clientDetailsMap.put(message.getSender(), clientDetails);
                    }else {
                        ClientDetails clientDetails = clientDetailsMap.get(message.getSender());
                        write(clientDetails.getSocketChannel(), message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        ChatServer chatServer = new ChatServer();
        chatServer.startListening();

    }
}
