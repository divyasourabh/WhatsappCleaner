package ds.com.whatsappcleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.text.Html;
import android.text.InputType;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Utils {

    private static final long verificationCodes[] = {920709,920907,890709,890907,181189,891118,210189,218901,120188,880112,123456};

    public static boolean isDevMode = true;
    public static boolean isShowSdcardFiles = false;
    public static boolean isImage,isVideo,isAudio,isVoice = false;
    public static final int apiLevel = Build.VERSION.SDK_INT;
    public static final String prefName = "DS_WhatsApp_Cleaner";

    public final static String APP_TITLE = "DS WhatsApp Cleaner";
    public final static String APP_PNAME = "ds.com.whatsappcleaner";

    public static boolean forPlayStoreVersion = false;
    public static boolean verificationCodeOnStart = true;

    public static boolean isSelectMode = false;
    public static boolean isNeedToRefresh = false;

    public static boolean mFirstTimeLaunch;
    public static boolean mShowHidden;
    public static boolean mAdsLaunch;

    public static int SETTING_REQUEST_CODE = 26;
    public static int MULTI_SHARE_REQUEST_CODE = 27;
    public static int MULTI_DELETE_REQUEST_CODE = 28;
    public static final String myAppPlayStoreLink = "https://play.google.com/store/apps/details?id="+APP_PNAME;
    public static final String myAppVideoLink = "https://www.youtube.com/watch?v=9rzx9Y3LPu0&feature=youtu.be";
    public static final String mPlayStoreLink = "https://play.google.com/store/apps/details?id="+APP_PNAME;
    public static final String mPlayStoreAllAppsLinks =  "https://play.google.com/store/apps/developer?id=Salient+Dep";

    public static final String myAppPlayStoreLinks[] = {"https://play.google.com/store/apps/details?id=ds.com.whatsappcleaner",
            "https://play.google.com/store/apps/details?id=downloader.ds.com.download",
            "https://play.google.com/store/apps/details?id=com.ds.phonoexplorer",
            "https://play.google.com/store/apps/details?id=com.ds.shareapps",
            "https://play.google.com/store/apps/details?id=com.ds.appmanager",
            "https://play.google.com/store/apps/details?id=downloader.ds.com.download",
            "https://play.google.com/store/apps/details?id=com.ds.phonoexplorer"};

    public final static String mInternalRoot = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String mDataFolder = Environment.getDataDirectory().getAbsolutePath();

    public static String newFileName = null;
    public static String mOldFileName = null;
    public static String mFileExtension = null;

    private final static String INVALID_CHAR[] = {

            "\\", "/", ":", "*", "?", "\"", "<", ">", "|", "\n", "[", "]", "(", ")", "@", "#", "!", "$", "%", "^", "&", "-", "~"

    };

    private static long mLastClickTime = 0;
    public static final long MIN_CLICK_INTERVAL = 2000;

    /**
     * sort by
     */
    public static final int NAME = 0;
    public static final int TIME = 1;
    public static final int SIZE = 2;

    public static final int ASC = 0;
    public static final int DESC = 1;

    public static int mCurrentSortBy;
    public static int mCurrentInOrder;

    static RadioButton toCheckInOrder;
    static RadioButton toCheckSortBy;

/*    public static void optionMenuSound(Context mContext) {
        if(mSoundStatus || mOptionMenuSoundStatus){
            MediaPlayer player;
            player = MediaPlayer.create(mContext, R.raw.click);
            player.setVolume(100,100);
            player.start();
        }
    }*/

    public static void renameDialog(final File mFile, Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Rename");

        final EditText input = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        mOldFileName = mFile.getAbsolutePath();
        mFileExtension = getFileExtension(mFile.getPath());

        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newFileName = input.getText().toString();

                        System.out.println("Old file: " + mOldFileName);
                        File oldFile = new File(mOldFileName);
                        File newFile = new File(mFile.getParent(), newFileName + "." + mFileExtension);

                        System.out.println("new file: " + newFile.getAbsolutePath());

                        if (oldFile.renameTo(newFile)) {
                            System.out.println("Succes! Name changed to: " + mFile.getName());
                        } else {
                            System.out.println("failed");
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

    public static void setShowInputMethod(final EditText mSearchEditText, final Context mContext) {
        mSearchEditText.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mContext != null) {

                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mSearchEditText, 0);

                }
            }
        }, 300);
    }

    public static void hideSoftKeyboard(final EditText mSearchEditText, final Context mContext) {
        mSearchEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mContext != null) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    public static void removeMediaThumbnail(Context context, String path) {
        ContentResolver resolver = context.getContentResolver();
        if (ds.com.whatsappcleaner.MediaFile.getMediaFileType(context, path) == ds.com.whatsappcleaner.FileType.AUDIO) {
            resolver.delete(Audio.Media.EXTERNAL_CONTENT_URI, Images.Media.DATA + "=" + "\"" + path + "\"", null);
        } else if (ds.com.whatsappcleaner.MediaFile.getMediaFileType(context, path) == ds.com.whatsappcleaner.FileType.VIDEO) {
            resolver.delete(Video.Media.EXTERNAL_CONTENT_URI, Images.Media.DATA + "=" + "\"" + path + "\"", null);
        }
    }

    public static String getFileSize(Uri fileUri) {

        File filenew = new File(fileUri.getPath());
        int size = Integer.parseInt(String.valueOf(filenew.length()));
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static long getTotalSize(File file) {
        long totalSize = 0;
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                if (fileList != null)
                    for (File content : fileList) {
                        if (content.isDirectory())
                            totalSize += getTotalSize(content);
                        else {
                            totalSize += content.length();
                        }
                    }
            } else {
                return file.length();
            }
        }
        return totalSize;
    }

    public static long folderSize(File directory) {
        long length = 0;
        if (directory == null) {
            return length;
        }
        try {
            for (File file : directory.listFiles()) {
                if (file != null) {
                    if (file.isFile())
                        length += file.length();
                    else
                        length += folderSize(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }
    public static long folderFileCount(File directory) {
        int count = 0;
        if (directory == null) {
            return count;
        }
        try {
            for (File file : directory.listFiles()) {
                if (file != null) {
                    if (file.isFile())
                        count++;
                    else
                        count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

/*    public static void StorageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Storage details");
        builder.setMessage(Html.fromHtml(
                "<b>" + "IMAGES :- " + "</b>" +
                Utils.folderFileCount( new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Images")) + " files - " +
                Utils.returnSize(Utils.folderSize( new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Images"))) + "<br><br>" +

                "<b>" + "AUDIO :- " + "</b>" +
                Utils.folderFileCount( new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Audio")) + " files - " +
                Utils.returnSize(Utils.folderSize( new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Audio"))) + "<br><br>" +

                 "<b>" + "VIDEO :- " + "</b>" +
                 Utils.folderFileCount( new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Video")) + " files - " +
                 Utils.returnSize(Utils.folderSize( new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Video"))) + "<br><br>" +

                 "<b>" + "VOICE :- " + "</b>" +
                 Utils.folderFileCount( new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Voice Notes")) + " files - " +
                 Utils.returnSize(Utils.folderSize( new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Voice Notes"))) + "<br><br>"
        ));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        try{
            alert.show();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }*/

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getMIMEcategory(String aMIMEtype) {

        if (aMIMEtype != null) {

            aMIMEtype = aMIMEtype.substring(0, aMIMEtype.lastIndexOf("/", aMIMEtype.length() - 1)) + "/*";

        } else {

            aMIMEtype = "application/*";
        }

        return aMIMEtype;
    }

    public static String getFileType() {

        return null;
    }

    public static String getFileExtension(String filePath) {
        int index = filePath.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return filePath.substring(index + 1);

    }

    private static java.text.DateFormat shortDateFormat = null;

    private static java.text.DateFormat TimeFormat = null;

    private static StringBuffer mDateString = null;

    public static void resetDateFormatStrings() {

        shortDateFormat = null;

        TimeFormat = null;

    }

    public static String getDateFormatByFormatSetting(final Context context, long date) {

        // String dateString = "";

        if (context == null) {

            return null;
        }

        Time time = new Time();

        time.setToNow();
        long now = time.toMillis(true);
        if (now - date < 0) {
            date /= 1000;
        }
        if (shortDateFormat == null)
            shortDateFormat = DateFormat.getDateFormat(context);
        if (TimeFormat == null)
            TimeFormat = DateFormat.getTimeFormat(context);
        if (mDateString != null)
            mDateString = null;
        mDateString = new StringBuffer(shortDateFormat.format(date));
        mDateString.append(' ').append(
                TimeFormat.format(date));
        return mDateString.toString();
    }

    public static String getSmallFormatedDateFromLong(final Context context, long date) {
        if (context == null) {
            return "";
        }
        java.text.DateFormat shortDateFormat = null;
        shortDateFormat = DateFormat.getDateFormat(context);
        if (mDateString != null)
            mDateString = null;
        mDateString = new StringBuffer(shortDateFormat.format(date));
        return mDateString.toString();
    }

    public static void sendScanFile(Context context, String filePath) {

        //Intent intent = new Intent( Intent.ACTION_MEDIA_MOUNTED, Uri.parse( "file://" + filePath ) );
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath));

        context.sendBroadcast(intent);
    }

    public static boolean checkIfFilesNameIsValid(String fName) {
        if (fName.length() <= 0 || fName.indexOf('\\') >= 0 || fName.indexOf('/') >= 0 || fName.indexOf(':') >= 0
                || fName.indexOf('?') >= 0 || fName.indexOf('<') >= 0 || fName.indexOf('>') >= 0
                || fName.indexOf('"') >= 0 || fName.indexOf('|') >= 0)
            return false;
        else
            return true;
    }

    public static String returnSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        try {
            return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Calculating ...";
    }

    public static boolean isClickValid() {

        Long newClickTime = SystemClock.elapsedRealtime();

        if (newClickTime - mLastClickTime < MIN_CLICK_INTERVAL) {

            Log.d("Phone Explorer", "invalid click - time diff = "
                    + (newClickTime - mLastClickTime));
            return false;

        } else {

            mLastClickTime = newClickTime;
            return true;
        }
    }

    public static Comparator<ds.com.whatsappcleaner.FileInfo> NameComparator = new Comparator<ds.com.whatsappcleaner.FileInfo>() {

        @Override
        public int compare(ds.com.whatsappcleaner.FileInfo e1, ds.com.whatsappcleaner.FileInfo e2) {
            String s1 = e1.filename.toString();
            String s2 = e2.filename.toString();
            return compareName(s1, s2);
        }


    };
    public static Comparator<ds.com.whatsappcleaner.FileInfo> ModifiedDateComparator = new Comparator<ds.com.whatsappcleaner.FileInfo>() {

        @Override
        public int compare(ds.com.whatsappcleaner.FileInfo e1, ds.com.whatsappcleaner.FileInfo e2) {
            File file1 = new File(e1.fileUri.getPath());
            long lastmodified1 = file1.lastModified();
            File file2 = new File(e2.fileUri.getPath());
            long lastmodified2 = file2.lastModified();
            //			String lastmodifiedtime = Utils.getDateFormatByFormatSetting(mContext, lastmodified * 1000);

            String s1 = Long.toString(lastmodified1);//Objects.toString(lastmodified1, null);
            String s2 = Long.toString(lastmodified2);//Objects.toString(lastmodified2, null);
            return compareName(s1, s2);
        }
    };
    public static Comparator<ds.com.whatsappcleaner.FileInfo> SizeComparator = new Comparator<ds.com.whatsappcleaner.FileInfo>() {

        @Override
        public int compare(ds.com.whatsappcleaner.FileInfo e1, ds.com.whatsappcleaner.FileInfo e2) {
            File file1 = new File(e1.fileUri.getPath());
            //			double size1 = Double.parsed(String.valueOf(file1.length()));
            File file2 = new File(e2.fileUri.getPath());
            //			double size2 = Double.parseInt(String.valueOf(file2.length()));

            //			String s1 =  Objects.toString(Utils.getTotalSize(file1), null);
            //			String s2 = Objects.toString(Utils.getTotalSize(file2), null);
            if (Utils.mCurrentInOrder == Utils.ASC) {
                return Long.valueOf(file1.length()).compareTo(Long.valueOf(file2.length()));
            } else {
                return Long.valueOf(file2.length()).compareTo(Long.valueOf(file1.length()));
            }
        }
    };

    public static int compareName(String entry1, String entry2) {
        Collator collator = Collator.getInstance();
        collator.setStrength(Collator.SECONDARY);
        if (Utils.mCurrentInOrder == Utils.ASC)
            return collator.compare(entry1.toLowerCase(), entry2.toLowerCase());
        else if (Utils.mCurrentInOrder == Utils.DESC) {
            return collator.compare(entry2.toLowerCase(), entry1.toLowerCase());
        } else {
            return 0;
        }
    }


    public static void doHelp(final Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Let me help you.");
        builder.setMessage(Html.fromHtml("<b>" + "* " + "</b>" + "This Application can use to manage WhatsApp Sent/Receive data, which take space of your device." + "<br><br>" +
                "<b>" + "* " + "</b>" + "Swipe to explore sent and receive files (category wise) and can perform various operations like Open/Share/Delete/Rename." + "<br><br>" +
                "<b>" + "* " + "</b>" + "For Sent item file size is in GREEN color and for received item file size in RED color." + "<br><br>" +
                "<b>" + "* " + "</b>" + "Start selection mode and Share/Delete multiple files." + "<br><br>" +
                "<b>" + "* " + "</b>" + "Select Storage detail option to check category wise information." + "<br><br>" +
                "<b>" + "* " + "</b>" + "Sort file list as per your need." + "<br><br>" +
                "<b>" + "* " + "</b>" + "On right-top WhatsApp Icon to easy excess." ));
        builder.setCancelable(false);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });
        builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppVideoLink)));
            }
        });
        AlertDialog alert = builder.create();
        try{
            alert.show();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void addShortcut(Context context) {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(context,
                ds.com.whatsappcleaner.MainActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent addIntent = new Intent();
        addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, APP_TITLE);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context,
                        R.mipmap.whatsappcleaner));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }

    private final static int DAYS_UNTIL_FREE = 7;
    private final static int DOWNLOAD_UNTIL_FREE = 15;

    public static boolean isShareDailogShow(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);

        if (prefs.getBoolean("share_dailog_dontshowagain", false)) {
            return false;
        }

        SharedPreferences.Editor editor = prefs.edit();
        long download_count = prefs.getLong("download_count", 0) + 1;
        editor.putLong("download_count", download_count);
        Long date_firstdownload = prefs.getLong("date_firstdownload", 0);

        if (date_firstdownload == 0) {
            date_firstdownload = System.currentTimeMillis();
            editor.putLong("date_firstdownload", date_firstdownload);
        }

        if (download_count >= DOWNLOAD_UNTIL_FREE) {
            if (System.currentTimeMillis()  >= date_firstdownload +
                    (DAYS_UNTIL_FREE * 24 * 60 * 60 * 1000)) {
                showShareAppDialog(context, editor);
                return  true;
            }
        }

        editor.commit();
        return false;
    }

    private final static int DAYS_SHOW_ADS_AFTER_SHARE_APP = 3;

    public static boolean adsActivationDaysCount(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (prefs.getBoolean("ads_activation_day_count_dontshowagain", false)) {
//            IsFreeDownloadCountExceed(context);
            return true;
        }
        long day_count = prefs.getLong("ads_activation_day_count", 0) + 1;
        if(day_count > DAYS_SHOW_ADS_AFTER_SHARE_APP){
            editor.putBoolean("ads_activation_day_count_dontshowagain", true);
            editor.commit();
            return true;
        }else{
            editor.putLong("ads_activation_day_count",day_count);
            editor.commit();
            return false;
        }
    }

    private final static int FREE_DOWNLOAD_COUNT = 7;

    public static boolean IsFreeDownloadCountExceed(Context context){

        SharedPreferences prefs = context.getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long download_count = prefs.getLong("free_download_count_ads", 0) + 1;
        if(download_count > FREE_DOWNLOAD_COUNT){
            editor.putLong("free_download_count_ads", 0);
            editor.commit();
            return true;
        }else{
            editor.putLong("free_download_count_ads",download_count);
            editor.commit();
            return false;
        }
    }

    private final static int FREE_OPERATION_COUNT = 12;
    public static boolean IsFreeOperationExceed(Context context){

        SharedPreferences prefs = context.getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long download_count = prefs.getLong("free_operation_count_ads", 0) + 1;
        if(download_count > FREE_OPERATION_COUNT){
            editor.putLong("free_operation_count_ads", 0);
            editor.commit();
            return true;
        }else{
            editor.putLong("free_operation_count_ads",download_count);
            editor.commit();
            return false;
        }
    }

    public static void showShareAppDialog(final Context mContext, final SharedPreferences.Editor editor) {

        AlertDialog.Builder shareAppDialog = new AlertDialog.Builder(mContext);
        shareAppDialog.setTitle("Share " + Utils.APP_TITLE);
        shareAppDialog.setMessage("One time Share " + Utils.APP_TITLE + " with only one friend to continue with this app." + "\n\n" +
                "Once your friend install this app, verification code will generate on his screen."+ "\n\n" +
                "Enter that code to continue with download. " + "\n\n" +
                "For any query feel free to contact me at: dsinnovation89@gmail.com" + "\n");

        shareAppDialog.setPositiveButton("Share App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                if (editor != null) {
//                    editor.putBoolean("share_dailog_dontshowagain", true);
//                    editor.commit();
//                }
                shareThisApp(mContext);
                verificationCodeDialog(mContext);
                dialog.dismiss();
            }
        });

        shareAppDialog.setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                return;
            }
        });
        AlertDialog alert1 = shareAppDialog.create();
        alert1.show();
    }

    public static void verificationCodeDialog(final Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//        alertDialog.setTitle("");
        alertDialog.setMessage("Enter Verification Code.");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Ask 6 digit code from your friend.");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
//        lp.setMargins(50, 50, 50, 20);
        input.setPadding(70,80,0,10);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        long inputCode;
                        try{
                            inputCode = Long.parseLong(input.getText().toString());
                        }catch (NumberFormatException e){
                            inputCode = 0;
                            e.printStackTrace();
                        }
                        boolean isCorrect = false;
                        for ( int i=0; i < verificationCodes.length; i++){
                            if( verificationCodes[i] == inputCode ){
                                SharedPreferences prefs = context.getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("share_dailog_dontshowagain", true);
                                editor.putBoolean("verification_dailog_dontshowagain", true);
                                editor.commit();
                                isCorrect = true;
                                Toast.makeText(context,"Free life time membership granted.",Toast.LENGTH_LONG).show();
                            }
                        }
                        if(!isCorrect){
                            Toast.makeText(context,"Please enter correct verification code.",Toast.LENGTH_LONG).show();
                            verificationCodeDialog(context);
                        }
                    }
                });

        alertDialog.setNegativeButton("Share App",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        shareThisApp(context);
                    }
                });

        alertDialog.show();
    }

    public static void shareThisApp(Context context){
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, Utils.APP_TITLE);
            intent.putExtra(Intent.EXTRA_TEXT, "Our other apps link :  " + Utils.mPlayStoreAllAppsLinks);
            intent.setType("application/zip");
            final PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo packageInfo : packages) {
                if(packageInfo.packageName.contains(APP_PNAME)){
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(
                            packageInfo.sourceDir)));
                    break;
                }
            }
            if (null != intent) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public static void shareVerificationCodeDialog(final Context mContext) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        Random r = new Random();
        int index = r.nextInt(10 - 0) + 0;
        builder1.setTitle("Verification code for your friend : ");
        builder1.setMessage("" + verificationCodes[index]);
        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doHelp(mContext);
            }
        });
        builder1.setCancelable(false);
        AlertDialog alert1 = builder1.create();
        try{
            alert1.show();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void ifInstallFromPlayStoreDialog(final Context mContext) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setTitle("Developer Message.");
        builder1.setMessage("Due to Youtube and Play Store policy, you can't download youtube video by this version, you may ask developer for FREE PRO Version APK via mail.");
        builder1.setPositiveButton("Send Mail", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intentEmail = new Intent(Intent.ACTION_SEND);
                intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"dsinnovation89@gmail.com"});
//                intentEmail.putExtra(Intent.EXTRA_BCC, new String[]{"prabhatiaswal@gmail.com"});
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Please send DS Downloader PRO version APK");
                intentEmail.setType("plain/html");
                mContext.startActivity(intentEmail);
            }
        });
        AlertDialog alert1 = builder1.create();
        alert1.show();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}