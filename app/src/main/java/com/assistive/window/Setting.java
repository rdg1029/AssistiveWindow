package com.assistive.window;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.widget.*;
import android.widget.RadioGroup.*;
import com.fsn.cauly.*;
import com.google.android.gms.ads.*;
import java.util.*;
import android.view.*;

public class Setting extends Activity
{
	
	RadioButton fv_left,fv_right,search_google,search_bing,search_yahoo,search_naver,search_daum,search_zum;
	CheckBox fv_restart;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.setting);
		super.onCreate(savedInstanceState);
		
		Locale locale_ad = getResources().getConfiguration().locale;
		String language_ad =  locale_ad.getLanguage();
		if(language_ad.equals("ko")) {
			CaulyAdInfo adinfo = new CaulyAdInfoBuilder("Kfb7pVkx").effect("BottomSlide").bannerHeight("Fixed_50").build();
			CaulyAdView cauly = new CaulyAdView(this);
			cauly.setAdInfo(adinfo);

			RelativeLayout caulylayout = (RelativeLayout)findViewById(R.id.setting_layout_caulyad);
			caulylayout.addView(cauly);
		}
		else if(!language_ad.equals("ko")) {
			AdView ad = new AdView(this);
			AdRequest adrq = new AdRequest.Builder().build();
			ad.setAdSize(AdSize.SMART_BANNER);
			ad.setAdUnitId("ca-app-pub-6801525820868787/7783187153");
			ad.loadAd(adrq);

			RelativeLayout admoblayout = (RelativeLayout)findViewById(R.id.setting_layout_admob);
			admoblayout.addView(ad);
		}
		
		SharedPreferences pref = getSharedPreferences("setting", 0);
		final SharedPreferences.Editor editor = pref.edit();
		
		RadioGroup fv_location = (RadioGroup)findViewById(R.id.setting_fv_position);
		fv_left = (RadioButton)findViewById(R.id.setting_fv_left);
		fv_right = (RadioButton)findViewById(R.id.setting_fv_right);
		boolean fvleft = pref.getBoolean("fvleft", true);
		boolean fvright = pref.getBoolean("fvright", false);
		fv_left.setChecked(fvleft);
		fv_right.setChecked(fvright);
		
		fv_location.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup p1, int p2)
				{
					switch(p2) {
						case R.id.setting_fv_left :
							editor.remove("fv_location");
							editor.commit();
							editor.putInt("fv_location", 0);
							editor.commit();
							
							isServiceRunningCheck();
							
							break;
							
						case R.id.setting_fv_right :
							editor.remove("fv_location");
							editor.commit();
							editor.putInt("fv_location", 1);
							editor.commit();
							
							isServiceRunningCheck();
							
							break;
					}
				}
			});
			
		fv_restart = (CheckBox)findViewById(R.id.setting_fv_restart);
		Boolean fvrestart = pref.getBoolean("fvrestart", true);
		fv_restart.setChecked(fvrestart);
		fv_restart.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					if(((CheckBox)v).isChecked()) {
						editor.remove("fv_restart");
						editor.commit();
						editor.putInt("fv_restart", 1);
						editor.commit();
					}
					else {
						editor.remove("fv_restart");
						editor.commit();
						editor.putInt("fv_restart", 2);
						editor.commit();
					}
				}
		});

		RadioGroup fl_search = (RadioGroup)findViewById(R.id.setting_fl_search);
		search_google = (RadioButton)findViewById(R.id.setting_fl_google);
		search_bing = (RadioButton)findViewById(R.id.setting_fl_bing);
		search_yahoo = (RadioButton)findViewById(R.id.setting_fl_yahoo);
		search_naver = (RadioButton)findViewById(R.id.setting_fl_naver);
		search_daum = (RadioButton)findViewById(R.id.setting_fl_daum);
		search_zum = (RadioButton)findViewById(R.id.setting_fl_zum);
		
		boolean chkg = pref.getBoolean("search_google", true);
		boolean chkb = pref.getBoolean("search_bing", false);
		boolean chky = pref.getBoolean("search_yahoo", false);
		boolean chkn = pref.getBoolean("search_naver", false);
		boolean chkd = pref.getBoolean("search_daum", false);
		boolean chkz = pref.getBoolean("search_zum", false);
		
		search_google.setChecked(chkg);
		search_bing.setChecked(chkb);
		search_yahoo.setChecked(chky);
		search_naver.setChecked(chkn);
		search_daum.setChecked(chkd);
		search_zum.setChecked(chkz);
		
		fl_search.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup p1, int p2)
				{
					switch(p2) {
						case R.id.setting_fl_google:
							editor.remove("search");
							editor.commit();
							editor.putInt("search", 0);
							editor.commit();

							break;

						case R.id.setting_fl_bing:
							editor.remove("search");
							editor.commit();
							editor.putInt("search", 1);
							editor.commit();

							break;
							
						case R.id.setting_fl_yahoo:
							editor.remove("search");
							editor.commit();
							editor.putInt("search", 2);
							editor.commit();
							
							break;
							
						case R.id.setting_fl_naver:
							editor.remove("search");
							editor.commit();
							editor.putInt("search", 3);
							editor.commit();

							break;

						case R.id.setting_fl_daum:
							editor.remove("search");
							editor.commit();
							editor.putInt("search", 4);
							editor.commit();

							break;

						case R.id.setting_fl_zum:
							editor.remove("search");
							editor.commit();
							editor.putInt("search", 5);
							editor.commit();

							break;
					}
				}
		});
		
		Locale locale = getResources().getConfiguration().locale;
		String language =  locale.getLanguage();
		if(!language.equals("ko")) {
			fl_search.removeView(search_naver);
			fl_search.removeView(search_daum);
			fl_search.removeView(search_zum);
		}
	}
	
	public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.assistive.window.FloatingView".equals(service.service.getClassName())) {
                stopService(new Intent(Setting.this,FloatingView.class));	
				startService(new Intent(Setting.this,FloatingView.class));
            }
        }
        return false;
	}

	@Override
	protected void onStop()
	{	
		SharedPreferences pref = getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = pref.edit();
		fv_left = (RadioButton)findViewById(R.id.setting_fv_left);
		fv_right = (RadioButton)findViewById(R.id.setting_fv_right);
		fv_restart = (CheckBox)findViewById(R.id.setting_fv_restart);
		search_google = (RadioButton)findViewById(R.id.setting_fl_google);
		search_bing = (RadioButton)findViewById(R.id.setting_fl_bing);
		search_yahoo = (RadioButton)findViewById(R.id.setting_fl_yahoo);
		editor.putBoolean("fvleft",fv_left.isChecked());
		editor.putBoolean("fvright",fv_right.isChecked());
		editor.putBoolean("fvrestart", fv_restart.isChecked());
		editor.putBoolean("search_google", search_google.isChecked());
		editor.putBoolean("search_bing", search_bing.isChecked());
		editor.putBoolean("search_yahoo", search_yahoo.isChecked());
		editor.putBoolean("search_naver", search_naver.isChecked());
		editor.putBoolean("search_daum", search_daum.isChecked());
		editor.putBoolean("search_zum", search_zum.isChecked());
		editor.commit();
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	
	
}
