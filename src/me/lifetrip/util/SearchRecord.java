package me.lifetrip.util;

import java.util.ArrayList;

import me.lifetrip.provider.RecordInfo.CallRecordInfo;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SearchRecord {
	private static final String TAG = "SearchRecord";
	public static int GROUPBY_BYNAME = 1;
	public static int GROUPBY_BYDATE = 2;
	int mOrderType = 0;
	Context mContext = null;
	
	SearchRecord(Context context) {
		mContext = context;
	}
	
	//根据名字归类返回所有的数据
	ArrayList<RecordStruct> listAllRecord_byName()
	{
		return listRecord(null, null, null, null, GROUPBY_BYNAME);
	}
	
	//正常的按照日期降序输出排列
	ArrayList<RecordStruct> listAllRecord_byDate()
	{
		return listRecord(null, null, null, null, GROUPBY_BYDATE);
	}
	
	//返回对应号码的所有电话
	ArrayList<RecordStruct> listALLRecord_forNum(String phoneNum)
	{
		String selection = CallRecordInfo.PHONENUM + " = ?";
		String[] selectionArgs;
		String mobileNumString;
		
		mobileNumString = phoneNumParse.getMobileFormPhoneNumber(phoneNum);
		
		if (null == mobileNumString) {
			selectionArgs = new String[] {phoneNum};
		}
		else {
			selectionArgs = new String[] {mobileNumString};
		}
		
		return listRecord(null, selection, selectionArgs, null, GROUPBY_BYDATE);
	}
	
	//从数据库中获取对应的数据，将得到的数据作为一个链表返回
	ArrayList<RecordStruct> listRecord(String[] projection, String selection, 
			String[] selectionArgs, String sortOrder, int groupBy)
	{
		ArrayList<RecordStruct> recordList = new ArrayList<RecordStruct>();
		Uri queryUri;
			
		if ((GROUPBY_BYNAME == groupBy) || (GROUPBY_BYDATE == groupBy)) {
			mOrderType = groupBy;
		}
		else {
			Log.e(TAG, "the list order type is invalid: "+Integer.toString(groupBy));
			mOrderType = GROUPBY_BYDATE;
		}
		
		queryUri = constructUri(mOrderType);
		Cursor c = null;
		try {
			c = mContext.getContentResolver().query(queryUri, projection, selection, selectionArgs, sortOrder);
			
			if (c.moveToFirst())
			{
				RecordStruct curRecordStruct;
				boolean insertOK = false;
				while (!c.isAfterLast()) {
					curRecordStruct = getCurData(c);
					insertOK = recordList.add(curRecordStruct);
					if (false == insertOK) {
						Log.e(TAG, "error in insert the query data to the list");
					}				
					c.moveToNext();
				}
			}
		}
		catch (Exception e) {
			Log.e(TAG, "list record error: "+e.getMessage());
		}
		finally {
			if (null != c)
			{
				c.close();
			}
		}
		return recordList;
	}
	
	Uri constructUri(int groupBy)
	{
		Uri queryUri = CallRecordInfo.CONTENT_URI;
		if (GROUPBY_BYNAME == groupBy)
		{
			queryUri = Uri.withAppendedPath(queryUri, "groupby_name");
		}
		return queryUri;
	}
	
	RecordStruct getCurData(Cursor c) 
	{
		RecordStruct curDataRecordStruct = new RecordStruct();
		int index;
		index = c.getColumnIndex(CallRecordInfo.PHONENUM);
		curDataRecordStruct.phoneNum = c.getString(index);
		
		index = c.getColumnIndex(CallRecordInfo.NAMEINCONTACT);
		curDataRecordStruct.nameString = c.getString(index);
		
		index = c.getColumnIndex(CallRecordInfo.CALLTIME);
		curDataRecordStruct.callTimeString = c.getString(index);
		
		index = c.getColumnIndex(CallRecordInfo.CALLLENTH);
		curDataRecordStruct.callLen = c.getInt(index);
		
		index = c.getColumnIndex(CallRecordInfo.CALL_IN_OUT);
		curDataRecordStruct.isInCall = c.getInt(index);
		
		return curDataRecordStruct;
	}
	
}
