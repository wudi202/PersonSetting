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
			    case TelephonyManager.CALL_STATE_IDLE: //�һ�
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
			    case TelephonyManager.CALL_STATE_RINGING: //����
			    {			    	
			        break;
			    }
			    case TelephonyManager.CALL_STATE_OFFHOOK: //ͨ����
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
		TELEPHONE_REMOTE_1,   //3λ����+7λ�绰����
		TELEPHONE_REMOTE_2,   //4λ����+7λ�绰
		TELEPHONE_REMOTE_3,   //3λ����+8λ�绰
		TELEPHONE_REMOTE_4,   //4λ����+8λ�绰
		PHONE_UNKONWN         //����δ֪�绰���������绰��
	}

	String getRecFileName(String incomingNumber)
	{
		String strOutFile = null;
		/*
		index = 0
		if ͨѶ¼���ܹ��ҵ���Ӧ�ĵ绰����
		    strOutFile = ͨѶ¼����+����
		else
		    strOutFile = incomingNumber+����
		    
		while ��callrecordĿ¼�����ҵ�strOutFile��Ӧ���ļ�
	        strOutFile += index++
		return strOutFile
		*/
		return strOutFile;
	}
	
	//�����绰���룬��strOriginNum�ܽ��������ŵ����ݣ�strArea��ʾ�������ţ�strFinalNum��ʾ��ȥ�����ź��ʵ�ʺ��룬����ֵ��ʾ���ǵ绰���������
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
            case 11:  //�������ȼ۸���һЩ���ֻ�/4λ����+7λ�绰/3λ����+8λ�绰
            {
            	//ȡǰ�����λ������λȥ���ݿ�����ȥ��Ļ���Ƚϸ��ӡ�
            	//�����Ļ�����Ϊ��λ�����űϾ��Ƚ��٣���һ������ֱ�Ӱ��������������
            	//�����Ļ���ֱ�ӱ��������������ˡ�
            	//�����Ҫ������չ�Ļ������Կ����������������ݿ���ߴ��ĵ���ֱ�Ӷ�����
            	//����Ϊ�˼���ֱ��д����
            	if ('1' == strRealPhone.charAt(0))   //�ֻ���һλ��1
            	{
            		enPhoneType = PHONETYPE.MOBILEPHONENUM;
            	}
            	else if ('0' == strRealPhone.charAt(0))
            	{
            		//3λ����
            		if (('1' == strRealPhone.charAt(1)) || ('2' == strRealPhone.charAt(1)))
            		{
            			enPhoneType = PHONETYPE.TELEPHONE_REMOTE_3;
                    	strArea = strRealPhone.substring(0, 2);
                    	strFinalNum = strRealPhone.substring(3);                			
            		}
            		else //4λ����
            		{
                    	enPhoneType = PHONETYPE.TELEPHONE_REMOTE_2;
                    	strArea = strRealPhone.substring(0, 3);
                    	strFinalNum = strRealPhone.substring(4);                			
            		}
            	}
            	break;
            }
            case 12:    //4λ����+8λ�绰
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
    
    //�ӵ绰�����ȡ��Ӧ����ϵ������
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
        //�ҵ��˶�Ӧ����ϵ��        
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
