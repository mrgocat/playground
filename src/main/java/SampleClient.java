import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import static java.lang.System.*;

public class SampleClient {
	private FhirPaitentClient _client;
	private StopWatch _timer;
	
	public SampleClient(FhirPaitentClient client, StopWatch timer) {
		this._client = client;
		this._timer = timer;
		_client.registerTimer(timer);
	}
    public long searchWithFamilyName(String name, CacheControlDirective cacheObj) {
    	Bundle response = _client.searchWithFamilyName(name, cacheObj);
    	return _timer.requestStopWatch();
    }
    public long searchWithFamilyNames(ArrayList<String> familyNames, boolean noCache) {
    	CacheControlDirective cacheObj = new CacheControlDirective().setNoCache(noCache);
    	long totalTime = 0;
    	//familyNames.forEach(this::searchWithFamilyName);
    	for(String name:familyNames) {
    		totalTime += searchWithFamilyName(name, cacheObj);
    	}
    	return totalTime/familyNames.size();
    }
    
    public static void main(String[] theArgs) {
	    StopWatch timer = new StopWatch();
	    FhirPaitentClient client = new FhirPaitentClient();

    	SampleClient fhir = new SampleClient(client, timer);
    	ArrayList<String> familyNames = new ArrayList<>();
    	
    	try(BufferedReader reader = new BufferedReader(new InputStreamReader(SampleClient.class.getClassLoader().getResourceAsStream("FamilyNames.txt")))) {
    		for(String line; (line = reader.readLine()) != null;) {
    			familyNames.add(line);
    		}
    	}catch(IOException e) {
    		e.printStackTrace();
    	}

    	long avgTime = fhir.searchWithFamilyNames(familyNames, false);
    	out.println("Average time for the first loop=" + avgTime + "ms");
    	avgTime = fhir.searchWithFamilyNames(familyNames, false);
    	out.println("Average time for the second loop=" + avgTime + "ms");
    	avgTime = fhir.searchWithFamilyNames(familyNames, true);
    	out.println("Average time for the third loop=" + avgTime + "ms");
    }
}
class FhirPaitentClient{
	private static final String CLIENT_PATH = "http://hapi.fhir.org/baseR4";
	private static final String RESOURCE = "Patient";
	
	private IGenericClient _client;
	public FhirPaitentClient() {
	    FhirContext fhirContext = FhirContext.forR4();
	    _client = fhirContext.newRestfulGenericClient(CLIENT_PATH);
	    _client.registerInterceptor(new LoggingInterceptor(false));
	}
	public void registerTimer(StopWatch timer) {
		_client.registerInterceptor(timer);
	}
	public Bundle searchWithFamilyName(String name, CacheControlDirective cacheObj){
		return _client
	        .search()
	        .forResource(RESOURCE)
	        .where(Patient.FAMILY.matches().value(name))
	        .returnBundle(Bundle.class).cacheControl(cacheObj)
	        .execute();
	}
}
class StopWatch implements IClientInterceptor{
	long startTime;
	long responTime;
	public long requestStopWatch() {
		return responTime;
	}
	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		startTime = System.currentTimeMillis();
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) throws IOException {
		// TODO Auto-generated method stub
		responTime = System.currentTimeMillis() - startTime;
	}	
}
