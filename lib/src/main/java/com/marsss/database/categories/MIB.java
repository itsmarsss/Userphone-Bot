package com.marsss.database.categories;

import com.marsss.callerphone.Callerphone;
import com.marsss.callerphone.ToolSet;
import com.marsss.callerphone.msginbottle.entities.Bottle;
import com.marsss.callerphone.msginbottle.entities.Page;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MIB {
    public static final Logger logger = LoggerFactory.getLogger(MIB.class);

    public static boolean createMIB(String id, String message, boolean anon) {
        MongoCollection<Document> mibCollection = Callerphone.dbConnector.getMibCollection();

        try {
            List<Document> pages = new ArrayList<>();

            long time = Instant.now().getEpochSecond();

            pages.add(new Document()
                    .append("pageNum", 0)
                    .append("author", id)
                    .append("message", message)
                    .append("signed", anon)
                    .append("released", time));

            String midUUID = ToolSet.generateUUID();

            InsertOneResult result = mibCollection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("id", midUUID)
                    .append("pages", pages)
                    .append("created", time));

            if (result.getInsertedId() != null) {
                logger.info("Added new MIB: {}", id);
            } else {
                logger.error("MIB addition not acknowledged for MIB: {}", id);
            }
            return true;
        } catch (MongoException me) {
            logger.error("Unable to add new MIB: {}", me.getMessage());
        }
        return false;
    }

    public static Bottle findBottle() {
        MongoCollection<Document> mibCollection = Callerphone.dbConnector.getMibCollection();

        try {
            List<Document> randomDocument = mibCollection.aggregate(
                            Collections.singletonList(Aggregates.sample(1)))
                    .into(new ArrayList<>());

            if (!randomDocument.isEmpty()) {
                return parseDocumentToBottle(randomDocument.get(0));
            }
            return null;
        } catch (MongoException me) {
            logger.error("Unable to find MIB: {}", me.getMessage());
        }
        return null;
    }

    public static Bottle getBottle(String uuid) {
        MongoCollection<Document> mibCollection = Callerphone.dbConnector.getMibCollection();

        try {
            Document mibDocument = mibCollection.find(new Document("id", uuid)).first();

            logger.info("MIB: {}", uuid);
            return parseDocumentToBottle(mibDocument);
        } catch (MongoException me) {
            logger.error("Unable to get MIB {}: {}", uuid, me.getMessage());
            return null;
        }
    }


    public static boolean addMIBPage(String id, String message, boolean anon, String uuid) {
        MongoCollection<Document> collection = Callerphone.dbConnector.getMibCollection();

        try {
            Bottle bottle = getBottle(uuid);

            if (bottle == null) {
                return false;
            }

            int newPageNum = bottle.getPages().size();
            long currentTime = Instant.now().getEpochSecond();

            Page newPage = new Page(newPageNum, id, message, anon, currentTime);

            bottle.getPages().add(newPage);

            ArrayList<Document> updatedPages = new ArrayList<>();
            for (Page page : bottle.getPages()) {
                Document pageDoc = new Document("pageNum", page.getPageNum())
                        .append("author", page.getAuthor())
                        .append("message", page.getMessage())
                        .append("signed", page.isSigned())
                        .append("released", page.getReleased());
                updatedPages.add(pageDoc);
            }

            collection.updateOne(new Document("id", uuid), new Document("$set", new Document("pages", updatedPages)));

            return true;
        } catch (MongoException me) {
            logger.error("Unable to update MIB {}: {}", uuid, me.getMessage());
            return false;
        }
    }


    private static Bottle parseDocumentToBottle(Document mib) {
        String uuid = mib.containsKey("id") ? mib.getString("id") : "unknown";

        List<Document> pagesDocs = mib.getList("pages", Document.class);
        ArrayList<Page> pages = new ArrayList<>();

        if (pagesDocs != null) {
            for (Document pageDoc : pagesDocs) {
                try {
                    int pageNum = pageDoc.containsKey("pageNum") ? pageDoc.getInteger("pageNum") : Integer.MAX_VALUE;
                    String author = pageDoc.containsKey("author") ? pageDoc.getString("author") : "unknown";
                    String message = pageDoc.containsKey("message") ? pageDoc.getString("message") : "*No found content.*";
                    boolean signed = pageDoc.containsKey("signed") ? pageDoc.getBoolean("signed") : false;
                    long released = pageDoc.containsKey("released") ? pageDoc.getLong("released") : Instant.now().getEpochSecond();

                    pages.add(new Page(pageNum, author, message, signed, released));
                } catch (Exception e) {
                    logger.error("Error parsing page id {}: {}", uuid, e.getMessage());
                }
            }
        }

        return new Bottle(uuid, pages);
    }
}
