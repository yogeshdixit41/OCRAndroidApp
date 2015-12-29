package yogesh.atArxxus.newsreader;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;






import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class NewsResponseListViewActivity extends Activity {

	JSONObject newsJsonObject;
	HashMap<String, String> newsHashMap = new HashMap<String, String>();
	ArrayList<String> newsArrayList = new ArrayList<String>();
	ListView newsListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_response_list_view);
		
		
		newsListView = (ListView) findViewById(R.id.NewsList);
		Intent i = getIntent();
		ArrayList<String> newsInJson= i.getStringArrayListExtra("data");
		
		try {
				for(String eachNews:newsInJson)
				{
					newsJsonObject = new JSONObject(eachNews);
					JSONArray jsonMainNode = newsJsonObject.optJSONArray("item");
					int jsonArrLen = jsonMainNode.length();
			
					for(int j=0;j<jsonArrLen;j++)
					{
						JSONObject childJsonObject = jsonMainNode.getJSONObject(j);
				
						String news = childJsonObject.getString("body");//optString("body").toString();
				
						if(news.equalsIgnoreCase("no news found"))
						{
							Intent next_activity = new Intent(getApplicationContext(), NewsResponseActivity.class);
							next_activity.putExtra("news", "No news found please search again");
							startActivity(next_activity);
					
						}
				
				//newsMap[j] = news;
				//Toast.makeText(getApplicationContext(), news, Toast.LENGTH_LONG).show();
						else
						{
							if(news.equalsIgnoreCase(""))
							{
								continue;
							}
							String news_headline = news.substring(0,news.indexOf("."));
							news_headline = "NEWS:"+ " " + news_headline;
							newsArrayList.add(news_headline);
							newsHashMap.put(news_headline, news);
						}
					}
				}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, newsArrayList);
		
		
		newsListView.setAdapter(adapter);
		
		newsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
				String selectedHeadline = newsListView.getItemAtPosition(position).toString();
				String final_news = newsHashMap.get(selectedHeadline);
				Intent next_activity = new Intent(getApplicationContext(), NewsResponseActivity.class);
				next_activity.putExtra("news", final_news);
				startActivity(next_activity);
				
			}
			

			
		});
		
		
	}


}
