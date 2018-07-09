package hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Properties;

import org.junit.FixMethodOrder;

import static org.hamcrest.Matchers.equalTo;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomConfigTest {
	
	@Autowired
	public ConfigurableEnvironment environment;

	@Test
    public void changeConfiguration() throws Exception {

//			assertEquals(environment.getProperty("spring.datasource.url").toString(), "jdbc:mysql://localhost:3306/workshop");	
//    	assertEquals(environment.getProperty("spring.datasource.username").toString(), "demouser");
//    	assertEquals(environment.getProperty("spring.datasource.password").toString(), "12345678");
    		
//    		Properties props = new Properties();
//    		props.put("spring.datasource.url", "test");
//    		environment.getPropertySources().addFirst(new PropertiesPropertySource("myProps", props));
//    		
//    		assertEquals(environment.getProperty("spring.datasource.url").toString(), "test");	
    } 
    



	
}
