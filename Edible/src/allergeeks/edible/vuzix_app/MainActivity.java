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
	private String id = "Bitte scanne deinen Kopplungscode";
	boolean created = false;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		token = new Token();
		
		created = token.fileExistance("token.txt", main);
		if(created){
			id = "Vorhanden";
		}else{
			id = "nicht Vorhanden";
		}
		debugspeech = (TextView)findViewById(R.id.result);
		barcode = (TextView)findViewById(R.id.scan_content);
		barcode.setText(id);
		debugspeech.setText("Debug: ");
		
		
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
		
		
	}	

	
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		
		
		if (scanningResult != null) {
			//we have a result
			String barcodenummer = scanningResult.getContents();
			barcode.setText("Result: " + barcodenummer); 
			
			
			}
			else{
				barcode.setText("Es konnte kein Barcode erkannt werden.");	//Bei API beachten
			}
		}//close if scanning != null
		


		
	
	
	
	
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


