package me.lifetrip.provider;

import me.lifetrip.provider.RecordInfo.CallRecordInfo;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class RecordInfoProvider extends ContentProvider{
	RecordDatabaseHelp mRecordSqlHelper;
	public static final UriMatcher mmatcher = new UriMatcher(UriMatcher.NO_MATCH);
	public static final int CALL_RECORD_DIR = 1;
	public static final int CALL_RECORD_ITEM = 2;
	public static final int CALL_RECROD_GROUP_BYNAME = 3;
	
	static {
		mmatcher.addURI(RecordInfo.AUTHORITY, "record_info", CALL_RECORD_DIR);
		mmatcher.addURI(RecordInfo.AUTHORITY, "record_info/#", CALL_RECORD_ITEM);
		mmatcher.addURI(RecordInfo.AUTHORITY, "record_info/groupby_name", CALL_RECROD_GROUP_BYNAME);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int affected = 0;
		if (null == uri) {
			return 0;
		}
		switch (mmatcher.match(uri)) {
		case CALL_RECORD_DIR:
		{
			SQLiteDatabase db = mRecordSqlHelper.getWritableDatabase();
			affected = db.delete(CallRecordInfo.TABLE, selection, selectionArgs);	
		}
		case CALL_RECORD_ITEM:
			String fileIdString = uri.getLastPathSegment();
			SQLiteDatabase db = mRecordSqlHelper.getWritableDatabase();
			affected = db.delete(CallRecordInfo.TABLE, CallRecordInfo._ID+" = ?", new String[] { fileIdString });
			break;
		default:
			break;
		}
		if (0 != affected) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return affected;
	}

	@Override
	//获取对应的MIME值
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (mmatcher.match(uri)) {
		case CALL_RECORD_DIR:
		{
			return CallRecordInfo.RECORD_CALL_CONTENT;
		}
		case CALL_RECORD_ITEM:
		{
			return CallRecordInfo.RECORD_CALL_ITEM_CONTENT;
		}
		case CALL_RECROD_GROUP_BYNAME:
		{
		    return CallRecordInfo.RECORD_CALL_RECROD_GROUP_BYNAME;	
		}
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		Uri insertUri = null;
		switch (mmatcher.match(arg0)) {
		case CALL_RECORD_DIR:
		case CALL_RECORD_ITEM:
			insertUri = CallRecordInfo.CONTENT_URI;
			long fileId = mRecordSqlHelper.insertNewCall(arg1);
			insertUri = Uri.withAppendedPath(CallRecordInfo.CONTENT_URI, Long.toString(fileId));
			break;

		default:
			throw new IllegalArgumentException("the insert uri is invalid");
		}
		getContext().getContentResolver().notifyChange(arg0, null);
		return insertUri;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mRecordSqlHelper = new RecordDatabaseHelp(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs,
			String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mRecordSqlHelper.getReadableDatabase();
		SQLiteQueryBuilder dbQuery = new SQLiteQueryBuilder();
		dbQuery.setTables(CallRecordInfo.TABLE);
		dbQuery.setProjectionMap(CallRecordInfo.projectionMap);
		Cursor cursor = null;
		if (null == sortOrder) {
			sortOrder = CallRecordInfo.DEFAULT_SORT_ORDER;
		}
		switch (mmatcher.match(uri)) {
		case CALL_RECORD_DIR:
			cursor = dbQuery.query(db, projectionIn, selection, selectionArgs, null, null, sortOrder);
			break;
		case CALL_RECORD_ITEM:
			dbQuery.appendWhere(CallRecordInfo._ID+" = "+uri.getLastPathSegment());
			cursor = dbQuery.query(db, projectionIn, selection, selectionArgs, null, null, sortOrder);
			break;
		case CALL_RECROD_GROUP_BYNAME:
			cursor = dbQuery.query(db, projectionIn, selection, selectionArgs, CallRecordInfo.NAMEINCONTACT, null, "DESC");
		default:
			break;
		}
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mRecordSqlHelper.getWritableDatabase();
		SQLiteQueryBuilder dbQuery = new SQLiteQueryBuilder();
		dbQuery.setTables(CallRecordInfo.TABLE);
		dbQuery.setProjectionMap(CallRecordInfo.projectionMap);	
		switch (mmatcher.match(uri)) {
		case CALL_RECORD_DIR:
			db.update(CallRecordInfo.TABLE, values, selection, selectionArgs);
			break;
		case CALL_RECORD_ITEM:
			db.update(CallRecordInfo.TABLE, values, CallRecordInfo._ID+" = "+uri.getLastPathSegment() + (!TextUtils.isEmpty(selection)?" AND "+selection:""), selectionArgs);
			break;

		default:
			break;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return 0;
	}

}

class RecordDatabaseHelp extends SQLiteOpenHelper {

	private String TAG = "sqliteHelper";
	Context context = null;
	public RecordDatabaseHelp(Context context) {
		super(context, RecordInfo.DATABASE_NAME, null, RecordInfo.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CallRecordInfo.CREATE_STATEMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		return;
	}
	
	long insertNewCall (ContentValues values) {
		String fileName = values.getAsString(CallRecordInfo.FILENAME);
	    String phoneNum = values.getAsString(CallRecordInfo.PHONENUM);
	    String name = values.getAsString(CallRecordInfo.NAMEINCONTACT);
	    String callTime = values.getAsString(CallRecordInfo.CALLTIME);
	    int callLen = values.getAsInteger(CallRecordInfo.CALLLENTH);
	    int isInCall = values.getAsInteger(CallRecordInfo.CALL_IN_OUT);
	    
	    return insertNewCall(fileName, phoneNum, name, callTime, callLen, isInCall);
	}
	
	long insertNewCall(String fileName, String phoneNum, String name, String callTime, int callLen, int isInCall) {
		if ((null == fileName) || (null == phoneNum) || (null == name) || (null == callTime)) {
			Log.e(TAG, "when insert some data is null");
			throw new IllegalArgumentException("when insert, the datas can not be null+" +
					" fileName="+fileName+" phoneNum="+phoneNum+" name="+name
					+" callTime="+callTime);
		}
		Cursor c = null;
		long fileID = 0;
		try
		{
			SQLiteDatabase db = getWritableDatabase();
			
			
			ContentValues values = new ContentValues();
			values.put(CallRecordInfo.FILENAME, fileName);
			values.put(CallRecordInfo.PHONENUM, phoneNum);
			values.put(CallRecordInfo.NAMEINCONTACT, name);
			values.put(CallRecordInfo.CALLTIME, callTime);
			values.put(CallRecordInfo.CALLLENTH, callLen);
			values.put(CallRecordInfo.CALL_IN_OUT, isInCall);
			
			int updated = db.update(CallRecordInfo.TABLE, values, CallRecordInfo.FILENAME+" = ?", new String[] {fileName});
			if (0 == updated) {
				fileID = db.insert(CallRecordInfo.TABLE, null, values);
			}
			else {
				c = db.query(CallRecordInfo.TABLE, new String[] {CallRecordInfo._ID}, CallRecordInfo.FILENAME+" = ?", new String[] {fileName}, null, null, null);
				if (c.moveToFirst()) {
					fileID = c.getLong(0);
				}
			}
		}
		catch (Exception e){
			Log.e(TAG, "insert something to to db error");
		}
		finally {
			if (null != c) {
				c.close();
			}
		}	
		return fileID;
	}
}