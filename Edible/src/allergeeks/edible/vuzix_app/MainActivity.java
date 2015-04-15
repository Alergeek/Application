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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	private String id, page = "http://edible.ddns.net";// = "Bitte scanne deinen Kopplungscode";
	boolean created;
	String testexecute;
	Button scan;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		debugspeech = (TextView)findViewById(R.id.result);
		view = (ImageView)findViewById(R.id.imageView1);
		barcode = (TextView)findViewById(R.id.scan_content);
		scan = (Button)findViewById(R.id.scan);
		token = new Token();
		debugspeech.setText("Debug: ");
		//created = token.fileExistance("token.txt", main);
		view.setImageResource(R.drawable.hello);
		//token.delete(main);
		created = token.fileExistance("token.txt", main);
		scan.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				if(!created){
					toast = Toast.makeText(main, "Bitte koppeln Sie jetzt Ihr Gerät", Toast.LENGTH_LONG);
					toast.show();
				}
				IntentIntegrator scanIntegrator = new IntentIntegrator(main);
				scanIntegrator.initiateScan();
				
			}
		});
//###########################Voice#######################################################################  
		vc = new myVoiceControl(this){
		
			
			protected void onRecognition(String arg0) {


				debugspeech.setText(arg0);
				if(arg0.equals("select")){
					if(!created){
						toast = Toast.makeText(main, "Bitte koppeln Sie jetzt Ihr Gerät", Toast.LENGTH_LONG);
						toast.show();
					}
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
		String authToken ="";
		if(created){ //go in if a token file exists
			try {
				
//#########################Testparams###########################################################################	
				//ean = "1234567890123";
				//authToken ="1111111111111111111";
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
					view.setImageResource(R.drawable.heart);
				}else if (result.equals("not edible")){
					view.setImageResource(R.drawable.brokenheart);
				}else if (result.equals("tokenerror")){//token abgelaufen
					toast = Toast.makeText(main, "Ihr Vuzix Gerät ist nicht mit einem Account verbunden", Toast.LENGTH_LONG);
					toast.show();
					view.setImageResource(R.drawable.wlan);
				}else if (result.equals("not available")){//produkt nicht vorhanden
					toast = Toast.makeText(main, "Das gescannte Produkt ist uns leider nicht bekannt", Toast.LENGTH_LONG);
					toast.show();
					view.setImageResource(R.drawable.wlan);
				}else{//sonstige errors
					toast = Toast.makeText(main, "Es ist ein Fehler aufgetreten, bitte versuchen Sie es später erneut", Toast.LENGTH_LONG);
					toast.show();
					view.setImageResource(R.drawable.wlan);
				}
				
			}
		}

	public class PostSession extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			String url = page+"/api/v1/session/";
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				List<NameValuePair> postParams = new ArrayList<NameValuePair>();
				postParams.add(new BasicNameValuePair("barcode", params[0]));
				
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParams);
				
				request.setEntity(formEntity);
				HttpResponse response = httpclient.execute(request);
				int status = response.getStatusLine().getStatusCode();
				
/*				String data = EntityUtils.toString(response.getEntity());
				JSONObject jObj = new JSONObject(data);
				String authToken = jObj.getString("token");*/
				
				
				switch(status){
				case 200: 	String data = EntityUtils.toString(response.getEntity());
							JSONObject jObj = new JSONObject(data);
							String authToken = jObj.getString("authToken");
							token.createToken(authToken, main);
							created = token.fileExistance("token.txt", main);
							//String test = token.readToken(main);
							//created = token.fileExistance("token.txt", main);
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
				toast = Toast.makeText(main, "Kopplung war erfolgreich", Toast.LENGTH_LONG);
				toast.show();
				view.setImageResource(R.drawable.heart);//Token wurde generiert und gespeichert
			}else{
				toast = Toast.makeText(main, "Kopplung war NICHT erfolgreich", Toast.LENGTH_LONG);
				toast.show();
				view.setImageResource(R.drawable.brokenheart);//Token wurde nicht gespeichert
			}
			
			
			
		}
	}


}




