//!NOTIFICATION: ʹ���������Ҫ����ͨѶ¼��Ȩ��

package me.lifetrip.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

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

/*
 * @NOTIFICATION: ʹ���������Ҫ����ͨѶ¼��Ȩ��
 * @author    leiyanzhang 
 * @version   ver 1.0 
 * @Description   �������Ҫ�����ǽ�ϵ绰���Ե绰������н�����һ����õ��绰������(�ֻ��ţ��̻���)����һ������ڹ̻�
 *                �����Ե����õ��绰�����š���ǰ��֧�ֹ��ڵĵ绰���룬����+86��ʽ�ĵ绰����Ҳ����֧��(�����绰�����յ��ĺ���)
 *                ����һЩ�ܳ���IP�绰֮��ĿǰҲ��֧�ֽ���
 * @param    �Է�����˵�� �Է�����ĳ������˵�� 
 * @return    �Է�����˵�� �Է�������ֵ��˵�� 
 * @exception  �Է�����˵�� �Է��������׳����쳣����˵��
*/
public class phoneNumParse {
	/*
	 * @Description	��ԭʼ�����������������Ӧ�����ź�ȥ�������Ժ�ĺ���
	 * see		  ��λ�ĵ绰���ţ�"010", "020", "021", "022", "023", 
		          "024", "025", "027", "028", "029"
	 * @param     strOriginNum	input	ԭʼ�ĵ绰����
	 *            strCountry		output	���Һţ�Ŀǰ�������ֳ���+86���������ֵΪnull
	 *            strArea		output	����
	 *            strFinalNum	output	ȥ�������Ժ�ĵ绰���룬����绰����δ֪�Ļ���strFinalNum��strOriginNum��ͬ 
	 * @return    ���ص绰�����ͣ�������������PHONETYPE��˵��
	 * @exception  �Է�����˵�� �Է��������׳����쳣����˵��
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
	    //����绰���룬ȥ��ǰ���+86֮��(����еĻ�),ʣ�µ�λ��������Ҫ��Ļ�����Ϊ�Ƿ�ֱ�ӷ���
	    if (strRealPhone.length() > 11 || strRealPhone.length() < 7)
	    {
	    		return enPhoneType;
	    }
        
        switch (strRealPhone.length())
        {
            //���λ����ʾ�Ǳ��ص绰
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
	            	//������λ���ţ�����01����02��ͷ�ģ���λ���ţ��ǲ�������������ʼ��
	            	if ('1' == strRealPhone.charAt(0))   //�ֻ���һλ��1
	            	{
	            		enPhoneType = PHONETYPE.MOBILEPHONENUM;
	            	}
	            	else if ('0' == strRealPhone.charAt(0))   //�̻�������0��ʼ��
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
	 * @Description       �ӵ绰���������ȡ����ֻ���
	 * @param     strPhoneNum	input	ԭʼ�ĵ绰����  
	 * @return    ���ؽ����Ժ����¶���ֻ��ţ����������벻�ǿ��Խ������ֻ��ŵĻ�(��̻�)���򷵻�null
	 * @exception  �Է�����˵�� �Է��������׳����쳣����˵��
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
	 * @Description       �ӵ绰�����ȡ��Ӧ����ϵ������, ��Ҫ��ȡͨѶ¼��Ȩ��
	 * @param     strPhoneNum	input	ԭʼ�ĵ绰����  
	 * @return    ���صõ���ͨѶ¼�е���ϵ�����֣����û���ҵ��Ļ��ط���null
	 * @exception  �Է�����˵�� �Է��������׳����쳣����˵��
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
	        //�ҵ��˶�Ӧ����ϵ��        
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
