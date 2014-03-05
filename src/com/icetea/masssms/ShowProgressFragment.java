package com.icetea.masssms;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class ShowProgressFragment extends Fragment {

	static ShowProgressFragment mFragment;
	
	
	View view;
	int progressCount;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		view = inflater.inflate(R.layout.show_progress_fragment, null);
		
		progressCount = getArguments().getInt("PROGRESS_COUNT");
		
		return view;
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
