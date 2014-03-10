package com.icetea.masssms;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.icetea.masssms.R;
import com.icetea.masssms.MainActivity.SendTextTask;
public class InputMessageFragment extends Fragment {
	EditText selContacts;
	EditText sendText;
	Button sendBtn;
	View view;
	String numbers = new String();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.input_message_fragment, null);
		selContacts = (EditText)view.findViewById(R.id.contacts_text_id);
		sendText = (EditText)view.findViewById(R.id.send_text_id);
		sendBtn = (Button)view.findViewById(R.id.send_btn_id);
		selContacts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				getMainActivity().showSelDialog();
			}
		});
		
		sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				
				if(numbers == null || TextUtils.isEmpty(numbers)){
					numbers = selContacts.getText().toString();
					if(TextUtils.isEmpty(numbers)){
						Toast.makeText(getMainActivity(), "number is null", Toast.LENGTH_SHORT).show();
						return ;
					}
				}
				if(TextUtils.isEmpty(getMessageText())){
					Toast.makeText(getMainActivity(), "message text is null", Toast.LENGTH_SHORT).show();
					return ;
				}
				
				SendTextTask task = getMainActivity().new SendTextTask();
				task.execute(numbers,getMessageText().toString());
				getMainActivity().showProgress();
			}
		});
		
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
	
	public void setNumbers(String numberString){
		numbers = numberString;
	}
	
	private void calControlsLocation(){
		DisplayMetrics metrics = new DisplayMetrics();
		getMainActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		
		ViewGroup.LayoutParams params = selContacts.getLayoutParams();
		
		selContacts.setX((float) (width*0.3));
		selContacts.setY((float) (height*0.1));
		selContacts.setWidth((int) (width*0.4));
		sendText.setX((float) (width*0.3));
		sendText.setY((float) (height * 0.2));
		sendText.setWidth((int) (width*0.4));
		sendBtn.setX((float)(width*0.4));
		sendBtn.setY((float) (height*0.6));
	}
	
	
}
