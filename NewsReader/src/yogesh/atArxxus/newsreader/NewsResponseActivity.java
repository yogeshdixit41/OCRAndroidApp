package yogesh.atArxxus.newsreader;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ArrowKeyMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;


public class NewsResponseActivity extends Activity implements TextToSpeech.OnInitListener {

	TextView singleNewsTextView;
	Button readNewsForMeButton;
	Button stopReadingButton;
	TextToSpeech tts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_response);
		
		readNewsForMeButton = (Button) findViewById(R.id.readNewsForMeButton);
		stopReadingButton = (Button) findViewById(R.id.stopReading);
		tts = new TextToSpeech(this, this);
		singleNewsTextView = (TextView) findViewById(R.id.singleNewsView);
		
		singleNewsTextView.setMovementMethod(ArrowKeyMovementMethod.getInstance());
		Intent getNews = getIntent();
		String news=getNews.getStringExtra("news");
		singleNewsTextView.setText(news);
		
		OnClickListener readNewsForMeOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String newsToRead = singleNewsTextView.getText().toString();
				if(!newsToRead.equals(null))
				{
					tts.speak(newsToRead, TextToSpeech.QUEUE_FLUSH, null);
				}
			}
		};
		readNewsForMeButton.setOnClickListener(readNewsForMeOnClickListener);
		
		
		
		OnClickListener stopReadingNewsListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tts.stop();
				
			}
		};
		stopReadingButton.setOnClickListener(stopReadingNewsListener);
	}
	@Override
	public void onInit(int status) {
		tts.setLanguage(Locale.getDefault());
		tts.setSpeechRate((float) 0.5);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		tts.shutdown();
	}

}
