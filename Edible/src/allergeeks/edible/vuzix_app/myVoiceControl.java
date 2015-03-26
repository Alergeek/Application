package allergeeks.edible.vuzix_app;
import android.content.Context;
import android.util.Log;

import com.vuzix.speech.VoiceControl;

public class myVoiceControl extends VoiceControl {
	
	public myVoiceControl(Context context) {
	super(context);
	}

	public myVoiceControl(Context context, String[] grammars) {
			super(context, grammars);
			}
	
	public myVoiceControl(Context context, String[] grammars, String[] wordlist) {
			super(context, grammars, wordlist);
			}
	



	@Override
	protected void onRecognition(String arg0) {
		// TODO Auto-generated method stub
	//	String result = arg0;
	//	TextView view;
			
		
		
		
	}


}
