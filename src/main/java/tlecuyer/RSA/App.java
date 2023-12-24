package tlecuyer.RSA;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Scanner;

import org.bson.Document;

public class App {
    public static void main(String[] args) {
    	//Connect to mongoDB as the test user
        String connectionString = "REMOVED";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        
        //Disabled logging from mongoDB
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger mongLogger = loggerContext.getLogger("org.mongodb.driver");
        mongLogger.setLevel(Level.OFF);
        
        //Runs an instance of the program and utilizes the mongo client
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
            	//Connect to the databases and collections
            	MongoDatabase privateInfo = mongoClient.getDatabase("privateInformation");
            	MongoDatabase publicInfo = mongoClient.getDatabase("publicInformation");
            	MongoCollection<Document> publicCollection = publicInfo.getCollection("keys");
            	MongoCollection<Document> privateCollection = privateInfo.getCollection("keys");
            	
                do {
                	//Get the users name
                	RSAHelper RSA = new RSAHelper();
                	Scanner scan = new Scanner(System.in);
                	mongoHelper mongoHelper = new mongoHelper();
                	System.out.print("Enter username: ");
                	String username = scan.next();
                	
                	//See if the user exists
                	Document search = new Document("Name", username);
                    FindIterable<Document> userInfo = publicCollection.find(search)
                            .projection(Projections.include("public key"));
                    //Creates a document that contains the public key of the user
                    Document foundUser = userInfo.first();
                    if(foundUser != null) {
                    	System.out.println("Existing user found!");
                    	System.out.println("You can send a message to:");
                    	//Prints out all the names available to send a message to except yourself
                    	DistinctIterable<String> distinctNames = publicCollection.distinct("Name", String.class);
                    	for (String name : distinctNames) {
                            if (!name.equals(username)) {
                                System.out.println(name);
                            }
                        }
                    	System.out.print("Choose a user to send a message to: ");
                    	//Choose who to send a message to
                        String recipientName = scan.next();

                        //Make sure the chosen name exists and is not the users name
                        Document recipientSearch = new Document("Name", recipientName);
                        FindIterable<Document> userFound = publicCollection.find(recipientSearch);
                        //Document containing the recipients information
                        Document recipient = userFound.first();
                        
                        //If this is a valid recipient
                        if (recipient != null && !recipientName.equals(username)) {
                            System.out.print("Message to send to " + recipientName + ": ");
                            //Stores message that is being sent
                            String messageToSend = scan.next();
                            String recipientPublicKeyS = (mongoHelper.findKey("publicInformation", "keys", "public key", recipientName));
                            //Gets recipient public key
                            BigInteger recipientPublicKey = new BigInteger(recipientPublicKeyS);
                            //Encrypts message using public key
                            BigInteger encryptedMessage = RSA.sendToEncryption(messageToSend, recipientPublicKey);
                            System.out.println("\n\nEncrypted message: " + encryptedMessage);
                            //The recipient gets their private key
                            String recipientPrivateKeyS = (mongoHelper.findKey("privateInformation", "keys", "private key", recipientName));
                            BigInteger recipientPrivateKey = new BigInteger(recipientPrivateKeyS);
                            System.out.println(recipientPrivateKey);
                            String decryptedMessage = RSA.recieveFromEncryption(encryptedMessage, recipientPrivateKey, recipientPublicKey);
                            System.out.println("\n\n" + recipientName + " decrypted the message using their private key: " + decryptedMessage+"\n\n");
                        } else {
                            System.out.println("Invalid recipient name or trying to send a message to yourself.");
                        }
                    }
                    else {
                    	//Generate a new public key and assign it to the collection
                    	BigInteger publicKey = RSA.generatePublicKey();
                    	String publicKeyHolder = publicKey.toString();
                    	Document publicKeyDoc = new Document("Name", username)
                                .append("public key", publicKeyHolder);
                    	publicCollection.insertOne(publicKeyDoc);
                    	//Generate a new private key and assign it to the collection
                    	RSA.calculateLambda();
                		BigInteger privateKey = RSA.calculatePrivateKey();
                		String privateKeyHolder = privateKey.toString();
                		Document privateKeyDoc = new Document("Name", username)
                                .append("private key", privateKeyHolder);
                    	privateCollection.insertOne(privateKeyDoc);
                    }
                } while(true);
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
