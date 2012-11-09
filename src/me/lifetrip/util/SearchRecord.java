package me.lifetrip.util;

import java.io.File;
import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class SearchRecord {
	private static final String TAG = "SearchRecord";
	private String RecordDir = null;
	public static int ORDER_BYNAME = 1;
	public static int ORDER_BYDATA = 2;
	int mOrderType = ORDER_BYDATA;
	Context mContext = null;
	
	SearchRecord(Context context, String dir) {
		RecordDir = dir;
		mContext = context;
	}
	
	//按照对应的排列顺序排列所有的记录显示
	ArrayList<String> listAllRecord(int orderType)
	{
		ArrayList<String> recordList = null;
		
		if (null == RecordDir) {
			if (null != mContext) {
				Toast.makeText(mContext, "No record dir", Toast.LENGTH_SHORT).show();
			}
			Log.e(TAG, "the record dir is null");
			return recordList;
		}
			
		if ((ORDER_BYNAME == orderType) || (ORDER_BYDATA == orderType)) {
			mOrderType = orderType;
		}
		else {
			Log.e(TAG, "the list order type is invalid: "+Integer.toString(orderType));
		}
		
		File dirFile = new File(RecordDir);
		File allFiles[] = dirFile.listFiles();
		if (0 == allFiles.length) {
			Log.d(TAG, "there is no record!");
			return recordList;
		}
		
		return recordList;
	}
}
