package hello;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
//    		updateEnvironment();
    	
//		Properties props = new Properties();
//		props.put("spring.datasource.url", "test");
//		PropertiesPropertySource propSource = new PropertiesPropertySource("myProps", props);
//		environment.getPropertySources().addFirst(propSource);
        SpringApplication.run(Application.class, args);
    }
    
//    private static void updateEnvironment()
//    {
//		SpringApplication application = new SpringApplication(Application.class);
//		Map<String, Object> map = new HashMap<>();
//		map.put("spring.datasource.username", "aa");
//		application.setDefaultProperties(map);≈∆
//    }
}
