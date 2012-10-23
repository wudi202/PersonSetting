package me.lifetrip.service;

import java.io.File;

import android.R.string;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class telService extends android.app.Service
{
	static boolean isServiceStarted = false;
	public telService()
	{}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		try {
		    Thread thr = new Thread(new ServiceWork(), "TelRecord");
		    isServiceStarted = true;
		    
		}
		catch (Exception e)
		{
			Log.e("TelRecordService", "service error" + e.toString());
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isServiceStarted = false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	class ServiceWork implements Runnable
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
	        mPhoneCallListener myPhoneListener = new mPhoneCallListener(telService.this);
	        TelephonyManager telMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
	        telMgr.listen(myPhoneListener, myPhoneListener.LISTEN_CALL_STATE);			
		}
		
	}
}

class mPhoneCallListener extends PhoneStateListener 
{
    private int lastRingMode = -1;
    int columnIndex = 0;
    boolean bCallTrans = false;
    Context context = null;
    MediaRecorder recodeRecorder = null;
    String path = null;
    String TAG = "mPhoneCallListener";
    
    
    mPhoneCallListener(Context context)
    {
    	this.context = context;
    }
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		// TODO Auto-generated method stub
		Boolean isRecordBegin = false;
		String msg = null;
		
		try
		{
			switch (state)
			{
			    case TelephonyManager.CALL_STATE_IDLE: //挂机
			    {
			    	if (isRecordBegin && (null != recodeRecorder))
			    	{
			    	    recodeRecorder.stop();
			    	    recodeRecorder.release();
			    	    recodeRecorder = null;
			    	    isRecordBegin = false;
			    	}
			    	break;
			    }
			    case TelephonyManager.CALL_STATE_RINGING: //响铃
			    {			    	
			        break;
			    }
			    case TelephonyManager.CALL_STATE_OFFHOOK: //通话中
			    {
			    	if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			    	{
			    		break;
			    	}
			        msg = "the calling number is" + incomingNumber;
			        Log.d(TAG, msg);
			        recodeRecorder = new MediaRecorder();
			    	path = Environment.getExternalStorageDirectory()+"/callrecord/"+incomingNumber+".mp3";
			    	//recodeRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			    	recodeRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
			    	recodeRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			    	recodeRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			    	File audioFile = new File(path);
			    	recodeRecorder.setOutputFile(audioFile.getAbsolutePath());
			    	recodeRecorder.prepare();
			    	recodeRecorder.start();
			    	isRecordBegin = true;
			    	break;
			    }
			    default:
			    	break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		catch (Exception e)
		{
			Log.e(TAG, "PhoneCall listener creat error" + e.getMessage());
		}	
	}

	enum PHONETYPE
	{
		MOBILEPHONENUM,
		TELEPHONE_LOCAL,
		TELEPHONE_REMOTE_1,   //3位区号+7位电话号码
		TELEPHONE_REMOTE_2,   //4位区号+7位电话
		TELEPHONE_REMOTE_3,   //3位区号+8位电话
		TELEPHONE_REMOTE_4,   //4位区号+8位电话
		PHONE_UNKONWN         //其他未知电话，比如国外电话等
	}

	String getRecFileName(String incomingNumber)
	{
		String strOutFile = null;
		/*
		index = 0
		if 通讯录总能够找到对应的电话号码
		    strOutFile = 通讯录名字+日期
		else
		    strOutFile = incomingNumber+日期
		    
		while 在callrecord目录总能找到strOutFile对应的文件
	        strOutFile += index++
		return strOutFile
		*/
		return strOutFile;
	}
	
	//解析电话号码，从strOriginNum总解析处区号等数据，strArea表示的是区号，strFinalNum表示的去掉区号后的实际号码，返回值表示的是电话号码的类型
    PHONETYPE parsePhoneNum(String strOriginNum, String strCountry, String strArea, String strFinalNum)
    {
    	String strRealPhone = strOriginNum;
    	PHONETYPE enPhoneType = PHONETYPE.PHONE_UNKONWN;
    	strCountry = null;
    	strArea = null;
        String[] AreaID_3Bits = {"010", "020", "021", "022", "023", 
		          "024", "025", "027", "028", "029"};         	
    	int num_len = strOriginNum.length();

        if (13 == num_len)
        {
        	if (strRealPhone.substring(0, 2).equals("+86"))
        	{
        		strRealPhone = strRealPhone.substring(3);
        	}
        }
        
        strFinalNum = strRealPhone;
        if (strRealPhone.length() > 11 || strRealPhone.length() < 7)
        {
        	return enPhoneType;
        }
        
        
        switch (strRealPhone.length())
        {
            case 7:
            case 8:
            {
            	enPhoneType = PHONETYPE.TELEPHONE_LOCAL;
            	break;
            }
            case 10:   //3+7
            {
            	enPhoneType = PHONETYPE.TELEPHONE_REMOTE_1;
            	strArea = strRealPhone.substring(0, 2);
            	strFinalNum = strRealPhone.substring(3);
            	break;
            }
            case 11:  //这个情况比价复杂一些：手机/4位区号+7位电话/3位区号+8位电话
            {
            	//取前面的三位或者四位去数据库里面去查的话会比较复杂。
            	//这样的话，因为三位的区号毕竟比较少，用一个数组直接把这个都给记下来
            	//这样的话，直接遍历这个数组就行了。
            	//如果需要考虑扩展的话，可以考虑让这个数组从数据库或者从文档中直接读出来
            	//这里为了简便就直接写死了
            	if ('1' == strRealPhone.charAt(0))   //手机第一位是1
            	{
            		enPhoneType = PHONETYPE.MOBILEPHONENUM;
            	}
            	else if ('0' == strRealPhone.charAt(0))
            	{
            		//3位区号
            		if (('1' == strRealPhone.charAt(1)) || ('2' == strRealPhone.charAt(1)))
            		{
            			enPhoneType = PHONETYPE.TELEPHONE_REMOTE_3;
                    	strArea = strRealPhone.substring(0, 2);
                    	strFinalNum = strRealPhone.substring(3);                			
            		}
            		else //4位区号
            		{
                    	enPhoneType = PHONETYPE.TELEPHONE_REMOTE_2;
                    	strArea = strRealPhone.substring(0, 3);
                    	strFinalNum = strRealPhone.substring(4);                			
            		}
            	}
            	break;
            }
            case 12:    //4位区号+8位电话
            {
            	enPhoneType = PHONETYPE.TELEPHONE_REMOTE_4;
            	strArea = strRealPhone.substring(0, 3);
            	strFinalNum = strRealPhone.substring(4);
            	break;
            }
            default:
            	break;               	
        }
    	
    	return enPhoneType;
    }
 
    /*
    String getMobileFormPhoneNumber(String strPhoneNum)
    {
    	String strMobileNum = null;
    	PHONETYPE enPhoneType = PHONETYPE.PHONE_UNKONWN;
    	String strCountry = null;
    	String strArea= null;
    	String strFinalNum = null;
    	if (null == strPhoneNum)
    	{
    		return null;
    	}
    	
    	enPhoneType = parsePhoneNum(strPhoneNum, strCountry, strArea, strFinalNum);
    	if (PHONETYPE.MOBILEPHONENUM == enPhoneType)
    	{
    		strMobileNum = strFinalNum;
    	}
    	return strMobileNum;
    }*/
    
    //从电话号码获取对应的联系人名字
    String getContactName(String strPhoneNum)
    {
    	PHONETYPE enPhoneType = PHONETYPE.PHONE_UNKONWN;
    	String strCountry = null;
    	String strArea= null;
    	String strFinalNum = null;   
    	String selection = null;
    	String name = null;
    	String msg = null;
    	
    	if (null == strPhoneNum)
    		return name;
    	enPhoneType = parsePhoneNum(strPhoneNum, strCountry, strArea, strFinalNum);
    	if (PHONETYPE.MOBILEPHONENUM == enPhoneType)
    	{
		    selection = ContactsContract.CommonDataKinds.Phone.NUMBER+" = "+'"' + strFinalNum +'"'
                         + " OR " + ContactsContract.CommonDataKinds.Phone.NUMBER+" = "+"\"+86" + strFinalNum +'"';
    	}
    	else {
		    selection = ContactsContract.CommonDataKinds.Phone.NUMBER+" = "+'"' + strPhoneNum +'"'
            + " OR " + ContactsContract.CommonDataKinds.Phone.NUMBER+" = "+'"' + strFinalNum +'"';			
		}
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
                                                      null, selection, null, null);

        msg = "the number of matched name is" + c.getCount();
        Log.d(TAG, msg);
        //找到了对应的联系人        
        if ((null != c) && (0 != c.getCount()))
        {
        	int nameFieldColumnIndex = c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
        	name = c.getString(nameFieldColumnIndex);
            msg = "the first matched name is" + name;
            Log.d(TAG, msg);        	
        }
        return name;
    }
}
