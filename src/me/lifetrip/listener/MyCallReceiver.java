package me.lifetrip.listener;

import me.lifetrip.view.PersonSettingActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class MyCallReceiver extends BroadcastReceiver {
	private SharedPreferences mSharedPreferences;
    private String PREFS_NAME = "sample.personalsetting.com";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
            if ((null == intent) || (null == intent.getAction()))
            {
        	        return;
            }
          
            if (intent.getAction().equals( Intent.ACTION_BOOT_COMPLETED))
            {
        	        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        	        boolean isRecord = mSharedPreferences.getBoolean("RecordCall", false);
        	        //开机启动时,这个listen肯定还没有被设置,这里设置一下
        	        if (true == isRecord)
              	{
        		        PersonSettingActivity.SetCallLisener(context, true);
        	        }
	        }
		    else if (intent.getAction().equals( Intent.ACTION_NEW_OUTGOING_CALL)) {
		    	    //如果收到的是拨出电话的广播的话，这里应该是要记录一下拨出的电话号码的。
		    	    //但是不能直接开始侦听，需要等电话接通以后才需要开始侦听
	        	    String outCallNumString = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
	        	    PhoneCallListener.SetOutNum(outCallNumString);
			}
		} 
		catch(Exception e)
	    	{
	    		Log.e("broadcast", e.getMessage());
	    	}	
	}

}
