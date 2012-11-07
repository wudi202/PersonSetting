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
			    case TelephonyManager.CALL_STATE_IDLE: //挂机
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
			    case TelephonyManager.CALL_STATE_RINGING: //响铃
			    {
				    	Log.d("listen", "ring"+incomingNumber);
				    	//只有在这里我们才能得到呼叫的电话号码，在offhook和idle状态中我们都是不能得到来电号码的
				    	if ((null != incomingNumber) && ("" != incomingNumber))
				    	{
				    		callNum = incomingNumber;
				    	}
				        break;			
				}
			    case TelephonyManager.CALL_STATE_OFFHOOK: //通话中
			    {
				    	Log.d("listen", "in call"+incomingNumber);
				    	//如果service已经启动开始记录的话，又有新电话不做处理，这个只能都一起记在上一个电话里面了
				    	if (null != serIntent)
				    	{
				    		break;
				    	}
				    	
				    	//正常来讲，对于拨入和拨出的电话，这个传入的值都是""，对于拨出的电话，这个号码在receiver里面得到
				    	//对于拨入的电话，在上面的ringing的时候我们可以得到，下面的处理是不需要的
				    	//if ("" != incomingNumber)
				    	//{
				    	//	callNum = incomingNumber;
				    	//}
				    	
				    	//在通话开始的时候，如果直接开始录音监听的话，会出现问题，会导致后面的其他时间得不到通知
				    //所以不能在这里直接开始，这里起了一个service进行处理，起一个后台进程其实也是可以的			    	
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
    	    //如果是在通话过程中拨出去的新电话，因为service已经开始了，这里这个值被改写也不会影响啥
    	    callNum = outCallNum;
    	    Log.d("shit", outCallNum);
    }
}
