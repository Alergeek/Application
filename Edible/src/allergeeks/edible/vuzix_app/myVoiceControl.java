package allergeeks.edible.vuzix_app;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vuzix.speech.VoiceControl;

public class myVoiceControl extends VoiceControl {
	
	Context context2;
	public myVoiceControl(Context context) {
	super(context);
	context2 = context;
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
	//getView(0, null, null, arg0);
	//	TextView view;

	
	


}
}
