package com.nhc.nhc_game.view;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nhc.nhc_game.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ImportView extends Activity implements OnClickListener{
	
    private WebView webView;
 
    private final static String CLIENT_ID = "fd146da592b3470eaa9b120ca716c896";
    private final static String CLIENT_SECRET = "21eedbc8dccc414d8caf64d7a0554dff";
    private final static String CALLBACK_URL = "com.example.runkeeperapi://RunKeeperIsCallingBack";
    
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_data);
        
      //Force to login on every launch.
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
 
        webView = (WebView) findViewById(R.id.import_webview);
        //This is important. JavaScript is disabled by default. Enable it.
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.VISIBLE);       
        
        getAuthorizationCode();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	private void getAuthorizationCode() {
        String authorizationUrl = "https://runkeeper.com/apps/authorize?response_type=code&client_id=%s&redirect_uri=%s";
        authorizationUrl = String.format(authorizationUrl, CLIENT_ID, CALLBACK_URL);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(CALLBACK_URL)) {
                    final String authCode = Uri.parse(url).getQueryParameter("code");
                    webView.setVisibility(View.GONE);
                    getAccessToken(authCode);
                    return true;
                }
 
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
 
        webView.loadUrl(authorizationUrl);
    }
	
	private void getAccessToken(String authCode) {
        String accessTokenUrl = "https://runkeeper.com/apps/token?grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s";
        final String finalUrl = String.format(accessTokenUrl, authCode, CLIENT_ID, CLIENT_SECRET, CALLBACK_URL);
 
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
 
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(finalUrl);
 
                    HttpResponse response = client.execute(post);
 
                    String jsonString = EntityUtils.toString(response.getEntity());
                    final JSONObject json = new JSONObject(jsonString);
 
                    String accessToken = json.getString("access_token");
                    getTotalDistance(accessToken);
 
                } catch (Exception e) {
                	Toast.makeText(getApplicationContext(),"Exception occured: " ,Toast.LENGTH_SHORT).show();
                    //displayToast("Exception occured:(");
                    e.printStackTrace();
                    resetUi();
                }
 
            }
        });
 
        networkThread.start();
    }
	
	private void getTotalDistance(String accessToken) {        
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://api.runkeeper.com/records");
            
            get.addHeader("Authorization", "Bearer " + accessToken);
            get.addHeader("Accept", "*/*");
            
            HttpResponse response = client.execute(get);
            
            String jsonString = EntityUtils.toString(response.getEntity());
            JSONArray jsonArray = new JSONArray(jsonString);
            findTotalWalkingDistance(jsonArray);
 
        } catch (Exception e) {
        	Toast.makeText(getApplicationContext(),"Exception occured: " ,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            resetUi();
        }
    }
	
	private void findTotalWalkingDistance(JSONArray arrayOfRecords) {
        try {
            //Each record has activity_type and array of statistics. Traverse to  activity_type = Walking
            for (int ii = 0; ii < arrayOfRecords.length(); ii++) {
                JSONObject statObject = (JSONObject) arrayOfRecords.get(ii);
                if ("Walking".equalsIgnoreCase(statObject.getString("activity_type"))) {
                    //Each activity_type has array of stats, navigate to "Overall" statistic to find the total distance walked.
                    JSONArray walkingStats = statObject.getJSONArray("stats");
                    for (int jj = 0; jj < walkingStats.length(); jj++) {
                        JSONObject iWalkingStat = (JSONObject) walkingStats.get(jj);
                        if ("Overall".equalsIgnoreCase(iWalkingStat.getString("stat_type"))) {
                            long totalWalkingDistanceMeters = iWalkingStat.getLong("value");
                            double totalWalkingDistanceMiles = totalWalkingDistanceMeters * 0.00062137;
                            displayTotalWalkingDistance(totalWalkingDistanceMiles);
                            return;
                        }
                    }
                }
            }
            Toast.makeText(getApplicationContext(), "Something went wrong!!!",Toast.LENGTH_SHORT).show();
            //displayToast("Something went wrong!!!");
        } catch (JSONException e) {
        	Toast.makeText(getApplicationContext(),"Exception occured: " ,Toast.LENGTH_SHORT).show();
            //displayToast("Exception occured:(");            
            e.printStackTrace();
            resetUi();
        }
    }
	
	private void resetUi(){
        
    }    
    
    private void displayTotalWalkingDistance(double totalWalkingDistanceMiles) {
        final String milesWalkedMessage = (totalWalkingDistanceMiles < 1) ? "0 miles?, You get no respect, Start walking already!!!" : 
            String.format("Cool, You have walked %.2f miles so far.", totalWalkingDistanceMiles);            
        
        Toast.makeText(getApplicationContext(),milesWalkedMessage ,Toast.LENGTH_SHORT).show();
        resetUi();
    }
 /*
    private void displayToast(final String message) {
        runOnUiThread(new Runnable() {
 
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
	*/
	
}
