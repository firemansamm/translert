package com.translert.bus;

import java.io.IOException;
//import java.util.List;
 
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HTTPtoJSONString {
	
	static String response = null;

	public HTTPtoJSONString() {
		
	}
	
	public String doit (URI url) throws ClientProtocolException, IOException {
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        response = EntityUtils.toString(httpEntity);
		
		return response;
		
	}

}
