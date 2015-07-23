package jp.classmethod.android.sample.volley;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.classmethod.android.sample.volley.BitmapCache;
import jp.classmethod.android.sample.volley.GsonRequest;
import jp.classmethod.android.sample.volley.ImageLoadingActivity;
import jp.classmethod.android.sample.volley.JSONLoader;
import jp.classmethod.android.sample.volley.Weather;
import jp.classmethod.android.sample.volley.WeatherInfo;
import jp.classmethod.android.sample.volley.XMLRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	private final MainActivity self = this;
	private RequestQueue mQueue;
	private long requestStartTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mQueue = Volley.newRequestQueue(getApplicationContext());

		findViewById(R.id.volley).setOnClickListener(this);
		findViewById(R.id.http_client).setOnClickListener(this);
		findViewById(R.id.image_loading).setOnClickListener(this);
		findViewById(R.id.load_image).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.volley:
			// requestVolley();
			// stringRequest()
			// jsonObjectRequest();

			// jsonJectRequest1();
			//xmlRequest();
			gsonRequest();
			break;
		case R.id.http_client:
			requestHttpClient();
			break;
		case R.id.image_loading:
			startActivity(new Intent(self, ImageLoadingActivity.class));
			break;
		case R.id.load_image:
			loadNetworkImageView();
			break;
		}
	}

	private void requestVolley() {
		// Volley でリクエスト
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
		mQueue.add(new JsonObjectRequest(Method.GET, url, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						long time = System.currentTimeMillis()
								- requestStartTime;
						Log.d(TAG, "Volley Request finished : " + time);
					}
				}, null));
		requestStartTime = System.currentTimeMillis();
		mQueue.start();
	}

	private void requestHttpClient() {
		// HttpClient でリクエスト
		getSupportLoaderManager().initLoader(0, null,
				new LoaderCallbacks<JSONObject>() {
					@Override
					public Loader<JSONObject> onCreateLoader(int id,
							Bundle bundle) {
						requestStartTime = System.currentTimeMillis();
						return new JSONLoader(getApplicationContext());
					}

					@Override
					public void onLoadFinished(Loader<JSONObject> loader,
							JSONObject result) {
						long time = System.currentTimeMillis()
								- requestStartTime;
						Log.d(TAG, "HttpClient Request finished : " + time);
						getSupportLoaderManager().destroyLoader(0);
					}

					@Override
					public void onLoaderReset(Loader<JSONObject> loader) {
					}
				});
	}

	private void loadNetworkImageView() {
		String url = "http://dev.classmethod.jp/wp-content/uploads/2013/04/android_eyecatch.png";
		NetworkImageView view = (NetworkImageView) findViewById(R.id.network_image_view);
		view.setImageUrl(null, null);
		view.setImageUrl(url, new ImageLoader(mQueue, new BitmapCache()));
	}

	private void stringRequest() {

		Log.d(TAG, "visitBaidu");
		StringRequest stringRequest = new StringRequest(
				"https://www.baidu.com", new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage(), error);
					}
				});

		mQueue.add(stringRequest);
	}

	private void jsonObjectRequest() {

		Log.d(TAG, "visitweather");
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				"http://m.weather.com.cn/mweather/101010100.shtml", null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "jsonobject=" + response);

						objectToString(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Log.e("TAG", error.getMessage(), error);
					}
				});
		mQueue.add(jsonObjectRequest);
	}

	private void jsonJectRequest1() {
		StringRequest stringRequest = new StringRequest(Method.POST,
				"http://m.weather.com.cn/mweather/101010100.shtml",
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("params1", "value1");
				map.put("params2", "value2");
				return map;
			}
		};
		mQueue.add(stringRequest);
	}

	private int objectToString(JSONObject obj) {
		JSONArray newArray = new JSONArray();
		JSONObject newJson = new JSONObject();

		try {
			Iterator it = obj.keys();

			while (it.hasNext()) {
				String key = (String) it.next();
				String value = obj.getString(key);
				JSONArray array = obj.getJSONArray(key);
				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonobject = array.getJSONObject(i);
					jsonobject.put("name", key);
					jsonobject.put("exp",
							key + "=" + jsonobject.getString("value"));
					newArray.put(jsonobject);
				}
			}
			newJson.put("groups", newArray);
			System.out.println(newJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private int xmlRequest() {
		XMLRequest xmlRequest = new XMLRequest(
				"http://flash.weather.com.cn/wmaps/xml/china.xml",
				new Response.Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						try {
							int eventType = response.getEventType();
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {
								case XmlPullParser.START_TAG:
									String nodeName = response.getName();
									if ("city".equals(nodeName)) {
										String pName = response
												.getAttributeValue(0);
										Log.d(TAG, "pName is " + pName);
									}
									break;
								}
								eventType = response.next();
							}
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage(), error);
					}
				});
		mQueue.add(xmlRequest);

		return 0;
	}

	private int gsonRequest() {
		GsonRequest<Weather> gsonRequest = new GsonRequest<Weather>(
				"http://www.weather.com.cn/data/sk/101010100.html",
				Weather.class, new Response.Listener<Weather>() {
					@Override
					public void onResponse(Weather weather) {
						WeatherInfo weatherInfo = weather.getWeatherinfo();
						Log.d(TAG, "city is " + weatherInfo.getCity());
						Log.d(TAG, "temp is " + weatherInfo.getTemp());
						Log.d(TAG, "time is " + weatherInfo.getTime());
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
					}
				});
		mQueue.add(gsonRequest);
		return 0;
	}
}
