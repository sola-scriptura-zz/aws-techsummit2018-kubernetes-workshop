package hello.logics;

import java.io.File;
import java.util.List;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.regions.Regions;

public class S3FileTransfer {
	
	public boolean checkBucket(String bucket, Regions region) {
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
		if (s3.doesBucketExistV2(bucket)) {return true;
		} else {
			//create a bucket
			 try {
	        Bucket b = s3.createBucket(bucket);
	    } catch (AmazonS3Exception e) {
	        System.err.println(e.getErrorMessage());
	        return false; //error
	    }
		}
		return true;
	}
	
	public void put(String bucket, String key, File file, Regions region)
	{

	}
	
	public List<String> list(String prefix, Regions region) 
	{
		return null;
		 
	}
	


}
