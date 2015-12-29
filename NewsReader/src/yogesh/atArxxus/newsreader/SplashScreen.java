package yogesh.atArxxus.newsreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

public class SplashScreen extends Activity {
	
    private Thread mSplashThread;    
    private ImageView splashImageView;
    private AnimationDrawable frameAnimation;
    public static  String EXTERNAL_STORAGE_STRING ; //= Environment.getExternalStorageDirectory().toString();
    public static  String DATA_PATH ;//= EXTERNAL_STORAGE + "/NewsReader/";
    public static final String lang = "eng";
    private static final String TAG = "SplashScreen";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen_activity);
		
		EXTERNAL_STORAGE_STRING = getFilesDir().toString();
		DATA_PATH = EXTERNAL_STORAGE_STRING + "/NewsReader/";
		
		splashImageView = (ImageView) findViewById(R.id.splashImageView);
		splashImageView.setBackgroundResource(R.drawable.loading);
		
		frameAnimation = (AnimationDrawable) splashImageView.getBackground();
		
		splashImageView.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				frameAnimation.start();
			}
		});
	
		mSplashThread = new Thread() {
			
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				synchronized (this) 
				{
					
					
					String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

					for (String path : paths) {
						File dir = new File(path);
						if (!dir.exists()) {
							if (!dir.mkdirs()) {
								Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
								return;
							} else {
								Log.v(TAG, "Created directory " + path + " on sdcard");
								
							}
						}

					}
					
					
					if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
						try {

							AssetManager assetManager = getAssets();
							InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
							//GZIPInputStream gin = new GZIPInputStream(in);
							OutputStream out = new FileOutputStream(DATA_PATH
									+ "tessdata/" + lang + ".traineddata");

							// Transfer bytes from in to out
							byte[] buf = new byte[1024];
							int len;
							//while ((lenf = gin.read(buff)) > 0) {
							while ((len = in.read(buf)) > 0) {
								out.write(buf, 0, len);
							}
							in.close();
							//gin.close();
							out.close();
							
							Log.v(TAG, "Copied " + lang + " traineddata");
						} catch (IOException e) {
							Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
						}
					}
					
					
					try 
					{
						sleep(4000);
					} 
					catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
					Intent firstActivity = new Intent(getApplicationContext(), FirstActivity.class);
					startActivity(firstActivity);
					finish();
				
			}
		};
		
		mSplashThread.start();
	}

}
