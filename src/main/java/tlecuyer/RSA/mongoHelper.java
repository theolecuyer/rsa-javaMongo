package tlecuyer.RSA;

import org.bson.Document;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class mongoHelper {
	String connectionString = "REMOVED";
    ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .serverApi(serverApi)
            .build();
    MongoClient mongoClient = MongoClients.create(settings);
    //Disabled logging from mongoDB
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger mongLogger = loggerContext.getLogger("org.mongodb.driver");
	
    public String findKey(String dbName, String collectionName, String fieldName, String username) {
        // Assuming you have initialized your MongoDB connection
        
        // Get the database and collection
        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        // Prepare a query to find the document with the given username
        Document query = new Document("Name", username);

        // Execute the query
        FindIterable<Document> result = collection.find(query);

        // Check if the username exists
        Document userDocument = result.first();
        if (userDocument != null) {
            // Retrieve the associated field value that is not the username
            Object value = userDocument.get(fieldName);
            return value != null ? value.toString() : null;
        } else {
            return null; // Username not found
        }
    }
}
