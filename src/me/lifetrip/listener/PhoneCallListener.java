package me.lifetrip.listener;

import java.io.File;
import me.lifetrip.service.telService;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallListener extends PhoneStateListener 
{
    int columnIndex = 0;
    boolean bCallTrans = false;
    Context context = null;
    MediaRecorder recodeRecorder = null;
    String path = null;
    String TAG = "mPhoneCallListener";
    String myRecDir = null;
    File audioFile;
    Boolean isRecordBegin = false;
    Thread temp;
    static String callNum = null;
    static boolean isServiceStarted = false;
    Intent serIntent = null;
    
    
    public PhoneCallListener(Context context)
    {
    		this.context = context;
    }
        
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		// TODO Auto-generated method stub
		try
		{
			switch (state)
			{
			    case TelephonyManager.CALL_STATE_IDLE: //�һ�
			    {				 
			        callNum = null;
				    	Log.d("listen", "idle"+incomingNumber);
				    	if (null != serIntent)
				    	{
				    	    context.stopService(serIntent);
				    	}
				    	serIntent = null;
				    	break;
			    }
			    case TelephonyManager.CALL_STATE_RINGING: //����
			    {
				    	Log.d("listen", "ring"+incomingNumber);
				    	//ֻ�����������ǲ��ܵõ����еĵ绰���룬��offhook��idle״̬�����Ƕ��ǲ��ܵõ���������
				    	if ((null != incomingNumber) && ("" != incomingNumber))
				    	{
				    		callNum = incomingNumber;
				    	}
				        break;			
				}
			    case TelephonyManager.CALL_STATE_OFFHOOK: //ͨ����
			    {
				    	Log.d("listen", "in call"+incomingNumber);
				    	//���service�Ѿ�������ʼ��¼�Ļ��������µ绰�����������ֻ�ܶ�һ�������һ���绰������
				    	if (null != serIntent)
				    	{
				    		break;
				    	}
				    	
				    	//�������������ڲ���Ͳ����ĵ绰����������ֵ����""�����ڲ����ĵ绰�����������receiver����õ�
				    	//���ڲ���ĵ绰���������ringing��ʱ�����ǿ��Եõ�������Ĵ����ǲ���Ҫ��
				    	//if ("" != incomingNumber)
				    	//{
				    	//	callNum = incomingNumber;
				    	//}
				    	
				    	//��ͨ����ʼ��ʱ�����ֱ�ӿ�ʼ¼�������Ļ�����������⣬�ᵼ�º��������ʱ��ò���֪ͨ
				    //���Բ���������ֱ�ӿ�ʼ����������һ��service���д�����һ����̨������ʵҲ�ǿ��Ե�			    	
				    	serIntent = new Intent(context, me.lifetrip.service.telService.class);
				    	serIntent.putExtra(telService.CALLNUM, callNum);
				    	context.startService(serIntent);
			        	break;
			    }
			    default:
			        	break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		catch (Exception e)
		{
			Log.e(TAG, "PhoneCall listener creat error: " + e.getMessage());
		}	
	}

    public static void SetOutNum(String outCallNum) {
    	    //�������ͨ�������в���ȥ���µ绰����Ϊservice�Ѿ���ʼ�ˣ��������ֵ����дҲ����Ӱ��ɶ
    	    callNum = outCallNum;
    	    Log.d("shit", outCallNum);
    }
}
