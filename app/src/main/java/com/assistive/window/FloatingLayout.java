package com.assistive.window;
import android.app.*;
import android.os.*;
import android.content.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.media.*;
import android.net.*;
import android.bluetooth.*;
import android.widget.CompoundButton.*;
import android.graphics.drawable.*;
import android.text.*;
import android.widget.SeekBar.*;
import android.view.animation.*;
import android.view.inputmethod.*;
import android.provider.*;
import android.location.*;
import org.w3c.dom.*;
import java.net.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import java.util.*;
import android.icu.text.*;

public class FloatingLayout extends Service
{
	
	private WindowManager windowManager;
	private View view;
	private ViewGroup parentView, childView;
	private Animation left_in, left_out, right_in, right_out;
	private Intent intent;
	private int ringtone_value, media_value, alarm_value;
	private String str;
	private SharedPreferences sp;
	private double latitude, longitude;
	private String provider, page, temp, weather, str_temp, str_weather, str_date, getTime;
	private TextView weather_info, tv_temp, tv_weather, update_date;
	
	int count;
	
	private Boolean isNetWork() { //network state
		ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
		boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
		boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

		if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void onCreate()
	{
		sp = getSharedPreferences("setting", 0);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
		WindowManager.LayoutParams.MATCH_PARENT,
		WindowManager.LayoutParams.MATCH_PARENT,
		WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
		WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
			PixelFormat.TRANSLUCENT);
		
		if(sp.getInt("fv_location", 0) == 0) {
			params.gravity = Gravity.CENTER | Gravity.LEFT;
			view = inflater.inflate(R.layout.floating_layout_left, null); //FloatingView Position Left
		}
		else if(sp.getInt("fv_location", 0) == 1) {
			params.gravity = Gravity.CENTER | Gravity.RIGHT;
			view = inflater.inflate(R.layout.floating_layout_right, null); //FloatingView Position Right
		}
		parentView = (FrameLayout)view.findViewById(R.id.parentview);
		childView = (LinearLayout)view.findViewById(R.id.floating_layout);
			
		windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(parentView, params);
		if(sp.getInt("fv_location", 0) == 0) {
			left_in = AnimationUtils.loadAnimation(this, R.anim.translate_left_in);
			childView.startAnimation(left_in);
		}else if(sp.getInt("fv_location", 0) == 1) {
			right_in = AnimationUtils.loadAnimation(this, R.anim.translate_right_in);
			childView.startAnimation(right_in);
		}
		
		final EditText searchtext = (EditText)view.findViewById(R.id.search_edittext);// search edittext
		searchtext.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
					if(v.getId() == R.id.search_edittext && actionId == EditorInfo.IME_ACTION_SEARCH) {
						
						String text = searchtext.getText().toString();
						switch(sp.getInt("search", 0)) {

							case 0 :
								intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q="+text));
								break;
							case 1 :
								intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.bing.com/search?q="+text));
								break;
							case 2 :
								intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://search.yahoo.com/mobile/s?p="+text));
								break;
							case 3 :
								intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://m.search.naver.com/search.naver?query="+text));
								break;
							case 4 :
								intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://m.search.daum.net/search?nil_profile=simpleurl&w=tot&q="+text));
								break;
							case 5 :
								intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://m.search.zum.com/search.zum?method=uni&option=accu&qm=f_typing.top&query="+text));
								break;
							
						}
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Be able to startActivity from outside of an Activity
						startActivity(intent);
						stopSelf();
					}
					return false;
				}
		});
		
		final AudioManager audiomanager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		//Volume Control
		final SeekBar ringtone = (SeekBar)view.findViewById(R.id.volume_ringer); // Ringtone Volume Control
		int ringtone_max = audiomanager.getStreamMaxVolume(AudioManager.STREAM_RING);
		int ringtone_current = audiomanager.getStreamVolume(AudioManager.STREAM_RING);
		ringtone.setMax(ringtone_max);
		ringtone.setProgress(ringtone_current);
		ringtone.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar p1, final int p2, boolean p3)
				{
					audiomanager.setStreamVolume(AudioManager.STREAM_RING, p2, 0);
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}
		});
		Button ringtone_down = (Button)view.findViewById(R.id.ringer_down);
		ringtone_down.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					ringtone_value = ringtone.getProgress();
					ringtone_value--;
					ringtone.setProgress(ringtone_value);
				}
		});
		Button ringtone_up = (Button)view.findViewById(R.id.ringer_up);
		ringtone_up.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					ringtone_value = ringtone.getProgress();
					ringtone_value++;
					ringtone.setProgress(ringtone_value);
				}
		});
		
		final SeekBar media = (SeekBar)view.findViewById(R.id.volume_media); //Media Volume Control
		int media_max = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int media_current = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
		media.setMax(media_max);
		media.setProgress(media_current);
		media.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3)
				{
					audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, p2, 0);
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}
		});
		Button media_down = (Button)view.findViewById(R.id.media_down);
		media_down.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					media_value = media.getProgress();
					media_value--;
					media.setProgress(media_value);
				}
		});
		Button media_up = (Button)view.findViewById(R.id.media_up);
		media_up.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					media_value = media.getProgress();
					media_value++;
					media.setProgress(media_value);
				}
		});
		
		final SeekBar alarm = (SeekBar)view.findViewById(R.id.volume_alarm); // Alarm Volume Control
		int alarm_max = audiomanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		int alarm_current = audiomanager.getStreamVolume(AudioManager.STREAM_ALARM);
		alarm.setMax(alarm_max);
		alarm.setProgress(alarm_current);
		alarm.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3)
				{
					audiomanager.setStreamVolume(AudioManager.STREAM_ALARM, p2, 0);
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}
		});
		Button alarm_down = (Button)view.findViewById(R.id.alarm_down);
		alarm_down.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					alarm_value = alarm.getProgress();
					alarm_value--;
					alarm.setProgress(alarm_value);
				}
		});
		Button alarm_up = (Button)view.findViewById(R.id.alarm_up);
		alarm_up.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					alarm_value = alarm.getProgress();
					alarm_value++;
					alarm.setProgress(alarm_value);
				}
		});
		Button memo = (Button)view.findViewById(R.id.btn_memo); //Button Simple Memo
		memo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					stopSelf();
					startService(new Intent(getApplicationContext(), SimpleMemo.class));
				}
			});
			
		SharedPreferences pref_memo = getSharedPreferences("memopref", 0); //Memo preview sharedpreference
		TextView memopreview = (TextView)view.findViewById(R.id.memo_preview);
		str = pref_memo.getString("memo", str);
		memopreview.setText(str);
		if(memopreview.getText().toString().length() == 0) {
			memopreview.setText(getResources().getString(R.string.simple_memo_preview));
		}
		
		SharedPreferences pref_weather = getSharedPreferences("weather_pref", 0);
		tv_temp = (TextView)view.findViewById(R.id.temp);
		tv_weather = (TextView)view.findViewById(R.id.weather);
		weather_info = (TextView)view.findViewById(R.id.weather_info);
		update_date = (TextView)view.findViewById(R.id.date);
		
		str_temp = pref_weather.getString("temp", str_temp);
		str_weather = pref_weather.getString("weather", str_weather);
		str_date = pref_weather.getString("date", str_date);
		tv_temp.setText(str_temp);
		tv_weather.setText(str_weather);
		update_date.setText(str_date);
		
		if(tv_temp.getText().toString().length() == 0 && tv_weather.getText().toString().length() == 0) {
			weather_info.setText(getString(R.string.weather_info));
			update_date.setText("");
		}
		
		LinearLayout update_weather = (LinearLayout)view.findViewById(R.id.fl_update);
		update_weather.setOnClickListener(new View.OnClickListener() { //weather update onclick

				@Override
				public void onClick(View p1)
				{
					final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
					
					if(isNetWork() == true && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						weather_info.setText(R.string.weather_loading);
						tv_temp.setText("");
						tv_weather.setText("");
						update_date.setText("");
						temp = "";
						weather = "";
						count = 0;

						lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
												  100, // 통지사이의 최소 시간간격 (miliSecond)
												  1, // 통지사이의 최소 변경거리 (m)
												  mLocationListener);
						lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
												  100, // 통지사이의 최소 시간간격 (miliSecond)
												  1, // 통지사이의 최소 변경거리 (m)
												  mLocationListener);
					}
					else {
						weather_info.setText(R.string.weather_check);
						tv_temp.setText("");
						tv_weather.setText("");
						update_date.setText("");
						temp = "";
						weather = "";
					}
				}
		});
		
		final Button close = (Button)view.findViewById(R.id.close); //close button
		close.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					if(sp.getInt("fv_location", 0) == 0) {
						left_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left_out);
						childView.startAnimation(left_out);
					}else if(sp.getInt("fv_location", 0) == 1) {
						right_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right_out);
						childView.startAnimation(right_out);
					}
					new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								if(view != null) {
									windowManager.removeView(view);
									view = null;
								}
								stopSelf();
							}
						}, 400);
					close.setClickable(false);
				}
			});
			
		final Button close_outside = (Button)view.findViewById(R.id.close_outside); //close button(outside)
		close_outside.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					if(sp.getInt("fv_location", 0) == 0) {
						left_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left_out);
						childView.startAnimation(left_out);
					}else if(sp.getInt("fv_location", 0) == 1) {
						right_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right_out);
						childView.startAnimation(right_out);
					}
					new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								if(view != null) {
									windowManager.removeView(view);
									view = null;
								}
								stopSelf();
							}
						}, 400);
					close_outside.setClickable(false);
				}
			});
		
		super.onCreate();
	}
	
	private final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location)
		{
			longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            /*
			double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
			*/
            provider = location.getProvider();   //위치제공자
			//Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
			//Network 위치제공자에 의한 위치변화
			//Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
			
			page = "http://api.wunderground.com/api/b6671f0269c4e83e/conditions/forecast/alert/q/" + latitude + "," + longitude + ".xml";

			if(count == 0) {
				if(provider != null) {
					GetXMLTask parseTask = new GetXMLTask();
					parseTask.execute(page);
				}
			}
		}

		@Override
		public void onStatusChanged(String p1, int p2, Bundle p3)
		{
			// TODO: Implement this method
		}

		@Override
		public void onProviderEnabled(String p1)
		{
			// TODO: Implement this method
		}

		@Override
		public void onProviderDisabled(String p1)
		{
			// TODO: Implement this method
		}
	};
	
	private class GetXMLTask extends AsyncTask<String, Void, Document>
	{

		private Document doc;

		@Override
		protected Document doInBackground(String... urls) {
			URL url;
			try {
				url = new URL(page);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder(); 
				doc = db.parse(new InputSource(url.openStream()));
				doc.getDocumentElement().normalize();
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
			}
			return doc;
		}

		@Override
		protected void onPostExecute(Document doc) {
			NodeList nodeList = doc.getElementsByTagName("current_observation"); 
			for(int i = 0; i< nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				Element fstElmnt = (Element) node;

				NodeList node_temp = fstElmnt.getElementsByTagName("temp_c");
				temp += node_temp.item(0).getChildNodes().item(0).getNodeValue() + "°C";

				NodeList node_weather = fstElmnt.getElementsByTagName("weather");
				weather += node_weather.item(0).getChildNodes().item(0).getNodeValue();

			}
			
			Locale locale = getResources().getConfiguration().locale;
			final String language =  locale.getLanguage();
			if(language.equals("ko")) {
				if(weather.equals("Clear")) {
					weather = "맑음";
				}
				if(weather.equals("Drizzle")) {
					weather = "이슬비";
				}
				if(weather.equals("Light Drizzle")) {
					weather = "이슬비";
				}
				if(weather.equals("Heavy Drizzle")) {
					weather = "이슬비";
				}
				if(weather.equals("Rain")) {
					weather = "비";
				}
				if(weather.equals("Light Rain")) {
					weather = "비";
				}
				if(weather.equals("Heavy Rain")) {
					weather = "비";
				}
				if(weather.equals("Snow")) {
					weather = "눈";
				}
				if(weather.equals("Light Snow")) {
					weather = "눈";
				}
				if(weather.equals("Heavy Snow")) {
					weather = "눈";
				}
				if(weather.equals("Snow Grains")) {
					weather = "쌀알눈";
				}
				if(weather.equals("Light Snow Grains")) {
					weather = "쌀알눈";
				}
				if(weather.equals("Heavy Snow Grains")) {
					weather = "쌀알눈";
				}
				if(weather.equals("Ice Crystals")) {
					weather = "빙정";
				}
				if(weather.equals("Light Ice Crystals")) {
					weather = "빙정";
				}
				if(weather.equals("Heavy Ice Crystals")) {
					weather = "빙정";
				}
				if(weather.equals("Ice Pellets")) {
					weather = "싸라기눈";
				}
				if(weather.equals("Light Ice Pellets")) {
					weather = "싸라기눈";
				}
				if(weather.equals("Heavy Ice Pellets")) {
					weather = "싸라기눈";
				}
				if(weather.equals("Hail")) {
					weather = "우박";
				}
				if(weather.equals("Light Hail")) {
					weather = "우박";
				}
				if(weather.equals("Heavy Hail")) {
					weather = "우박";
				}
				if(weather.equals("Mist")) {
					weather = "엷은 안개";
				}
				if(weather.equals("Light Mist")) {
					weather = "엷은 안개";
				}
				if(weather.equals("Heavy Mist")) {
					weather = "엷은 안개";
				}
				if(weather.equals("Fog")) {
					weather = "안개";
				}
				if(weather.equals("Light Fog")) {
					weather = "안개";
				}
				if(weather.equals("Heavy Fog")) {
					weather = "안개";
				}
				if(weather.equals("Fog Patches")) {
					weather = "안개";
				}
				if(weather.equals("Light Fog Patches")) {
					weather = "안개";
				}
				if(weather.equals("Heavy Fog Patches")) {
					weather = "안개";
				}
				if(weather.equals("Smoke")) {
					weather = "연기";
				}
				if(weather.equals("Light Smoke")) {
					weather = "연기";
				}
				if(weather.equals("Heavy Smoke")) {
					weather = "연기";
				}
				if(weather.equals("Volcanic Ash")) {
					weather = "화산재";
				}
				if(weather.equals("Light Volcanic Ash")) {
					weather = "화산재";
				}
				if(weather.equals("Heavy Volcanic Ash")) {
					weather = "화산재";
				}
				if(weather.equals("Widespread Dust")) {
					weather = "흙 먼지";
				}
				if(weather.equals("Light Widespread Dust")) {
					weather = "흙 먼지";
				}
				if(weather.equals("Heavy Widespread Dust")) {
					weather = "흙 먼지";
				}
				if(weather.equals("Sand")) {
					weather = "모래";
				}
				if(weather.equals("Light Sand")) {
					weather = "모래";
				}
				if(weather.equals("Heavy Sand")) {
					weather = "모래";
				}
				if(weather.equals("Haze")) {
					weather = "실안개";
				}
				if(weather.equals("Light Haze")) {
					weather = "실안개";
				}
				if(weather.equals("Heavy Haze")) {
					weather = "실안개";
				}
				if(weather.equals("Spray")) {
					weather = "비말";
				}
				if(weather.equals("Light Spray")) {
					weather = "비말";
				}
				if(weather.equals("Heavy Spray")) {
					weather = "비말";
				}
				if(weather.equals("Dust Whirls")) {
					weather = "회오리 바람";
				}
				if(weather.equals("Light Dust Whirls")) {
					weather = "회오리 바람";
				}
				if(weather.equals("Heavy Dust Whirls")) {
					weather = "회오리 바람";
				}
				if(weather.equals("Sandstorm")) {
					weather = "모래 바람";
				}
				if(weather.equals("Light Sandstorm")) {
					weather = "모래 바람";
				}
				if(weather.equals("Heavy Sandstorm")) {
					weather = "모래 바람";
				}
				if(weather.equals("Low Drifting Snow")) {
					weather = "눈";
				}
				if(weather.equals("Light Low Drifting Snow")) {
					weather = "눈";
				}
				if(weather.equals("Heavy Low Drifting Snow")) {
					weather = "눈";
				}
				if(weather.equals("Low Drifting Sand")) {
					weather = "모래";
				}
				if(weather.equals("Light Low Drifting Sand")) {
					weather = "모래";
				}
				if(weather.equals("Heavy Low Drifting Sand")) {
					weather = "모래";
				}
				if(weather.equals("Blowing Snow")) {
					weather = "눈";
				}
				if(weather.equals("Light Blowing Snow")) {
					weather = "눈";
				}
				if(weather.equals("Heavy Blowing Snow")) {
					weather = "눈";
				}
				if(weather.equals("Blowing Widespread Dust")) {
					weather = "흙 먼지";
				}
				if(weather.equals("Light Blowing Widespread Dust")) {
					weather = "흙 먼지";
				}
				if(weather.equals("Heavy Blowing Widespread Dust")) {
					weather = "흙 먼지";
				}
				if(weather.equals("Blowing Sand")) {
					weather = "모래";
				}
				if(weather.equals("Light Blowing Sand")) {
					weather = "모래";
				}
				if(weather.equals("Heavy Blowing Sand")) {
					weather = "모래";
				}
				if(weather.equals("Rain Mist")) {
					weather = "안개를 동반한 비";
				}
				if(weather.equals("Light Rain Mist")) {
					weather = "안개를 동반한 비";
				}
				if(weather.equals("Heavy Rain Mist")) {
					weather = "안개를 동반한 비";
				}
				if(weather.equals("Rain Showers")) {
					weather = "소나기";
				}
				if(weather.equals("Light Rain Showers")) {
					weather = "소나기";
				}
				if(weather.equals("Heavy Rain Showers")) {
					weather = "소나기";
				}
				if(weather.equals("Snow Showers")) {
					weather = "눈";
				}
				if(weather.equals("Light Snow Showers")) {
					weather = "눈";
				}
				if(weather.equals("Heavy Snow Showers")) {
					weather = "눈";
				}
				if(weather.equals("Snow Blowing Snow Mist")) {
					weather = "눈";
				}
				if(weather.equals("Light Snow Blowing Snow Mist")) {
					weather = "눈";
				}
				if(weather.equals("Heavy Snow Blowing Snow Mist")) {
					weather = "눈";
				}
				if(weather.equals("Ice Pellet Showers")) {
					weather = "싸라기눈";
				}
				if(weather.equals("Light Ice Pellet Showers")) {
					weather = "싸라기눈";
				}
				if(weather.equals("Heavy Ice Pellet Showers")) {
					weather = "싸라기눈";
				}
				if(weather.equals("Hail Showers")) {
					weather = "우박";
				}
				if(weather.equals("Light Hail Showers")) {
					weather = "우박";
				}
				if(weather.equals("Heavy Hail Showers")) {
					weather = "우박";
				}
				if(weather.equals("Small Hail Showers")) {
					weather = "우박";
				}
				if(weather.equals("Light Small Hail Showers")) {
					weather = "우박";
				}
				if(weather.equals("Heavy Small Hail Showers")) {
					weather = "우박";
				}
				if(weather.equals("Thunderstorm")) {
					weather = "뇌우";
				}
				if(weather.equals("Light Thunderstorm")) {
					weather = "뇌우";
				}
				if(weather.equals("Heavy Thunderstorm")) {
					weather = "뇌우";
				}
				if(weather.equals("Thunderstorms and Rain")) {
					weather = "뇌우";
				}
				if(weather.equals("Light Thunderstorms and Rain")) {
					weather = "뇌우";
				}
				if(weather.equals("Heavy Thunderstorms and Rain")) {
					weather = "뇌우";
				}
				if(weather.equals("Thunderstorms and Snow")) {
					weather = "뇌우를 동반한 눈";
				}
				if(weather.equals("Light Thunderstorms and Snow")) {
					weather = "뇌우를 동반한 눈";
				}
				if(weather.equals("Heavy Thunderstorms and Snow")) {
					weather = "뇌우를 동반한 눈";
				}
				if(weather.equals("Thunderstorms and Ice Pellets")) {
					weather = "뇌우를 동반한 싸라기눈";
				}
				if(weather.equals("Thunderstorms with Small Hail")) {
					weather = "뇌우를 동반한 우박";
				}
				if(weather.equals("Light Thunderstorms with Small Hail")) {
					weather = "뇌우를 동반한 우박";
				}
				if(weather.equals("Heavy Thunderstorms with Small Hail")) {
					weather = "뇌우를 동반한 우박";
				}
				if(weather.equals("Thunderstorms with Hail")) {
					weather = "뇌우를 동반한 우박";
				}
				if(weather.equals("Light Thunderstorms with Hail")) {
					weather = "뇌우를 동반한 우박";
				}
				if(weather.equals("Heavy Thunderstorms with Hail")) {
					weather = "뇌우를 동반한 우박";
				}
				if(weather.equals("Freezing Drizzle")) {
					weather = "얼어 붙은 이슬비";
				}
				if(weather.equals("Light Freezing Drizzle")) {
					weather = "얼어 붙은 이슬비";
				}
				if(weather.equals("Heavy Freezing Drizzle")) {
					weather = "얼어 붙은 이슬비";
				}
				if(weather.equals("Freezing Rain")) {
					weather = "얼어 붙은 비";
				}
				if(weather.equals("Light Freezing Rain")) {
					weather = "얼어 붙은 비";
				}
				if(weather.equals("Heavy Freezing Rain")) {
					weather = "얼어 붙은 비";
				}
				if(weather.equals("Freezing Fog")) {
					weather = "얼어 붙은 안개";
				}
				if(weather.equals("Light Freezing Fog")) {
					weather = "얼어 붙은 안개";
				}
				if(weather.equals("Heavy Freezing Fog")) {
					weather = "얼어 붙은 안개";
				}
				if(weather.equals("Patches of Fog")) {
					weather = "안개";
				}
				if(weather.equals("Shallow Fog")) {
					weather = "안개";
				}
				if(weather.equals("Partial Fog")) {
					weather = "일부 안개";
				}
				if(weather.equals("Overcast")) {
					weather = "흐림";
				}
				if(weather.equals("Partly Cloudy")) {
					weather = "구름 조금";
				}
				if(weather.equals("Mostly Cloudy")) {
					weather = "구름 많음";
				}
				if(weather.equals("Scattered Clouds")) {
					weather = "흩어진 구름";
				}
				if(weather.equals("Small Hail")) {
					weather = "약한 우박";
				}
				if(weather.equals("Squalls")) {
					weather = "스콜";
				}
				if(weather.equals("Funnel Cloud")) {
					weather = "깔때기 구름";
				}
				if(weather.equals("Unknown Precipitation")) {
					weather = "알수없는 강수";
				}
				if(weather.equals("Unknown")) {
					weather = "알수없음";
				}
			}
			
			Date date = new Date(System.currentTimeMillis());
			if(language.equals("ko")) {
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yy-MM-dd hh:mm:ss");
				getTime = sdf.format(date);
			}
			else if(!language.equals("ko")) {
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd-yy hh:mm:ss");
				getTime = sdf.format(date);
			}
			
			weather_info.setText("");
			tv_temp.setText(temp);
			tv_weather.setText(weather);
			update_date.setText(getString(R.string.weather_last_update) + getTime);
			++count;

			super.onPostExecute(doc);
		}


	}
	

	@Override
	public void onDestroy()
	{
		if(view != null) {
			windowManager.removeView(view);
			view = null;
		}
		
		SharedPreferences pref = getSharedPreferences("weather_pref", 0);
		SharedPreferences.Editor editor = pref.edit();
		str_temp = tv_temp.getText().toString();
		str_weather = tv_weather.getText().toString();
		str_date = update_date.getText().toString();
		editor.putString("temp", str_temp);
		editor.putString("weather", str_weather);
		editor.putString("date", str_date);
		editor.commit();
		
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}

}
