package ds.com.whatsappcleaner;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class MediaLoader {

    public static final int MAX_THUMBNAIL_COUNT = 150;
    public static final int DELETE_OLD_THUMBNAIL_COUNT = 50;

    private final static int MICRO_WIDTH = 190;

    private final static int MICRO_HEIGHT = 140;

    private static final int GET_THUMBNAIL_TIME = 15000000;

    private static final HashMap<String, Drawable> sThumbnailCache = new HashMap<String, Drawable>();

    private static final ArrayList<String> mThumbnailKeyList = new ArrayList<String>();

    public static final String GOLF_TEMP_PATH = Environment.getExternalStorageDirectory().toString() + "/.thumbnails/golf/tmp";

    public static final String GOLF_TEMP_VIDEO_PATH = Environment.getExternalStorageDirectory().toString() + "/Golf/";


    public static void deleteImageBitmap(Context context, String path) {

        ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver != null) {

            try {

                contentResolver.delete(Images.Media.EXTERNAL_CONTENT_URI,
                        Images.Media.DATA + "=" + "\"" + path + "\"", null);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    public static void deleteVideoBitmap(Context context, String path) {

        ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver != null) {

            try {

                contentResolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        Images.Media.DATA + "=" + "\"" + path + "\"", null);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    public static boolean isThumbnailExist(String path) {

        synchronized (sThumbnailCache) {

            return mThumbnailKeyList.contains(path);
        }
    }


    public static Drawable getThumbnailDrawableWithoutMakeCache(String path) {

        synchronized (sThumbnailCache) {

            return sThumbnailCache.get(path);
        }
    }


    public static Drawable getThumbnailDrawableWithMakeCache(Context context, int CategoryType, int fileType, String path) {

        Drawable resultDrawable = null;

        Bitmap resultBitmap = null;

        synchronized (sThumbnailCache) {

            resultDrawable = sThumbnailCache.get(path);
        }

        if (resultDrawable == null) {

            if (ds.com.whatsappcleaner.MediaFile.isImageFileType(fileType)) {
                resultBitmap = getImageBitmap(context, path);


            } else if (ds.com.whatsappcleaner.MediaFile.isVideoFileType(fileType)) {
                resultBitmap = getVideoBitmap(context, path);

            } else if (ds.com.whatsappcleaner.MediaFile.isAudioFileType(fileType)) {
                resultBitmap = getAlbumartBitmap(context, path);

            } else if (ds.com.whatsappcleaner.MediaFile.isInstallFileType(fileType)) {
                resultBitmap = getApkBitmap(context, path);
            } else if (ds.com.whatsappcleaner.MediaFile.isDocumentFileType(fileType)) {
                resultBitmap = getDocBitmap(context, path);
            }
            if (resultBitmap != null) {

                resultDrawable = new BitmapDrawable(context.getResources(), resultBitmap);

                synchronized (sThumbnailCache) {

                    if (mThumbnailKeyList.size() > MAX_THUMBNAIL_COUNT) {

                        int i = 0;

                        do {

                            sThumbnailCache.remove(mThumbnailKeyList.get(0));

                            mThumbnailKeyList.remove(0);

                            i++;

                        } while (i < DELETE_OLD_THUMBNAIL_COUNT);
                    }

                    mThumbnailKeyList.add(path);

                    sThumbnailCache.put(path, resultDrawable);
                }

            }
        }

        return resultDrawable;
    }

    public static Bitmap getDocBitmap(Context context, String filePath) {
        Uri uri = Uri
                .parse("content://com.samsung.docthumbnail.thumbnailprovider/thumbnailpath");
        Cursor thumbnailCursor = context.getContentResolver().query(uri, null,
                filePath, null, null);
        String path = "";
        if (thumbnailCursor != null) {
            if (thumbnailCursor.moveToFirst()) {
                if (thumbnailCursor.getColumnIndex("thumbnail_path") != -1) {
                    path = thumbnailCursor.getString(thumbnailCursor
                            .getColumnIndex("thumbnail_path"));
                }
            }
            thumbnailCursor.close();
        }
        Bitmap ret = null;
        File bitmapFile = new File(path);
        if (bitmapFile.exists()) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, 96, 96);

            options.inJustDecodeBounds = false;
            ret = BitmapFactory.decodeFile(path, options);
        }

        if (thumbnailCursor != null) {

            thumbnailCursor.close();
        }

        return ret;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static final int MAX_NUM_PIXELS_THUMBNAIL = 512 * 384;
    private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 160 * 120;
    private static final int UNCONSTRAINED = -1;

    public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;

    public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;

    public static Bitmap createImageThumbnail(String filePath, int kind) {
        boolean wantMini = (kind == Images.Thumbnails.MINI_KIND);
        int targetSize = wantMini
                ? TARGET_SIZE_MINI_THUMBNAIL
                : TARGET_SIZE_MICRO_THUMBNAIL;
        int maxPixels = wantMini
                ? MAX_NUM_PIXELS_THUMBNAIL
                : MAX_NUM_PIXELS_MICRO_THUMBNAIL;
        SizedThumbnailBitmap sizedThumbnailBitmap = new SizedThumbnailBitmap();
        Bitmap bitmap = null;

        if (bitmap == null) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(filePath);
                FileDescriptor fd = stream.getFD();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd, null, options);
                if (options.mCancel || options.outWidth == -1
                        || options.outHeight == -1) {
                    return null;
                }
                options.inSampleSize = computeSampleSize(
                        options, targetSize, maxPixels);
                options.inJustDecodeBounds = false;

                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (OutOfMemoryError oom) {
                oom.printStackTrace();
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }

        if (kind == Images.Thumbnails.MICRO_KIND) {
            // now we make it a "square thumbnail" for MICRO_KIND thumbnail
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    TARGET_SIZE_MICRO_THUMBNAIL, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }


    private static class SizedThumbnailBitmap {
        public byte[] mThumbnailData;
        public Bitmap mBitmap;
        public int mThumbnailWidth;
        public int mThumbnailHeight;
    }

    private static void createThumbnailFromEXIF(String filePath, int targetSize,
                                                int maxPixels, SizedThumbnailBitmap sizedThumbBitmap) {
        if (filePath == null) return;

        ExifInterface exif = null;
        byte[] thumbData = null;
        try {
            exif = new ExifInterface(filePath);
            if (exif != null) {
                thumbData = exif.getThumbnail();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        BitmapFactory.Options fullOptions = new BitmapFactory.Options();
        BitmapFactory.Options exifOptions = new BitmapFactory.Options();
        int exifThumbWidth = 0;
        int fullThumbWidth = 0;

        // Compute exifThumbWidth.
        if (thumbData != null) {
            exifOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length, exifOptions);
            exifOptions.inSampleSize = computeSampleSize(exifOptions, targetSize, maxPixels);
            exifThumbWidth = exifOptions.outWidth / exifOptions.inSampleSize;
        }

        // Compute fullThumbWidth.
        fullOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, fullOptions);
        fullOptions.inSampleSize = computeSampleSize(fullOptions, targetSize, maxPixels);
        fullThumbWidth = fullOptions.outWidth / fullOptions.inSampleSize;

        // Choose the larger thumbnail as the returning sizedThumbBitmap.
        if (thumbData != null && exifThumbWidth >= fullThumbWidth) {
            int width = exifOptions.outWidth;
            int height = exifOptions.outHeight;
            exifOptions.inJustDecodeBounds = false;
            sizedThumbBitmap.mBitmap = BitmapFactory.decodeByteArray(thumbData, 0,
                    thumbData.length, exifOptions);
            if (sizedThumbBitmap.mBitmap != null) {
                sizedThumbBitmap.mThumbnailData = thumbData;
                sizedThumbBitmap.mThumbnailWidth = width;
                sizedThumbBitmap.mThumbnailHeight = height;
            }
        } else {
            fullOptions.inJustDecodeBounds = false;
            sizedThumbBitmap.mBitmap = BitmapFactory.decodeFile(filePath, fullOptions);
        }
    }


    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }


    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    private static Bitmap CheckNCreateImageThumbnail(Context context, String CachePath) {

        Bitmap retBmp = null;

        retBmp = createImageThumbnail(CachePath, Images.Thumbnails.MICRO_KIND);

        int degree = getExifOrientation(CachePath);

        if (degree != 0) {

            retBmp = ds.com.whatsappcleaner.BitmapUtils.rotate(retBmp, degree);
        }

        if (retBmp == null) {

            Log.d("Phone_Explorer", "CheckNCreateImageThumbnail: retBmp is null.");
        }

        return retBmp;
    }


    public static void removeThumbnailCache(String path) {

        if (isThumbnailExist(path)) {

            synchronized (sThumbnailCache) {

                sThumbnailCache.remove(path);

                mThumbnailKeyList.remove(path);
            }
        }
    }

    public static void clearThumbnailCache() {

        if (sThumbnailCache != null && mThumbnailKeyList != null) {
            synchronized (sThumbnailCache) {

                sThumbnailCache.clear();

                mThumbnailKeyList.clear();

            }
        }
    }

    public static void changeThumbnailCache(String srcPath, String dstPath) {

        if (isThumbnailExist(srcPath)) {

            synchronized (sThumbnailCache) {

                mThumbnailKeyList.add(dstPath);

                sThumbnailCache.put(dstPath, sThumbnailCache.get(srcPath));

                sThumbnailCache.remove(srcPath);

                mThumbnailKeyList.remove(srcPath);
            }
        }
    }

    public static Bitmap getImageBitmap(Context context, String path) {

        ContentResolver contentResolver = context.getContentResolver();

        Bitmap retBmp = null;

        BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

        int sampleSize = 4;

        sBitmapOptions.inSampleSize = sampleSize;

        sBitmapOptions.inDither = false;

        if (contentResolver != null) {

            Cursor c = null;

            try {

                //2012. 02. 29 yhPark [Tablet_All] : query text is upper case or lower case does not distinguish
                c = contentResolver.query(Images.Media.EXTERNAL_CONTENT_URI, null,
                        Images.Media.DATA + "= ? COLLATE LOCALIZED", new String[]{path}, null);
                if ((c == null || c.getCount() == 0) && path.endsWith(".gif")) {
                    return null;
                }
                if (c != null) {

                    if (c.moveToFirst()) {

                        int index = c.getColumnIndex(Images.Media._ID);

                        long id = c.getLong(index);


                        int Thumnail_kind = Images.Thumbnails.MICRO_KIND;

                        try {

                            retBmp = Images.Thumbnails.getThumbnail(contentResolver, id,
                                    Thumnail_kind, sBitmapOptions);

                        } catch (OutOfMemoryError ex) {

                            ex.printStackTrace();
                        }

                        if (retBmp != null) {

                            int index2 = c.getColumnIndex(Images.Media.ORIENTATION);

                            int degree = c.getInt(index2);

                            if (degree == 0) {

                                degree = getExifOrientation(path);
                            }

                            if (degree != 0) {

                                retBmp = ds.com.whatsappcleaner.BitmapUtils.rotate(retBmp, degree);
                            }
                        }
                    }

                    c.close();
                }

            } catch (Exception e) {

                e.printStackTrace();

                if (c != null) {

                    c.close();
                }
            }
        }

        if (retBmp == null) {

            // because of v1 : add
            Matrix m = new Matrix();

            Bitmap tempBmp = null;

            try {

                try {
                    if (path.endsWith(".golf")) {
                        File tmpDir = new File(GOLF_TEMP_PATH);
                        tmpDir.mkdirs();
                        retBmp = CreateThumbnails(path);
                    } else {

                        retBmp = createImageThumbnail(path, Images.Thumbnails.MICRO_KIND);
                    }

                } catch (OutOfMemoryError ex) {

                    ex.printStackTrace();
                }

                int degree = getExifOrientation(path);

                if (degree != 0) {

                    retBmp = ds.com.whatsappcleaner.BitmapUtils.rotate(retBmp, degree);
                }


                if (retBmp == null) {

                    Log.d("Phone_Explorer", "getImageBitmap: retBmp is null.");
                }


            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        return retBmp;
    }


    public static Bitmap getVideoBitmap(String path) {

        Bitmap b = null;

        if (b == null) {

            File file = new File(path);

            b = getVideoThumbBitmap(file);
        }

        return b;
    }


    public static Bitmap getVideoBitmap(Context context, String path) {

        Bitmap b = null;

        BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

        int sampleSize = 4;

        sBitmapOptions.inSampleSize = sampleSize;

        sBitmapOptions.inDither = false;

        ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver != null) {

            Cursor c = null;

            try {

                c = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Video.Media.DATA + "=" + "\"" + path + "\"", null, null);

                if (c != null) {

                    if (c.moveToFirst()) {

                        int index = c.getColumnIndex(MediaStore.Video.Media._ID);

                        long id = c.getLong(index);

                        b = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id,
                                MediaStore.Video.Thumbnails.MICRO_KIND, sBitmapOptions);
                    }

                    c.close();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        if (b == null) {

            File file = new File(path);

            b = getVideoThumbBitmap(file);
        }
        return b;
    }


    private static Bitmap getVideoThumbBitmap(File file) {

        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);

        if (bitmap == null) {

            return null;
        }

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, MICRO_WIDTH, MICRO_HEIGHT, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return bitmap;
    }

    public static Bitmap getApkBitmap(Context context, String path) {

        Bitmap retBmp = null;

        PackageInfo mPkgInfo = context.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

        if (mPkgInfo != null) {

            try {

                Drawable d = getApkDrawable(context, mPkgInfo.applicationInfo, path);

                if (d != null) {

                    retBmp = getResBitmap(d);
                }

            } catch (OutOfMemoryError e) {

                e.printStackTrace();

                Log.e("Phone_Explorer", "OutOfMemoryError occurs when get the bitmap of apk");

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        return retBmp;
    }


    public static Drawable getApkDrawable(Context context, ApplicationInfo appInfo, String path) throws Exception {

        Drawable icon = null;

        appInfo.sourceDir = path;

        appInfo.publicSourceDir = path;

        if (appInfo.icon != 0) {

            icon = appInfo.loadIcon(context.getPackageManager());
        }

        return icon;
    }


    private static Bitmap getResBitmap(Drawable d) throws OutOfMemoryError {

        Bitmap bmp = null;

        int w = 150;

        int h = 150;

        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bmp);

        d.setBounds(0, 0, w - 1, h - 1);

        d.draw(c);

        return bmp;
    }


    public static int computeSampleSize(BitmapFactory.Options options, int target) {

        int w = options.outWidth;

        int h = options.outHeight;


        int candidateW = w / target;

        int candidateH = h / target;

        int candidate = Math.max(candidateW, candidateH);


        if (candidate == 0) {

            return 1;
        }

        if (candidate > 1) {

            if ((w > target) && (w / candidate) < target) {

                candidate -= 1;
            }
        }

        if (candidate > 1) {

            if ((h > target) && (h / candidate) < target) {

                candidate -= 1;
            }
        }

        return candidate;
    }


    public static Bitmap getSampleSizeBitmap(int targetWidthHeight, String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        Bitmap bm;

        options.inJustDecodeBounds = true;

        bm = BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false;

        if (targetWidthHeight != -1) {

            options.inSampleSize = computeSampleSize(options, targetWidthHeight);
        }

        bm = BitmapFactory.decodeFile(path, options);

        int degree = 0;

        degree = getExifOrientation(path);

        if (degree != 0) {

            bm = ds.com.whatsappcleaner.BitmapUtils.rotate(bm, degree);
        }

        return bm;
    }


    public static int getExifOrientation(String filepath) {

        int degree = 0;

        ExifInterface exif = null;

        try {

            exif = new ExifInterface(filepath);

        } catch (IOException ex) {

            Log.e("Phone_Explorer", "cannot read exif", ex);
        }

        if (exif != null) {

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1) {

                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;

                    default:
                        break;
                }
            }
        }

        return degree;
    }

    public static Bitmap getAlbumartBitmap(Context context, String path) {
        ContentResolver contentResolver = context.getContentResolver();
        Bitmap bitmap = null;
        if (contentResolver != null) {

            Cursor c = null;
            int albumId = -1;
            try {

                c = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Audio.Media.DATA + "=" + "\"" + path + "\"", null, null);
                if (c != null) {

                    if (c.moveToFirst()) {
                        albumId = c.getInt(c.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                    }

                    c.close();
                }

                if (albumId != -1)
                    bitmap = getAlbumartBitmap(context, albumId);

            } catch (Exception e) {
                e.printStackTrace();
                if (c != null) {
                    c.close();
                }
            }

        }
        return bitmap;
    }

    public static Bitmap getAlbumartBitmap(Context context, int albumID) {

        Bitmap bitmap = null;

        final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        sBitmapOptions.inDither = false;

        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");

        int width = 128;

        int height = 128;

        ParcelFileDescriptor fileDescriptor = null;

        try {

            Uri uri = ContentUris.withAppendedId(artworkUri, albumID);

            fileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");

            if (fileDescriptor == null) {

                return null;
            }

            int sampleSize = 1;

            sBitmapOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, sBitmapOptions);

            if (sBitmapOptions.outHeight > height || sBitmapOptions.outWidth > width) {

                if (sBitmapOptions.outWidth > sBitmapOptions.outHeight) {

                    sampleSize = Math.round(sBitmapOptions.outHeight / (float) height);

                } else {

                    sampleSize = Math.round(sBitmapOptions.outWidth / (float) width);
                }
            }

            sBitmapOptions.inSampleSize = sampleSize;

            sBitmapOptions.inJustDecodeBounds = false;

            Bitmap bm = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor.getFileDescriptor(), null, sBitmapOptions);

            if (bm != null) {

                if (sBitmapOptions.outWidth != width || sBitmapOptions.outHeight != height) {

                    bitmap = Bitmap.createScaledBitmap(bm, width, height, true);

                    bm.recycle();

                } else {

                    bitmap = bm;

                }

            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();

        } catch (OutOfMemoryError e) {

            e.printStackTrace();

            throw e;

        } finally {

            if (fileDescriptor != null) {


                try {

                    fileDescriptor.close();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }


    public Bitmap thumbBitmap(String filePath, String mimeType, int size) {

        Bitmap b = getSampleSizeBitmap(size, filePath);

        if (b != null) {

            Matrix m = new Matrix();

            float scale = Math.min(1F, size / (float) b.getWidth());

            m.setScale(scale, scale);

            Bitmap scaledBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

            b.recycle();

            return scaledBitmap;

        } else {

            return null;
        }
    }

    public static Bitmap CreateThumbnails(String golfFilePath) {
        String tempFilePath = getJpgTempFilePath(GOLF_TEMP_PATH, golfFilePath, false);
        return BitmapFactory.decodeFile(tempFilePath);
    }

    public static String getJpgTempFilePath(String folder, String filePath, boolean allFrame) {
        try {
            int indexEnd = filePath.lastIndexOf(".golf");
            int indexBegin = filePath.lastIndexOf("/");
            String fileName = filePath.substring(indexBegin, indexEnd);
            StringBuilder strBuild = new StringBuilder(folder);
            strBuild.append(fileName);
            strBuild.append("_");
            String tempPath = strBuild.toString();
            strBuild.append(0); // 1st frame only
            strBuild.append(".jpg");
            GolfDecoder.generateGolfFile(tempPath, filePath, allFrame);
            return strBuild.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class GolfDecoder {
        private static void saveBufferToFile(String fileName, int format, int size, byte[] buffer) {
            String ext = ".jpg";
            File fl = new File(fileName + ext);
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(fl);
                stream.write(buffer, 0, size);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

        public static void generateGolfFile(String tempFilePath, String FilePath, boolean allFrame) {
            FileInputStream inStream = null;
            try {
                File f = new File(FilePath);
                inStream = new FileInputStream(f);
                if (inStream != null) {
                    int len = (int) f.length();
                    byte[] byteArray = new byte[len];
                    inStream.read(byteArray, 0, len);
                    ByteBuffer byteBuf = ByteBuffer.wrap(byteArray);
                    byteBuf.order(ByteOrder.nativeOrder());
                    byteBuf.position(0);
                    IntBuffer ib = byteBuf.asIntBuffer();
                    ib.get(); // signature
                    // retriev
                    int ver_major = ib.get();
                    int ver_minor = ib.get();
                    if (ver_major < 1 || ver_minor < 4) {
                        if (inStream != null) {
                            inStream.close();
                        }
                        return;
                    }

                    int header_length = ib.get();
                    int format = ib.get();
                    int nFrames = ib.get();

                    // read off irrelevant info.
                    ib.get(); // centerX,
                    ib.get(); // centerY
                    ib.get(); // fps
                    ib.get(); // nFOIs
                    // FOIs Array Start
                    ib.get();
                    ib.get();
                    ib.get();
                    ib.get();
                    ib.get();
                    ib.get();
                    ib.get();
                    ib.get();
                    ib.get(); // FOIs Array End
                    ib.get(); // w
                    ib.get(); // h
                    ib.get(); // log info
                    int num_video_seq = 0;

                    if (allFrame) {
                        num_video_seq = ib.get();
                        if (num_video_seq <= 0)
                            return;
                    }
                    byteBuf.position(header_length);

                    if (ver_major >= 1 && ver_minor >= 2) {
                        // all header fields are retrieved and we are at the beginning position of body
                        IntBuffer ib1 = byteBuf.asIntBuffer();
                        // leave middle/DMZ
                        // ...
                        // need to read offset table from tail
                        int offset_start_in_ib = ib1.remaining() - nFrames - num_video_seq - 1; // offset start in ib space "-1" for thumbnail
                        int[] offsetArray = new int[num_video_seq + 1 + nFrames]; // read one more offset for final frame
                        ib1.position(offset_start_in_ib);

                        ib1.get(offsetArray);
                        int count = allFrame ? (num_video_seq + 1 + nFrames) : 1;
                        for (int i = 0; i < count; ++i) {
                            int size = 0;
                            if (count == (num_video_seq + nFrames))
                                size = offset_start_in_ib - offsetArray[i];
                            else
                                size = offsetArray[i + 1] - offsetArray[i];
                            try {
                                byte[] data = new byte[size];
                                System.arraycopy(byteBuf.array(), offsetArray[i], data, 0, size);
                                saveBufferToFile(tempFilePath + i, format, size, data);
                            } catch (OutOfMemoryError e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

