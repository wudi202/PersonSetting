package me.lifetrip.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

import me.lifetrip.util.phoneNumParse;

import com.uraroji.garage.android.lame.SimpleLame;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class telService extends android.app.Service
{
	public static String CALLNUM = "callnum";
	public static String RECORDTYPE = "recordtype";
	public static String deffilename = "unknow_";
    private CallRecord mcallRecord;
    private static final String TAG = "telService";
    private boolean isInRecord = false;
    private int recordType = CallRecord.RECORD_MP3;
	
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
				mcallRecord = new CallRecord(this);
			}
			recordType = intent.getIntExtra(RECORDTYPE, CallRecord.RECORD_MP3);
			mcallRecord.StartRecord(callnum, recordType);
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
			    mcallRecord = new CallRecord(this);
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
		try {
			isInRecord = false;
			if (null != mcallRecord) {
				mcallRecord.StopRecord();
			}
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
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
	public static final String dirName = "callrecord";
    private int mSampleRate = 8000;	
	MediaRecorder recodeRecorder = null;
	private static final String TAG = "callrecord";
	File audioFile = null;
	private boolean recordstarted = false;
	public static int RECORD_MP3 = 1;
	public static int RECORD_3GPP = 2;
	int mrecordtype = RECORD_3GPP;
    RecordMICInMp3 mRecordMICInMp3 = null;
    private Context context;
	
	public CallRecord(Context context) {
		this.context = context;
		//callnumString = telService.deffilename;
	}

	//��ʼ��¼�����ﻻ��mp3��ʽ��¼�Ļ�����Ҫ��һ���޸�
	void StartRecord(String callnum, int recordtype) {
		if ((RECORD_3GPP != recordtype) && (RECORD_MP3 != recordtype)) {
			Log.e(TAG, "the record type is not support: "+ Integer.toString(recordtype));
			recordtype = RECORD_3GPP;
		}
		mrecordtype = recordtype;
		if (RECORD_3GPP == recordtype)
		{
			try {
				audioFile = AddRecordPath(callnum,"3gpp");
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
		else {
			try {
				audioFile = AddRecordPath(callnum, "mp3");
				if (null == audioFile) {
					Log.e(TAG, "add the record path fail");
					return;
				}
				mRecordMICInMp3 = new RecordMICInMp3(audioFile, mSampleRate);
				mRecordMICInMp3.start();
			}
			catch (Exception e) {
				Log.e(TAG, "mp3"+e.getStackTrace()+"\n"+e.getMessage());
			}
		}
	}
	
	//ֹͣ��¼
	void StopRecord() {
		if (RECORD_3GPP == mrecordtype) {
			if ((null != recodeRecorder) && (true == recordstarted))
			{
			    recodeRecorder.stop();
			    recodeRecorder.release();
			    recodeRecorder = null;
			}
		}
		else {
		    if (null != mRecordMICInMp3) {
		    	    //���������ʵʱ�ĵõ���¼��ʼ��֪ͨ�������run����Ҫʹ��handle֪ͨ��
		    	    //��������Ӧ��û�б�Ҫ�������������Ҫʵʱ��ȡ״̬�Ļ������������ȡ�Ϳ�����
		    	    recordstarted = mRecordMICInMp3.isRecording();
		    	    if (true == recordstarted) {
		    	    	    mRecordMICInMp3.stopRecord();
		    	    	    //����������ֱ�Ӹ���null�Ļ�����֪���᲻�ᵼ���������ǰ�����գ�Ȼ��run��û�����ꡣ
		    	    	    //������Ȼ��stopservice�ˣ�service�����Ӧ�ú���ҲҪ��ɾ���ɣ�����������Բ��ø�0��
		    	    	    mRecordMICInMp3 = null;
		    	    }
		    }
		}
		recordstarted = false;
		return;
	}
	
	void setSampleRate(int sampleRate) {
		if (sampleRate > 0) {
		    mSampleRate = sampleRate;
		}
		else {
			Log.e(TAG, "set the sample rate error: sampleRate = "+Integer.toString(sampleRate));
		}
	}
    //������Ƶ���������������������Ҫ���޸�, suffix��ʾ��׺�������Ҫmp3��ʽʹ��mp3,����ʹ��3gpp
	//������ʽ��20121107-***-1.map3
    File AddRecordPath(String callnum, String suffix) throws IOException
    {
    	    if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
    	    	    Log.e("callrecord", "the SD card can not be used");
    	    	    return null;
    	    }
    	    
    	    String callnumString = telService.deffilename;
    		if ((null != callnum) && ("" != callnum)){
    			String contactName = phoneNumParse.getContactName(context, callnum);
    			if (null != contactName) {
    				callnumString = contactName;
    			} 
    			else {
    				callnumString = callnum;	
    			}
    		}
    	
    		java.util.Date curTime = new java.util.Date(System.currentTimeMillis());
    		int curYear = curTime.getYear()+1900;
    		int curMon = curTime.getMonth();
    		int curDay = curTime.getDay();
    		String thisDay = String.format("%d%2d%2d-", curYear,curMon,curDay);
    		thisDay = thisDay.replace(" ", "0");
    		
        String myRecDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+dirName+"/";
        File fileDir = new File(myRecDir); 
        if(!fileDir.exists()){
        	 fileDir.mkdirs();
        }
        
        //���Ҫ����ļ����Ѿ����ڵĻ����ں������index
        File audioFile = new File(myRecDir+thisDay+callnumString+"."+suffix);
        int nameindx = 0;
        while ((null != audioFile) && (audioFile.exists()))
        {
	        	nameindx ++;
	        	audioFile = new File(myRecDir+thisDay+callnumString+"-"+Integer.toString(nameindx)+"."+suffix);
        }
        if (null != audioFile) {
            audioFile.createNewFile();
        }
        return audioFile;
    }
}

class RecordMICInMp3 extends Thread {
	int mSampleRate;
	File outFile;
	boolean mIsRecording = false;
	private static final String TAG = "recordMICinMP3";

	static {
		System.loadLibrary("mp3lame");
	}
	
	RecordMICInMp3(File outfile, int mSampleRate) {
		this.mSampleRate = mSampleRate;
		this.outFile = outfile;
	}
@Override
	public void run() {
		// TODO Auto-generated method stub
	    if (null == outFile)
	    {
	      	Log.e(TAG, "the output file is null");
	    	    return;
	    }
		super.run();
		android.os.Process
		.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		final int minBufferSize = AudioRecord.getMinBufferSize(
				mSampleRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		//�����С��buffer��СС��0
		if (minBufferSize < 0) {
			Log.e(TAG, "minbuffersize < 0, and the value is: "+Integer.toString(minBufferSize));
			return;
		}
		
		//����Ҳ�������ò���Դ����MediaRecordһ��������Ӧ��Ҳֻ����MIC
		AudioRecord audioRecord = new AudioRecord(
				MediaRecorder.AudioSource.MIC, mSampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);

		// PCM buffer size (5sec)
		short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 5]; // SampleRate[Hz] * 16bit * Mono * 5sec
		byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "when create the mp3 file, error happened: "+e.getMessage());
			return;
		}

		// Lame init
		SimpleLame.init(mSampleRate, 1, mSampleRate, 32);

		mIsRecording = true;
		try {
			try {
				audioRecord.startRecording(); // ¼����ʼ
			} catch (IllegalStateException e) {
				return;
			}

			try {
				int readSize = 0;
				while (mIsRecording) {
					readSize = audioRecord.read(buffer, 0, minBufferSize);
					if (readSize < 0) {
                         //����������
						Log.e(TAG, "the read buffer < 0:"+Integer.toString(readSize));
						break;
					}
					//�����ǰbufferû�����ݣ��������
					else if (readSize == 0) {
						;
					}
					// ������������Զ������ݣ��������mp3����
					else {
						int encResult = SimpleLame.encode(buffer,
								buffer, readSize, mp3buffer);
						if (encResult < 0) {
							Log.e(TAG, "some error happened in mp3 encode:"+Integer.toString(encResult));
							break;
						}
						if (encResult != 0) {
							try {
								output.write(mp3buffer, 0, encResult);
							} catch (IOException e) {
								Log.e(TAG, "some error happened in write mp3 buffer:"+e.getMessage());
								break;
							}
						}
					}
				}

				int flushResult = SimpleLame.flush(mp3buffer);
				if (flushResult < 0) {
					Log.e(TAG, "some error happened in flush buffer to mp3 file:"+Integer.toString(flushResult));
				}
				if (flushResult != 0) {
					try {
						output.write(mp3buffer, 0, flushResult);
					} catch (IOException e) {
						Log.e(TAG, "some error happened in write buffer to mp3 file:"+e.getMessage());
					}
				}

				try {
					output.close();
				} catch (IOException e) {
					Log.e(TAG, "some error happened in close file:"+e.getMessage());
				}
			} finally {
				audioRecord.stop(); // ¼��ֹͣ
				audioRecord.release();
			}
		} finally {
			SimpleLame.close();
			mIsRecording = false; // ¼������
		}		
		
	}

    public boolean isRecording() {
    	    return mIsRecording;
    }
    
    public void stopRecord() {
    	    mIsRecording = false;
    }
}

