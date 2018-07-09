package hello.controller;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainControllerIT {
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/workshop/all");
    }

    @Test
    public void getAll() throws Exception {
//        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
//        assertThat(response.getBody(), containsString("[{\\\"id\\\":1,\\\"name\\\":\\\"First\\\",\\\"email\\\":\\\"ex1@gmail.com\\\"}]\""));
    }

}
