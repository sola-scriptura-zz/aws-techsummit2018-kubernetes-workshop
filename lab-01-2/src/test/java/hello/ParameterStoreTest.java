package hello;


import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;



import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ParameterStoreTest {
	@Test
    public void testGetParamenterFromStore() {
//			AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
//			GetParameterRequest parameterRequest = new GetParameterRequest();
//			parameterRequest.withName("datasource.password").setWithDecryption(Boolean.valueOf(true));
//			GetParameterResult parameterResult = client.getParameter(parameterRequest);
//			String password = parameterResult.getParameter().getValue();
//			String version = parameterResult.getParameter().getVersion().toString();
//			assertEquals(password, "12345678");
//			assertEquals(version, "1");		
    }

}
