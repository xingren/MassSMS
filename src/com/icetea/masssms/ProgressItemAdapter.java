package com.icetea.masssms;

import java.util.ArrayList;
import java.util.WeakHashMap;

import org.w3c.dom.Text;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressItemAdapter extends BaseAdapter {

	
	class KeyPositionPair
	{
		public int key;//indicate MainActivity sendCount
		public int value; //indicate progress value
		public int maxValue;
		public String message_text;
	}


	protected static final int DELETE_PROGRESS = 2;


	private static final String TAG = "ProgressItemAdapter";
	
	
	Handler mHander = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch (msg.what) {
			case DELETE_PROGRESS:
				Log.d(TAG,"be remove " + msg.arg1);
				removeItem(msg.arg1);
				ProgressItemAdapter.this.notifyDataSetChanged();
				break;

			default:
				break;
			}
			
		}
		
	};
	
	ArrayList<KeyPositionPair> mProgressValueList = new ArrayList<KeyPositionPair>();
	
	MainActivity mContext;
	
	public ProgressItemAdapter(MainActivity context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mProgressValueList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(position < mProgressValueList.size())
			return mProgressValueList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		if(position < mProgressValueList.size())
			return mProgressValueList.size();
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View view ;
		ViewHolder holder;
		if(convertView != null){
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		else{
			view = LayoutInflater.from(mContext).inflate(R.layout.progress_list_item, null);
			holder = new ViewHolder();
			holder.progressBar = (ProgressBar) view.findViewById(R.id.progress_id);
			holder.tv = (TextView)view.findViewById(R.id.progress_message_text_id);
			view.setTag(holder);
		}
		
		holder.progressBar.setMax(mProgressValueList.get(position).maxValue);
		holder.progressBar.setProgress(mProgressValueList.get(position).value);
		holder.tv.setText(String.valueOf(mProgressValueList.get(position).value) + "/" +
		String.valueOf(mProgressValueList.get(position).maxValue) + "  " +
				mProgressValueList.get(position).message_text);
		return view;
	}
	class ViewHolder{
		public ProgressBar progressBar;
		public TextView tv;
	}
	
	void removeItem(int key){
		
		int i;
		for(i = 0;i < mProgressValueList.size();i++){
			if(mProgressValueList.get(i).key == key){
				break;
			}
		}
		if(i < mProgressValueList.size())
			mProgressValueList.remove(i);
		
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		
		
		ArrayList<Integer> mRemoves = new ArrayList<Integer>();
		
		for(int i = 0;i < mProgressValueList.size();i++){
			if(mProgressValueList.get(i).value == mProgressValueList.get(i).maxValue){
				mRemoves.add(mProgressValueList.get(i).key);
			}
		}
		
		for(int i = 0;i < mRemoves.size();i++){
				Message msg = new Message();
				msg.what = DELETE_PROGRESS;
				msg.arg1 = mRemoves.get(i);
				mHander.sendMessageDelayed(msg,5000);
		}
		
		
		super.notifyDataSetChanged();
	}

	public void updateOne(int key) {
		// TODO Auto-generated method stub
		for(int i = 0;i < mProgressValueList.size();i++)
			if(mProgressValueList.get(i).key == key)
				mProgressValueList.get(i).value++;
		
		notifyDataSetChanged();
	}

	public void addOne(int key,int value, int maxValue, String messageText) {
		// TODO Auto-generated method stub
		Log.d(TAG,"add one " + "key " + key + " maxValue " + maxValue + " message text " + messageText);
		KeyPositionPair pair = new KeyPositionPair();
		pair.key = key;
		pair.value = value;
		pair.maxValue = maxValue;
		pair.message_text = messageText;
		mProgressValueList.add(pair);		
		notifyDataSetChanged();
	}
	
}
