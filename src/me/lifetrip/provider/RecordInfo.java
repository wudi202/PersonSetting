package me.lifetrip.provider;

import java.util.HashMap;

import android.net.Uri;
import android.provider.BaseColumns;

public class RecordInfo {
    //provider��Ȩ��
	public static final String AUTHORITY = "com.me.lifetrip.RecordInfo";
    
    //���ݿ���
    public static final String DATABASE_NAME = "recordinfo.db";
    public static final int DATABASE_VERSION = 1;
   
    private RecordInfo() {}
    
    public static final class CallRecordInfo implements BaseColumns
    {
	    	private CallRecordInfo() {}
	    	    	
	    	//���ʱ��URI
	    	public static final Uri CONTENT_URI = Uri.parse("content://com.me.lifetrip.RecordInfo/record_info");
	    	
	    	//Ĭ�ϰ���ʱ���Ⱥ����򣬽��µķ���ǰ��
	    	public static final String DEFAULT_SORT_ORDER = "CALLTIME DESC";
	    	
	    	public static final String RECORD_CALL_CONTENT = "vnd.android.cursor.dir/vnd.lifetrip.record_info";
	    	public static final String RECORD_CALL_ITEM_CONTENT = "vnd.android.cursor.dir/vnd.lifetrip.record_info_item";

        //����
        //��¼��Ϣ
        public static final String TABLE = "record_info";
	        
	    	//������̳���BaseColumns�Ѿ�Ĭ�ϵ�������_ID�ֶ�
	    //�ļ���  �������  ͨ��¼������  ����ʱ��   �绰ʱ��   ���绹��ȥ��
	    	public static final String FILENAME = "filename";
	    	public static final String FILENAME_TYPE = "TEXT NOT NULL";
	    	public static final String PHONENUM = "phonenum";
	    	public static final String PHONENUM_TYPE = "TEXT";
	    	public static final String NAMEINCONTACT = "name";
	    	public static final String NAMEINCONTACT_TYPE = "TEXT";
	    	public static final String CALLTIME= "call_time";
	    	public static final String CALLTIME_TYPE = "TIMESTAMP NOT NULL"; //���������Ҫ����ʱ��ı�ʾ��ʽ
	    	public static final String CALLLENTH = "call_length";
	    	public static final String CALLLENTH_TYPE = "INTEGER";
	    	public static final String CALL_IN_OUT = "in_out";
	    	public static final String CALL_IN_OUT_TYPE = "BOOLEAN";//0��ʾin��1��ʾout
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
