package ds.com.whatsappcleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class About_Us_Activity extends Activity {

    LayoutInflater inflater;
    List<String> appList;
    List<String> appDescription;
    List<String> appIcon;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.in, R.anim.out);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_about__us);
        mContext = this;

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Resources re = getResources();

        String appTitle[] = re.getStringArray(R.array.appName);
        String appDesc[] = re.getStringArray(R.array.appdescription);
        String appsIcon[] = re.getStringArray(R.array.appIcon);

        appList = new ArrayList<String>();
        appDescription = new ArrayList<String>();
        appIcon = new ArrayList<String>();

        appList = Arrays.asList(appTitle);
        appDescription = Arrays.asList(appDesc);
        appIcon = Arrays.asList(appsIcon);

        ListView list = (ListView) findViewById(R.id.app_listview);

        final CustomAboutUsAdapter adapter = new CustomAboutUsAdapter(this, R.layout.aboutus_app_list_row, appList, appDescription, appIcon);

        list.setAdapter(adapter);

        //		list.setVisibility(View.GONE);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppPlayStoreLinks[position])));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.about__us_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
