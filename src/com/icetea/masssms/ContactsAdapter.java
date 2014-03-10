package com.icetea.masssms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import com.icetea.masssms.MainActivity.ContactBean;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.TextView;

public class ContactsAdapter extends BaseAdapter {

	
	private MainActivity mContext;
	WeakHashMap<String, ContactBean> mContacts;
	ArrayList<ContactBean> mContactsList = new ArrayList<MainActivity.ContactBean>();
	boolean[]  isSelected;
	
	public ContactsAdapter(MainActivity context) {
		// TODO Auto-generated constructor stub
		super();
		mContext = context;
		mContacts = context.getContacts();
		isSelected = new boolean[mContacts.size()];
		for(int i = 0;i < isSelected.length;i++){
			isSelected[i]= false;			
		}
		Iterator iterator = mContacts.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, ContactBean> entry = (Entry<String, ContactBean>) iterator.next();
			if(entry != null)
				mContactsList.add(entry.getValue());
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mContacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(position < mContacts.size())
			return mContacts.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View view = convertView;
		ViewHolder holder = null;
		ContactBean bean = mContactsList.get(position);
		if(view == null){
			view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item, null);
			holder = new ViewHolder();
			holder.textView = (TextView)view.findViewById(R.id.text_id);
			holder.checkable = (Checkable)view.findViewById(R.id.checked_id);
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		BitmapDrawable bmpDrawable = new BitmapDrawable(mContext.getResources(), bean.photo);
		
		Drawable drawable = bmpDrawable;
		
		drawable.setBounds(bmpDrawable.getBounds());
		
		holder.textView.setCompoundDrawables(drawable, null, null, null);
		
		holder.textView.setText(bean.name + "  " + bean.number);
		
		holder.checkable.setChecked(isSelected[position]);
		
		
		return view;
	}
	public void setContacts(WeakHashMap<String, ContactBean> mContacts2){
		mContacts = mContacts2;
		isSelected = new boolean[mContacts.size()];
		for(int i = 0;i < isSelected.length;i++)
			isSelected[i] = false;
		
		Iterator iterator = mContacts.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, ContactBean> entry = (Entry<String, ContactBean>) iterator.next();
			if(entry != null)
				mContactsList.add(entry.getValue());
		}
	}
	
	public void setSelected(int position,boolean selected){
		if(position < isSelected.length)
			isSelected[position] = selected;
	}
	
	public boolean[] getSelected(){
		return isSelected;
	}
	public void clearSelected(){
		for(int i = 0;i < isSelected.length;i++)
			isSelected[i] = false;
	}
	public class ViewHolder {
		public TextView textView;
		public Checkable checkable;
	}
}
