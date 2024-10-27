package client;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ClientUI {

    private JTextField username;
    private JPasswordField password;
    private JLabel prompt;

    public void start(){
        username = new JTextField(20);
        password = new JPasswordField(20);
        prompt = new JLabel("", SwingConstants.CENTER);
        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(a -> loginButtonActionListener());

        JPanel mainPanel = new JPanel();
        mainPanel.add(usernameLabel);
        mainPanel.add(username);
        mainPanel.add(passwordLabel);
        mainPanel.add(password);
        mainPanel.add(loginButton);
        mainPanel.add(prompt);

        JFrame mainFrame = new JFrame("NavChatter");
        mainFrame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        mainFrame.setSize(400, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private boolean login(String username, String password){
        boolean isValid = false;
        String dbUrl = "jdbc:mysql://localhost:3306/navchatter";
        String dbUser = "root";
        String dbPass = "Priyam123";

        try(Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            isValid = resultSet.next();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Cannot connect to DB");
        }

        return isValid;
    }

    private void loginButtonActionListener(){
        String typedUsername = username.getText();
        String typedPassword = new String(password.getPassword());

        if(login(typedUsername, typedPassword)){
            prompt.setText("Login Successful");
        }else {
            prompt.setText("Invalid Credentials");
        }
    }

    public static void main(String[] args) {
        ClientUI clientUI = new ClientUI();
        clientUI.start();
    }

}
