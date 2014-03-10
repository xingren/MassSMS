package com.icetea.masssms.adapter;

import com.icetea.masssms.MainActivity;
import com.icetea.masssms.R;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelSpinnerAdapter extends BaseAdapter {

	MainActivity mContext;
	String fragment_names[] = {"发送信息","显示进度"};
	public SelSpinnerAdapter(MainActivity context) {
		// TODO Auto-generated constructor stub
		super();
		mContext = context;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragment_names.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return fragment_names[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View view = null;
		ViewHolder holder;
		if(convertView != null){
			view = convertView;
			holder = (ViewHolder) view.getTag();
			
		}
		else{
			view = LayoutInflater.from(mContext).inflate(R.layout.spiner_adapter, null);
			holder = new ViewHolder();
			holder.tv = (TextView)view.findViewById(R.id.fragment_name_id);
			view.setTag(holder);
		}
		
		holder.tv.setText(fragment_names[position]);
		
		return view;
	}
	class ViewHolder{
		public TextView tv;
	}
}
