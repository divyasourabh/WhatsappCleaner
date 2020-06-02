package ds.com.whatsappcleaner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedDataStore {
    private static SharedDataStore sInstance = null;

    private static SharedPreferences mPref = null;

    private static boolean mSoundStatus;

    private static boolean mOptionMenuSoundStatus;

    private static boolean mShowFloatingIconStatus;

    private static boolean mShowFloatingIconRebootStatus;

    private static boolean mFirstTimeLaunch;

    private static boolean mAdsLaunch;

    private static boolean mShowHidden;

    private static int mCurrentSortBy;

    private static int mCurrentInOrder;

    synchronized public static SharedDataStore getInstance(Context context) {

        if (sInstance == null) {

            sInstance = new SharedDataStore();
            if (context != null) {
                mPref = context.getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
            }
            initValue();
        }

        return sInstance;
    }

    public static SharedDataStore getInstance() {

        return sInstance;
    }

    private static void initValue() {
        if (mPref != null && sInstance != null) {
            mSoundStatus = mPref.getBoolean("sound", true);
            mOptionMenuSoundStatus = mPref.getBoolean("option_menu_sound", true);
            mShowFloatingIconStatus = mPref.getBoolean("show_floating_icon", true);
            mShowFloatingIconRebootStatus = mPref.getBoolean("show_floating_icon_reboot", true);
            mFirstTimeLaunch = mPref.getBoolean("first_time_launch", true);
            mShowHidden = mPref.getBoolean("show_hidden", false);
            mCurrentSortBy = mPref.getInt("current_sort_by", 0);
            mCurrentInOrder = mPref.getInt("current_order_by", 0);
        }

    }

    public boolean getFirstTimeStatus() {
        return mFirstTimeLaunch;
    }

    // FirstTimeLaunch
    public synchronized void setFirstTimeStatus(boolean value) {
        Utils.mFirstTimeLaunch = value;
        mFirstTimeLaunch = value;
        if (mPref == null) {
            return;
        }
        Editor editor = mPref.edit();
        editor.putBoolean("first_time_launch", Utils.mFirstTimeLaunch);
        editor.commit();
    }

    public boolean getAdsStatus() {
        return mAdsLaunch;
    }

    // FirstTimeLaunch
    public synchronized void setAdsStatus(boolean value) {
//        Utils.mAdsLaunch = value;
        mAdsLaunch = value;
        if (mPref == null) {
            return;
        }
        Editor editor = mPref.edit();
        editor.putBoolean("ads_launch", Utils.mAdsLaunch);
        editor.commit();
    }
//	public boolean getSoundStatus() {
//		return mSoundStatus;
//	}

//	public boolean getoptionMenuSoundStatus() {
//		return mOptionMenuSoundStatus;
//	}

    //	public boolean getFloatingIconStatus() {
//		return mShowFloatingIconStatus;
//	}
//	public boolean getFloatingIconRebootStatus() {
//		return mShowFloatingIconRebootStatus;
//	}
    public boolean getShowHiddenStatus() {
        return mShowHidden;
    }

    /*	public void setSoundStatus(boolean soundValue) {
            Utils.mSoundStatus = soundValue;
            mSoundStatus = soundValue;
        }

        public void setoptionMenuSoundStatus(boolean value) {
            Utils.mOptionMenuSoundStatus = value;
            mOptionMenuSoundStatus = value;
        }

        public void setFloatingIconStatus(boolean show) {
            Utils.mFloatinIconStatus = show;
            mShowFloatingIconStatus = show;
        }
        public void setFloatingIconRebootStatus(boolean show) {
            Utils.mFloatinIconRebootStatus = show;
            mShowFloatingIconRebootStatus = show;
        }*/
    public void setShowHiddenStatus(boolean show) {
        Utils.mShowHidden = show;
        mShowHidden = show;
    }

    // current sort by
    public synchronized void setCurrentSortBy(int currentListBy) {
        mCurrentSortBy = currentListBy;
        Utils.mCurrentSortBy = currentListBy;
        if (mPref == null) {
            return;
        }
        Editor editor = mPref.edit();
        editor.putInt("current_sort_by", Utils.mCurrentSortBy);
        editor.commit();
    }

    public synchronized int getCurrentSortBy() {
        return mCurrentSortBy;
    }

    // current order by
    public synchronized void setCurrentInOrder(int currentInOrder) {
        mCurrentInOrder = currentInOrder;
        Utils.mCurrentInOrder = currentInOrder;
        if (mPref == null) {
            return;
        }
        Editor editor = mPref.edit();
        editor.putInt("current_order_by", Utils.mCurrentInOrder);
        editor.commit();
    }

    public synchronized int getCurrentInOrder() {
        return mCurrentInOrder;
    }
/*    //For Setting Activity only
    public boolean commitSettings() {
		if (mPref == null) {
			return false;
		}

		Editor editor = mPref.edit();

		// settings
		editor.putBoolean( "sound", mSoundStatus );
		editor.putBoolean( "option_menu_sound", mOptionMenuSoundStatus );
		editor.putBoolean( "show_floating_icon", mShowFloatingIconStatus );
		editor.putBoolean( "show_floating_icon_reboot", mShowFloatingIconRebootStatus );
		editor.putBoolean( "first_time_launch", mFirstTimeLaunch);
		editor.putBoolean( "show_hidden", mShowHidden);
		return editor.commit();
	}*/
}
