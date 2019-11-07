package com.assistive.window;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import com.fsn.cauly.*;
import com.google.android.gms.ads.*;
import java.util.*;
import android.graphics.*;
import android.support.v4.app.*;
import android.net.*;

public class MainActivity extends Activity implements CaulyCloseAdListener
{

    ToggleButton tb;
    TextView tb_text, tb_text_ex;
    CaulyCloseAd CloseAd;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
            }
            PermissionCheck();
        }

        LinearLayout main = (LinearLayout)findViewById(R.id.layout_main);
        Animation anim_main = AnimationUtils.loadAnimation(this, R.anim.fade_main);
        main.startAnimation(anim_main);

        CaulyAdInfo CloseAdInfo = new CaulyAdInfoBuilder("Kfb7pVkx").build();
        CloseAd = new CaulyCloseAd();
        CloseAd.setAdInfo(CloseAdInfo);
        CloseAd.setCloseAdListener(this);

        Locale locale = getResources().getConfiguration().locale;
        String language =  locale.getLanguage();
        if(language.equals("ko")) {
            CaulyAdInfo adinfo = new CaulyAdInfoBuilder("Kfb7pVkx").effect("BottomSlide").bannerHeight("Fixed_50").build();
            CaulyAdView cauly = new CaulyAdView(this);
            cauly.setAdInfo(adinfo);

            RelativeLayout caulylayout = (RelativeLayout)findViewById(R.id.layout_caulyad);
            caulylayout.addView(cauly);
        }
        else if(!language.equals("ko")) {
            AdView ad = new AdView(this);
            AdRequest adrq = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            ad.setAdSize(AdSize.SMART_BANNER);
            ad.setAdUnitId("ca-app-pub-6801525820868787/7783187153");
            ad.loadAd(adrq);

            RelativeLayout admoblayout = (RelativeLayout)findViewById(R.id.layout_admob);
            admoblayout.addView(ad);
        }

        tb = (ToggleButton)findViewById(R.id.tb_main);
        tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton p1, boolean isChecked)
            {
                if(isChecked == true) {
                    startService(new Intent(MainActivity.this,FloatingView.class));
                }
                else if(isChecked == false) {
                    stopService(new Intent(MainActivity.this,FloatingView.class));
                }
            }
        });
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.assistive.window.FloatingView".equals(service.service.getClassName())) {
                tb.setChecked(true);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(CloseAd.isModuleLoaded()) {
                CloseAd.show(this);
            }
            else {
                showDefaultClosePopup();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showDefaultClosePopup()
    {
        new AlertDialog.Builder(this).setTitle("").setMessage(getString(R.string.main_exit))
                .setPositiveButton(getString(R.string.main_exit_y), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.main_exit_n),null)
                .show();
    }

    // CaulyCloseAdListener
    @Override
    public void onFailedToReceiveCloseAd(CaulyCloseAd ad, int errCode,String errMsg) {
    }
    // CloseAd의 광고를 클릭하여 앱을 벗어났을 경우 호출되는 함수이다.
    @Override
    public void onLeaveCloseAd(CaulyCloseAd ad) {
    }
    // CloseAd의 request()를 호출했을 때, 광고의 여부를 알려주는 함수이다.
    @Override
    public void onReceiveCloseAd(CaulyCloseAd ad, boolean isChargable) {

    }
    //왼쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
    @Override
    public void onLeftClicked(CaulyCloseAd ad) {

    }
    //오른쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
    //Default로는 오른쪽 버튼이 종료로 설정되어있다.
    @Override
    public void onRightClicked(CaulyCloseAd ad) {
        finish();
    }
    @Override
    public void onShowedCloseAd(CaulyCloseAd ad, boolean isChargable) {
    }

    public void StartMsg() {
        try{
            SharedPreferences mPref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);

            Boolean bFirst = mPref.getBoolean("isFirst", false);
            if(bFirst == false)
            {
                SharedPreferences.Editor editor = mPref.edit();
                editor.putBoolean("isFirst", true);
                editor.commit();
                Toast.makeText(getApplicationContext(), getString(R.string.main_htu), Toast.LENGTH_LONG).show();
            }
            if(bFirst == true)
            {

            }

        }
        catch(Exception e) {

        }
    }

    public void PermissionCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 0) {
            if(grantResults[0] == 0) {
                StartMsg();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false).setMessage(getResources().getString(R.string.main_permit_msg))
                        .setPositiveButton(getResources().getString(R.string.main_permit_y), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                try {
                                    //Open the specific App Info page:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + "com.assistive.window"));
                                    startActivity(intent);

                                } catch ( ActivityNotFoundException e ) {
                                    //e.printStackTrace();

                                    //Open the generic Apps page:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                    startActivity(intent);

                                }
                                StartMsg();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    public void setting(View v) {
        Intent i = new Intent(this, Setting.class);
        startActivity(i);
    }

    public void info(View v) {
        Intent i = new Intent(this, Info.class);
        startActivity(i);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(CloseAd != null) {
            CloseAd.resume(this);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
