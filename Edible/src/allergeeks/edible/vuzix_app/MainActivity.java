package allergeeks.edible.vuzix_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.speech.VoiceControl;

public class MainActivity extends Activity {
	
	private Activity main = this;
	private TextView result, contentTxt;
	private VoiceControl vc;
	Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	result = (TextView)findViewById(R.id.result);
		
		
		result.setText("Test");
		vc = new myVoiceControl(this);
		
		/*if(vc != null){
			 toast.makeText(this,"klappt",Toast.LENGTH_LONG).show();
			
			 
		}else{
			 toast.makeText(this,"klappt nicht",Toast.LENGTH_LONG).show();	
		}
		*/

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


}


