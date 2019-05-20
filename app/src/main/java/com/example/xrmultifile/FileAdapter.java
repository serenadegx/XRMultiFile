package com.example.xrmultifile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    List<XRFile> mData = new ArrayList<>();

    public FileAdapter() {
    }

    public FileAdapter(List<XRFile> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        XRFile file = mData.get(position);
        switch (file.getFileType()) {
            case 0:     //Directory

                break;
            case 1:     //picture

                break;
            case 2:     //video

                break;
            case 3:     //pdf

                break;
            case 4:     //word

                break;
            case 5:     //sheet

                break;
            case 6:     //zip

                break;
            default:    //other

                break;
        }

        holder.name.setText(file.getName());
        holder.des.setText(file.getSize() + "    " + file.getTime());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setNewData(List<XRFile> data) {
        mData = data;
        notifyDataSetChanged();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView name;
        TextView des;
        CheckBox cb;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            name = itemView.findViewById(R.id.name);
            des = itemView.findViewById(R.id.des);
            cb = itemView.findViewById(R.id.cb);
        }
    }
}
