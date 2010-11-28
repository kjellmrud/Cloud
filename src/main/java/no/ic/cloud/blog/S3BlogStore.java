package no.ic.cloud.blog;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import no.ic.cloud.blog.s3Utils.S3Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thomas.pronstad
 * Date: 27.nov.2010
 * Time: 11:03:56
 */
public class S3BlogStore implements BlogStore {
    private static Logger LOGGER = LoggerFactory.getLogger(S3BlogStore.class);
    private static final String ICCLOUD = "iccloud";
    private AmazonS3 s3;

    public S3BlogStore() {
        try {
            s3 = S3Connection.getConnection();
        } catch (IOException e) {
            LOGGER.error("Cant establish connection", e);
        }
    }

    public List<String> getThreads() {
        List<String> threads = new ArrayList<String>();
        ObjectListing ol = s3.listObjects(ICCLOUD);
        //We dont bother to handle pagination
        if (ol.isTruncated()) throw new Error("Oh crap, too many threads");
        List<S3ObjectSummary> os = ol.getObjectSummaries();
        for (S3ObjectSummary s3s : os) {
            String key = s3s.getKey();
            threads.add(key);
        }
        return threads;
    }

    public void addThread(String name) {
        ObjectMetadata objectMetaData = new ObjectMetadata();
        InputStream thread = new ByteArrayInputStream("welcome".getBytes());
        objectMetaData.setContentLength("welcome".getBytes().length);
        s3.putObject(new PutObjectRequest(ICCLOUD, name, thread, objectMetaData));
        LOGGER.info("*********THREAD: " + name + " created ********");

    }

    public List<String> getPosts(String inputThread) {
        List<String> threadList = getThreads();
        if (!threadList.contains(inputThread)) {
            LOGGER.warn("THREAD DOES NOT EXIST!");
            throw new IllegalStateException();
        }
        List<String> posts = new ArrayList<String>();
        try {
            ObjectListing ol = s3.listObjects(ICCLOUD);
            if (ol.isTruncated()) throw new Error("Oh crap, too many threads");
            List<S3ObjectSummary> threads = ol.getObjectSummaries();
            for (S3ObjectSummary thread : threads) {
                String name = thread.getKey();
                if (name.equals(inputThread)) {
                    posts = getS3ObjectsForThread(name);

                }
            }
        } catch (IOException e) {
            LOGGER.error("Problem with reader", e);
        }
        return posts;
    }

    public void addPost(String thread, String post) {
        List<String> threads = getThreads();
        if (threads.contains(thread)) {
            List<String> posts = getPosts(thread);
            ObjectMetadata objectMetaData = new ObjectMetadata();
            InputStream postStream;
            StringBuilder content = new StringBuilder();
            for (String p : posts) {
                content.append(p);
                content.append("\n");
            }
            content.append(post);
            LOGGER.debug("*************Adding posts: " + content.toString());
            postStream = new ByteArrayInputStream(content.toString().getBytes());
            objectMetaData.setContentLength(content.toString().getBytes().length);
            s3.putObject(new PutObjectRequest(ICCLOUD, thread, postStream, objectMetaData));
        } else {
            LOGGER.warn("THREAD DID NOT EXIST: " + thread + ". Post not inserted");
        }
    }
    
    private List<String> getS3ObjectsForThread(String name) throws IOException {
         LOGGER.debug("*************** GETTING OBJECTS FOR Thread: " + name);
         List<String> posts = new ArrayList<String>();
         S3Object s3o = s3.getObject(new GetObjectRequest(ICCLOUD, name));
         InputStream is = s3o.getObjectContent();
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         while (br.ready()) {
             posts.add(br.readLine());
         }
         return posts;
     }

}
