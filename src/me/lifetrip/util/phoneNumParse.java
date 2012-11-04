//!NOTIFICATION: 使用这个类需要查阅通讯录的权限

package me.lifetrip.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

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

/*
 * @NOTIFICATION: 使用这个类需要查阅通讯录的权限
 * @author    leiyanzhang 
 * @version   ver 1.0 
 * @Description   该类的主要作用是结合电话薄对电话号码进行解析，一方面得到电话的类型(手机号，固化等)，另一方面对于固话
 *                还可以单独得到电话的区号。当前仅支持国内的电话号码，对于+86格式的电话号码也可以支持(包括电话本和收到的号码)
 *                对于一些很长的IP电话之类目前也不支持解析
 * @param    对方法的说明 对方法中某参数的说明 
 * @return    对方法的说明 对方法返回值的说明 
 * @exception  对方法的说明 对方法可能抛出的异常进行说明
*/
public class phoneNumParse {
	/*
	 * @Description	从原始号码里面解析出来对应的区号和去掉区号以后的号码
	 * see		  三位的电话区号："010", "020", "021", "022", "023", 
		          "024", "025", "027", "028", "029"
	 * @param     strOriginNum	input	原始的电话号码
	 *            strCountry		output	国家号，目前仅能区分出来+86的情况，该值为null
	 *            strArea		output	区号
	 *            strFinalNum	output	去掉区号以后的电话号码，如果电话类型未知的话，strFinalNum和strOriginNum相同 
	 * @return    返回电话的类型，具体归类见上面PHONETYPE的说明
	 * @exception  对方法的说明 对方法可能抛出的异常进行说明
	*/
    PHONETYPE parsePhoneNum(String strOriginNum, String strCountry, String strArea, String strFinalNum)
    {
	    	String strRealPhone = strOriginNum;
	    	PHONETYPE enPhoneType = PHONETYPE.PHONE_UNKONWN;
	    	strCountry = null;
	    	strArea = null;
	    	int num_len = strOriginNum.length();
	
	    if (13 == num_len)
	    {
	        	if (strRealPhone.substring(0, 2).equals("+86"))
	        	{
	        		strRealPhone = strRealPhone.substring(3);
	        	} 
	        	else {
				return enPhoneType;
			}
	    }
	   
	    strFinalNum = strRealPhone;
	    //如果电话号码，去掉前面的+86之后(如果有的话),剩下的位数不满足要求的话，认为非法直接返回
	    if (strRealPhone.length() > 11 || strRealPhone.length() < 7)
	    {
	    		return enPhoneType;
	    }
        
        switch (strRealPhone.length())
        {
            //这个位数表示是本地电话
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
	            	//对于三位区号，都是01或者02开头的；四位区号，是不会用这两个开始的
	            	if ('1' == strRealPhone.charAt(0))   //手机第一位是1
	            	{
	            		enPhoneType = PHONETYPE.MOBILEPHONENUM;
	            	}
	            	else if ('0' == strRealPhone.charAt(0))   //固话都是以0开始的
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
	 * @Description       从电话号码里面获取裸的手机号
	 * @param     strPhoneNum	input	原始的电话号码  
	 * @return    返回解析以后的裸露的手机号，如果这个号码不是可以解析的手机号的话(如固话)，则返回null
	 * @exception  对方法的说明 对方法可能抛出的异常进行说明
	*/
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
    }
    
	/*
	 * @Description       从电话号码获取对应的联系人名字, 需要获取通讯录的权限
	 * @param     strPhoneNum	input	原始的电话号码  
	 * @return    返回得到的通讯录中的联系人名字，如果没有找到的话回返回null
	 * @exception  对方法的说明 对方法可能抛出的异常进行说明
	*/    
    String getContactName(Context context, String strPhoneNum)
    {
	    	PHONETYPE enPhoneType = PHONETYPE.PHONE_UNKONWN;
	    	String strCountry = null;
	    	String strArea= null;
	    	String strFinalNum = null;   
	    	String selection = null;
	    	String name = null;
	    	Cursor c = null;
	    	
	    	if ((null == context) || (null == strPhoneNum))
	    		return null;
	    	
	    	enPhoneType = parsePhoneNum(strPhoneNum, strCountry, strArea, strFinalNum);
	    	if (PHONETYPE.MOBILEPHONENUM == enPhoneType)
	    	{
		    selection = ContactsContract.CommonDataKinds.Phone.NUMBER+" = "+'"' + strFinalNum +'"'
                         + " OR " + ContactsContract.CommonDataKinds.Phone.NUMBER+" = "+"\"+86" + strFinalNum +'"';
	    	}
	    	else if (PHONETYPE.TELEPHONE_LOCAL == enPhoneType)
	    	{
			    selection = ContactsContract.CommonDataKinds.Phone.NUMBER+" = "+'"' + strFinalNum +'"';	    		
	    	}
	    	else 
	    	{
			    selection = ContactsContract.CommonDataKinds.Phone.NUMBER+" = "+'"' + strPhoneNum +'"';			
		}
	    	
	    	try {
	        c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
	                                                      null, selection, null, null);
	        //找到了对应的联系人        
	        if ((null != c) && (0 != c.getCount()))
	        {
		        	int nameFieldColumnIndex = c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
		        	name = c.getString(nameFieldColumnIndex);
	        }
	    	}
	    	catch (Exception e) {
	    		return null;
	    	}
	    	finally {
	    		if (null != c) {
	    			c.close();	
	    		}
	    	}
        return name;
    }
}
