package hello.logics;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import javax.imageio.IIOException;

/*
 * This is test cases for ResizeImage. 
 * attendees should create ResizeImage Class based on this test cases
 */
@SpringBootTest
public class ResizeImageTest {
	
	//Use the ExpectedException rule. This rule lets you indicate not only what exception you are expecting, 
	//but also the exception message you are expecting:
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	// This is test for exception handling
	@Test(expected = IOException.class)
	public void testReadFileExeption() throws IOException
	{
		ResizeImage r = new ResizeImage();
		r.readFile("/Users/seonpark/Documents/Development-2018/workspace/hello/temp/1.png");	
		thrown.expect(IOException.class);
		thrown.expectMessage("Can't read input file!");
	}
	
	@Test
	public void testReadFile() throws IOException  
	{
		ResizeImage r = new ResizeImage();
		r.readFile("/Users/seonpark/Documents/Development-2018/workspace/hello/temp/a.jpeg");	
		assertNotNull(r.getOrgImage());
	
	}
	
	@Test
	public void testResize() throws IOException  
	{
		ResizeImage r = new ResizeImage();
		r.readFile("/Users/seonpark/Documents/Development-2018/workspace/hello/temp/a.jpeg");	
		r.resize(200, 100);
		assertNotNull(r.getResizedImage());
	
	}
	
	@Test
	public void testSaveFile() throws IOException  
	{
		ResizeImage r = new ResizeImage();
		r.readFile("/Users/seonpark/Documents/Development-2018/workspace/hello/temp/a.jpeg");	
		r.resize(200, 100);
		r.writeFile("/Users/seonpark/Documents/Development-2018/workspace/hello/temp/a-1.jpeg", "jpeg");
		assertNotNull(r.getResizedImage());
	}	
	
}
