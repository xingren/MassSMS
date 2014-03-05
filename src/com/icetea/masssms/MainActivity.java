package com.icetea.masssms;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.WeakHashMap;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	

	/**获取库Phon表字段**/  
	private static final String[] PHONE_PROJECTION = {Phone.DISPLAY_NAME,Phone.NUMBER,Phone.PHOTO_ID,Phone.CONTACT_ID};	 
	  /**联系人显示名称**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
      
    /**电话号码**/  
    private static final int PHONES_NUMBER_INDEX = 1;  
      
    /**头像ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
     
    /**联系人的ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 3;
	public static final String NUMBER = "NUMBER";
	public static final String TAG = "MassSMS";
	public static final String SEND_COUNT = "COUNT";  

    
    WeakHashMap<String, ContactBean> mContacts = new WeakHashMap<String,ContactBean>();
    ArrayList<ArrayList<String>> mSendFailedList = new ArrayList<ArrayList<String>>();
    AlertDialog.Builder dialogBuilder;
    ContactsAdapter mContactsAdapter;
    ListView mContactListView;
    View layoutView;
    AlertDialog selectedDialog;
    Button okBtn,cancleBtn;
    static int sendCount = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		mContactsAdapter = new ContactsAdapter(this);
		getContactsFromDb();
		layoutView = getLayoutInflater().inflate(R.layout.select_constact, null);
		mContactListView = (ListView)layoutView.findViewById(R.id.constacts_list_id);
		mContactListView.setAdapter(mContactsAdapter);
		mContactListView.setOnItemClickListener(itemClickListener);
		okBtn = (Button)layoutView.findViewById(R.id.ok_id);
		cancleBtn = (Button)layoutView.findViewById(R.id.cancle_id);
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StringBuilder builder = new StringBuilder();
				
				boolean[] selected = mContactsAdapter.getSelected();
				int count = 0;
				for(int i = 0;i < selected.length;i++){
					if(selected[i]){
						builder.append(((ContactBean)mContactsAdapter.getItem(i)).number + ";");
					}
				}
				SendTextTask task = new SendTextTask();
				task.execute(builder.toString(),sendText.getText().toString());
			}
		});
		
		dialogBuilder = new AlertDialog.Builder(this).setTitle("选择联系人");
		dialogBuilder.setView(layoutView);
		dialogBuilder.setCancelable(true);
		selectedDialog = dialogBuilder.create();
		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	class ContactBean{
		public Long id;
		public String name;
		public String number;
		public Bitmap photo;
		
	}
	
	public WeakHashMap<String, ContactBean> getContacts(){
		return mContacts;
	}
	
	void getContactsFromDb(){
		
		ContentResolver resolver = this.getContentResolver();
		
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONE_PROJECTION, null,null,null);
		
		if(phoneCursor != null){
			while(phoneCursor.moveToNext()){
				
				ContactBean bean = new ContactBean();
				
				String number = phoneCursor.getString(PHONES_NUMBER_INDEX);
				
				if(TextUtils.isEmpty(number)){
					continue;
				}
				
				bean.number = number;
				bean.name = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				bean.id = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				Long photoId = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				Bitmap photo;
				
				if(photoId > 0){
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, bean.id);
					InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
					
					photo = BitmapFactory.decodeStream(stream);
					
				}else{
					photo = BitmapFactory.decodeResource(getResources(), R.drawable.contact_photo);
				}
				bean.photo = photo;
				mContacts.put( bean.number,bean);
				
			}
		}
		mContactsAdapter.setContacts(mContacts);
		if(mContactsAdapter != null)
			mContactsAdapter.notifyDataSetChanged();
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.checkable.toggle();
			mContactsAdapter.setSelected(position, holder.checkable.isChecked());
		}
	};
	
	class SendTextTask extends AsyncTask<String, Integer, Void>{

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			ArrayList<String> list = new ArrayList<String>();
			mSendFailedList.add(list);
			
			String numberString = params[0];
			
			String[] numbers = numberString.split(";");
			String text = params[1];
			
			for(int i = 0;i < numbers.length;i++){
				Intent intent = new Intent("SENT_SMS_ACTION");
				intent.putExtra(NUMBER, numbers[i]);
				intent.putExtra(SEND_COUNT, sendCount);
				PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(),i , intent, PendingIntent.FLAG_ONE_SHOT);
				SmsManager.getDefault().sendTextMessage(numbers[i], null, text, mSendPI,null);
			}
			sendCount ++;
			return null;
		}
		
	}
	
	class SMSSendResultReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String number = intent.getStringExtra(NUMBER);
			
			switch (getResultCode()) {
			case RESULT_OK:
				Log.d(TAG,number + " Send success");
				break;
			default:
				int tmpCount = intent.getIntExtra(SEND_COUNT, 0);
				if(tmpCount < mSendFailedList.size())
					mSendFailedList.get(tmpCount).add(number);
				Log.w(TAG, number + " Send failed");
				break;
			}
		}
		
	}
	
	
}
