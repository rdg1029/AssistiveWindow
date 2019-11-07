package com.assistive.window;

import android.app.*;
import android.content.*;
import android.content.pm.*;

public class Receiver extends BroadcastReceiver
{
	SharedPreferences sp;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		sp = context.getSharedPreferences("setting", 0);
		String action = intent.getAction();
		if(action.equals("android.intent.action.BOOT_COMPLETED")) {
			if(sp.getInt("fv_restart", 0) == 1) {
				Intent fv_boot = new Intent(context, FloatingView.class);
				context.startService(fv_boot);
			}
		}
		
		if(intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
			Intent fv_replaced = new Intent(context, FloatingView.class);
			context.startService(fv_replaced);
		}
	}
}
