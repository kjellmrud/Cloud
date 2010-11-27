package no.ic.cloud.blog;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import no.ic.cloud.blog.s3Utils.S3Connection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thomas.pronstad
 * Date: 27.nov.2010
 * Time: 11:03:56
 * To change this template use File | Settings | File Templates.
 */
public class S3BlogStore implements BlogStore {

    public List<String> getThreads() {
        List<String> threads = new ArrayList<String>();
        AmazonS3 s3 = null;
        try {
            s3 = S3Connection.getConnection();
            ObjectListing ol = s3.listObjects("iccloud");
            if (ol.isTruncated()) throw new Error("Oh crap, too many threads");
            List<S3ObjectSummary> os = ol.getObjectSummaries();
            for (S3ObjectSummary s3s : os) {
                String key = s3s.getKey();
                S3Object s3o = s3.getObject(new GetObjectRequest("iccloud", key));
                //InputStream is = s3o.getObjectContent();
                //BufferedReader br = new BufferedReader(new InputStreamReader(is));
                threads.add(key);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return threads;
    }

    public void addThread(String name) {
        AmazonS3 s3 = null;
        try {
            s3 = S3Connection.getConnection();
            if (getThreads().contains(name)) {
                s3.deleteObject("iccloud", name);
            }
            ObjectMetadata objectMetaData = new ObjectMetadata();
            InputStream thread = new ByteArrayInputStream("".getBytes());
            objectMetaData.setContentLength("".getBytes().length);
            s3.putObject(new PutObjectRequest("iccloud", name, thread, objectMetaData));
        } catch (IOException e) {
            //LOG this
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public List<String> getPosts(String thread) {
        List<String> posts = new ArrayList<String>();
        AmazonS3 s3 = null;
        try {
            s3 = S3Connection.getConnection();
            ObjectListing ol = s3.listObjects("iccloud");
            if (ol.isTruncated()) throw new Error("Oh crap, too many posts");
            List<S3ObjectSummary> os = ol.getObjectSummaries();
            for (S3ObjectSummary s3s : os) {
                String key = s3s.getKey();
                S3Object s3o = s3.getObject(new GetObjectRequest("iccloud", key));
                InputStream is = s3o.getObjectContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while (br.ready()){
                    posts.add(br.readLine());
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return posts;
    }

    public void addPost(String thread, String post) {
        List<String> threads = getThreads();
        if (threads.contains(thread)) {
            List<String> posts = getPosts(thread);

            AmazonS3 s3 = null;
            try {
                s3 = S3Connection.getConnection();
                ObjectMetadata objectMetaData = new ObjectMetadata();
                InputStream postStream;
                StringBuilder content = new StringBuilder();
                for (String p : posts) {
                    content.append(p);
                    content.append("\n");
                }
                content.append(post);
                postStream = new ByteArrayInputStream(content.toString().getBytes());
                objectMetaData.setContentLength(content.toString().getBytes().length);
                s3.putObject(new PutObjectRequest("iccloud", thread, postStream, objectMetaData));
            } catch (IOException e) {
                //LOG this
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        } else {
            //LOG
        }
    }
}
