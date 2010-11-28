package no.ic.cloud.blog.s3Utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: thomas.pronstad
 * Date: 27.nov.2010
 * Time: 12:39:16
 */
public class S3Connection {
    public static String AWS_PROPERTIES = "/AwsCredentials.properties";

    private static AmazonS3 instance;

    public static AmazonS3 getConnection() throws IOException {
        if (instance == null ) {
            URL url = AmazonS3.class.getResource(AWS_PROPERTIES);
            AWSCredentials credentials = new PropertiesCredentials(url.openStream());
            instance = new AmazonS3Client(credentials);
        }
        return instance;
    }
}
