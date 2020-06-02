package ds.com.whatsappcleaner;

public enum FileType {
    IMAGE(1),
    VIDEO(2),
    AUDIO(3),
    DOCUMENT(4),
    DOWNLOADEDAPPS(5);

    FileType(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    private final int mId;
}
