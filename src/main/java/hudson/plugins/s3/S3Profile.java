package hudson.plugins.s3;

import hudson.FilePath;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.kohsuke.stapler.DataBoundConstructor;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3Profile {
    private String name;
    private String accessKey;
    private String secretKey;
    private final AtomicReference<AmazonS3Client> client = new AtomicReference<AmazonS3Client>(null);

    public S3Profile() {
    }

    @DataBoundConstructor
    public S3Profile(String name, String accessKey, String secretKey) {
        this.name = name;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        client.set(new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey)));
    }

    public final String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public final String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public final String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AmazonS3Client getClient() {
        if (client.get() == null) {
            client.set(new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey)));
        }
        return client.get();
    }

    public void check() throws Exception {
        getClient().listBuckets();
    }
    
    
   
    public void upload(String bucketName, FilePath filePath) throws IOException, InterruptedException {
        if (filePath.isDirectory()) {
            throw new IOException(filePath + " is a directory");
        }
        
        final Destination dest = new Destination(bucketName,filePath.getName());
        
        try {
            getClient().putObject(dest.bucketName, dest.objectName, filePath.read(), /*metadata=*/null);
        } catch (Exception e) {
            throw new IOException("put " + dest + ": " + e);
        }
    }
    
 
    
}
