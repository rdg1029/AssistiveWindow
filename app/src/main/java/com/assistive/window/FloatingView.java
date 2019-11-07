package com.assistive.window;
import android.app.*;
import android.os.*;
import android.content.*;
import android.view.*;
import android.graphics.*;
import android.widget.RadioGroup.*;
import android.widget.*;
import android.text.style.*;
import android.view.WindowManager;
import android.view.animation.*;
import android.view.GestureDetector.*;
import android.support.v4.app.*;

public class FloatingView extends Service
{
	private View view;
	private ViewGroup pv;
	private ImageView fv;
	private WindowManager windowmanager;
	private WindowManager.LayoutParams params;
	
	private float TouchY;
	private int ViewY;
	private boolean isMove = false;
	GestureDetector mGestureDetector;
	
	Receiver receiver;
	
	private View.OnTouchListener viewTouchListner = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN :
					isMove = false;
					
					TouchY = event.getRawY();
					ViewY = params.y;
					
				break;
				
				case MotionEvent.ACTION_UP :
					
					isMove = true;
					
				break;
				
				case MotionEvent.ACTION_MOVE :
					isMove = true;
					
					int y = (int) (event.getRawY() - TouchY);
					
					final int num = 5;
					if ((y > -num && y < num)) {
						isMove = false;
						break;
					}
					
					params.y = ViewY + y;
					
					windowmanager.updateViewLayout(view, params);
					
					break;
			}
			return mGestureDetector.onTouchEvent(event);
		}
	};
	
	private class SimpleListener extends SimpleOnGestureListener
	{

		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			startService(new Intent(getApplicationContext(), FloatingLayout.class));
			return super.onDoubleTap(e);
		}
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		SharedPreferences pref = getSharedPreferences("setting", 0);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.floatingview, null);
		
		mGestureDetector = new GestureDetector(getApplicationContext(), new SimpleListener());
		
		view.setOnTouchListener(viewTouchListner);
		
		params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
			PixelFormat.TRANSLUCENT);
			
		pv = (FrameLayout)view.findViewById(R.id.pv_fv);
		fv = (ImageView)view.findViewById(R.id.fv);
		
		if(pref.getInt("fv_location", 0) == 0) {
			fv.setBackgroundDrawable(getResources().getDrawable(R.drawable.fv_left));
			params.gravity = Gravity.CENTER | Gravity.LEFT;
			Animation fv_left = AnimationUtils.loadAnimation(this, R.anim.translate_left_in);
			fv.startAnimation(fv_left);
		}
		else if(pref.getInt("fv_location", 0) == 1) {
			fv.setBackgroundDrawable(getResources().getDrawable(R.drawable.fv_right));
			Animation fv_right = AnimationUtils.loadAnimation(this, R.anim.translate_right_in);
			fv.startAnimation(fv_right);
			params.gravity = Gravity.CENTER | Gravity.RIGHT;
		}

		windowmanager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowmanager.addView(pv, params);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Notification notification = new NotificationCompat.Builder(FloatingView.this)
		.setContentTitle("Assistive Window")
		.setContentText(getString(R.string.foreground_notification))
		.setSmallIcon(R.mipmap.ic_launcher, 0)
		.setPriority(Notification.PRIORITY_MIN).build();
		startForeground(1029, notification);
		
		return START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		if(view != null) {
			windowmanager.removeView(view);
			view = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}

	
}
