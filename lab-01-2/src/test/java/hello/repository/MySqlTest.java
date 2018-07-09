package hello.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import hello.Application;
import hello.model.User;
import hello.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
//@WebAppConfiguration
public class MySqlTest {
	
	@Autowired
	UserRepository repository;
  
	@Test
	public void test () {
		
		repository.deleteAll();
		
    User user1 = new User();
    user1.setName("Jeff Bar");
    user1.setEmail("bar@gmail.com");

    repository.save(user1);

    User user2 = new User();
    user2.setName("John Bell");
    user2.setEmail("bell@gmail.com");

    repository.save(user2);
        
		assertEquals(repository.count(), 2);
	}

}
