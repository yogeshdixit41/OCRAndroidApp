package yogesh.atArxxus.newsreader;

import java.io.File;
import java.io.IOException;

import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.JpegIO;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.tesseract.android.TessBaseAPI;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CapturedImagePreview extends Activity{

	
	
	static private ImageView capturedImageView;
	static String mPathString; 
	DisplayMetrics displaymetrics;
	Button proceed_to_get_result;
	Button homeButton;
	Button recapture_image;
	String recognizedText = null;
	
	
	Bitmap image_preview,enhanced_bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_captured_image_preview);
		
		proceed_to_get_result = (Button) findViewById(R.id.accept);
		recapture_image = (Button) findViewById(R.id.retry);
		capturedImageView = (ImageView) findViewById(R.id.capturedImageView);
		homeButton = (Button) findViewById(R.id.homeButton);
		
		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		Intent i = getIntent();
		mPathString = i.getStringExtra("imagepathstring");

		//		Toast.makeText(getApplicationContext(), mPathString, Toast.LENGTH_LONG).show();

		
		try {
			
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inDither = false;
			o.inScaled = false;
			o.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			image_preview = BitmapFactory.decodeFile(mPathString, o);
			File image_file_on_sdcard = new File(mPathString);
			image_file_on_sdcard.delete();
			capturedImageView.setImageBitmap(image_preview);
			//Toast.makeText(getApplicationContext(), "calling tess scanning", Toast.LENGTH_SHORT).show();
			
			} catch (Exception e) {
			// TODO: handle exception
			}
		
		
		/*onClickListener for to proceed to results*/
		OnClickListener proceed_toResult_listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myTessScanning scanImage = new myTessScanning();
				scanImage.execute();
			}
		};
		proceed_to_get_result.setOnClickListener(proceed_toResult_listener);
		
		
		/*onClickListener for to recapture image*/
		OnClickListener rcaptureImageListener  = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent cameraPreviewIntent = new Intent(getApplicationContext(), CameraPreview.class);
				cameraPreviewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(cameraPreviewIntent);
				
			}
		};
		recapture_image.setOnClickListener(rcaptureImageListener);
		
		/*onClickListener for home buton*/
		OnClickListener homeButtoClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent homeScreen = new Intent(getApplicationContext(), FirstActivity.class);
				startActivity(homeScreen);
				
			}
		};
		homeButton.setOnClickListener(homeButtoClickListener);
		}
		
	
	private class myTessScanning extends AsyncTask<Void, Void, String>{

		private ProgressDialog imageScanningProgressDialog = new ProgressDialog(CapturedImagePreview.this);
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			final String final_recognized_text=myTessScanning();
			
			
			return final_recognized_text;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			imageScanningProgressDialog.dismiss();
			Intent resultViewActivityIntent = new Intent(getApplicationContext(), ResultViewActivity.class);
			resultViewActivityIntent.putExtra("scanned_text", result);
			startActivity(resultViewActivityIntent);
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			imageScanningProgressDialog.setCancelable(false);
			imageScanningProgressDialog.setCanceledOnTouchOutside(false);
			imageScanningProgressDialog.setTitle("Please wait !");
			imageScanningProgressDialog.setMessage("Scanning image...");
			imageScanningProgressDialog.show();
		}
		
		
		
		
	}
	//@yogesh tesseract method to scan the image for text and return the recognized text
	public String myTessScanning(){
		
		
		//Toast.makeText(getApplicationContext(), "in myTessScanning", Toast.LENGTH_SHORT).show();
		    /*Bitmap monochrome_bitmap = Bitmap.createBitmap(image_preview.getWidth(),image_preview.getHeight(),Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(monochrome_bitmap);
			ColorMatrix ma = new ColorMatrix();
			ma.setSaturation(0); // setting matrix saturation to grey scale
			Paint paint = new Paint();
			paint.setColorFilter(new ColorMatrixColorFilter(ma));
			canvas.drawBitmap(image_preview, 0, 0, paint);
			image_preview = Bitmap.createScaledBitmap(image_preview, 860, 490, false);*/
			
			Pix bitmaptopix = ReadFile.readBitmap(image_preview);
			Pix greyscale_pix = Convert.convertTo8(bitmaptopix);
			enhanced_bitmap = enhance(greyscale_pix,displaymetrics.widthPixels,false);
			//Toast.makeText(getApplicationContext(), "after enhancement", Toast.LENGTH_SHORT).show();
			
			TessBaseAPI baseApi = new TessBaseAPI();
			baseApi.init(SplashScreen.DATA_PATH,SplashScreen.lang, TessBaseAPI.OEM_TESSERACT_ONLY);
			baseApi.setImage(enhanced_bitmap);
			recognizedText = baseApi.getUTF8Text();
			baseApi.end();
			return recognizedText;
			//Toast.makeText(getApplicationContext(), recognizedText + "reached final", Toast.LENGTH_LONG).show();
			//System.out.println(recognizedText);
		
	}
	
	
	
	
	public static Bitmap enhance(Pix captured,int dev_width,boolean isCropped)
	{
		Bitmap enhanced_bitmap = null;
		Pix thresholded_image = null;
		
		//Bitmap resized = Bitmap.createScaledBitmap(captured,860,490,true);
	
		//Pix orignal_image = ReadFile.readBitmap(captured);
		
		Pix normalized_image = AdaptiveMap.backgroundNormMorph(captured);
		
		
		
		if(dev_width == 320)
		{
			if(!isCropped)
			{
				thresholded_image= Binarize.otsuAdaptiveThreshold(normalized_image,32,32,8,8,0.0f);
				System.out.println("processed for 320 no crop");
			}
			else
			{
				thresholded_image= Binarize.otsuAdaptiveThreshold(normalized_image,32,32,8,8,0.0f);
				System.out.println("processed for 320 with crop");
			}
		}
		else 
		{
			//thresholded_image = Binarize.otsuAdaptiveThreshold(normalized_image,102,102,10,10,0.0f);
			if(!isCropped)
			{
				thresholded_image = Binarize.otsuAdaptiveThreshold(normalized_image,300,300,8,8,0.0f);
				System.out.println("processed for 480 or more with no crop");
			}
			else
			{
				thresholded_image = Binarize.otsuAdaptiveThreshold(normalized_image,300,300,8,8,0.0f);
				System.out.println("processed for 480 or more with crop");
			}
		}
		
		float skew_angle = Skew.findSkew(thresholded_image,90.0f,5.0f,8,4,0.01f);
		
        if(!(skew_angle > 2.0f || skew_angle < -2.0f))
        {
        	byte[] data = JpegIO.compressToJpeg(thresholded_image,100,false);
        	enhanced_bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        }
        else
        {
        	Pix rotated_image = Rotate.rotate(thresholded_image,skew_angle);

        	System.out.println("In Skew");
        	byte[] data = JpegIO.compressToJpeg(rotated_image,100,false);
        	enhanced_bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        }
		if(enhanced_bitmap!=null)
			return enhanced_bitmap;
		return null;
	}
	


}
