package yogesh.atArxxus.newsreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.R.bool;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class FirstActivity extends Activity {
	
	
	private static final String TAG = "FirstActivity.java";
	protected String _path;
	Context context;
	public static final int REQUEST_CODE = 1234;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		//@yogesh code for making application directory and tessdata directory
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		
		Button captureButton = (Button)findViewById(R.id.capture_button);
		Button KeyboardButton = (Button)findViewById(R.id.keyboard_btn);
		Button voiceButton = (Button)findViewById(R.id.voice_btn);
		
		
		// check if device has camera
		int numberOfCameras = Camera.getNumberOfCameras();
		context = this;
		PackageManager pm = context.getPackageManager();
		
		final boolean deviceHasCameraFlag = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
		
		if(numberOfCameras==0 || !deviceHasCameraFlag)
		{
			Log.e(TAG, "Device has no camera" + numberOfCameras);
			Toast.makeText(getApplicationContext(), "Device has no camera", Toast.LENGTH_SHORT).show();
			captureButton.setEnabled(false);
		}
		else
		{
			Log.e(TAG, "Device has camera" + deviceHasCameraFlag + numberOfCameras);
		}
		
		
		// capture button listener
		OnClickListener captureButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(deviceHasCameraFlag)
				{
				Intent cameraPreviewActivity = new Intent(getApplicationContext(), CameraPreview.class);
				startActivity(cameraPreviewActivity);
				}

			}
		};
		captureButton.setOnClickListener(captureButtonListener);
		
		// keboard button listener
		OnClickListener keyBoardButtonListener =  new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent resultViewActivityIntent = new Intent(getApplicationContext(), ResultViewActivity.class);
				startActivity(resultViewActivityIntent);
			}
		};
		KeyboardButton.setOnClickListener(keyBoardButtonListener);
		
		
		//Voice button listener
		OnClickListener voiceButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startSpeechRecognizer();
			}
		};
		voiceButton.setOnClickListener(voiceButtonListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first, menu);
		MenuItem help = menu.add("Help");
		OnMenuItemClickListener helpMenuItem = new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "clicked on help", Toast.LENGTH_SHORT).show();
				return false;
			}
		};
		help.setOnMenuItemClickListener(helpMenuItem);
		return true;
	}
	
	
	
	
	public void startSpeechRecognizer()
	{
    	
		Intent speech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		speech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-US");
		try
		{
			startActivityForResult(speech, REQUEST_CODE);
			
		}
		catch(ActivityNotFoundException e)
		{
			Toast.makeText(getApplicationContext(), "please check the net connection and try again", Toast.LENGTH_SHORT).show();
			
		}
	}
    
	
	protected void onActivityResult(int requestcode,int resultcode,Intent data)
	{
		
		
		super.onActivityResult(requestcode, resultcode, data);

		if(1234==requestcode)
		{
			switch(resultcode)
			{
			case  RESULT_OK:
				if(null != data)
				{
					String voiceHeadline = null;
					
					ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					voiceHeadline = text.get(0);
					
					Intent resultViewActivityIntent = new Intent(getApplicationContext(), ResultViewActivity.class);
					resultViewActivityIntent.putExtra("scanned_text", voiceHeadline);
					startActivity(resultViewActivityIntent);
				}// end of inner if
				break;
			}//end of switch
		}//end of if
    }

}
