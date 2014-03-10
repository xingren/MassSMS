package com.icetea.masssms;

import android.provider.ContactsContract.CommonDataKinds.Phone;

public interface Contanst {
	public static final int SUCCESS = 0;
	public static final int FAILED = 1;
	public static final int START_SEND = 3;
	/**获取库Phon表字段**/  
	public static final String[] PHONE_PROJECTION = {Phone.DISPLAY_NAME,Phone.NUMBER,Phone.PHOTO_ID,Phone.CONTACT_ID};	 
	  /**联系人显示名称**/  
	public static final int PHONES_DISPLAY_NAME_INDEX = 0;  
      
    /**电话号码**/  
	public static final int PHONES_NUMBER_INDEX = 1;  
      
    /**头像ID**/  
	public static final int PHONES_PHOTO_ID_INDEX = 2;  
     
    /**联系人的ID**/  
	public static final int PHONES_CONTACT_ID_INDEX = 3;
	public static final String NUMBER = "NUMBER";
	public static final String TAG = "MassSMS";
	public static final String SEND_COUNT = "COUNT";
	public static final String ITEMS_NUM = "ITEMS_NUM";  
	public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
}
