package ds.com.whatsappcleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class AppRater {

    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 10;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);

        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);

        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {

        AlertDialog.Builder rateUsDialog = new AlertDialog.Builder(mContext);
        rateUsDialog.setTitle("Rate " + Utils.APP_TITLE);
        rateUsDialog.setMessage("If you enjoy using " + Utils.APP_TITLE + ", Please take a moment to rate it." + "\n\n" + "Thanks for your support !!!" + "\n");

        rateUsDialog.setPositiveButton("Rate Us", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if( mContext != null ){
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppPlayStoreLink)));
                    if (editor != null) {
                        editor.putBoolean("dontshowagain", true);
                        editor.commit();
                    }
                }
                dialog.dismiss();
            }
        });

        rateUsDialog.setNeutralButton("Remind me later", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (editor != null) {
                    editor.putLong("launch_count", 5);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });

        rateUsDialog.setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();


            }
        });
        AlertDialog alert1 = rateUsDialog.create();
        alert1.show();
    }
}
