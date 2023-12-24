package tlecuyer.RSA;

import javax.swing.*;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

//This GUI is meant to show what might go on behind the scenes regarding RSA encryption
public class GUI extends JFrame {
	mongoHelper mongoHelper = new mongoHelper();
	RSAHelper RSA = new RSAHelper();
	
	public GUI() {
		//Disabled logging from mongoDB
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger mongLogger = loggerContext.getLogger("org.mongodb.driver");
        mongLogger.setLevel(Level.OFF);
		//All default settings for the GUI
        setTitle("Chat and Encryption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        //Creation of the main panel and chat panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));
        chatPanel.setPreferredSize(new Dimension(600, 150));
        JTextArea chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scrolling
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        //Creation of the encryption panel which will show encryption steps
        JPanel encryptionPanel = new JPanel(new BorderLayout());
        encryptionPanel.setBorder(BorderFactory.createTitledBorder("Encryption"));
        encryptionPanel.setPreferredSize(new Dimension(600, 150));
        JTextArea encryptionTextArea = new JTextArea();
        encryptionTextArea.setEditable(false);
        JScrollPane encryptionScrollPane = new JScrollPane(encryptionTextArea);
        encryptionScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        encryptionScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scrolling
        encryptionPanel.add(encryptionScrollPane, BorderLayout.CENTER);

        //Adds a new input panel to enter text in the input field
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Text"));
        JLabel enterTextLabel = new JLabel("Text:");
        JTextField inputField = new JTextField();

        //Action listener for every input given to the text box
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String inputText = inputField.getText().trim();
                if (!inputText.isEmpty()) {
                    chatTextArea.append("Alice to Bob: " + inputText + "\n");
                    inputField.setText("");
                    String recipientPublicKeyS = (mongoHelper.findKey("publicInformation", "keys", "public key", "Bob"));
                    //Gets recipient public key
                    BigInteger recipientPublicKey = new BigInteger(recipientPublicKeyS);
                    //Encrypts message using public key
                    BigInteger encryptedMessage = RSA.sendToEncryption(inputText, recipientPublicKey);
                    encryptionTextArea.append("Alice fetching Bob's public key: " + recipientPublicKey);
                    encryptionTextArea.append("\nEncrypted message with public key: " + encryptedMessage);
                    //The recipient gets their private key
                    String recipientPrivateKeyS = (mongoHelper.findKey("privateInformation", "keys", "private key", "Bob"));
                    BigInteger recipientPrivateKey = new BigInteger(recipientPrivateKeyS);
                    encryptionTextArea.append("\nBob Fetches his private key: " + recipientPrivateKey);
                    String decryptedMessage = RSA.recieveFromEncryption(encryptedMessage, recipientPrivateKey, recipientPublicKey);
                    encryptionTextArea.append("\nBob decrypted the message using his private key: " + decryptedMessage + "\n\n");
                }
            }
        });

        inputPanel.add(enterTextLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);

        mainPanel.add(chatPanel, BorderLayout.NORTH);
        mainPanel.add(encryptionPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public static void main(String[] args) {
    	GUI gui = new GUI();
        gui.setVisible(true);
    }
}