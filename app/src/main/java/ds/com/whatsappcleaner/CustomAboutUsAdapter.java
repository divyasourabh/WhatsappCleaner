package ds.com.whatsappcleaner;

import java.util.List;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class CustomAboutUsAdapter extends ArrayAdapter<String>{

	Context mContext;
	ViewHolder viewHolder;
	List<String> appsList;
	List<String> appsDesc;
	List<String> appImage;

	int[] appImageList = new int[]{R.drawable.phoneexplorer,R.drawable.shareapp,R.drawable.appmanager,R.drawable.fourpicsoonewords, R.drawable.notificationhider};

	public CustomAboutUsAdapter(Context context, int textViewResourceId,List<String> AppsList,List<String> AppsDesc,List<String> appImage) {
		super(context, textViewResourceId,AppsList);
		mContext = context;
		this.appsList = AppsList;
		this.appsDesc = AppsDesc;
		this.appImage = appImage;
	}

	private class ViewHolder{
		TextView AppName;
		TextView AppDesc;
		ImageView appIcon;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.aboutus_app_list_row, null);
			viewHolder = new ViewHolder();
			viewHolder.AppName = (TextView) convertView.findViewById(R.id.app_title);
			viewHolder.AppDesc = (TextView) convertView.findViewById(R.id.app_description);
			viewHolder.appIcon = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag( viewHolder);
		}
		else{
			
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.AppName.setText(appsList.get(position));
		viewHolder.AppDesc.setText(appsDesc.get(position));
//		if(appNumber < appImageList.length)
		
		TypedArray imgs = mContext.getResources().obtainTypedArray(R.array.appIcon);
		//get resourceid by index
		imgs.getResourceId(position, -1);
		// or set you ImageView's resource to the id
		viewHolder.appIcon.setImageResource(imgs.getResourceId(position, -1));
		imgs.recycle();
		
//			viewHolder.appIcon.setImageResource(appImageList[position]);
//		appNumber++;
		return convertView;

	}

}