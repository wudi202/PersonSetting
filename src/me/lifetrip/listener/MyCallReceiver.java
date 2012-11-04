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
        	        //��������ʱ,���listen�϶���û�б�����,��������һ��
        	        if (true == isRecord)
              	{
        		        PersonSettingActivity.SetCallLisener(context, true);
        	        }
	        }
		    else if (intent.getAction().equals( Intent.ACTION_NEW_OUTGOING_CALL)) {
		    	    //����յ����ǲ����绰�Ĺ㲥�Ļ�������Ӧ����Ҫ��¼һ�²����ĵ绰����ġ�
		    	    //���ǲ���ֱ�ӿ�ʼ��������Ҫ�ȵ绰��ͨ�Ժ����Ҫ��ʼ����
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
