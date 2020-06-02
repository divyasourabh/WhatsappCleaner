package ds.com.whatsappcleaner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {


    public ImageFragment() {
        // Required empty public constructor
    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    static Context mContext;
    ImageAdapter mImageAdapter;
    List<FileInfo> imageList;

    public static ImageFragment newInstance(int sectionNumber, Context context) {
        mContext = context;
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initAd(getActivity());
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        //Ads
        mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest;
        if (Utils.isDevMode) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("6F34A77DBC1CE047184CBCBF1EF48D04")
                    .build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        mAdView.loadAd(adRequest);

        mSwipeRefreshLayout = ((SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadImage().execute();
            }
        });
        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        mImageAdapter = new ImageAdapter(mContext, imageList);

        new LoadImage().execute();

        mImageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view == null)
                    return;
                String filetype = MimeUtils.guessMimeTypeFromExtension(
                        Utils.getFileExtension(view.findViewById(R.id.filename).getTag().toString()));
                String path = view.findViewById(R.id.filename).getTag().toString();
                if (Utils.isSelectMode) {
                    if (mImageAdapter != null)
                        mImageAdapter.toggleSelection(position, view.findViewById(R.id.filename).getTag().toString());
//                    popMenu(view, path,filetype,position);
                } else {
                    doPlay(mContext, path, position, filetype);
                }
                //                intent.setDataAndType(Uri.fromFile(new File(view.findViewById(R.id.filename).getTag().toString())), filetype);
//                getActivity().startActivity(intent);
            }
        });

        mImageAdapter.setOnCardOptionClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {

                final String filetype = MimeUtils.guessMimeTypeFromExtension(
                        Utils.getFileExtension(view.findViewById(R.id.fileoptions).getTag().toString()));
                final String path = view.findViewById(R.id.fileoptions).getTag().toString();
                popMenu(view, path, filetype, position);
            }
        });

        mImageAdapter.setOnCheckboxClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

//                Toast.makeText(mContext,"position = " +position,Toast.LENGTH_LONG).show();
                mImageAdapter.toggleSelection(position, view.getTag().toString());
            }
        });
        recList.setAdapter(mImageAdapter);
        return rootView;
    }

    public class LoadImage extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(String... params) {
            imageList = new ArrayList<FileInfo>();

            File whatsappFile = new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Images");
            if (!whatsappFile.exists()) {
                whatsappFile.mkdir();
            }
            File file[] = whatsappFile.listFiles();
            imageList.clear();
            try {
                for (int i = 0; i < file.length; i++) {
                    if (file[i].isFile()) {
                        Uri fileUri = Uri.fromFile(file[i]);
                        if (MediaFile.getMediaFileType(mContext, fileUri.getPath()) == FileType.IMAGE) {
                            FileInfo mFile = new FileInfo();
                            mFile.filename = file[i].getName().substring(0, file[i].getName().lastIndexOf('.'));
                            long lastModified = file[i].lastModified();
                            mFile.filedate = Utils.getDateFormatByFormatSetting(mContext, lastModified * 1000);
                            mFile.filesize = Utils.getFileSize(fileUri);
                            mFile.fileUri = fileUri;
                            mFile.filesent = false;  // Receive Files
                            imageList.add(mFile);
                        }
                    } else {
                        Log.d("Test123", "Directory file[i].getAbsolutePath() = " + file[i].getAbsolutePath());
                        File whatsappSentFolder = new File(file[i].getAbsolutePath());
                        if (!whatsappSentFolder.exists()) {
                            whatsappSentFolder.mkdir();
                        }
                        File file1[] = whatsappSentFolder.listFiles();
                        Log.d("Test123", "file1.length = " + file1.length);
                        for (int j = 0; j < file1.length; j++) {
                            if (file1[j].isFile()) {
                                Uri fileUri = Uri.fromFile(file1[j]);
//                                Log.d("Test123", "file1[j].getName() = " + file1[j].getName() );
                                if (MediaFile.getMediaFileType(mContext, fileUri.getPath()) == FileType.IMAGE) {
                                    FileInfo mFile = new FileInfo();
                                    mFile.filename = file1[j].getName().substring(0, file1[j].getName().lastIndexOf('.'));
                                    long lastModified = file1[j].lastModified();
                                    mFile.filedate = Utils.getDateFormatByFormatSetting(mContext, lastModified * 1000);
                                    mFile.filesize = Utils.getFileSize(fileUri);
                                    mFile.fileUri = fileUri;
                                    mFile.filesent = true;  //Sent Files
                                    imageList.add(mFile);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
                /* if (Utils.isShowSdcardFiles) {
                    String primary_sd = System.getenv("EXTERNAL_STORAGE");
                    if(primary_sd != null)
                        Log.d("Test12345","EXTERNAL_STORAGE = " + primary_sd);
                    String secondary_sd = System.getenv("SECONDARY_STORAGE");
                    if(secondary_sd != null)
                        Log.i("Test12345","SECONDARY_STORAGE = " + secondary_sd);

                    File whatsappSdCardFiles = new File(secondary_sd + "/Aa");

                    if (!whatsappSdCardFiles.exists()) {
                        Log.d("Test12345","MAKE DIR = " + whatsappSdCardFiles.mkdirs() + "");
                    }

                    File fileSdcard[] = whatsappSdCardFiles.listFiles();
                    try {
                        for (int i = 0; i < fileSdcard.length; i++) {
                            if (fileSdcard[i].isFile()) {
                                Uri fileUri = Uri.fromFile(fileSdcard[i]);
                                if (MediaFile.getMediaFileType(mContext, fileUri.getPath()) == FileType.IMAGE) {
                                    FileInfo sdcardFile = new FileInfo();
                                    sdcardFile.filename = fileSdcard[i].getName();
                                    long lastModified = fileSdcard[i].lastModified();
                                    sdcardFile.filedate = Utils.getDateFormatByFormatSetting(mContext, lastModified * 1000);
                                    sdcardFile.filesize = Utils.getFileSize(fileUri);
                                    sdcardFile.fileUri = fileUri;
                                    Log.d("Test123", "sdcardFile.fileUri.toString() = " + sdcardFile.fileUri.toString());
                                    sdcardFile.filesent = false;  // Receive Files
                                    imageList.add(sdcardFile);
                                }
                            } else {
                                Log.d("Test123", "Directory file[i].getAbsolutePath() = " + file[i].getAbsolutePath());
                                File whatsappSdCardSentFolder = new File(file[i].getAbsolutePath());
                                if (!whatsappSdCardSentFolder.exists()) {
                                    whatsappSdCardSentFolder.mkdir();
                                }
                                File fileSdcardSent[] = whatsappSdCardSentFolder.listFiles();
                                Log.d("Test123", "file1.length = " + fileSdcardSent.length);
                                for (int j = 0; j < fileSdcardSent.length; j++) {
                                    if (fileSdcardSent[j].isFile()) {
                                        Uri fileUri = Uri.fromFile(fileSdcardSent[j]);
//                                Log.d("Test123", "file1[j].getName() = " + file1[j].getName() );
                                        if (MediaFile.getMediaFileType(mContext, fileUri.getPath()) == FileType.IMAGE) {
                                            FileInfo voiceFile = new FileInfo();
                                            voiceFile.filename = fileSdcardSent[j].getName();
                                            long lastModified = fileSdcardSent[j].lastModified();
                                            voiceFile.filedate = Utils.getDateFormatByFormatSetting(mContext, lastModified * 1000);
                                            voiceFile.filesize = Utils.getFileSize(fileUri);
                                            voiceFile.fileUri = fileUri;
                                            voiceFile.filesent = true;  //Sent Files
                                            imageList.add(voiceFile);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (progress != null) {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mImageAdapter.setResultList(imageList);
            onRefreshSortBy();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            super.onPostExecute(result);
            Log.d("Test123", "notifyDataSetChanged  LoadImage ");
            mImageAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), null,
                    "Loading files ...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        new LoadImage().execute();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (Utils.IsFreeDownloadCountExceed(getActivity())) {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (Utils.isSelectMode) {
            menu.clear();
            inflater.inflate(R.menu.select_mode, menu);
        } else {
            inflater.inflate(R.menu.menu_main, menu);
        }
        String pkg = "com.whatsapp";
        if (getActivity() != null && appInstalledOrNot(pkg, getActivity())) {
            try {
                Drawable icon = getActivity().getPackageManager().getApplicationIcon(pkg);
                menu.findItem(R.id.action_whatsapp).setIcon(icon);
            } catch (PackageManager.NameNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            menu.findItem(R.id.action_whatsapp).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu != null && !Utils.isSelectMode) {
            Utils.mCurrentSortBy = SharedDataStore.getInstance(mContext).getCurrentSortBy();
            Utils.mCurrentInOrder = SharedDataStore.getInstance(mContext).getCurrentInOrder();

            if (Utils.mCurrentSortBy == Utils.NAME) {
                if (Utils.mCurrentInOrder == Utils.ASC)
                    menu.findItem(R.id.action_sort_by_name_asc).setChecked(true);
                else
                    menu.findItem(R.id.action_sort_by_name_dsc).setChecked(true);

            } else if (Utils.mCurrentSortBy == Utils.TIME) {
                if (Utils.mCurrentInOrder == Utils.ASC)
                    menu.findItem(R.id.action_sort_by_date_asc).setChecked(true);
                else
                    menu.findItem(R.id.action_sort_by_date_dsc).setChecked(true);

            } else if (Utils.mCurrentSortBy == Utils.SIZE) {
                if (Utils.mCurrentInOrder == Utils.ASC)
                    menu.findItem(R.id.action_sort_by_size_asc).setChecked(true);
                else
                    menu.findItem(R.id.action_sort_by_size_dsc).setChecked(true);

            } else {
                menu.findItem(R.id.action_sort_by_none).setChecked(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
/*            case R.id.action_storage_details:
                Utils.StorageDialog(getActivity());
                return true;*/
            case R.id.action_share:
                if (mImageAdapter.mSelectedImageList.size() > 0)
                    doMultipleShare();
                else
                    removeSelectMode();
                return true;

            case R.id.action_delete:
                if (mImageAdapter.mSelectedImageList.size() > 0)
                    doMultipleDelete();
                else
                    removeSelectMode();
                return true;
            case R.id.action_move_to_sdcard:
                if (mImageAdapter.mSelectedImageList.size() > 0)
                    doMultipleMove();
                else
                    removeSelectMode();
                return true;
            case R.id.action_select_all:
                selectAll();
                return true;
            case R.id.action_unselect_all:
                unSelectAll();
                return true;
            case R.id.action_whatsapp:
                if (getActivity() != null) {
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                    if (launchIntent != null)
                        startActivity(launchIntent);
                }
                return true;
            case R.id.action_select:
                if (mImageAdapter.imageList.size() > 0) {
                    Utils.isSelectMode = true;
                    startSelectMode(mContext);
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            case R.id.action_sort_by:
                return true;
            case R.id.action_sort_by_none:
                return true;
            case R.id.action_sort_by_name_asc:
                Utils.mCurrentInOrder = Utils.ASC;
                Utils.mCurrentSortBy = Utils.NAME;
                onRefreshSortBy();
                return true;

            case R.id.action_sort_by_name_dsc:
                Utils.mCurrentInOrder = Utils.DESC;
                Utils.mCurrentSortBy = Utils.NAME;
                onRefreshSortBy();
                return true;

            case R.id.action_sort_by_size_asc:
                Utils.mCurrentInOrder = Utils.ASC;
                Utils.mCurrentSortBy = Utils.SIZE;
                onRefreshSortBy();
                return true;

            case R.id.action_sort_by_size_dsc:
                Utils.mCurrentInOrder = Utils.DESC;
                Utils.mCurrentSortBy = Utils.SIZE;
                onRefreshSortBy();
                return true;

            case R.id.action_sort_by_date_asc:
                Utils.mCurrentInOrder = Utils.ASC;
                Utils.mCurrentSortBy = Utils.TIME;
                onRefreshSortBy();
                return true;

            case R.id.action_sort_by_date_dsc:
                Utils.mCurrentInOrder = Utils.DESC;
                Utils.mCurrentSortBy = Utils.TIME;
                onRefreshSortBy();
                return true;

            case R.id.action_share_this_app:
                Utils.shareThisApp(mContext);

            case R.id.action_settings:
                return true;
            case R.id.action_about_us:
                return true;
            case R.id.action_help:
                Utils.doHelp(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectAll() {
        mImageAdapter.selectAll();
    }

    public void unSelectAll() {
        mImageAdapter.unSelectAll();
    }

    public void refreshLoad() {
        new LoadImage().execute();
    }

    public void popMenu(View view, final String path, final String filetype, final int position) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (getActivity() != null && Utils.IsFreeOperationExceed(getActivity())) {
                            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }
                        switch (menuItem.getItemId()) {
                            case R.id.action_open:
                                doPlay(mContext, path, position, filetype);
                                return true;
                            case R.id.action_share:
                                doShare(mContext, path, position, filetype);
                                return true;
                            case R.id.action_rename:
                                doRename(mContext, path, position);
                                return true;
                            case R.id.action_move_to_sdcard:
                                doMove(mContext, path, position);
                                return true;
                            case R.id.action_delete:
                                doDelete(mContext, path, position);
                                return true;
                            case R.id.action_detail:
                                doDetails(mContext, path, position);
                                return true;
                            default:
                                return false;
                        }
                    }
                }
        );
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.card_popup_menu_image, popupMenu.getMenu());
        popupMenu.show();
    }

    protected void onRefreshSortBy() {
        // TODO Auto-generated method stub
        try {
            if (Utils.mCurrentSortBy == Utils.NAME) {
                if (mImageAdapter != null && mImageAdapter.imageList != null)
                    Collections.sort(mImageAdapter.imageList, Utils.NameComparator);
            } else if (Utils.mCurrentSortBy == Utils.TIME) {
                if (mImageAdapter != null && mImageAdapter.imageList != null)
                    Collections.sort(mImageAdapter.imageList, Utils.ModifiedDateComparator);
            } else if (Utils.mCurrentSortBy == Utils.SIZE) {
                if (mImageAdapter != null && mImageAdapter.imageList != null)
                    Collections.sort(mImageAdapter.imageList, Utils.SizeComparator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mImageAdapter != null) {
            mImageAdapter.notifyDataSetChanged();
        }
        SharedDataStore sharedDataStore = SharedDataStore.getInstance(mContext);
        sharedDataStore.setCurrentSortBy(Utils.mCurrentSortBy);
        sharedDataStore.setCurrentInOrder(Utils.mCurrentInOrder);
    }

    public void doPlay(Context mContext, String path, int position, String filetype) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String mFiletype = filetype;
        intent.setDataAndType(Uri.fromFile(new File(path)), filetype);
        if (intent != null && getActivity() != null) {
            try {
                    if (Utils.IsFreeOperationExceed(getActivity())) {
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }
                    getActivity().startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(getActivity(), "No App found to open this file.", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    }

    public void doShare(Context mContext, String path, int position, String filetype) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files share via " + Utils.APP_TITLE);
        intent.putExtra(Intent.EXTRA_TEXT, "Free available on Play store " + Utils.mPlayStoreLink);
        String mFiletype = filetype;
        intent.setType(mFiletype);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        if (intent != null && getActivity() != null) {
            try {
                getActivity().startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(getActivity(), "Due to security, you can't share this file.", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    }

    public void doRename(final Context mContext, String path, final int position) {
        final File mOldFileName = new File(path);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Rename");
        final String srcFilename = mImageAdapter.imageList.get(position).filename;

        final EditText input = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setFocusable(true);
        input.setSelectAllOnFocus(true);

        input.setPrivateImeOptions("inputType=PredictionOff;inputType=filename;disableEmoticonInput=true");
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);


        input.setText(srcFilename);
        input.setPadding(70, 70, 60, 10);
        lp.setMargins(60, 80, 50, 10);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newFileName = input.getText().toString();
                            /*							if (newFileName.equals(srcFilename) ) {
                                Toast.makeText(mContext, "Already exists", Toast.LENGTH_LONG).show();
								return;
							}*/

                        System.out.println("Old file: " + mOldFileName);

                        File newFile = new File(mOldFileName.getParent(), newFileName + "." + Utils.getFileExtension(mOldFileName.getPath()));

                        if (newFile.isDirectory()) {
                            Toast.makeText(mContext, "Rename failed", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (newFile.exists()) {
                            Toast.makeText(mContext, "Already exists at same path, Try with other name.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        System.out.println("new file: " + newFile.getAbsolutePath());

                        if (mOldFileName.renameTo(newFile)) {
                            Utils.removeMediaThumbnail(mContext, mOldFileName.getAbsolutePath());
                            mImageAdapter.imageList.get(position).filename = newFileName;
                            mImageAdapter.imageList.get(position).fileUri = Uri.fromFile(newFile);
                            mImageAdapter.notifyDataSetChanged();
//                            Toast.makeText(mContext,"Succes! Rename to : " + newFile.getName(),Toast.LENGTH_LONG).show();
                            Utils.sendScanFile(mContext, newFile.getAbsolutePath());
                        } else {
                            Toast.makeText(mContext, "Rename failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert1 = alertDialog.create();
        alert1.show();
    }

    public void doMove(final Context mContext, String path, final int position) {
        final File mOldFileName = new File(path);
        String parentFileName = mOldFileName.getParent();
        System.out.println("Old file: " + mOldFileName + " parentFileName = " + parentFileName);
        final String srcFilename = mImageAdapter.imageList.get(position).filename;
                            /*							if (newFileName.equals(srcFilename) ) {
                                Toast.makeText(mContext, "Already exists", Toast.LENGTH_LONG).show();
								return;
							}*/
        String primary_sd = System.getenv("EXTERNAL_STORAGE");
        if (primary_sd != null)
            Log.d("Test12345", "EXTERNAL_STORAGE = " + primary_sd);
        String secondary_sd = System.getenv("SECONDARY_STORAGE");
        if (secondary_sd != null)
            Log.i("Test12345", "SECONDARY_STORAGE = " + secondary_sd);

//        File whatsappSdCardFiles = new File(secondary_sd + "/Aa");
//        String newParentFilePath = "/storage/extSdCard" + "/WhatsApp/Media/WhatsApp Images";
        String newParentFilePath = secondary_sd + "/Aa";
        File newFileTemp = new File(newParentFilePath);
        if (!newFileTemp.exists()) {
            Log.d("Test12345", "in doMove Folder created = " + newFileTemp.mkdirs() + "");
            newFileTemp.mkdirs();
//            Log.d("Test1234", "in doMove Folder created in sdcard /storage/extSdCard/WhatsApp/Media/WhatsApp Images");
        }

        File newFile = new File(newParentFilePath, srcFilename.substring(0, srcFilename.lastIndexOf('.')) + "." + Utils.getFileExtension(mOldFileName.getPath()));
        System.out.println("new file: " + newFile.getAbsolutePath());
        if (newFile.isDirectory()) {
            Toast.makeText(mContext, "Move failed", Toast.LENGTH_LONG).show();
            return;
        }

        if (newFile.exists()) {
            Toast.makeText(mContext, "Already exists at same path.", Toast.LENGTH_LONG).show();
            return;
        }

//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//        startActivityForResult(intent, 42);

        if (mOldFileName.renameTo(newFile)) {
            Utils.removeMediaThumbnail(mContext, mOldFileName.getAbsolutePath());
            mImageAdapter.imageList.get(position).filename = srcFilename;
            mImageAdapter.imageList.get(position).fileUri = Uri.fromFile(newFile);
            mImageAdapter.notifyDataSetChanged();
            Toast.makeText(mContext, " Success ! File Move ", Toast.LENGTH_LONG).show();
            Utils.sendScanFile(mContext, newFile.getAbsolutePath());
        } else {
            Toast.makeText(mContext, "Move failed 1", Toast.LENGTH_LONG).show();
        }
    }

/*    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        if (resultCode == RESULT_OK) {
            Uri treeUri = resultData.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(getActivity(), treeUri);

            // List all existing files inside picked directory
            for (DocumentFile file : pickedDir.listFiles()) {
                Log.d("Test1234", "Found file " + file.getName() + " with size " + file.length());
            }

            // Create a new file and write into it
            DocumentFile newFile = pickedDir.createFile("text/plain", "My Novel");
        OutputStream out;
        try{
            out = getActivity().getContentResolver().openOutputStream(newFile.getUri());
            out.write("A long time ago...".getBytes());
            out.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

//        }
    }*/

    public void doMultipleMove() {
        Iterator it;
        it = mImageAdapter.mSelectedImageList.keySet().iterator();
        while (it.hasNext()) {
            int position = (Integer) it.next();
            File tempFile;
            tempFile = new File(mImageAdapter.mSelectedImageList.get(position));
            if (tempFile != null && tempFile.exists()) {
                doMove(getActivity(), tempFile.getAbsolutePath(), position);
            } else {
                Toast.makeText(mContext, "File not exist, Unable to move.", Toast.LENGTH_LONG).show();
            }
        }
        Toast.makeText(mContext, mImageAdapter.mSelectedImageList.size() + " items deleted.", Toast.LENGTH_LONG).show();
        removeItemFromList();
    }

    public void doDelete(final Context mContext, String path, final int position) {
        final File fdelete = new File(path);
        if (fdelete != null && fdelete.exists()) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
            builder1.setMessage(" Are you sure, you want to delete ?");
            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (fdelete != null && fdelete.delete()) {
                        Utils.removeMediaThumbnail(mContext, fdelete.getPath());
                        mImageAdapter.imageList.remove(position);
                        mImageAdapter.setResultList(mImageAdapter.imageList);
                        Toast.makeText(mContext, "File deleted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "File not exist in Phone Memory, Unable to delete File.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        } else {
            Toast.makeText(mContext, "File not exist, Unable to delete", Toast.LENGTH_LONG).show();
        }
    }

    public void doDetails(Context mContext, String path, int position) {
        File file = new File(path);
        long lastmodified = file.lastModified();
        String lastmodifiedtime = Utils.getDateFormatByFormatSetting(mContext, lastmodified * 1000);
        String mFilename = mImageAdapter.imageList.get(position).filename;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(Html.fromHtml("<b>" + "File Name : " + "</b>" + mFilename + "<br><br>" +
                "<b>" + "Size : " + "</b>" + Utils.getFileSize(Uri.fromFile(new File(path))) + "<br><br>" +
                "<b>" + "Last Modified Time : " + "</b>" + lastmodifiedtime + "<br><br>" +
                "<b>" + "File Location : " + "</b>" + path + "."))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void doMultipleDelete() {
        AlertDialog.Builder builder1;

        builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage(" Are you sure, you want to delete ?");
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteMultiple();
            }
        });
        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void deleteMultiple() {
        Iterator it;
        it = mImageAdapter.mSelectedImageList.keySet().iterator();
        while (it.hasNext()) {
            int position = (Integer) it.next();
            File tempFile;
            tempFile = new File(mImageAdapter.mSelectedImageList.get(position));
            if (tempFile != null && tempFile.exists()) {
                if (tempFile.delete()) {
                    Utils.removeMediaThumbnail(mContext, tempFile.getPath());
                } else {
                    Toast.makeText(mContext, "Some files not exist in Phone Memory, Unable to delete that Files.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext, "File not exist, Unable to delete", Toast.LENGTH_LONG).show();
            }
        }
        Toast.makeText(mContext, mImageAdapter.mSelectedImageList.size() + " items deleted.", Toast.LENGTH_LONG).show();
        removeItemFromList();
    }

    public void removeItemFromList() {
        new LoadImage().execute();
        removeSelectMode();
    }

    public void doMultipleShare() {
        Iterator it;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files share via " + Utils.APP_TITLE);
        intent.putExtra(Intent.EXTRA_TEXT, "Free available on Play store " + Utils.mPlayStoreLink);
        intent.setType("*/*"); /* This example is sharing jpeg images. */

        ArrayList<Uri> files = new ArrayList<Uri>();
        it = mImageAdapter.mSelectedImageList.keySet().iterator();

        while (it.hasNext()) {
            final int position = (Integer) it.next();
            files.add(Uri.fromFile(new File(mImageAdapter.mSelectedImageList.get(position))));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        if (intent != null && getActivity() != null) {
            getActivity().startActivity(intent);
        }
        removeSelectMode();
    }

    public void startSelectMode(Context context) {
        if (mImageAdapter != null)
            mImageAdapter.notifyDataSetChanged();
    }

    public void removeSelectMode() {
        if (getActivity() != null)
            getActivity().invalidateOptionsMenu();
        if (mImageAdapter != null)
            mImageAdapter.removeSelection();
    }

    public boolean appInstalledOrNot(String app, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(app, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void initAd(Context context) {

        if (Utils.isDevMode) {
            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId("ca-app-pub-123456789/123456789");

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("6F34A77DBC1CE047184CBCBF1EF48D04")
                    .build();
            mInterstitialAd.loadAd(adRequest);
        } else {
            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId(getString(R.string.Interstitial_ad_ad_unit_id));
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            mInterstitialAd.loadAd(adRequestBuilder.build());
        }
    }
}
