package me.lifetrip.view;

import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class PersonSettingActivity extends Activity {
    /** Called when the activity is first created. */
	private SharedPreferences mSharedPreferences;
    private String PREFS_NAME;
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        PREFS_NAME = "sample.personalsetting.com";
        mSharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        
        Boolean iscallrecord = mSharedPreferences.getBoolean("callrecord", false);
        
        CheckBox callrecord = (CheckBox)this.findViewById(R.id.callrecord);
        callrecord.setChecked(iscallrecord);

        callrecord.setOnCheckedChangeListener(new OnCheckedChangeListener() {	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Boolean nowChecked = !isChecked;
				
				//如果是新选中的话，需要启动服务开始对电话的侦听，否则需要关闭对电话的侦听服务
				if (nowChecked)
				{
					Boolean sdCardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
					if (!sdCardExit)
					{
						//这里可以显示弹窗提示需要插入sd卡
						Log.e("Activity", "set check error");
						return;
					}
				}
				else {
					
				}
				
				buttonView.setChecked(nowChecked);
				
				Editor tEditor = mSharedPreferences.edit();
				tEditor.putBoolean("callrecord", nowChecked);				
			}
		});
    }
}