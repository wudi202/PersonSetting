package me.lifetrip.view;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import me.lifetrip.provider.RecordInfo.CallRecordInfo;
import me.lifetrip.service.telService;
import me.lifetrip.util.RecordStruct;
import me.lifetrip.util.SearchRecord;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.StaticLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ShowRecordActivity extends ListActivity {
    public SearchRecord searchResult = null;
    public static final int showGroupByDate = 1;
    private static final int showGroupByName = 2;
    private static final int showSpecial = 3;
    private static String SHOWTYPE = "showtype";
    private static String SPECIALNUM = "specialnum";
    public static final String TAG = "ShowRecordActivity";
    
    private int showType =showGroupByDate;    
    private String specialNumString = null;
    Cursor curRecordCursor = null;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			OpenAlertDialog("警告", "没有插入SD卡");
			return;
		}
		
		String callNumString = String.valueOf(((TextView)v.findViewById(R.id.callnum)).getText());
		String callTimeString = String.valueOf(((TextView)v.findViewById(R.id.calltime)).getText());
		
		String selection = CallRecordInfo.CALLTIME + " = ? AND " + CallRecordInfo.NAMEINCONTACT + " = ?";
		String selectionArgs[] = new String[] {callTimeString, callNumString};
		Cursor c = null;
		try {
		c = getContentResolver().query(CallRecordInfo.CONTENT_URI, new String[] {CallRecordInfo.FILENAME}, selection, selectionArgs, null);
        if (c.moveToFirst()) {
        	    String Filename = c.getString(0);
        	    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+telService.dirName+"/"+Filename;
        	    File file = new File(filePath);
        	    openFile(file);
        	    c.close();
        }
        }
		catch (Exception e) {
			Log.e(TAG, "error in open file: "+e.getMessage());
		}
		finally {
			if (null != c) {
				c.close();
			}
		}
		
	}
	
    private void OpenAlertDialog(String myTitle, String myMsg)
    {
	    	AlertDialog.Builder myBuilder = new AlertDialog.Builder(this).setTitle(myTitle).setMessage(myMsg);
	    	
	    	myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				return;
			}

		});
    	
	    	AlertDialog myAlertDiag = myBuilder.create();
	    	myAlertDiag.show();
    }	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		SimpleCursorAdapter adapter;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showrecord);
		if (null == searchResult) {
			searchResult = new SearchRecord(this);
		}
		if (null != savedInstanceState)
		{
			showType = savedInstanceState.getInt(SHOWTYPE);
			specialNumString = savedInstanceState.getString(SPECIALNUM);
		}
		try {
			curRecordCursor = getShowResult();
	        if (null != curRecordCursor)
	        {
	        	    startManagingCursor(curRecordCursor);
	        	    String[] col = new String[] {CallRecordInfo.NAMEINCONTACT, CallRecordInfo.CALLTIME,
	        	    		                         CallRecordInfo.CALL_IN_OUT, CallRecordInfo.CALLLENTH};
	            int[] name = new int[] {R.id.callnum, R.id.calltime, R.id.inout, R.id.calllen};
	            adapter = new SimpleCursorAdapter(this, R.layout.showrecord, curRecordCursor, col, name);
	            this.setListAdapter(adapter);
	        }
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		finally {
			if (null != curRecordCursor) {
				curRecordCursor.close();
			}
		}			
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt(SHOWTYPE, showType);
		outState.putString(SPECIALNUM, specialNumString);
		return;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
	
	}
	
	Cursor getShowResult() {
		Cursor curResult = null;
		try {
		switch (showType) {
			case showGroupByDate:
				curResult = searchResult.listAllRecord_byDate2();
				break;
			case showGroupByName:
				curResult = searchResult.listAllRecord_byName2();
				break;
			case showSpecial:
				curResult = searchResult.listALLRecord_forNum2(specialNumString);
				break;
			default:
				break;
			}
		}
		catch (Exception e) {
			Log.e(TAG, "showType="+String.valueOf(showType)+" SpecialNum="+specialNumString+
					" err info: "+e.getMessage());
		}
		return curResult;
	}
	
	private void openFile(File f) 
    {
      Intent intent = new Intent();
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setAction(android.content.Intent.ACTION_VIEW);
      
      /* 调用getMIMEType()来取得MimeType */
      String type = getMIMEType(f);
      /* 设置intent的file与MimeType */
      intent.setDataAndType(Uri.fromFile(f),type);
      startActivity(intent); 
    }

    /* 判断文件MimeType的method */
    private String getMIMEType(File f) 
    { 
      String type="";
      String fName=f.getName();
      /* 取得扩展名 */
      String end=fName.substring(fName.lastIndexOf(".")
      +1,fName.length()).toLowerCase(); 
      
      /* 依扩展名的类型决定MimeType */
      if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
      end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
      {
        type = "audio"; 
      }
      else if(end.equals("3gp")||end.equals("mp4"))
      {
        type = "video";
      }
      else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
      end.equals("jpeg")||end.equals("bmp"))
      {
        type = "image";
      }
      else if(end.equals("apk")) 
      { 
        /* android.permission.INSTALL_PACKAGES */ 
        type = "application/vnd.android.package-archive"; 
      } 
      else
      {
        type="*";
      }
      /*如果无法直接打开，就跳出软件列表给用户选择 */
      if(end.equals("apk")) 
      { 
      } 
      else 
      { 
        type += "/*";  
      } 
      return type;  
    } 
	
}