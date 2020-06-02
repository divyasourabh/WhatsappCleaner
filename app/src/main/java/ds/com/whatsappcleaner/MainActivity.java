package ds.com.whatsappcleaner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    Context mContext;
    private SharedDataStore mSharedDataStore;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mContext = this;
        AppRater.app_launched(mContext);
        initAd(mContext);
        mSharedDataStore = SharedDataStore.getInstance(mContext);
        Utils.mCurrentSortBy = mSharedDataStore.getCurrentSortBy();
        Utils.mCurrentInOrder = mSharedDataStore.getCurrentInOrder();
        Utils.mFirstTimeLaunch = mSharedDataStore.getFirstTimeStatus();
        if (Utils.mFirstTimeLaunch) {
//            if(Utils.verificationCodeOnStart)
//                Utils.shareVerificationCodeDialog(mContext);
            Utils.addShortcut(mContext);
            mSharedDataStore.setFirstTimeStatus(false);
        }
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (Utils.isSelectMode) {
            List<Fragment> a = getSupportFragmentManager().getFragments();
            for (Fragment f : a) {
                if (f instanceof ImageFragment) {
                    ImageFragment w = (ImageFragment) f;
                    w.removeSelectMode();
                } else if (f instanceof AudioFragment) {
                    AudioFragment w = (AudioFragment) f;
                    w.removeSelectMode();
                } else if (f instanceof VideoFragment) {
                    VideoFragment w = (VideoFragment) f;
                    w.removeSelectMode();
                } else {
                    VoiceFragment w = (VoiceFragment) f;
                    w.removeSelectMode();
                }
            }
            return;
        }
        super.onBackPressed();
        if (Utils.IsFreeDownloadCountExceed(mContext)) {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_storage_details:
                StorageDialog();
                return true;
            case R.id.action_rate_us:
                if( mContext != null ){
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppPlayStoreLink)));
                }
                return true;
            case R.id.action_about_us:
                startActivity(new Intent(mContext, About_Us_Activity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        if (Utils.isSelectMode) {
            List<Fragment> a = getSupportFragmentManager().getFragments();
            for (Fragment f : a) {
                if (f instanceof ImageFragment) {
                    ImageFragment w = (ImageFragment) f;
                    w.removeSelectMode();
                } else if (f instanceof AudioFragment) {
                    AudioFragment w = (AudioFragment) f;
                    w.removeSelectMode();
                } else if (f instanceof VideoFragment) {
                    VideoFragment w = (VideoFragment) f;
                    w.removeSelectMode();
                } else {
                    VoiceFragment w = (VoiceFragment) f;
                    w.removeSelectMode();
                }
            }
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                Utils.isAudio = false;
                Utils.isImage = true;
                Utils.isVideo = false;
                Utils.isVoice = false;
                return ImageFragment.newInstance(position + 1, mContext);
            } else if (position == 1) {
                Utils.isAudio = true;
                Utils.isImage = false;
                Utils.isVideo = false;
                Utils.isVoice = false;
                return AudioFragment.newInstance(position + 1, mContext);
            } else if (position == 2) {
                Utils.isAudio = false;
                Utils.isImage = false;
                Utils.isVideo = true;
                Utils.isVoice = false;
                return VideoFragment.newInstance(position + 1, mContext);
            } else {
                Utils.isAudio = false;
                Utils.isImage = false;
                Utils.isVideo = false;
                Utils.isVoice = true;
                return VoiceFragment.newInstance(position + 1, mContext);
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l) + "\n" +
//                            Utils.folderFileCount(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Images")) + " files - " /*+ "\n"*/ +
                            Utils.returnSize(Utils.folderSize(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Images")));

                case 1:
                    return getString(R.string.title_section2).toUpperCase(l) + "\n" +
//                            Utils.folderFileCount(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Audio")) + " files - " /*+ "\n"*/ +
                            Utils.returnSize(Utils.folderSize(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Audio")));
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l) + "\n" +
//                            Utils.folderFileCount(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Video")) + " files - " /*+ "\n"*/ +
                            Utils.returnSize(Utils.folderSize(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Video")));
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l) + "\n" +
//                            Utils.folderFileCount(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Voice Notes")) + " files - " /*+ "\n"*/ +
                            Utils.returnSize(Utils.folderSize(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Voice Notes")));
            }
            return null;
        }
    }

    private void initAd(Context context) {

        if (Utils.isDevMode) {
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId("ca-app-pub-123456789/123456789");

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("6F34A77DBC1CE047184CBCBF1EF48D04")
                    .build();
            mInterstitialAd.loadAd(adRequest);
        } else {
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(getString(R.string.Interstitial_ad_ad_unit_id));
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            mInterstitialAd.loadAd(adRequestBuilder.build());
        }
    }

    public void StorageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Storage details");
        builder.setMessage(Html.fromHtml(
                "<b>" + "IMAGE :- " + "</b>" +
                        Utils.folderFileCount(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Images")) + " files - " +
                        Utils.returnSize(Utils.folderSize(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Images"))) + "<br><br>" +

                        "<b>" + "AUDIO :- " + "</b>" +
                        Utils.folderFileCount(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Audio")) + " files - " +
                        Utils.returnSize(Utils.folderSize(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Audio"))) + "<br><br>" +

                        "<b>" + "VIDEO :- " + "</b>" +
                        Utils.folderFileCount(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Video")) + " files - " +
                        Utils.returnSize(Utils.folderSize(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Video"))) + "<br><br>" +

                        "<b>" + "VOICE :- " + "</b>" +
                        Utils.folderFileCount(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Voice Notes")) + " files - " +
                        Utils.returnSize(Utils.folderSize(new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Voice Notes"))) + "<br>"
        ));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });
        AlertDialog alert = builder.create();
        try {
            alert.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
