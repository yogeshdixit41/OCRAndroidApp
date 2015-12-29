package yogesh.atArxxus.newsreader;



import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResultViewActivity extends FragmentActivity{

	EditText resultTextView;
	String resultText;
	String finalResultText;
	String ignore_chars[] = {"'",";","{","}","[","]","(",")",":","*","$","|","^","#","!",">","<","`","~","\"","?","%","=","-","_"};
	String webServiceInputString = "";
	Button getNewsButton;
	String newsHeadline;
	Button homeButton;
	String newsPaperSelected;
	Button selectNewsPaper;
	NewsPaperListDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_view);
		

		
		// To listen to data connectivity
//	    final DataConnectionState checkState = new DataConnectionState();
//		TelephonyManager t = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//		t.listen(checkState, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		

		
		homeButton = (Button) findViewById(R.id.homeButton);
		resultTextView = (EditText) findViewById(R.id.resultEditText);
		selectNewsPaper = (Button) findViewById(R.id.selectNewsPaper);
		dialog = new NewsPaperListDialog();
		 
		//Spinner newsPaperNamesSpinner = (Spinner)findViewById(R.id.newsPaperNamesSpinner);
		
//		OnItemSelectedListener newsPaperNameItemSelectedListener = new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> spinner, View view,
//					int position, long id) {
//				 newsPaperSelected = spinner.getItemAtPosition(position).toString();
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> spinner) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
		
		//newsPaperNamesSpinner.setOnItemSelectedListener(newsPaperNameItemSelectedListener);
		
		
		OnClickListener slectNewsPaperListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dialog.show(getSupportFragmentManager(), "news paper list");
			}
		};
		selectNewsPaper.setOnClickListener(slectNewsPaperListener);

		Intent i = getIntent();
		resultText=i.getStringExtra("scanned_text");
		
		if(resultText != null)
		{
			String resultTextLines[] = resultText.split("\n");
			
			for(int j=0;j<resultTextLines.length;j++)
			{
				for(int k=0;k<ignore_chars.length;k++)
				{
					
					if(resultTextLines[j].contains(ignore_chars[k]))
						resultTextLines[j]=resultTextLines[j].replace(ignore_chars[k], "");
					
				}	
			}
			
			resultText = "";
			
			for(int j=0;j<resultTextLines.length;j++)
			{
				webServiceInputString = webServiceInputString + resultTextLines[j] + " "; 
				resultText =resultText + resultTextLines[j] + "\n";
			}
			resultTextView.setText("");
			resultTextView.setText(webServiceInputString);
		}
		
		
		getNewsButton = (Button) findViewById(R.id.getNews);
		OnClickListener getNewsClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				newsHeadline = resultTextView.getText().toString();
				
				if(newsHeadline.length() == 0)
				{
					Toast.makeText(getApplicationContext(), "Please enter the headline", Toast.LENGTH_SHORT).show();
				}
				else
				{
				newsHeadline = newsHeadline.replace("\n", " ");
				newsHeadline = newsHeadline.replace(" ","%20");
				//String[] myTrial = {"y","u"};
				
				ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetorkInfo = cm.getActiveNetworkInfo();
				boolean isDataConnected = activeNetorkInfo!=null && activeNetorkInfo.isConnected();
				
				
				ArrayList<String> newsPaperList = dialog.getSelectedNewsPaper();
				ArrayList<String> selectedNewsPaperUrl = new ArrayList<String>();
				for(String newsPaper:newsPaperList)
				{
					if(newsPaper.equalsIgnoreCase("Times Of India"))
					{
						selectedNewsPaperUrl.add("http://protected-hollows-7036.herokuapp.com/toi/" + newsHeadline);
					}
					else if(newsPaper.equalsIgnoreCase("Hindustan Times"))
					{
						selectedNewsPaperUrl.add("http://protected-hollows-7036.herokuapp.com/ht/" + newsHeadline);
					}
				}
				if(selectedNewsPaperUrl.isEmpty())
				{
					Toast.makeText(getApplicationContext(), "Please select news paper first and then proceed", Toast.LENGTH_SHORT).show();
				}
				else if(isDataConnected)
				{
					
					Log.e("url", ""+selectedNewsPaperUrl);
					HttpGetter get = new HttpGetter();
					get.execute(selectedNewsPaperUrl);
				}
				else if(!isDataConnected) 
				{
					
					Toast.makeText(getApplicationContext(), "Please check data connection", Toast.LENGTH_SHORT).show();
				}

			  }
			}
		};
		
		getNewsButton.setOnClickListener(getNewsClickListener);
		
		
		OnClickListener homeButtonClickListener = new OnClickListener() {
			
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent firstActivity = new Intent(getApplicationContext(), FirstActivity.class);
				firstActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(firstActivity);
				
			}
		};
		
		homeButton.setOnClickListener(homeButtonClickListener);
		
	}


	
	
	private class HttpGetter extends AsyncTask<ArrayList<String>, Void , ArrayList<String>> {

		private ProgressDialog inProgressDialog = new ProgressDialog(ResultViewActivity.this);
		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... urls) {

			try {
				
				ArrayList<String> stringData = new ArrayList<String>();
				
				for(String url:urls[0])
				{
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url);
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String getResponseString = httpClient.execute(httpGet,responseHandler);
					stringData.add(getResponseString);
					
				}

				return stringData;

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
			
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			inProgressDialog.dismiss();
			Intent next = new Intent(getApplicationContext(), NewsResponseListViewActivity.class);
			next.putStringArrayListExtra("data", result);
			startActivity(next);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			inProgressDialog.setTitle("Please Wait !");
			inProgressDialog.setMessage("Loading news...");
			inProgressDialog.show();
			inProgressDialog.setCanceledOnTouchOutside(false);
			inProgressDialog.setCancelable(false);
			
		}
		
		
	}




/*	@Override
	public void onItemSelected(AdapterView<?> spinner, View view, int pos,
			long id) {
		
		 newsPaperSelected = spinner.getItemAtPosition(pos).toString();
	}


	@Override
	public void onNothingSelected(AdapterView<?> spinner) {
		
		newsPaperSelected = "All";
	}*/

}
