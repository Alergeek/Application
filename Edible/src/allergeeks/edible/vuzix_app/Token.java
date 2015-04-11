package allergeeks.edible.vuzix_app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;



public class Token {
	
	public Token(){
		super();
		}

	public void createToken(String arg0, Context ctr) throws IOException{
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		
		try {
			fos = ctr.openFileOutput("token.txt", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fos);
			osw.write(arg0);
			
		} catch (Throwable t) {
			// TODO Auto-generated catch block
			Log.e("token", "save()", t);
		}finally{
			if(osw != null){
				try{
					osw.close();
				}catch(IOException e){
					Log.e("token", "osw.close()", e);
				}
			}
			if(fos != null){
				try{
					fos.close();
				}catch(IOException e){
					Log.e("token", "fos.close()", e);
				}
			}
		}
		
	}
	
	public String readToken(Context ctr) throws IOException{
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = null;
		InputStreamReader isr= null;
		BufferedReader br = null;
		
		try {
			fis = ctr.openFileInput("token.txt");
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			String s;
			
			while((s = br.readLine()) != null){
				if(sb.length() > 0){
					sb.append('\n');
				}
				sb.append(s);
			}
			
		} catch (Throwable t) {
			// FILENOTFOUND
			Log.e("token", "load()", t);
		}finally{
			if(br != null){
				try{
					br.close();
				}catch(IOException e){
					Log.e("token", "br.close()", e);
				}
			}
			if(isr != null){
				try{
					isr.close();
				}catch(IOException e){
					Log.e("token", "isr.close()", e);
				}
			}
			if(fis != null){
				try{
					fis.close();
				}catch(IOException e){
					Log.e("token", "fis.close()", e);
				}
			}
		}
		return sb.toString();
	}	

	public boolean fileExistance(String fname, Context ctr){
	    File file = ctr.getFileStreamPath(fname);
	    return file.exists();
	}	
	
	public boolean delete(Context ctr){
		String dir = ctr.getFilesDir().getAbsolutePath();
	    File file = new File (dir, "token.txt");
	    return file.delete();
	}
	
}
