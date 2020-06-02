package ds.com.whatsappcleaner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailLoader {

    private static final String MODULE = "ThumbnailLoader";

    private static final String THREAD_NAME = "ThumbnailLoader";

    private static final int DECODE_COMPLETE = 0;


    public static boolean SCROLL_DONE = true;

    public static final ThumbnailLoaderThread sThumbnailLoader = new ThumbnailLoaderThread();

    static {

        Log.d("Downloader", "ThumbnailLoaderThread start");

        sThumbnailLoader.start();

    }


    public static class ThumbnailLoaderThread extends Thread {

        public Handler mBackThreadHandler;


        public ThumbnailLoaderThread() {

            Log.d("Downloader", "ThumbnailLoaderThread");


        }


        @Override
        public void run() {

            Log.d("Downloader", "ThumbnailLoaderThread run...");

            Looper.prepare();

            mBackThreadHandler = new Handler(new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {


                    if (SCROLL_DONE == false) {

                        ThumbnailInfo info = (ThumbnailInfo) msg.obj;
                        mBackThreadHandler.sendMessageAtFrontOfQueue(mBackThreadHandler.obtainMessage(0, info));

                        return true;
                    }

                    ThumbnailInfo info = (ThumbnailInfo) msg.obj;

                    try {
                        info.mThumbnailDrawable = ds.com.whatsappcleaner.MediaLoader.getThumbnailDrawableWithMakeCache(info.mThumbnailInfoContext, info.mCategoryType, info.mFileType, info.mPath);

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    if (info.mScrollState != OnScrollListener.SCROLL_STATE_IDLE) {

                        try {

                            sleep(200);

                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }

                    sThumbnailUpdateHandler.sendMessageAtFrontOfQueue(
                            sThumbnailUpdateHandler.obtainMessage(DECODE_COMPLETE, msg.what, -1, info));

                    return true;
                }
            });

            Log.d("Downloader", "ThumbnailLoader back handler created");

            Looper.loop();
        }


        public void quit() {

            Looper looper = Looper.myLooper();

            if (looper != null) {

                looper.quit();
            }
        }
    }


    private final static Handler sThumbnailUpdateHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == DECODE_COMPLETE) {

                ThumbnailInfo info = (ThumbnailInfo) msg.obj;

                String tag = (String) info.mIconImageView.getTag();

                if (tag != null && tag.equals(info.mPath)) {
                    if (info.mThumbnailDrawable != null) {


                        info.mIconImageView.setImageDrawable(info.mThumbnailDrawable);

                        final AlphaAnimation anim = new AlphaAnimation(0, 1);

                        anim.setDuration(500);

                        info.mIconImageView.startAnimation(anim);
                    }
                }
            }
        }
    };


    public static class ThumbnailInfo {

        public Context mThumbnailInfoContext;

        public ImageView mIconImageView;

        public TextView mTitleTextView;

        public ImageView mThumbnailImageView;

        public ImageView mOverlayIconImageView;

        public ImageView mDocExtnOverlay;

        public int mFileType;

        public String mPath;

        public Drawable mThumbnailDrawable;

        public int mScrollState;

        public int mViewMode;

        public int mCategoryType;
    }
}
