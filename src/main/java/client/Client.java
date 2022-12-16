package client;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;

public class Client {

    private MongoClient mongoClient;

    private MongoDatabase database;

    public void connect() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/myMongoDb"));
        database = mongoClient.getDatabase("myMongoDb");
    }

    public void disconnect() {
        mongoClient.close();
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
