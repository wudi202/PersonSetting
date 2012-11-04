package me.lifetrip.service;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class telService extends android.app.Service
{
	public static String CALLNUM = "callnum";
	public static String deffilename = "unknow_";
    private CallRecord mcallRecord;
    private static final String TAG = "telService";
    private boolean isInRecord = false;
    
	public telService()
	{}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		handleCommenet(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	void handleCommenet(Intent intent) {
		String callnum = deffilename;
		if (true ==  isInRecord) {
			return;
		}
		isInRecord = true;
		
		if (null != intent)
		{
			callnum = intent.getStringExtra(CALLNUM);
			if (null == mcallRecord)
			{
				Log.e(TAG, "the recorder is destroy by some error");
				mcallRecord = new CallRecord();
			}
			mcallRecord.StartRecord(callnum);
		}
		return;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		try {
		    if (null == mcallRecord)
		    {
			    mcallRecord = new CallRecord();
		    }
		}
		catch (Exception e)
		{
			Log.e(TAG, "service error" + e.toString());
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		isInRecord = false;
		if (null != mcallRecord) {
			mcallRecord.StopRecord();
		}
		mcallRecord = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean GetIsRecording() {
		return isInRecord;		
	}
}

class CallRecord
{
	String callnumString;
	public static final String dirName = "callrecord";
	MediaRecorder recodeRecorder = null;
	private static final String TAG = "callrecord";
	File audioFile = null;
	private boolean recordstarted = false;
	
	public CallRecord() {
		callnumString = telService.deffilename;
	}

	//开始记录，这里换成mp3格式记录的话还需要做一下修改
	void StartRecord(String callnum) {
		try {
			audioFile = AddRecordPath(callnum);
			if (null == audioFile) {
				Log.e(TAG, "add the record path fail");
				return;
			}
	        recodeRecorder = new MediaRecorder();
		    recodeRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
		    recodeRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    recodeRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		    recodeRecorder.setOutputFile(audioFile.getAbsolutePath());
		    recodeRecorder.prepare();
		    recodeRecorder.start();
			recordstarted = true;
		} catch (Exception e) {
			Log.e(TAG, e.getStackTrace()+"\n"+e.getMessage());
			audioFile.delete();
		}
	}
	
	//停止记录
	void StopRecord() {
		if ((null != recodeRecorder) && (true == recordstarted))
		{
		    recodeRecorder.stop();
		    recodeRecorder.release();
		}
		recordstarted = false;
		return;
	}
	
    //根据设计的命名规则，这个命令函数还需要做修改
    File AddRecordPath(String callnum) throws IOException
    {
    	    if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
    	    	    Log.e("callrecord", "the SD card can not be used");
    	    	    return null;
    	    }
    	    
    		if ((null == callnum) || ("" == callnum)){
    			this.callnumString = telService.deffilename;
    		}
    		
        String myRecDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+dirName+"/";
        File fileDir = new File(myRecDir); 
        if(!fileDir.exists()){
        	 fileDir.mkdirs();
        }
        
        //如果要存的文件名已经存在的话，在后面加上index
        File audioFile = new File(myRecDir+callnumString+".3gpp");
        int nameindx = 0;
        while ((null != audioFile) && (audioFile.exists()))
        {
	        	nameindx ++;
	        	audioFile = new File(myRecDir+callnumString+"_"+Integer.toString(nameindx));
        }
        if (null != audioFile) {
            audioFile.createNewFile();
        }
        return audioFile;
    }
}
