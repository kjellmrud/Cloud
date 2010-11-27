package no.ic.cloud.blog;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import no.ic.cloud.blog.s3Utils.S3Connection;

import java.io.File;
import java.io.IOException;
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addThread(String name) {
        AmazonS3 s3 = null;
        try {
            s3 = S3Connection.getConnection();
            for (Bucket b : s3.listBuckets()) {
                b.getName();
            }
            s3.putObject(new PutObjectRequest("", "key", new File("")));
        } catch (IOException e) {
            //LOG this
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public List<String> getPosts(String thread) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addPost(String thread, String post) {
        try {
            AmazonS3 s3 = S3Connection.getConnection();
        } catch (IOException e) {
            //LOG THIS
            e.printStackTrace();
        }
    }
}
