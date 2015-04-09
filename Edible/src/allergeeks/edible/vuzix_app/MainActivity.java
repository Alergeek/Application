package allergeeks.edible.vuzix_app;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vuzix.speech.VoiceControl;

public class MainActivity extends Activity {
	
	private Activity main = this;
	private TextView debugspeech,  barcode;
	private VoiceControl vc;
	Toast toast;
	Token token; 
	private String id;// = "Bitte scanne deinen Kopplungscode";
	boolean created;
	HttpRequest request;
	String testexecute;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		debugspeech = (TextView)findViewById(R.id.result);
		barcode = (TextView)findViewById(R.id.scan_content);
		token = new Token();
		debugspeech.setText("Debug: ");
		request = new HttpRequest();
		
		
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
		
//##########################Tokencheck###################################################################		
		created = token.fileExistance("token.txt", main);
		


	
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanningResult != null) {
			//we have a result
			String barcodenummer = scanningResult.getContents();
			
			if(!created){
					try{
						
						String answer = request.Session(barcodenummer);
						if(answer != null){
							token.createToken(answer, main);
							id = barcodenummer;
							created = true;
							barcode.setText(id);
						}	
					}catch(Exception e ){
						
					};
				
			}else{
				try {
					id = token.readToken(main);
					barcode.setText(id);
					String answer = request.Request(id, barcodenummer);
					if(answer !=null){
						if(answer.equals("401")){
							token.delete(main);
							// TODO delete 
							toast = Toast.makeText(getApplicationContext(), "Ihr Gerät ist nicht mit einem Account verbunden, bitte koppeln Sie Ihr Gerät", Toast.LENGTH_LONG);
							toast.show(); 
						}else{
							// TODO Antwort parsen auf blacklist
							
						}
					}else{
						toast = Toast.makeText(getApplicationContext(), "Fehler beim Verbinden zum Server", Toast.LENGTH_LONG);
						toast.show();	
					} // ANSWER != NULL
				} catch (IOException e) {
					
					e.printStackTrace();
				};
			}//!CREATED
			
			
		}else{
			toast = Toast.makeText(getApplicationContext(), "Leider konnte der Barcode nicht erkannt werden", Toast.LENGTH_SHORT);
			toast.show();
		}//SCANNING != NULL
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

}


