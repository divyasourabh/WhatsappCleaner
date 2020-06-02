package ds.com.whatsappcleaner;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.FileViewHolder> {

    List<FileInfo> imageList;
    Context mContext;
    public HashMap<Integer, String> mSelectedImageList;
    SparseBooleanArray mSelectedImageIds;

    public ImageAdapter() {
    }

    public ImageAdapter(Context context, List<FileInfo> imageList) {
        this.imageList = new ArrayList<FileInfo>();
        mContext = context;
        mSelectedImageIds = new SparseBooleanArray();
        mSelectedImageList = new HashMap<Integer, String>();
    }

    @Override
    public int getItemCount() {
        if (imageList != null) {
            return imageList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(final FileViewHolder viewHolder, final int position) {
        FileInfo imageInfo = imageList.get(position);
        loadThumbnail(viewHolder, imageInfo.fileUri);
        viewHolder.mFilename.setText(imageInfo.filename);
        viewHolder.mFilename.setTag(imageInfo.fileUri.getPath()); //SetTag
        viewHolder.mCheckBox.setTag(imageInfo.fileUri.getPath()); //SetTag
        viewHolder.mFileOptions.setTag(imageInfo.fileUri.getPath()); //SetTag
        viewHolder.mFilesize.setText(imageInfo.filesize);
        viewHolder.mFiledate.setText(imageInfo.filedate);
        if (imageInfo.filesent) {
            viewHolder.mFileOptions.setImageResource(R.drawable.cardmoreoptionsent);
            viewHolder.mFilesize.setTextColor(Color.parseColor("#ff05BB70"));
        } else {
            viewHolder.mFileOptions.setImageResource(R.drawable.cardmoreoptionreceive);
            viewHolder.mFilesize.setTextColor(Color.parseColor("#ffD70E21"));
        }
        Log.d("TEST123", "Uri = " + imageInfo.fileUri.getPath());

        if (Utils.isSelectMode) {
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            viewHolder.mFileOptions.setVisibility(View.GONE);
            if (mSelectedImageIds != null && mSelectedImageIds.get(position)) {
                viewHolder.mCheckBox.setChecked(true);
            } else {
                viewHolder.mCheckBox.setChecked(false);
            }
        } else {
            viewHolder.mFileOptions.setVisibility(View.VISIBLE);
            viewHolder.mCheckBox.setVisibility(View.GONE);
        }
    }

    public void toggleSelection(int position, String path) {
        if (mSelectedImageIds.get(position)) {
            mSelectedImageIds.delete(position);
            mSelectedImageList.remove(position);
        } else {
            mSelectedImageList.put(position, path);
            mSelectedImageIds.put(position, true);
        }
        notifyDataSetChanged();
    }

    public void removeSelection() {
        if (mSelectedImageIds != null && mSelectedImageList != null) {
            mSelectedImageIds.clear();
            mSelectedImageList.clear();
            Utils.isSelectMode = false;
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        int size = imageList.size();
        for (int i = 0; i < size; i++) {
            if (mSelectedImageIds != null && mSelectedImageList != null) {
                mSelectedImageIds.put(i, true);
                mSelectedImageList.put(i, imageList.get(i).fileUri.getPath());
            }
        }
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        if (mSelectedImageIds != null && mSelectedImageList != null) {
            mSelectedImageIds.clear();
            mSelectedImageList.clear();
        }
        notifyDataSetChanged();
    }

    public void DownloadAdapterNotifyDataSetChanged() {
        notifyDataSetChanged();
    }

    OnItemClickListener mItemClickListener;
    OnItemClickListener mCheckboxClickListener;
    OnItemClickListener mCardOptionClickListener;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setOnCheckboxClickListener(final OnItemClickListener mCheckboxClickListener) {
        this.mCheckboxClickListener = mCheckboxClickListener;
    }

    public void setOnCardOptionClickListener(final OnItemClickListener mCardOptionClickListener) {
        this.mCardOptionClickListener = mCardOptionClickListener;
    }

    public void setResultList(List<FileInfo> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview_list, viewGroup, false);

        return new FileViewHolder(itemView);
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        protected ImageView mThumbnail;
        protected TextView mFilename;
        protected TextView mFilesize;
        protected TextView mFiledate;
        protected ImageButton mFileOptions;
        protected CheckBox mCheckBox;

        public FileViewHolder(View v) {
            super(v);
            mThumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            mFilename = (TextView) v.findViewById(R.id.filename);
            mFilesize = (TextView) v.findViewById(R.id.filesize);
            mFiledate = (TextView) v.findViewById(R.id.filedate);
            mFileOptions = (ImageButton) v.findViewById(R.id.fileoptions);
            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);

            mFileOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCardOptionClickListener != null) {
                        mCardOptionClickListener.onItemClick(view, getPosition());
                    }
                }
            });
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCheckboxClickListener != null) {
                        mCheckboxClickListener.onItemClick(view, getPosition());
                    }
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }
    }

    private void loadThumbnail(FileViewHolder holder, Uri uri) {
        // TODO Auto-generated method stub
        loadThumbnail(holder, uri.getPath(), -1);
    }

    private void loadThumbnail(FileViewHolder vh, String path, int i) {
        Drawable thumbnail = MediaLoader.getThumbnailDrawableWithoutMakeCache(path);
        int fileType = MediaFile.getFileTypeInt(path, mContext);
        if (thumbnail == null) {
            int defaultIconResId;
            defaultIconResId = R.mipmap.image;
            if (vh.mThumbnail != null) {
                vh.mThumbnail.setImageResource(defaultIconResId);
            }
            Handler h = ThumbnailLoader.sThumbnailLoader.mBackThreadHandler;
            ThumbnailLoader.ThumbnailInfo info = new ThumbnailLoader.ThumbnailInfo();
            info.mThumbnailInfoContext = mContext;
            if (vh.mThumbnail != null) {
                info.mIconImageView = vh.mThumbnail;
                info.mIconImageView.setTag(path);
            }
            info.mFileType = fileType;
            info.mPath = path;
            if (h != null) {
                h.sendMessageAtFrontOfQueue(h.obtainMessage(0, info));
            }
        } else {
            if (vh.mThumbnail != null) {
                vh.mThumbnail.setTag(path);
            }
            if (vh.mThumbnail != null) {
                vh.mThumbnail.setImageDrawable(thumbnail);
            }
        }
    }
}
