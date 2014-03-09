package com.icetea.masssms;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InputMessageFragment extends Fragment {
	EditText selContacts;
	EditText sendText;
	Button sendBtn;
	View view;	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.input_message_fragment, null);
		
		selContacts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				getMainActivity().showSelDialog();
			}
		});
		selContacts = (EditText)view.findViewById(R.id.contacts_text_id);
		sendText = (EditText)view.findViewById(R.id.send_text_id);
		sendBtn = (Button)view.findViewById(R.id.send_btn_id);
		calControlsLocation();
		return view;
	}
	private MainActivity getMainActivity() {
		// TODO Auto-generated method stub
		return (MainActivity)getActivity();
	}
	public String getMessageText(){
		return sendText.getText().toString();
	}
	
	public
	
	private void calControlsLocation(){
		DisplayMetrics metrics = null;
		getMainActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		
		ViewGroup.LayoutParams params = selContacts.getLayoutParams();
		
		selContacts.setX((float) (width*0.3));
		selContacts.setY((float) (height*0.2));
		
		sendText.setX((float) (width*0.3));
		sendText.setY((float) (height * 0.4));
		
		
	}
	
	
}
