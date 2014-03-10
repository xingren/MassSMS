package com.icetea.masssms;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.WeakHashMap;

import com.icetea.masssms.ContactsAdapter.ViewHolder;
import com.icetea.masssms.adapter.SelSpinnerAdapter;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity implements OnNavigationListener,Contanst {

	WeakHashMap<String, ContactBean> mContacts = new WeakHashMap<String,ContactBean>();
    WeakHashMap<Integer,ArrayList<String>> mSendFailedList = new WeakHashMap<Integer,ArrayList<String>>();
    AlertDialog.Builder dialogBuilder;
    ContactsAdapter mContactsAdapter;
    ListView mContactListView;
    View layoutView;
    AlertDialog selectedDialog;
    Button okBtn,cancleBtn;
    static int sendCount = 0;
    
    InputMessageFragment mInputMessageFragment;
    ShowProgressFragment mShowProgressFragment;
    ActionBar mActionBar;
    private boolean isInputMessageFragment;
    
    public Handler mProgressHandler;
    
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
				Log.d(TAG, "dialog ok " + mInputMessageFragment.getMessageText());
				if(mInputMessageFragment == null || mInputMessageFragment.getMessageText() == null || TextUtils.isEmpty(mInputMessageFragment.getMessageText()))
					return;
				
				StringBuilder builder = new StringBuilder();
				
				boolean[] selected = mContactsAdapter.getSelected();
				int count = 0;
				for(int i = 0;i < selected.length;i++){
					if(selected[i]){
						builder.append(((ContactBean)mContactsAdapter.getItem(i)).number + ";");
					}
				}
				mInputMessageFragment.setNumbers(builder.toString());
				selectedDialog.dismiss();
			}
		});
		
		dialogBuilder = new AlertDialog.Builder(this).setTitle("选择联系人");
		dialogBuilder.setView(layoutView);
		dialogBuilder.setCancelable(true);
		selectedDialog = dialogBuilder.create();
		
		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setListNavigationCallbacks(new SelSpinnerAdapter(this), this);
		mInputMessageFragment = new InputMessageFragment();
		mShowProgressFragment = new ShowProgressFragment();
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_id, mInputMessageFragment);
		ft.show(mInputMessageFragment);
		ft.commit();
		isInputMessageFragment = true;
		
		registerReceiver(new SMSSendResultReceiver(), new IntentFilter("SENT_SMS_ACTION"));
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
		Log.d(TAG,"all contacts size " + mContacts.size());
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
	
	
	public class SendTextTask extends AsyncTask<String, Integer, Void>{

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			ArrayList<String> list = new ArrayList<String>();
			mSendFailedList.put(Integer.valueOf(sendCount),list);
			
			String numberString = params[0];
			
			String[] numbers = numberString.split(";");
			String text = params[1];
			
			for(int i = 0;i < numbers.length;i++){
				Intent intent = new Intent("SENT_SMS_ACTION");
				intent.putExtra(NUMBER, numbers[i]);
				intent.putExtra(SEND_COUNT, sendCount);
				intent.putExtra(ITEMS_NUM, numbers.length);
				intent.putExtra(MESSAGE_TEXT, text);
				PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(),i , intent, PendingIntent.FLAG_ONE_SHOT);
				SmsManager.getDefault().sendTextMessage(numbers[i], null, text, mSendPI,null);
				Message msg = new Message();
				msg.what = START_SEND;
				Bundle bundle = new Bundle();
				bundle.putInt(ITEMS_NUM, numbers.length);
				bundle.putInt(SEND_COUNT,sendCount);
				bundle.putString(MESSAGE_TEXT,text );
				msg.setData(bundle);
				mProgressHandler.sendMessage(msg);
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
			Bundle bundle;
			Message msg;
			switch (getResultCode()) {
			case RESULT_OK:
				Log.d(TAG,number + " Send success");
				msg = new Message();
				msg.what = SUCCESS;
				bundle = new Bundle();
				bundle.putInt(ITEMS_NUM, intent.getIntExtra(ITEMS_NUM, 0));
				bundle.putInt(SEND_COUNT,intent.getIntExtra(SEND_COUNT,0));
				bundle.putString(MESSAGE_TEXT, intent.getStringExtra(MESSAGE_TEXT));
				msg.setData(bundle);
				mProgressHandler.sendMessage(msg);
				break;
			default:
				int tmpCount = intent.getIntExtra(SEND_COUNT,0);
				if(tmpCount < mSendFailedList.size())
					mSendFailedList.get(Integer.valueOf(tmpCount)).add(number);
				msg = new Message();
				msg.arg1 = SUCCESS;
				msg.arg2 = getResultCode();
				mProgressHandler.sendMessage(msg);
				Log.w(TAG, number + " Send failed");
				break;
			}
		}
		
	}
	
	public void showSelDialog(){
		mContactsAdapter.clearSelected();
		selectedDialog.show();
	}
	
	public void sendMessageText(String text){
		
	}
	
	public void showProgress(){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_id, mShowProgressFragment);
		ft.show(mShowProgressFragment);
		isInputMessageFragment = false;
		ft.commit();
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		
		if(isInputMessageFragment && itemPosition == 0 || !isInputMessageFragment && itemPosition == 1)
			return false;
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		switch (itemPosition) {
		case 0:
			ft.replace(R.id.fragment_id, mInputMessageFragment);
			ft.show(mInputMessageFragment);
			isInputMessageFragment = true;
			break;
		case 1:
			ft.replace(R.id.fragment_id, mShowProgressFragment);
			ft.show(mShowProgressFragment);
			isInputMessageFragment = false;
			break;
		}
		ft.commit();

		return false;
	}
	
	public void setProgressHandler(Handler handler){
		mProgressHandler = handler;
	}
}
