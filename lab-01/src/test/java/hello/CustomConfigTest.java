package hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;



@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomConfigTest {
	
	@Autowired
	public ConfigurableEnvironment environment;

	@Test
    public void changeConfiguration() throws Exception {

			assertEquals(environment.getProperty("spring.datasource.url").toString(), "jdbc:mysql://localhost:3306/workshop");	
    		assertEquals(environment.getProperty("spring.datasource.username").toString(), "demouser");
    		assertEquals(environment.getProperty("spring.datasource.password").toString(), "12345678");
 	
    } 
	
}
