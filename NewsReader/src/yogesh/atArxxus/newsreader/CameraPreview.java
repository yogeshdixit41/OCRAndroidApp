package yogesh.atArxxus.newsreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.string;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;


public class CameraPreview extends Activity implements SensorEventListener {

	//public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/NewsReader/";
	private Preview mPreview; 
	private Button mTakePicture;
	private TouchView mView;
	private String mPath;

	private boolean mAutoFocus = true;

	private boolean mFlashBoolean = false;

	private SensorManager mSensorManager;
	private Sensor mAccel;
	private boolean mInitialized = false;
	private float mLastX = 0;
	private float mLastY = 0;
	private float mLastZ = 0;
	private Rect rec = new Rect();

	private int mScreenHeight;
	private int mScreenWidth;

	private boolean mInvalidate = false;
	private File mLocation=null;
	File StorageDir;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//@yogesh creating directory for storing images 
		StorageDir = new File(SplashScreen.DATA_PATH + "images_taken");
		 if(!StorageDir.exists()){
			 if(!StorageDir.mkdir()){
				 Log.v("Image_taken directory creation:","directory creation failed");
				 return;
			 }
		 }
		 
		super.onCreate(savedInstanceState);
		//Log.i(TAG, "onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// display our (only) XML layout - Views already ordered
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// for landscape view
		
		// the accelerometer is used for autofocus
		mSensorManager = (SensorManager) getSystemService(Context.
				SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor(Sensor.
				TYPE_ACCELEROMETER);

		// get the window width and height to display buttons
		// according to device screen size
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		mScreenHeight = displaymetrics.heightPixels;
		mScreenWidth = displaymetrics.widthPixels;
		
		
		// I need to get the dimensions of this drawable to set margins
		// for the ImageView that is used to take pictures
		Drawable mButtonDrawable = this.getResources().getDrawable(R.drawable.cameranew);

		mTakePicture = (Button) findViewById(R.id.startcamerapreview);

		// setting where I will draw the ImageView for taking pictures
	/*	LayoutParams lp = new LayoutParams(mTakePicture.getLayoutParams());
		lp.setMargins((int)((double)mScreenWidth*.85),
				(int)((double)mScreenHeight*.70) ,
				(int)((double)mScreenWidth*.85)+mButtonDrawable.
				getMinimumWidth(), 
				(int)((double)mScreenHeight*.70)+mButtonDrawable.
				getMinimumHeight());
		mTakePicture.setLayoutParams(lp); */
		
		// rec is used for onInterceptTouchEvent. I pass this from the
		// highest to lowest layer so that when this area of the screen
		// is pressed, it ignores the TouchView events and passes it to
		// this activity so that the button can be pressed.
		
		rec.set((int)((double)mScreenWidth*.85),
				(int)((double)mScreenHeight*.10) ,
				(int)((double)mScreenWidth*.85)+mButtonDrawable.getMinimumWidth(), 
				(int)((double)mScreenHeight*.70)+mButtonDrawable.getMinimumHeight());
		mButtonDrawable = null;
		mTakePicture.setOnClickListener(previewListener);
		// get our Views from the XML layout
		mPreview = (Preview) findViewById(R.id.preview);
		mView = (TouchView) findViewById(R.id.left_top_view);
		mView.setRec(rec);

	}

	// this is the autofocus call back
	private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

		public void onAutoFocus(boolean autoFocusSuccess, Camera arg1) {
			//Wait.oneSec();
			mAutoFocus = true;
		}};
		
		// with this I get the ratio between screen size and pixels
		// of the image so I can capture only the rectangular area of the
		// image and save it.
		public Double[] getRatio(){
			Size s = mPreview.getCameraParameters().getPreviewSize();
			double heightRatio = (double)s.height/(double)mScreenHeight;
			double widthRatio = (double)s.width/(double)mScreenWidth;
			Double[] ratio = {heightRatio,widthRatio};
			return ratio;
		}

		// I am not using this in this example, but its there if you want
		// to turn on and off the flash.
		/*private OnClickListener flashListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mFlashBoolean){
					mPreview.setFlash(false);
				}
				else{
					mPreview.setFlash(true);
				}
				mFlashBoolean = !mFlashBoolean;
			}

		};*/

		// This method takes the preview image, grabs the rectangular
		// part of the image selected by the bounding box and saves it.
		// A thread is needed to save the picture so not to hold the UI thread.
		private OnClickListener previewListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mAutoFocus){
					mAutoFocus = false;
					//mPreview.setCameraFocus(myAutoFocusCallback);
					Wait.oneSec();
					Thread tGetPic = new Thread( new Runnable() {
						public void run() {
							Double[] ratio = getRatio();
							int left = (int) (ratio[1]*(double)mView.getmLeftTopPosX());
							// 0 is height
							int top = (int) (ratio[0]*(double)mView.getmLeftTopPosY());

							int right = (int)(ratio[1]*(double)mView.getmRightBottomPosX());

							int bottom = (int)(ratio[0]*(double)mView.getmRightBottomPosY());

							
							try {

								mLocation = createImageFile();
								
							} catch (IOException e) {
								// TODO: handle exception
							}
							savePhoto(mPreview.getPic(left,top,right,bottom));
							mAutoFocus = true;
						} 
					});
					tGetPic.start();
				}
				boolean pressed = false;
				if (!mTakePicture.isPressed()){
					pressed = true;
				}
			}	   
		};

		// just to close the app and release resources.
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {

			if (keyCode == KeyEvent.KEYCODE_BACK){
				finish();
			}
			return super.onKeyDown(keyCode, event); 
		}
		
		private boolean savePhoto(Bitmap bm) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(mPath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bm.compress(CompressFormat.JPEG, 100, fos);
			Intent capturedImagePreviewIntent = new Intent(getApplicationContext(), CapturedImagePreview.class);
			capturedImagePreviewIntent.putExtra("imagepathstring", mPath);
			startActivity(capturedImagePreviewIntent);
			
			return true;
		}

		public boolean onInterceptTouchEvent(MotionEvent ev) {
			final int action = ev.getAction();
			boolean intercept = false;
			switch (action) {
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_DOWN:
				float x = ev.getX();
				float y = ev.getY();
				// here we intercept the button press and give it to this 
				// activity so the button press can happen and we can take 
				// a picture.
				if ((x >= rec.left) && (x <= rec.right) && (y>=rec.top) && (y<=rec.bottom)){
					intercept = true;
				}
				break;
			}
			return intercept;
		}

		// mainly used for autofocus to happen when the user takes a picture
		// I also use it to redraw the canvas using the invalidate() method
		// when I need to redraw things.
		public void onSensorChanged(SensorEvent event) {

			if (mInvalidate == true){
				mView.invalidate();
				mInvalidate = false;
			}
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			if (!mInitialized){
				mLastX = x;
				mLastY = y;
				mLastZ = z;
				mInitialized = true;
			}
			float deltaX  = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);

			if (deltaX > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
				mAutoFocus = false;
				mPreview.setCameraFocus(myAutoFocusCallback);
			}
			if (deltaY > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
				mAutoFocus = false;
				mPreview.setCameraFocus(myAutoFocusCallback);
			}
			if (deltaZ > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing) */
				mAutoFocus = false;
				mPreview.setCameraFocus(myAutoFocusCallback);
			}

			mLastX = x;
			mLastY = y;
			mLastZ = z;

		}
		
		
		//@yogesh-creating file to store the image
		
		private File createImageFile() throws IOException
		{

		    String TimeStamp = new SimpleDateFormat("yyyyMMDdd_HHmmss").format(new Date());
		    String ImageFile = "NewsReader" + TimeStamp + "_";
		    //File StorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		    File image = File.createTempFile(ImageFile, ".jpg", StorageDir); 

		    mPath = image.getAbsolutePath();

		    return image;

		}
		
		
		// extra overrides to better understand app lifecycle and assist debugging
		@Override
		protected void onDestroy() {
			super.onDestroy();
			//Log.i(TAG, "onDestroy()");
		}

		@Override
		protected void onPause() {
			super.onPause();
			//Log.i(TAG, "onPause()");
			mSensorManager.unregisterListener(this);
		}

		@Override
		protected void onResume() {
			super.onResume();
			mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
			//Log.i(TAG, "onResume()");
		}

		@Override
		protected void onRestart() {
			super.onRestart();
			//Log.i(TAG, "onRestart()");
		}

		@Override
		protected void onStop() {
			super.onStop();
			//Log.i(TAG, "onStop()");
		}

		@Override
		protected void onStart() {
			super.onStart();
			//Log.i(TAG, "onStart()");
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

}