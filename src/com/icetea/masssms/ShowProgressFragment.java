package com.icetea.masssms;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ShowProgressFragment extends Fragment implements Contanst {

	

	static ShowProgressFragment mFragment;
	
	ProgressItemAdapter mProgressItemAdapter;
	
	
	View view;
	ListView listView;
	int progressCount;
	
	HashSet<Integer> mProgressSet = new HashSet<Integer>();
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle bundle;
			bundle = msg.getData();
			int key = bundle.getInt(SEND_COUNT);
			int maxValue = bundle.getInt(ITEMS_NUM);
			String messageText = bundle.getString(MESSAGE_TEXT);
			switch(msg.what){
			case SUCCESS:
				
				
				if(mProgressSet.add(Integer.valueOf(key)) == false){//already has
					mProgressItemAdapter.updateOne(key);
				}
				else{
					mProgressItemAdapter.addOne(key,1,maxValue,messageText);
				}
				
				break;
			case FAILED:
				break;
			case START_SEND:
				
				mProgressSet.add(Integer.valueOf(key));
				mProgressItemAdapter.addOne(key,0,maxValue,messageText);
				break;
			}
			
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		view = inflater.inflate(R.layout.show_progress_fragment, null);
		listView = (ListView) view.findViewById(R.id.progress_list_id);
		mProgressItemAdapter = new ProgressItemAdapter(getMainActivity());
		listView.setAdapter(mProgressItemAdapter);
		
		//progressCount = getArguments().getInt("PROGRESS_COUNT");
		
		getMainActivity().setProgressHandler(mHandler);
		
		return view;
	}

	
	private MainActivity getMainActivity() {
		// TODO Auto-generated method stub
		return (MainActivity)getActivity();
	}


	public static ShowProgressFragment getInstance(){
		if(mFragment == null)
			mFragment = new ShowProgressFragment();
		return mFragment;
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	
}
