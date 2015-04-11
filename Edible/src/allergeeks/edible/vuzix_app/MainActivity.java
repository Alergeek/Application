package allergeeks.edible.vuzix_app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vuzix.speech.VoiceControl;

public class MainActivity extends Activity {
	
	ImageView view;
	private Activity main = this;
	private TextView debugspeech,  barcode;
	private VoiceControl vc;
	Toast toast;
	Token token; 
	private String id, page = "http://37.221.192.99";// = "Bitte scanne deinen Kopplungscode";
	boolean created;
	String testexecute;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		debugspeech = (TextView)findViewById(R.id.result);
		view = (ImageView)findViewById(R.id.imageView1);
		barcode = (TextView)findViewById(R.id.scan_content);
		token = new Token();
		debugspeech.setText("Debug: ");
		created = token.fileExistance("token.txt", main);
		
//###########################Voice#######################################################################  
		vc = new myVoiceControl(this){
		
			
			protected void onRecognition(String arg0) {
				//getView(0, null, null, arg0);
				//	TextView view;

				debugspeech.setText("Debug: " + arg0);
			
				if(arg0.equals("select")){
					IntentIntegrator scanIntegrator = new IntentIntegrator(main);
					scanIntegrator.initiateScan();
				
				}


			}
		};
//##########################/Voice#######################################################################

	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		String ean = scanningResult.getContents();
		String authToken;
		if(created){ //go in if a token file exists
			try {
				
//#########################Testparams###########################################################################	
				ean = "1234567890123";
				authToken ="1111111111111111111";
//#######################Testparams#############################################################################
				
				authToken = token.readToken(main);
				String url = page +"/api/v1/product/"+ean+"/"+authToken+"/";
				new GetProduct().execute(url);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{//authToken not exists
			
			new PostSession().execute(ean);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			IntentIntegrator scanIntegrator = new IntentIntegrator(main);
			scanIntegrator.initiateScan();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	protected void onResume(){
		super.onResume();
		vc.on();
	}

	
	protected void onPause(){
		super.onDestroy();
		vc.off();
	}

	protected void onDestroy(){
		super.onDestroy();
		if(vc != null){
			vc.destroy();
		}
	}	
	
	public class GetProduct extends AsyncTask<String, Void, String>{
			
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				
				try {
					//Get Edible Status
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(params[0]);
					HttpResponse response = client.execute(get);
					int status = response.getStatusLine().getStatusCode();
					
					//Check if server is connected 
					if(status == 200){
											
						HttpEntity entity = response.getEntity();
						String data = EntityUtils.toString(entity);
						
						JSONObject jObj = new JSONObject(data);
						String name = jObj.getString("edible");
						
						if(name.equals("True")){ //test ob essbar oder nicht
							name = "edible";
						}else{
							name = "not edible";
						}
						
					return name;	
					}else if(status == 401){//token abgelaufen
						token.delete(main);
						created = token.fileExistance("token.txt", main);
						String result = "tokenerror";
						return result;
					}else if(status == 404){//produkt nicht vorhanden
						String result = "not available";
						return result;						
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "false";
			}
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (result.equals("edible")){
					
					view.setImageResource(R.drawable.herz);
				}else if (result.equals("not edible")){
					view.setImageResource(R.drawable.gebrochnesherz);
				}else if (result.equals("tokenerror")){//token abgelaufen
					view.setImageResource(R.drawable.gebrochnesherz);
				}else if (result.equals("not available")){//produkt nicht vorhanden
					view.setImageResource(R.drawable.gebrochnesherz);
				}else{//sonstige errors
					view.setImageResource(R.drawable.ic_launcher);
				}
				
			}
		}

	public class PostSession extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			String url = page;
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				List<NameValuePair> postParams = new ArrayList<NameValuePair>();
				postParams.add(new BasicNameValuePair("barcode", params[0]));
				
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParams);
				
				request.setEntity(formEntity);
				HttpResponse response = httpclient.execute(request);
				int status = response.getStatusLine().getStatusCode();
				String authToken = EntityUtils.toString(response.getEntity());
				
				switch(status){
				case 200: 	token.createToken(authToken, main);
							created = token.fileExistance("token.txt", main);
							break;
				default: 	created = false;
							break;
				}
				
				
				
				return created;
			}catch(Exception e){
				
			}
			return created;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result){
				view.setImageResource(R.drawable.herz);//Token wurde generiert und gespeichert
			}else{
				view.setImageResource(R.drawable.gebrochnesherz);//Token wurde nicht gespeichert
			}
			
			
			
		}
	}



}




