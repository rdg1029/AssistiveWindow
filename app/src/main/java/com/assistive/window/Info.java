package com.assistive.window;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class Info extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
	}
	public void download_np(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=nm.security.namooprotector"));
		startActivity(intent);
	}
	public void download_aod(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.yong.aod"));
		startActivity(intent);
	}
	public void share(View v) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_TEXT, "Download Assistive Window! https://play.google.com/store/apps/details?id=com.assistive.window");
		Locale locale = getResources().getConfiguration().locale;
		String language =  locale.getLanguage();
		if(language.equals("ko")) {
			i.putExtra(Intent.EXTRA_TEXT, "지금 플레이스토어에서 Assistive Window를 설치해보세요! https://play.google.com/store/apps/details?id=com.assistive.window");
		}
		startActivity(i);
	}
	
}
