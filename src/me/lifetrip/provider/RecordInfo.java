package me.lifetrip.provider;

import java.util.HashMap;

import android.net.Uri;
import android.provider.BaseColumns;

public class RecordInfo {
    //provider的权限
	public static final String AUTHORITY = "com.me.lifetrip.RecordInfo";
    
    //数据库名
    public static final String DATABASE_NAME = "recordinfo.db";
    public static final int DATABASE_VERSION = 1;
   
    private RecordInfo() {}
    
    public static final class CallRecordInfo implements BaseColumns
    {
	    	private CallRecordInfo() {}
	    	    	
	    	//访问表的URI
	    	public static final Uri CONTENT_URI = Uri.parse("content://com.me.lifetrip.RecordInfo/record_info");
	    	
	    	//默认按照时间先后排序，较新的放在前面
	    	public static final String DEFAULT_SORT_ORDER = "CALLTIME DESC";
	    	
	    	public static final String RECORD_CALL_CONTENT = "vnd.android.cursor.dir/vnd.lifetrip.record_info";
	    	public static final String RECORD_CALL_ITEM_CONTENT = "vnd.android.cursor.dir/vnd.lifetrip.record_info_item";

        //表名
        //记录信息
        public static final String TABLE = "record_info";
	        
	    	//各表项，继承自BaseColumns已经默认的声明了_ID字段
	    //文件名  来电号码  通信录中名字  来电时间   电话时长   来电还是去电
	    	public static final String FILENAME = "filename";
	    	public static final String FILENAME_TYPE = "TEXT NOT NULL";
	    	public static final String PHONENUM = "phonenum";
	    	public static final String PHONENUM_TYPE = "TEXT";
	    	public static final String NAMEINCONTACT = "name";
	    	public static final String NAMEINCONTACT_TYPE = "TEXT";
	    	public static final String CALLTIME= "call_time";
	    	public static final String CALLTIME_TYPE = "TIMESTAMP NOT NULL"; //这个后续需要换成时间的表示形式
	    	public static final String CALLLENTH = "call_length";
	    	public static final String CALLLENTH_TYPE = "INTEGER";
	    	public static final String CALL_IN_OUT = "in_out";
	    	public static final String CALL_IN_OUT_TYPE = "BOOLEAN";//0表示in，1表示out
	    	public static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";

	    	public static final HashMap<String, String> projectionMap = new HashMap<String, String>();
	    static {
	        	projectionMap.put(FILENAME, FILENAME);
	        	projectionMap.put(PHONENUM, PHONENUM);
	        	projectionMap.put(NAMEINCONTACT, NAMEINCONTACT);
	        	projectionMap.put(CALLTIME, CALLTIME);
	        	projectionMap.put(CALLLENTH, CALLLENTH);
	        	projectionMap.put(CALL_IN_OUT, CALL_IN_OUT);
	    }
	    	  	
	    	static final String CREATE_STATEMENT = 	"CREATE TABLE "+ CallRecordInfo.TABLE + 
	    			"(" + BaseColumns._ID + " " + _ID_TYPE + ", " +
	    			FILENAME + " " + FILENAME_TYPE + ", " + PHONENUM + " " + PHONENUM_TYPE + ", " +
			    NAMEINCONTACT + " " + NAMEINCONTACT_TYPE + ", " + CALLTIME + " " + CALLTIME_TYPE + ", " + 
	    			CALLLENTH + " " + CALLLENTH_TYPE + ", " + CALL_IN_OUT + " " + CALL_IN_OUT_TYPE + ")";
    }
}
