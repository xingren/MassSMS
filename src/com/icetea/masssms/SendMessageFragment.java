package com.icetea.masssms;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SendMessageFragment extends Fragment {
	EditText selContacts;
	EditText sendText;
	View view;
	ContactsAdapter mContactsAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.send_message_fragment, null);
		mContactsAdapter = new ContactsAdapter(getMainActivity());
		selContacts.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				mContactsAdapter.clearSelected();
				selectedDialog.show();
			}
		});
		selContacts = (EditText)view.findViewById(R.id.contacts_text_id);
		sendText = (EditText)view.findViewById(R.id.send_text_id);
		return view;
	}
	private MainActivity getMainActivity() {
		// TODO Auto-generated method stub
		return (MainActivity)getActivity();
	}
	
	
	
}
