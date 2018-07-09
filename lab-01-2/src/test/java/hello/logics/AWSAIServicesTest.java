package hello.logics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.amazonaws.regions.Regions;

@SpringBootTest
public class AWSAIServicesTest {
	
	private String bucket = "seon-virginia-2016";
	private Regions region = Regions.US_EAST_1;
	String photo = "a.jpeg";
	
	@Test
	public void testRetrieveInfoFromPhoto()
	{
		AWSAIServices ai = new AWSAIServices();
		ai.retrieveInformation(bucket, photo, region);

	}
	
	@Test
	public void testTranslate()
	{
		AWSAIServices ai = new AWSAIServices();
		assertEquals(ai.translate("Hello World", "en", "es", region), "Hola Mundo");
	}

}
