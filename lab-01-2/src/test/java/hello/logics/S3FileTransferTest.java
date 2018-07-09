package hello.logics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.Region;

@SpringBootTest
public class S3FileTransferTest {
	
	private String bucket = "seon-singapore-201606";
	private Regions region = Regions.AP_SOUTHEAST_1;
	
	
	@Test
	public void testCheckBucket() //if there is no bucket, then creat it.
	{
		S3FileTransfer ft = new S3FileTransfer();
		assertTrue(ft.checkBucket(bucket, region));
	}
	
	@Test
	public void testList() 
	{
		S3FileTransfer ft = new S3FileTransfer();
		List<String> result = ft.list(bucket, region);
		assertNull(result);
//    result.forEach( (names)->System.out.println("## prefix = " + names) ); 
	}
	
	@Test
	public void testPut()
	{
		S3FileTransfer ft = new S3FileTransfer();
		ft.put(bucket, "a.jpeg", new File("/Users/seonpark/Documents/Development-2018/workspace/hello/temp/a.jpeg"), region);;
	}

}
