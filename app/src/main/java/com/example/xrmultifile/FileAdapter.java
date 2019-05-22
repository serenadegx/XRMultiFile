package com.example.xrmultifile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private FrameLayout mEmptyLayout;
    private Context mContext;
    private List<XRFile> mData = new ArrayList<>();
    private List<XRFile> mSelects = new ArrayList<>();
    private ItemClickListener mItemClickListener;
    private ItemSelectListener mItemSelectListener;
    private int mLimit = 1;

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
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileViewHolder holder, final int position) {
        final XRFile file = mData.get(position);
        switch (file.getFileType()) {
            case 0:     //Directory
                holder.iv.setImageResource(R.mipmap.ic_folder);
                break;
            case 1:     //picture
                Picasso.get()
                        .load(file.getFile())
                        .resize(px2dp(200), px2dp(200))
                        .centerCrop()
                        .into(holder.iv);
                break;
            case 2:     //video
                holder.iv.setImageResource(R.mipmap.ic_video);
                break;
            case 3:     //audio
                holder.iv.setImageResource(R.mipmap.ic_music);
                break;
            case 4:     //pdf
                holder.iv.setImageResource(R.mipmap.ic_pdf);
                break;
            case 5:     //word
                holder.iv.setImageResource(R.mipmap.ic_word);
                break;
            case 6:     //sheet
                holder.iv.setImageResource(R.mipmap.ic_excel);
                break;
            case 7:     //ppt
                holder.iv.setImageResource(R.mipmap.ic_ppt);
                break;
            case 8:     //txt
                holder.iv.setImageResource(R.mipmap.ic_txt);
                break;
            case 9:     //zip
                holder.iv.setImageResource(R.mipmap.ic_zip);
                break;
            default:    //other
                holder.iv.setImageResource(R.mipmap.ic_blank);
                break;
        }
        holder.cb.setChecked(mSelects.contains(file));
        holder.llSelect.setVisibility(file.getFile().isDirectory() ? View.GONE : View.VISIBLE);
        holder.name.setText(file.getName());
        holder.des.setText(file.getFileType() == XRFile.FOLDER ? file.getSize() : file.getSize() +
                "    " + file.getTime());
        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(v, file, position);
                }
            }
        });
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.cb.isChecked()) {
                    if (mSelects.size() < mLimit) {
                        mSelects.add(file);
                    } else {
                        holder.cb.setChecked(false);
                    }
                } else {
                    mSelects.remove(file);
                }
                mItemSelectListener.onItemSelectListener(v, mSelects, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
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

    public interface ItemClickListener {
        void onItemClickListener(View v, XRFile file, int position);
    }

    public interface ItemSelectListener {
        void onItemSelectListener(View v, List<XRFile> selects, int position);
    }

    public void setEmptyView(View emptyView) {
        boolean insert = false;
        if (mEmptyLayout == null) {
            mEmptyLayout = new FrameLayout(emptyView.getContext());
            final RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            if (lp != null) {
                layoutParams.width = lp.width;
                layoutParams.height = lp.height;
            }
            mEmptyLayout.setLayoutParams(layoutParams);
            insert = true;
        }
        mEmptyLayout.removeAllViews();
        mEmptyLayout.addView(emptyView);
        if (insert) {
            notifyItemInserted(0);
        }

    }
}
