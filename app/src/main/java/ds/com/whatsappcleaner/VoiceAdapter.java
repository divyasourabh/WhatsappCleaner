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


public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.FileViewHolder> {

    List<ds.com.whatsappcleaner.FileInfo> voiceList;
    Context mContext;
    public HashMap<Integer, String> mSelectedVoiceList;
    SparseBooleanArray mSelectedVoiceIds;

    public VoiceAdapter() {
    }

    public VoiceAdapter(Context context, List<ds.com.whatsappcleaner.FileInfo> voiceList) {
        this.voiceList = new ArrayList<ds.com.whatsappcleaner.FileInfo>();
        mContext = context;
        mSelectedVoiceIds = new SparseBooleanArray();
        mSelectedVoiceList = new HashMap<Integer, String>();
    }

    @Override
    public int getItemCount() {
        if (voiceList != null) {
            return voiceList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(final FileViewHolder viewHolder, final int position) {
        ds.com.whatsappcleaner.FileInfo voiceInfo = voiceList.get(position);
        loadThumbnail(viewHolder, voiceInfo.fileUri);
        viewHolder.mFilename.setText(voiceInfo.filename);
        viewHolder.mFilename.setTag(voiceInfo.fileUri.getPath()); //SetTag
        viewHolder.mCheckBox.setTag(voiceInfo.fileUri.getPath()); //SetTag
        viewHolder.mFileOptions.setTag(voiceInfo.fileUri.getPath()); //SetTag
        viewHolder.mFilesize.setText(voiceInfo.filesize);
        viewHolder.mFiledate.setText(voiceInfo.filedate);
//        if(voiceInfo.filesent){
//            viewHolder.mFileOptions.setImageResource(R.drawable.cardmoreoptionsent);
//            viewHolder.mFilesize.setTextColor(Color.parseColor("#ff05BB70"));
//        } else {
            viewHolder.mFileOptions.setImageResource(R.drawable.cardmoreoptionreceive);
            viewHolder.mFilesize.setTextColor(Color.parseColor("#ff000000"));
//        viewHolder.mFilesize.setTextColor(Color.parseColor("#ffD70E21"));
//    }
        Log.d("TEST123", "Uri = " + voiceInfo.fileUri.getPath());

        if (ds.com.whatsappcleaner.Utils.isSelectMode) {
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            viewHolder.mFileOptions.setVisibility(View.GONE);
            if (mSelectedVoiceIds != null && mSelectedVoiceIds.get(position)) {
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
        if (mSelectedVoiceIds.get(position)) {
            mSelectedVoiceIds.delete(position);
            mSelectedVoiceList.remove(position);
        } else {
            mSelectedVoiceList.put(position, path);
            mSelectedVoiceIds.put(position, true);
        }
        notifyDataSetChanged();
    }
    public void selectAll() {
        int size = voiceList.size();
        for( int i =0 ;i <size ;i++){
            if (mSelectedVoiceIds != null && mSelectedVoiceList != null) {
                mSelectedVoiceIds.put(i,true);
                mSelectedVoiceList.put(i,voiceList.get(i).fileUri.getPath());
            }
        }
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        if (mSelectedVoiceIds != null && mSelectedVoiceList != null) {
            mSelectedVoiceIds.clear();
            mSelectedVoiceList.clear();
        }
        notifyDataSetChanged();
    }

    public void removeSelection() {
        if (mSelectedVoiceIds != null && mSelectedVoiceList != null) {
            mSelectedVoiceIds.clear();
            mSelectedVoiceList.clear();
            ds.com.whatsappcleaner.Utils.isSelectMode = false;
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

    public void setResultList(List<ds.com.whatsappcleaner.FileInfo> voiceList) {
        this.voiceList = voiceList;
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
        Drawable thumbnail = ds.com.whatsappcleaner.MediaLoader.getThumbnailDrawableWithoutMakeCache(path);
        int fileType = ds.com.whatsappcleaner.MediaFile.getFileTypeInt(path, mContext);
        if (thumbnail == null) {
            int defaultIconResId;
            defaultIconResId = R.mipmap.voice;
            if (vh.mThumbnail != null) {
                vh.mThumbnail.setImageResource(defaultIconResId);
            }
            Handler h = ds.com.whatsappcleaner.ThumbnailLoader.sThumbnailLoader.mBackThreadHandler;
            ds.com.whatsappcleaner.ThumbnailLoader.ThumbnailInfo info = new ds.com.whatsappcleaner.ThumbnailLoader.ThumbnailInfo();
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
