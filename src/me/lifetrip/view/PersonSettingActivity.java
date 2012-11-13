package me.lifetrip.view;

import me.lifetrip.listener.PhoneCallListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class PersonSettingActivity extends Activity {
    protected static final int LENGTH_LONG = 10;
	/** Called when the activity is first created. */
	private SharedPreferences mSharedPreferences;
    private String PREFS_NAME;
    private static final String TAG = "MainActivity";
    public static PhoneCallListener myPhoneListener;
    public static TelephonyManager telMgr;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        PREFS_NAME = "sample.personalsetting.com";
        mSharedPreferences = getSharedPreferences(PREFS_NAME, 0);
 
        Boolean iscallrecord = mSharedPreferences.getBoolean("RecordCall", false);
		//设置监听
        if (true == iscallrecord) {
		    PersonSettingActivity.SetCallLisener(PersonSettingActivity.this, iscallrecord);
        }
        CheckBox callrecord = (CheckBox)this.findViewById(R.id.callrecord);
        callrecord.setChecked(iscallrecord);
        
        callrecord.setOnCheckedChangeListener(new OnCheckedChangeListener() {	
			@Override
			//这里的isChecked就已经是点击过后的结果了
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub				
				//如果是新选中的话，需要启动服务开始对电话的侦听，否则需要关闭对电话的侦听服务
				try {
					if (isChecked)
					{
						Boolean sdCardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
						if (!sdCardExit)
						{
							//这里可以显示弹窗提示需要插入sd卡,同时不能让其被选中
							OpenAlertDialog("告警","请插入SD卡");
							buttonView.setChecked(false);
							return;
						}						
					}	
					//设置或者取消监听
					PersonSettingActivity.SetCallLisener(PersonSettingActivity.this, isChecked);
					Editor tEditor = mSharedPreferences.edit();
					tEditor.putBoolean("RecordCall", isChecked);
					tEditor.commit();					
				} catch (Exception e) {
					// TODO: handle exception
					Log.e(TAG, "error in monitor setting: "+e.getMessage());
				}
			}
		});
        
        Button showRecordButton = (Button)this.findViewById(R.id.showrecord);
        //showRecordButton.setOnClickListener(new )
    }
    
    private void OpenAlertDialog(String myTitle, String myMsg)
    {
	    	AlertDialog.Builder myBuilder = new AlertDialog.Builder(this).setTitle(myTitle).setMessage(myMsg);
	    	
	    	myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				return;
			}

		});
    	
	    	AlertDialog myAlertDiag = myBuilder.create();
	    	myAlertDiag.show();
    }
    
    //设置lisener
    public static void SetCallLisener(Context context, boolean isSet)
    {
        if (!isSet && null == myPhoneListener)
        {
        		return;
        }
		if (null == myPhoneListener)
		{
            myPhoneListener = new PhoneCallListener(context);
            telMgr = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
		}
		if (isSet)
		{
			telMgr.listen(myPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
		else {
			telMgr.listen(myPhoneListener, PhoneStateListener.LISTEN_NONE);
		}
		
    }
    
    
    //下面把listener的类独立来写，这样代码比较清晰
    class ShowRecord_Onclick implements View.OnClickListener
    {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//出发显示记录
			if (R.id.showrecord == v.getId()) {
				
			}
		}
    }
}