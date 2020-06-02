package ds.com.whatsappcleaner;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class MediaFile {

    public static String sFileExtensions;

    private static HashMap<String, Integer> sMimeTypeToFileTypeMap = new HashMap<String, Integer>();

    private static HashMap<String, String> sMimeTypeToColorCodeMap = new HashMap<String, String>();

    private static HashMap<Integer, String> sFileTypeToColorCodeMap = new HashMap<Integer, String>();

    private static HashMap<String, ArrayList<String>> sMimeTypeToExtensionMap = new HashMap<String, ArrayList<String>>();

    private static HashMap<String, String> sExtensionToMimeType = new HashMap<String, String>();

    private static ArrayList<String> sDocumentExtensions = new ArrayList<String>();

    private static HashMap<String, MediaFileType> sExtensionToMediaFileTypeMap = new HashMap<String, MediaFileType>();


    // Audio file types
    public static final int FILE_TYPE_MP3 = 1;

    public static final int FILE_TYPE_M4A = 2;

    public static final int FILE_TYPE_WAV = 3;

    public static final int FILE_TYPE_AMR = 4;

    public static final int FILE_TYPE_AWB = 5;

    public static final int FILE_TYPE_WMA = 6;

    public static final int FILE_TYPE_OGG = 7;

    public static final int FILE_TYPE_AAC = 8;

    public static final int FILE_TYPE_3GA = 9;

    public static final int FILE_TYPE_FLAC = 10;

    public static final int FILE_TYPE_M4B = 11;

    public static final int FILE_TYPE_PYA = 12;

    public static final int FILE_TYPE_ISMA = 13;

    public static final int FILE_TYPE_MP4_AUDIO = 16;

    public static final int FILE_TYPE_3GP_AUDIO = 17;

    public static final int FILE_TYPE_3G2_AUDIO = 18;

    public static final int FILE_TYPE_ASF_AUDIO = 19;

    public static final int FILE_TYPE_3GPP_AUDIO = 20;

    public static final int FILE_TYPE_QCP = 21;

    private static final int FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3;

    private static final int LAST_AUDIO_FILE_TYPE = FILE_TYPE_QCP;

    public static final int FILE_TYPE_MID = 22;

    public static final int FILE_TYPE_SMF = 23;

    public static final int FILE_TYPE_IMY = 24;

    public static final int FILE_TYPE_SPM = 25;

    private static final int FIRST_MIDI_FILE_TYPE = FILE_TYPE_MID;

    private static final int LAST_MIDI_FILE_TYPE = FILE_TYPE_SPM;

    // Video file types
    public static final int FILE_TYPE_MP4 = 31;

    public static final int FILE_TYPE_M4V = 32;

    public static final int FILE_TYPE_3GPP = 33;

    public static final int FILE_TYPE_3GPP2 = 34;

    public static final int FILE_TYPE_WMV = 35;

    public static final int FILE_TYPE_MPG = 36;

    public static final int FILE_TYPE_ASF = 37;

    public static final int FILE_TYPE_AVI = 38;

    public static final int FILE_TYPE_DIVX = 39;

    public static final int FILE_TYPE_FLV = 40;

    public static final int FILE_TYPE_MKV = 41;

    public static final int FILE_TYPE_MOV = 42;

    public static final int FILE_TYPE_PYV = 43;

    public static final int FILE_TYPE_SKM = 44;

    public static final int FILE_TYPE_K3G = 45;

    public static final int FILE_TYPE_AK3G = 46;

    public static final int FILE_TYPE_WEBM = 47;

    public static final int FILE_TYPE_RM = 48;

    public static final int FILE_TYPE_RMVB = 49;

    public static final int FILE_TYPE_SDP = 50;

    public static final int FILE_TYPE_ISMV = 51;

    public static final int FILE_TYPE_TS = 52;

    private static final int FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4;

    private static final int LAST_VIDEO_FILE_TYPE = FILE_TYPE_TS;

    // Image file types
    public static final int FILE_TYPE_JPEG = 61;

    public static final int FILE_TYPE_GIF = 62;

    public static final int FILE_TYPE_PNG = 63;

    public static final int FILE_TYPE_BMP = 64;

    public static final int FILE_TYPE_WBMP = 65;

    public static final int FILE_TYPE_SRW = 66;

    public static final int FILE_TYPE_MPO = 67;

    public static final int FILE_TYPE_GOLF = 68;

    public static final int FILE_TYPE_WEBP = 69;

    private static final int FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG;

    private static final int LAST_IMAGE_FILE_TYPE = FILE_TYPE_WEBP;

    // Playlist file types
    public static final int FILE_TYPE_M3U = 71;

    public static final int FILE_TYPE_PLS = 72;

    public static final int FILE_TYPE_WPL = 73;

    private static final int FIRST_PLAYLIST_FILE_TYPE = FILE_TYPE_M3U;

    private static final int LAST_PLAYLIST_FILE_TYPE = FILE_TYPE_WPL;

    // Documents
    public static final int FILE_TYPE_SSF = 74; // Story album

    public static final int FILE_TYPE_SPD = 75;

    public static final int FILE_TYPE_SNB = 76;

    public static final int FILE_TYPE_HWP = 77;

    public static final int FILE_TYPE_ASC = 78;

    public static final int FILE_TYPE_PPS = 79;

    public static final int FILE_TYPE_CSV = 80;

    public static final int FILE_TYPE_PDF = 81;

    public static final int FILE_TYPE_DOC = 82;

    public static final int FILE_TYPE_XLS = 83;

    public static final int FILE_TYPE_PPT = 84;

    public static final int FILE_TYPE_PPSX = 85;
    public static final int FILE_TYPE_TXT = 86;

    public static final int FILE_TYPE_GUL = 87;

    private static final int FIRST_DOCUMENT_FILE_TYPE = FILE_TYPE_SPD;

    private static final int LAST_DOCUMENT_FILE_TYPE = FILE_TYPE_GUL;


    public static final int FILE_TYPE_EBOOK = 89; // 2011. 08. 05 yhPark : e-book added

    // Flash files
    public static final int FILE_TYPE_SWF = 90;

    public static final int FILE_TYPE_SVG = 91;

    private static final int FIRST_FLASH_FILE_TYPE = FILE_TYPE_SWF;

    private static final int LAST_FLASH_FILE_TYPE = FILE_TYPE_SVG;

    // install files
    public static final int FILE_TYPE_APK = 100;

    public static final int FILE_TYPE_WGT = 101;

    private static final int FIRST_INSTALL_FILE_TYPE = FILE_TYPE_APK;

    private static final int LAST_INSTALL_FILE_TYPE = FILE_TYPE_WGT; // FILE_TYPE_APK; - allow WGT file

    // javaME files
    public static final int FILE_TYPE_JAD = 110;

    public static final int FILE_TYPE_JAR = 111;

    private static final int FIRST_JAVA_FILE_TYPE = FILE_TYPE_JAD;

    private static final int LAST_JAVA_FILE_TYPE = FILE_TYPE_JAR;

    public static final int FILE_TYPE_ICS = 119;
    // vnote, vcalender
    public static final int FILE_TYPE_VCS = 120; // vCalendar

    public static final int FILE_TYPE_VCF = 121; // vCard

    public static final int FILE_TYPE_VNT = 122; // vNote

    public static final int FILE_TYPE_VTS = 123; // Task

    public static final int FILE_TYPE_HTML = 126;

    public static final int FILE_TYPE_XML = 127;

    public static final int FILE_TYPE_XHTML = 128;

    public static final int FILE_TYPE_EML = 142;

    public static final int FILE_TYPE_SASF = 144;

    public static final int FILE_TYPE_SCC = 146;

    public static final int FILE_TYPE_SOL = 147;

    public static final int FILE_TYPE_P12 = 148;

    // archive files
    public static final int FILE_TYPE_ZIP = 200;

    public static final int FIRST_ARCHIVE_FILE_TYPE = FILE_TYPE_ZIP;

    public static final int LAST_ARCHIVE_FILE_TYPE = FILE_TYPE_ZIP;

    public static final int FILE_TYPE_DCF = 300;

    public static final int FILE_TYPE_SM4 = 301;

    public static final int FILE_TYPE_ODF = 302;

    private static final int FIRST_DRM_FILE_TYPE = FILE_TYPE_DCF;

    private static final int LAST_DRM_FILE_TYPE = FILE_TYPE_ODF;

    public static boolean isAudioFileType(int fileType) {
        return ((fileType >= FIRST_AUDIO_FILE_TYPE && fileType <= LAST_AUDIO_FILE_TYPE) || (fileType >= FIRST_MIDI_FILE_TYPE && fileType <= LAST_MIDI_FILE_TYPE));
    }


    public static boolean isVideoFileType(int fileType) {
        return (fileType >= FIRST_VIDEO_FILE_TYPE && fileType <= LAST_VIDEO_FILE_TYPE);
    }


    public static boolean isImageFileType(int fileType) {
        return (fileType >= FIRST_IMAGE_FILE_TYPE && fileType <= LAST_IMAGE_FILE_TYPE);
    }


    public static boolean isPlayListFileType(int fileType) {
        return (fileType >= FIRST_PLAYLIST_FILE_TYPE && fileType <= LAST_PLAYLIST_FILE_TYPE);
    }


    public static boolean isDocumentFileType(int fileType) {
        return (fileType >= FIRST_DOCUMENT_FILE_TYPE && fileType <= LAST_DOCUMENT_FILE_TYPE);
    }


    public static boolean isTxtFileType(int fileType) {
        return (fileType == FILE_TYPE_TXT);
    }


    public static boolean isFlashFileType(int fileType) {
        return (fileType >= FIRST_FLASH_FILE_TYPE && fileType <= LAST_FLASH_FILE_TYPE);
    }


    public static boolean isInstallFileType(int fileType) {
        return (fileType >= FIRST_INSTALL_FILE_TYPE && fileType <= LAST_INSTALL_FILE_TYPE);
    }


    public static boolean isJavaFileType(int fileType) {
        return (fileType >= FIRST_JAVA_FILE_TYPE && fileType <= LAST_JAVA_FILE_TYPE);
    }


    public static boolean isDrmFileType(int fileType) {
        return (fileType >= FIRST_DRM_FILE_TYPE && fileType <= LAST_DRM_FILE_TYPE);
    }


    public static boolean isMIDFileType(int fileType) {
        return (fileType >= FIRST_MIDI_FILE_TYPE && fileType <= LAST_MIDI_FILE_TYPE);
    }

    public static boolean isWmFileType(int fileType) {
        return (fileType == FILE_TYPE_WMA || fileType == FILE_TYPE_WMV);
    }

    public static boolean isPlayReadyType(int fileType) {
        return (fileType == FILE_TYPE_PYA || fileType == FILE_TYPE_PYV);
    }


    public static boolean isArchiveFileType(int fileType) {
        return (fileType >= FIRST_ARCHIVE_FILE_TYPE && fileType <= LAST_ARCHIVE_FILE_TYPE);
    }

    public static class MediaFileType {

        public int fileType;

        public String mimeType;

        public String description;

        public int iconSmall;

        public int iconLarge;

        MediaFileType(int fileType, String mimeType, String desc, int iconSmall, int iconLarge) {
            this.fileType = fileType;
            this.mimeType = mimeType;
            this.description = desc;
            this.iconSmall = iconSmall;
            this.iconLarge = iconLarge;
        }

        MediaFileType(int fileType, String mimeType, String desc) {
            this.fileType = fileType;
            this.mimeType = mimeType;
            this.description = desc;
            this.iconSmall = iconSmall;
            this.iconLarge = iconLarge;
        }
    }


    static void addFileType(String extension, int fileType, String mimeType, String desc, String colorCode) {

        MediaFileType mediaFileType = new MediaFileType(fileType, mimeType, desc);

        sExtensionToMediaFileTypeMap.put(extension, mediaFileType);

        sMimeTypeToFileTypeMap.put(mimeType, fileType);

        sMimeTypeToColorCodeMap.put(mimeType, colorCode);

        sFileTypeToColorCodeMap.put(fileType, colorCode);

        ArrayList<String> extensionList = null;

        if ((extensionList = sMimeTypeToExtensionMap.get(mimeType)) == null) {

            extensionList = new ArrayList<String>();

            extensionList.add(extension);

            sMimeTypeToExtensionMap.put(mimeType, extensionList);

        } else {

            extensionList.add(extension);
        }

        sExtensionToMimeType.put(extension, mimeType);

        if (isDocumentFileType(fileType)) {

            sDocumentExtensions.add(extension);
        }
    }

    static {

        addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg", "Mpeg", "89C35C");
        addFileType("M4A", FILE_TYPE_M4A, "audio/mp4", "M4A", "FF8040");
        addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav", "WAVE", "FF8040");
        addFileType("AMR", FILE_TYPE_AMR, "audio/amr", "AMR", "98FF98");
        addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb", "AWB", "40BFFF");
        addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma", "WMA", "40BFFF");
        addFileType("OGG", FILE_TYPE_OGG, "audio/ogg", "OGG", "89C35C");
        addFileType("OGA", FILE_TYPE_OGG, "audio/ogg", "OGA", "89C35C");
        addFileType("AAC", FILE_TYPE_AAC, "audio/aac", "AAC", "40BFFF");
        addFileType("3GA", FILE_TYPE_3GA, "audio/3gpp", "3GA", "006633");
        addFileType("FLAC", FILE_TYPE_FLAC, "audio/flac", "FLAC", "003333");
        addFileType("MPGA", FILE_TYPE_MP3, "audio/mpeg", "MPGA", "009999");
        addFileType("MP4_A", FILE_TYPE_MP4_AUDIO, "audio/mp4", "MP4 Audio", "00CC99");
        addFileType("MP4A", FILE_TYPE_MP4_AUDIO, "audio/mp4", "MP4 Audio", "00CC99");
        addFileType("3GP_A", FILE_TYPE_3GP_AUDIO, "audio/3gpp", "3GP Audio", "99C68E");
        addFileType("3G2_A", FILE_TYPE_3G2_AUDIO, "audio/3gpp2", "3G2 Audio", "00CC99");
        addFileType("ASF_A", FILE_TYPE_ASF_AUDIO, "audio/x-ms-asf", "ASF Audio", "99C68E");
        addFileType("3GPP_A", FILE_TYPE_3GPP_AUDIO, "audio/3gpp", "3GPP", "009999");
        addFileType("MID", FILE_TYPE_MID, "audio/midi", "MIDI", "00CC99");
        addFileType("MID_A", FILE_TYPE_MID, "audio/mid", "MIDI", "00CC99");
        addFileType("XMF", FILE_TYPE_MID, "audio/midi", "XMF", "009999");
        addFileType("MXMF", FILE_TYPE_MID, "audio/midi", "MXMF", "00CC99");
        addFileType("RTTTL", FILE_TYPE_MID, "audio/midi", "RTTTL", "99C68E");
        addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi", "SMF", "00CC99");
        addFileType("SPMID", FILE_TYPE_SMF, "audio/sp-midi", "SPMID", "00CC99");
        addFileType("IMY", FILE_TYPE_IMY, "audio/imelody", "IMY", "00CC99");
        addFileType("MMF", FILE_TYPE_MID, "audio/midi", "MMF", "99C68E");

        addFileType("MIDI", FILE_TYPE_MID, "audio/midi", "MIDI", "00CC99");
        addFileType("RTX", FILE_TYPE_MID, "audio/midi", "MIDI", "00CC99");
        addFileType("OTA", FILE_TYPE_MID, "audio/midi", "MIDI", "00CC99");

        addFileType("PYA", FILE_TYPE_PYA, "audio/vnd.ms-playready.media.pya", "PYA", "B2C248");

        addFileType("M4B", FILE_TYPE_M4B, "audio/mp4", "M4B", "B2C248");
        addFileType("ISMA", FILE_TYPE_ISMA, "audio/isma", "ISMA", "B2C248");

        addFileType("MPEG", FILE_TYPE_MPG, "video/mpeg", "MPEG", "FFCC00");
        addFileType("MPG", FILE_TYPE_MPG, "video/mpeg", "MPEG", "FFCC00");
        addFileType("MP4", FILE_TYPE_MP4, "video/mp4", "MP4", "C25283");
        addFileType("M4V", FILE_TYPE_M4V, "video/mp4", "M4V", "C25283");
        addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp", "3GP", "FFCC00");
        addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp", "3GPP", "FFCC00");
        addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2", "3G2", "FFCC00");
        addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2", "3GPP2", "FFCC00");
        addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv", "WMV", "FFFF00");
        addFileType("ASF", FILE_TYPE_ASF, "video/x-ms-asf", "ASF", "FFFF00");
        addFileType("AVI", FILE_TYPE_AVI, "video/avi", "AVI", "FFFF00");
        addFileType("DIVX", FILE_TYPE_DIVX, "video/divx", "DIVX", "FFFF00");
        addFileType("FLV", FILE_TYPE_FLV, "video/flv", "FLV", "FFFF00");
        addFileType("MKV", FILE_TYPE_MKV, "video/mkv", "MKV", "FFFF00");
        addFileType("SDP", FILE_TYPE_SDP, "application/sdp", "SDP", "FFCC00");
        addFileType("TS", FILE_TYPE_TS, "video/mp2ts", "TS", "FFFF00");

        addFileType("PYV", FILE_TYPE_PYV, "video/vnd.ms-playready.media.pyv", "PYV", "FFCC00");

        addFileType("WEBM", FILE_TYPE_WEBM, "video/webm", "WEBM", "990000");
        addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg", "JPEG", "E55451");
        addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg", "JPEG", "E77471");
        addFileType("MY5", FILE_TYPE_JPEG, "image/vnd.tmo.my5", "JPEG", "F75D59");
        addFileType("GIF", FILE_TYPE_GIF, "image/gif", "GIF", "F9A7B0");
        addFileType("PNG", FILE_TYPE_PNG, "image/png", "PNG", "46C7C7");
        addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp", "Microsoft BMP", "0033FF");
        addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp", "Wireless BMP", "0033FF");
        addFileType("WEBP", FILE_TYPE_WEBP, "image/webp", "WEBP", "66CC00");
        addFileType("GOLF", FILE_TYPE_GOLF, "image/golf", "GOLF", "990000");

        addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl", "M3U", "66FF00");
        addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls", "PLS", "666666");
        addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl", "WPL", "666666");
        addFileType("PDF", FILE_TYPE_PDF, "application/pdf", "Acrobat PDF", "00FFCC");
        addFileType("RTF", FILE_TYPE_DOC, "application/msword", "Microsoft Office WORD", "FF6633");
        addFileType("DOC", FILE_TYPE_DOC, "application/msword", "Microsoft Office WORD", "FF6633");
        addFileType("DOCX", FILE_TYPE_DOC, "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "Microsoft Office WORD", "FF3366");

        addFileType("DOT", FILE_TYPE_DOC, "application/msword",
                "Microsoft Office WORD", "FF6633");

        addFileType("DOTX", FILE_TYPE_DOC, "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
                "Microsoft Office WORD", "FF6633");

        addFileType("CSV", FILE_TYPE_CSV, "text/comma-separated-values",
                "Microsoft Office Excel", "DDA0DD");

        addFileType("XLS", FILE_TYPE_XLS, "application/vnd.ms-excel",
                "Microsoft Office Excel", "FFCC33");

        addFileType("XLSX", FILE_TYPE_XLS, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "Microsoft Office Excel", "FFCC33");

        addFileType("XLT", FILE_TYPE_XLS, "application/vnd.ms-excel",
                "Microsoft Office Excel", "FFCC33");

        addFileType("XLTX", FILE_TYPE_XLS, "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
                "Microsoft Office Excel", "FFCC33");

        addFileType("PPS", FILE_TYPE_PPS, "application/vnd.ms-powerpoint",
                "Microsoft Office PowerPoint", "FF3333");

        addFileType("PPT", FILE_TYPE_PPT, "application/vnd.ms-powerpoint",
                "Microsoft Office PowerPoint", "F5DEB3");

        addFileType("PPTX", FILE_TYPE_PPT, "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "Microsoft Office PowerPoint", "F5DEB3");

        addFileType("POT", FILE_TYPE_PPT, "application/vnd.ms-powerpoint",
                "Microsoft Office PowerPoint", "FF3333");

        addFileType("POTX", FILE_TYPE_PPT, "application/vnd.openxmlformats-officedocument.presentationml.template",
                "Microsoft Office PowerPoint", "FF3333");
        addFileType("PPSX", FILE_TYPE_PPSX,
                "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
                "Microsoft Office PowerPoint", "FF3333");

        addFileType("ASC", FILE_TYPE_ASC, "text/plain", "Text Document", "993333");
        addFileType("TXT", FILE_TYPE_TXT, "text/plain", "Text Document", "E799A3");
        addFileType("GUL", FILE_TYPE_GUL, "application/jungumword", "Jungum Word", "993333");

        addFileType("EPUB", FILE_TYPE_EBOOK, "application/epub+zip", "eBookReader", "993333");
        addFileType("ACSM", FILE_TYPE_EBOOK, "application/vnd.adobe.adept+xml", "eBookReader", "9900CC");

        addFileType("SWF", FILE_TYPE_SWF, "application/x-shockwave-flash", "SWF", "DDA0DD");
        addFileType("SVG", FILE_TYPE_SVG, "image/svg+xml", "SVG", "993333");

        {
            addFileType("DCF", FILE_TYPE_DCF, "application/vnd.oma.drm.content", "DRM Content", "000033");
            addFileType("ODF", FILE_TYPE_ODF, "application/vnd.oma.drm.content", "DRM Content", "000033");
            addFileType("SM4", FILE_TYPE_SM4, "video/vnd.sdrm-media.sm4", "DRM Content", "000033");

        }

        addFileType("APK", FILE_TYPE_APK, "application/apk", "Android package install file", "006699");
        addFileType("JAD", FILE_TYPE_JAD, "text/vnd.sun.j2me.app-descriptor", "JAD", "006699");
        addFileType("JAR", FILE_TYPE_JAR, "application/java-archive", "JAR", "006699");
        addFileType("VCS", FILE_TYPE_VCS, "text/x-vCalendar", "VCS", "006699");
        addFileType("ICS", FILE_TYPE_VCS, "text/x-vCalendar", "ICS", "006699");
        addFileType("VTS", FILE_TYPE_VTS, "text/x-vtodo", "VTS", "006699");
        addFileType("VCF", FILE_TYPE_VCF, "text/x-vcard", "VCF", "006699");
        addFileType("VNT", FILE_TYPE_VNT, "text/x-vnote", "VNT", "006699");
        addFileType("HTML", FILE_TYPE_HTML, "text/html", "HTML", "336666");
        addFileType("HTM", FILE_TYPE_HTML, "text/html", "HTML", "336666");
        addFileType("XHTML", FILE_TYPE_XHTML, "text/html", "XHTML", "336666");
        addFileType("XML", FILE_TYPE_XML, "application/xhtml+xml", "XML", "336666");
        addFileType("WGT", FILE_TYPE_WGT, "application/vnd.samsung.widget", "WGT", "663366");
        addFileType("HWP", FILE_TYPE_HWP, "application/x-hwp", "HWP", "663366");
        addFileType("SSF", FILE_TYPE_SSF, "application/ssf", "SSF", "87CEEB");
        addFileType("SNB", FILE_TYPE_SNB, "application/snb", "SNB", "663366");
        addFileType("SPD", FILE_TYPE_SNB, "application/spd", "SPD", "87CEEB");
        addFileType("ZIP", FILE_TYPE_ZIP, "application/zip", "ZIP", "663366");
        addFileType("SASF", FILE_TYPE_SASF, "application/x-sasf", "SASF", "663366");
        addFileType("SOL", FILE_TYPE_SOL, "application/com.sec.soloist", "SOL", "663366");
        addFileType("SCC", FILE_TYPE_SCC, "application/vnd.samsung.scc.storyalbum", "SCC", "663366");

        addFileType("PFX", FILE_TYPE_P12, "application/x-pkcs12", "PFX", "663366");
        addFileType("P12", FILE_TYPE_P12, "application/x-pkcs12", "P12", "663366");
        StringBuilder builder = new StringBuilder();

        Iterator<String> iterator = sExtensionToMediaFileTypeMap.keySet().iterator();

        while (iterator.hasNext()) {

            if (builder.length() > 0) {

                builder.append(',');
            }

            builder.append(iterator.next());
        }

        sFileExtensions = builder.toString();
    }

    public static boolean needThumbnail(String name) {
        int fileType = getFileTypeInt(name);
        return isImageFileType(fileType) || isVideoFileType(fileType)
                || isInstallFileType(fileType);
    }


    public static MediaFileType getFileType(String path) {

        String ext = getExtension(path);

        if (ext == null)
            return null;

        MediaFileType mediaType = sExtensionToMediaFileTypeMap.get(ext);

        return mediaType;
    }

    public static boolean needToCheckMimeTypeWithExt(String ext) {
        if (ext == null) {
            return false;
        }

        ext = ext.toUpperCase();

        if ("MP4".equals(ext) || "3GP".equals(ext) ||
                "3G2".equals(ext) || "ASF".equals(ext) || "3GPP".equals(ext)
                || "SCC".equals(ext)) {
            return true;
        }
        return false;
    }

    public static boolean needToCheckMimeType(String path) {
        String ext = getExtension(path);

        if (ext == null) {
            return false;
        }

        if ("MP4".equals(ext) || "3GP".equals(ext) ||
                "3G2".equals(ext) || "ASF".equals(ext) || "3GPP".equals(ext)
                || "SCC".equals(ext)) {
            return true;
        }
        return false;
    }

    public static boolean needToCheckMimeTypeForAudioFilterExt(String ext) {

        if (ext == null) {

            return false;
        }

        if ("MP4_A".equals(ext) || "3GP_A".equals(ext) ||
                "3G2_A".equals(ext) || "ASF_A".equals(ext) || "3GPP_A".equals(ext)) {

            return true;
        }

        return false;
    }

    public static MediaFileType getFileType(String path, Context context, int fileType) {

        String ext = getExtension(path);

        if (ext == null)
            return null;

        MediaFileType mediaType = sExtensionToMediaFileTypeMap.get(ext);

        if (needToCheckMimeType(path) && !"SCC".equals(ext)) {

            if (isAudioFileType(fileType)) {

                mediaType = sExtensionToMediaFileTypeMap.get(ext + "_A");

            }

        }

        return mediaType;
    }


    public static int getFileTypeInt(String path) {
        MediaFileType mediaType = getFileType(path);
        return (mediaType == null ? 0 : mediaType.fileType);
    }

    public static int getFileTypeInt(String path, Context context) {

        if (needToCheckMimeType(path)) {
            String mimeType = getMimeTypeFromMediaStore(path, context);

            return getFileTypeForMimeType(mimeType);
        } else {
            MediaFileType mediaType = getFileType(path);
            return (mediaType == null ? 0 : mediaType.fileType);
        }
    }

    public static ds.com.whatsappcleaner.FileType getMediaFileType(Context context, String path) {
        MediaFileType mediaType = getFileType(path);
        if (mediaType != null) {
            int type = mediaType.fileType;
            if (type >= FIRST_AUDIO_FILE_TYPE && type <= LAST_AUDIO_FILE_TYPE
                    || (type >= FIRST_MIDI_FILE_TYPE && type <= LAST_MIDI_FILE_TYPE)) {
                return ds.com.whatsappcleaner.FileType.AUDIO;
            } else if (type >= FIRST_VIDEO_FILE_TYPE && type <= LAST_VIDEO_FILE_TYPE) {
                return ds.com.whatsappcleaner.FileType.VIDEO;
            } else if (type >= FIRST_DOCUMENT_FILE_TYPE && type <= LAST_DOCUMENT_FILE_TYPE) {
                return ds.com.whatsappcleaner.FileType.DOCUMENT;
            } else if (type >= FIRST_IMAGE_FILE_TYPE && type <= LAST_IMAGE_FILE_TYPE) {
                return ds.com.whatsappcleaner.FileType.IMAGE;
            }

        }
        return null;
    }

    public static int getFileTypeForMimeType(String mimeType) {

        Integer value = sMimeTypeToFileTypeMap.get(mimeType);

        return (value == null ? 0 : value.intValue());
    }

    public static String getColorCodeFormMimeType(String mimeType) {

        String value = sMimeTypeToColorCodeMap.get(mimeType);

        return (value == null ? 000000 + "" : value.toString());
    }

    public static String getColorCodeFormFileType(int fileType) {

        String value = sFileTypeToColorCodeMap.get(fileType);

        return (value == null ? 000000 + "" : value.toString());
    }

    public static String getMimeTypeForExtention(String extention) {

        String str = null;

        if (extention != null) {

            str = sExtensionToMimeType.get(extention.toUpperCase());
        }

        return str;

    }


    public static ArrayList<String> getExtensionFromMimeType(String mimeType) {

        if (mimeType.endsWith("*")) {

            String prefix = mimeType.substring(0, mimeType.indexOf('/'));

            String matchedMimeType;

            ArrayList<String> extensionResult = new ArrayList<String>();

            Set<String> keySet = sMimeTypeToExtensionMap.keySet();

            Iterator<String> it = keySet.iterator();

            while (it.hasNext()) {

                if ((matchedMimeType = it.next()).startsWith(prefix)) {

                    extensionResult.addAll(sMimeTypeToExtensionMap.get(matchedMimeType));
                }
            }

            if (extensionResult.size() == 0) {

                extensionResult = null;
            }

            return extensionResult;

        } else {

            return sMimeTypeToExtensionMap.get(mimeType);
        }
    }

    public static String getMimeTypeFromMediaStore(String path, Context context) {

        String ext = getExtension(path);

        if (ext == null) {

            return null;
        }

        String mimetype = null;

        try {

            if (SCCFileUtil.isSCCFile(path)) {

                mimetype = SCCFileUtil.getMimetypeFromSCCFile(path);
            } else if (isAudioInMediaStore(path, context)) {

                mimetype = sExtensionToMediaFileTypeMap.get(ext + "_A").mimeType;

            } else {

                mimetype = sExtensionToMediaFileTypeMap.get(ext).mimeType;
            }

        } catch (Exception e) {

            e.printStackTrace();

            mimetype = null;
        }

        return mimetype;
    }

    public static boolean isAudioInMediaStore(String path, Context context) {

        String mimeType = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {

            if (path != null) {
                retriever.setDataSource(path);
                mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        retriever.release();

        if (mimeType != null) {
            return isAudioFileType(getFileTypeForMimeType(mimeType));
        } else {
            return false;
        }
    }

    private static String getExtension(String path) {

        if (path == null) {

            return null;
        }

        int lastDot = path.lastIndexOf('.');

        if (lastDot < 0) {

            return null;
        }

        return path.substring(lastDot + 1).toUpperCase(Locale.ENGLISH);
    }

    public static String[] getDocumentExtensions() {

        return sDocumentExtensions.toArray(new String[sDocumentExtensions.size()]);
    }

    public static class SCCFileUtil {

        public static String getMimetypeFromSCCFile(String path) throws IOException {
            File sccFile = new File(path);
            FileInputStream fis = new FileInputStream(sccFile);
            byte[] pklenBuf = new byte[4];
            byte[] mimetypeBuf = new byte[128];

            try {
                fis.skip(22);
                fis.read(pklenBuf, 0, 4);
                int pklen = byteToInt(pklenBuf, ByteOrder.LITTLE_ENDIAN);
                fis.skip(12);
                fis.read(mimetypeBuf, 0, pklen);
                String result = new String(mimetypeBuf);
                fis.close();
                if (result.contains("application/vnd.samsung.scc") == false) {
                    return "ERR_NOT_SCC_FILE";
                }
                return result.trim();
            } catch (Exception e) {
                fis.close();
                return e.toString();
            }
        }

        public static boolean isSCCFile(String path) throws IOException {
            File sccFile = new File(path);
            FileInputStream fis = null;
            byte[] pklenBuf = new byte[4];
            byte[] mimetypeBuf = new byte[128];

            try {
                fis = new FileInputStream(sccFile);

                fis.skip(22);
                fis.read(pklenBuf, 0, 4);
                int pklen = byteToInt(pklenBuf, ByteOrder.LITTLE_ENDIAN);
                fis.skip(12);
                fis.read(mimetypeBuf, 0, pklen);
                String result = new String(mimetypeBuf);
                fis.close();
                if (result.contains("application/vnd.samsung.scc")) return true;
                else return false;
            } catch (Exception e) {
                if (fis != null)
                    fis.close();
                return false;
            }
        }

        private static int byteToInt(byte[] bytes, ByteOrder order) {
            ByteBuffer buff = ByteBuffer.allocate(4);
            buff.order(order);
            buff.put(bytes);
            buff.flip();
            return buff.getInt();
        }

        public static boolean isAudioInMediaStore(String path, Context context) {

            String mimeType = null;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            try {

                if (path != null) {
                    retriever.setDataSource(path);
                    mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            retriever.release();

            if (mimeType != null) {
                return isAudioFileType(getFileTypeForMimeType(mimeType));
            } else {
                return false;
            }
        }

        public static String getMimeType(String path) {
            MediaFileType mediaType = getFileType(path);

            return (mediaType == null ? "" : mediaType.mimeType);
        }

        private static String getExtension(String path) {

            if (path == null) {

                return null;
            }

            int lastDot = path.lastIndexOf('.');

            if (lastDot < 0) {

                return null;
            }

            return path.substring(lastDot + 1).toUpperCase(Locale.ENGLISH);
        }
    }
}
