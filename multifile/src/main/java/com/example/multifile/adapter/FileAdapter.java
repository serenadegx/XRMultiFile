package com.example.multifile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.multifile.R;
import com.example.multifile.entity.XRFile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY = 0;
    private static final int ITAM = 1;
    private FrameLayout mEmptyLayout;
    private Context mContext;
    private List<XRFile> mData = new ArrayList<>();
    private List<XRFile> mSelects = new ArrayList<>();
    private ItemClickListener mItemClickListener;
    private ItemSelectListener mItemSelectListener;
    private int mLimit = 1;
    private boolean isUseEmpty = false;
    private boolean isBrowse;

    public FileAdapter() {
    }

    public FileAdapter(int limit) {
        mLimit = limit;
    }

    public FileAdapter(List<XRFile> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        mContext = parent.getContext();
        if (viewType == EMPTY) {
            Log.i("mango", "onCreateViewHolder");
            holder = new EmptyViewHolder(mEmptyLayout);
        } else {
            holder = new FileViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_file, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FileViewHolder) {
            final FileViewHolder fileViewHolder = (FileViewHolder) holder;
            ((FileViewHolder) holder).llSelect.setVisibility(isBrowse ? View.GONE : View.VISIBLE);
            final XRFile file = mData.get(position);
            switch (file.getFileType()) {
                case 0:     //Directory
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_folder);
                    break;
                case 1:     //picture
                    Picasso.get()
                            .load(file.getFile())
                            .resize(px2dp(200), px2dp(200))
                            .centerCrop()
                            .into(fileViewHolder.iv);
                    break;
                case 2:     //video
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_video);
                    break;
                case 3:     //audio
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_music);
                    break;
                case 4:     //pdf
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_pdf);
                    break;
                case 5:     //word
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_word);
                    break;
                case 6:     //sheet
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_excel);
                    break;
                case 7:     //ppt
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_ppt);
                    break;
                case 8:     //txt
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_txt);
                    break;
                case 9:     //zip
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_zip);
                    break;
                default:    //other
                    fileViewHolder.iv.setImageResource(R.mipmap.ic_blank);
                    break;
            }
            fileViewHolder.cb.setChecked(mSelects.contains(file));
            if (isBrowse) {
                fileViewHolder.llSelect.setVisibility(View.GONE);
            } else {
                fileViewHolder.llSelect.setVisibility(file.getFile().isDirectory() ? View.GONE : View.VISIBLE);
            }
            fileViewHolder.name.setText(file.getName());
            fileViewHolder.des.setText(file.getFileType() == XRFile.FOLDER ? file.getSize() : file.getSize() +
                    "    " + file.getTime());
            fileViewHolder.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickListener(v, file, position);
                    }
                }
            });
            fileViewHolder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (fileViewHolder.cb.isChecked()) {
                        if (mSelects.size() < mLimit) {
                            mSelects.add(file);
                        } else {
                            fileViewHolder.cb.setChecked(false);
                        }
                    } else {
                        mSelects.remove(file);
                    }
                    mItemSelectListener.onItemSelectListener(v, mSelects, position);
                }
            });
        } else {
            Log.i("mango", "onBindViewHolder");
        }

    }

    public int getEmptyViewCount() {
        if (mEmptyLayout == null || mEmptyLayout.getChildCount() == 0) {
            return 0;
        }
        if (mData.size() != 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        if (getEmptyViewCount() == 1) {
            return 1;
        }
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mEmptyLayout != null && isUseEmpty) {
            Log.i("mango", "getItemViewType");
            isUseEmpty = false;
            return EMPTY;
        } else {
            return ITAM;
        }
    }

    public void setNewData(List<XRFile> data) {
        mData = data;
        notifyDataSetChanged();
    }

    private int px2dp(int i) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (i / scale + 0.5f);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setItemSelectListener(ItemSelectListener mItemSelectListener) {
        this.mItemSelectListener = mItemSelectListener;
    }

    public void setBrowse(boolean isBrowse) {
        this.isBrowse = isBrowse;
        notifyDataSetChanged();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView name;
        TextView des;
        CheckBox cb;
        ConstraintLayout next;
        LinearLayout llSelect;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            name = itemView.findViewById(R.id.name);
            des = itemView.findViewById(R.id.des);
            cb = itemView.findViewById(R.id.cb);
            next = itemView.findViewById(R.id.next);
            llSelect = itemView.findViewById(R.id.ll_select);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(View v, XRFile file, int position);
    }

    public interface ItemSelectListener {
        void onItemSelectListener(View v, List<XRFile> selects, int position);
    }

    public void setEmptyView(View emptyView) {
        isUseEmpty = true;
        Log.i("mango", "setEmptyView");
        if (mEmptyLayout == null) {
            mEmptyLayout = new FrameLayout(emptyView.getContext());
            final RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            if (lp != null) {
                layoutParams.width = lp.width;
                layoutParams.height = lp.height;
            }
            mEmptyLayout.setLayoutParams(layoutParams);
        }
        mEmptyLayout.removeAllViews();
        mEmptyLayout.addView(emptyView);
        notifyItemInserted(0);
    }
}
