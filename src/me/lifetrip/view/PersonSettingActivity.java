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
		//���ü���
        if (true == iscallrecord) {
		    PersonSettingActivity.SetCallLisener(PersonSettingActivity.this, iscallrecord);
        }
        CheckBox callrecord = (CheckBox)this.findViewById(R.id.callrecord);
        callrecord.setChecked(iscallrecord);
        
        callrecord.setOnCheckedChangeListener(new OnCheckedChangeListener() {	
			@Override
			//�����isChecked���Ѿ��ǵ������Ľ����
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub				
				//�������ѡ�еĻ�����Ҫ��������ʼ�Ե绰��������������Ҫ�رնԵ绰����������
				try {
					if (isChecked)
					{
						Boolean sdCardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
						if (!sdCardExit)
						{
							//���������ʾ������ʾ��Ҫ����sd��,ͬʱ�������䱻ѡ��
							OpenAlertDialog("�澯","�����SD��");
							buttonView.setChecked(false);
							return;
						}						
					}	
					//���û���ȡ������
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
    
    //����lisener
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
    
    
    //�����listener���������д����������Ƚ�����
    class ShowRecord_Onclick implements View.OnClickListener
    {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//������ʾ��¼
			if (R.id.showrecord == v.getId()) {
				
			}
		}
    }
}