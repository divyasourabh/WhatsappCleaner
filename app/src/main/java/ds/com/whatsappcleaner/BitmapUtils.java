package ds.com.whatsappcleaner;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;


public class BitmapUtils {

    private final static String MODULE = "BitmapUtils";


    public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, boolean scaleUp) {

        if (source == null) {

            Log.d("Downloader", "BitmapUtils.transform source bitmap is null");

            return null;
        }

        float scale;

        if (source.getWidth() < source.getHeight()) {

            scale = targetWidth / (float) source.getWidth();

        } else {

            scale = targetHeight / (float) source.getHeight();
        }

        Bitmap tmpBmp;

        if (scaler != null) {

            scaler.setScale(scale, scale);

            tmpBmp = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
            recycleOtherObject(source, tmpBmp);

        } else {

            tmpBmp = source;
        }

        int dx1 = Math.max(0, tmpBmp.getWidth() - targetWidth);

        int dy1 = Math.max(0, tmpBmp.getHeight() - targetHeight);

        Bitmap retBmp = Bitmap.createBitmap(tmpBmp, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

        recycleOtherObject(tmpBmp, retBmp);

        return retBmp;
    }


    public static Bitmap rotate(Bitmap b, int degrees) {

        Bitmap b2 = null;

        if (degrees != 0 && b != null) {

            Matrix m = new Matrix();

            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);

            try {

                b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

                recycleOtherObject(b, b2);

            } catch (OutOfMemoryError ex) {

                ex.printStackTrace();
            }
        }

        return b2;
    }

    private static void recycleOtherObject(Bitmap srcBitmap, Bitmap resultBitmap) {

        if (srcBitmap != null && resultBitmap != null) {

            if (srcBitmap.hashCode() != resultBitmap.hashCode()) {

                try {

                    srcBitmap.recycle();

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    }
}
