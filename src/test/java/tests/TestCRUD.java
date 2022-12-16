package tests;

import client.Client;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TestCRUD {

    private static final String COLLECTION_NAME = "customers";

    private static final Client client = new Client();

    @BeforeClass
    public static void beforeAll() {
        client.connect();
        client.getDatabase().createCollection(COLLECTION_NAME);
    }

    @AfterClass
    public static void afterAll() {
        client.getDatabase().getCollection(COLLECTION_NAME).drop();
        client.disconnect();
    }

    @Test
    public void testCRUD() {

        MongoDatabase database = client.getDatabase();

        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        // INSERT
        Document document1 = new Document();
        {
            document1.put("name", "Cornel");
            document1.put("company", "ibm");
            document1.put("putoare", "mare");
            collection.insertOne(document1);
        }
        {
            Document document2 = new Document();
            document2.put("name", "emilu");
            document2.put("company", "budava");
            document2.put("altceva", "nu stiu ce");
            collection.insertOne(document2);
        }
        Document document3 = new Document();
        {
            document3.put("name", "bestiutza");
            document3.put("company", "ibm");
            document3.put("putoare", "cea mai mare");
            collection.insertOne(document3);
        }

        // SEARCH
        {
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("company", "ibm");
            FindIterable<Document> cursor = collection.find(searchQuery);
            List<Document> retrievedDocuments = new ArrayList<>();
            cursor.forEach((Consumer<Document>) retrievedDocuments::add);

            Assert.assertEquals(2, retrievedDocuments.size());

            Assert.assertEquals(document1, retrievedDocuments.get(0));
            Assert.assertEquals(document3, retrievedDocuments.get(1));
            Assert.assertNotSame(document1, retrievedDocuments.get(0));
            Assert.assertNotSame(document3, retrievedDocuments.get(1));
        }

        // UPDATE
        {
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("name", "Cornel");

            Document update = new Document();
            update.put("putoare", "maxima");

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", update);

            collection.updateOne(searchQuery, updateObject);
        }

        // SEARCH
        {
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("name", "Cornel");
            FindIterable<Document> cursor = collection.find(searchQuery);
            List<Document> retrievedDocuments = new ArrayList<>();
            cursor.forEach((Consumer<Document>) retrievedDocuments::add);

            Assert.assertEquals(1, retrievedDocuments.size());

            // adjust expectations
            document1.put("putoare", "maxima");

            // verify content
            Assert.assertEquals(document1, retrievedDocuments.get(0));
            Assert.assertNotSame(document1, retrievedDocuments.get(0));
        }

        // DELETE
        {
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("name", "Cornel");
            collection.deleteOne(searchQuery);
        }

        // SEARCH
        {
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("name", "Cornel");
            FindIterable<Document> cursor = collection.find(searchQuery);
            List<Document> retrievedDocuments = new ArrayList<>();
            cursor.forEach((Consumer<Document>) retrievedDocuments::add);

            Assert.assertTrue(retrievedDocuments.isEmpty());
        }

    }
}
