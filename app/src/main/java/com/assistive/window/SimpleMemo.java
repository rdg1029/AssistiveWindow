package com.assistive.window;
import android.app.*;
import android.os.*;
import android.content.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.view.View.*;
import android.view.animation.*;

public class SimpleMemo extends Service
{
	private WindowManager windowManager;
	private View view;
	private ViewGroup memo_parentView, memo_childView, dialog_parentView, dialog_childView;
	private EditText memo;
	private String str;

	@Override
	public void onCreate()
	{
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.simple_memo, null);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
			PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.CENTER | Gravity.TOP;
		
		memo_parentView = (FrameLayout)view.findViewById(R.id.simple_memo_pv);
		memo_childView = (LinearLayout)view.findViewById(R.id.simple_memo_cv);

		windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
		windowManager.addView(memo_parentView, params);
		
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_in);
		memo_childView.startAnimation(anim);
		
		SharedPreferences pref = getSharedPreferences("memopref", 0);
		memo = (EditText)view.findViewById(R.id.memo);
		str = pref.getString("memo", str);
		memo.setText(str);
		Button closememo = (Button)view.findViewById(R.id.memo_close);
		closememo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					startService(new Intent(getApplicationContext(), FloatingView.class));
					stopSelf();
				}
		});
		Button clearmemo = (Button)view.findViewById(R.id.memo_clear);
		clearmemo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					dialog();
				}
		});
		
		super.onCreate();
	}
	
	public void dialog() {
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View dialog = inflater.inflate(R.layout.dialog, null);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
			PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.CENTER | Gravity.CENTER;

		dialog_parentView = (FrameLayout)dialog.findViewById(R.id.dialog_pv);
		dialog_childView = (LinearLayout)dialog.findViewById(R.id.dialog_cv);

		final WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
		wm.addView(dialog_parentView, params);

		Animation dialog_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
		dialog_childView.startAnimation(dialog_anim);

		Button yes = (Button)dialog.findViewById(R.id.memo_clear_yes);
		yes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					memo.setText("");
					wm.removeView(dialog);
				}
			});
		Button no = (Button)dialog.findViewById(R.id.memo_clear_no);
		no.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					wm.removeView(dialog);
				}
			});
	}
	
	@Override
	public void onDestroy()
	{
		if(view != null) {
			windowManager.removeView(view);
			view = null;
		}
		SharedPreferences pref = getSharedPreferences("memopref", 0);
		SharedPreferences.Editor editor = pref.edit();
		str = memo.getText().toString();
		editor.putString("memo", str);
		editor.commit();
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.simple_memo_savetoast), Toast.LENGTH_SHORT).show();
		
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}
}
