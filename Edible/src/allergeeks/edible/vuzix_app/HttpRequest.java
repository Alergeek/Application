package allergeeks.edible.vuzix_app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.util.Log;

public class HttpRequest {
	
	String page = "www.google.de";
	
	
	
	public HttpRequest(){
		
	}
	
	public String Request(String id, String product){
		String serveranswer = "";
		
		try{
			HttpClient client = new DefaultHttpClient();
			String url = page + id+"/"+product+"/";	
			Log.i("httpget", url);
		
			try{
			
				HttpGet httpget = new HttpGet(url);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				serveranswer = client.execute(httpget, responseHandler);
			
			}catch(Exception ex){
			
			};
		
		}catch(Exception e){
			
		};
		
		return serveranswer;
	}
	
	// TODO POST
	public String Session(String barcode){
		String serveranswer = "";
		
		try{
			HttpClient client = new DefaultHttpClient();
			String url = page; // TODO bis zur API route erweitern!
			HttpPost httppost = new HttpPost(url);
			Log.i("httppost", url);
		
			try{
				//data
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
				nameValuePair.add(new BasicNameValuePair("Kopplungscode", barcode));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			
				//Post Request
				HttpResponse response = client.execute(httppost);
				
				serveranswer = response.toString();
				
				
			}catch(ClientProtocolException e){
				
			
			}catch(IOException e){
				
			};
		
		}catch(Exception e){
			
		};
		
		return serveranswer;
	}
	
	
}
