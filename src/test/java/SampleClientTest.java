import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import ca.uhn.fhir.rest.api.CacheControlDirective;

import static org.mockito.BDDMockito.when;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SampleClientTest {
	
	@Mock
	FhirPaitentClient clientMock;
	
	@Mock
	StopWatch timerMock;	
	
	@Before
	public void setup() {
	//	Mock.init();
	}
	
	@Test
	public void test() {
		assertNotNull(clientMock);
		assertNotNull(timerMock);
		when(clientMock.searchWithFamilyName(anyString(), any(CacheControlDirective.class))).thenReturn(null);
		when(timerMock.requestStopWatch()).thenReturn(5L);
		
		SampleClient client = new SampleClient(clientMock, timerMock);
		
		long result = client.searchWithFamilyName("SMITH", null);
		assertEquals(5, result);
		
		ArrayList<String> list = new ArrayList<>();
		list.add("SMITH");
		list.add("Trump");
		list.add("Trudeau");
		result = client.searchWithFamilyNames(list, true);
		assertEquals(5, result);
		
	}

}
