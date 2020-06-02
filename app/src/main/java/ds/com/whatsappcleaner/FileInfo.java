package ds.com.whatsappcleaner;

import android.net.Uri;

/**
 * Created by Lenovo on 4/18/2015.
 */
public class FileInfo {
    protected String filename;
    protected String filesize;
    protected String filedate;
    protected Uri fileUri;
    protected Boolean filesent;


    protected static final String FILENAME_PREFIX = "filename_";
    protected static final String FILESIZE_PREFIX = "filesize_";
    protected static final String FILEDATE_PREFIX = "filedate_";
    protected static final String FILEURI_PREFIX = "fileuri_";
    protected static final String FILESENT_PREFIX = "fileuri_";
}
