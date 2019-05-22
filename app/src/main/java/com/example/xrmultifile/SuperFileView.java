package com.example.xrmultifile;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author guoxinrui
 * 展示文件
 */

public class SuperFileView extends FrameLayout implements TbsReaderView.ReaderCallback {
    private Context context;
    private TbsReaderView mTbsReaderView;
    private OnGetFilePathListener mOnGetFilePathListener;

    public SuperFileView(@NonNull Context context) {
        this(context, null);
    }

    public SuperFileView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperFileView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        this.context = context;
    }

    private void init(Context context) {
        mTbsReaderView = new TbsReaderView(context, this);
        this.addView(mTbsReaderView, new LinearLayout.LayoutParams(-1, -1));
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    public void show() {

        if (mOnGetFilePathListener != null) {
            mOnGetFilePathListener.onGetFilePath(this);
        }
    }

    public void displayFile(File file) {
        if (file != null && !TextUtils.isEmpty(file.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            String bsReaderTemp = "/storage/emulated/0/TbsReaderTemp";
            File bsReaderTempFile = new File(bsReaderTemp);

            if (!bsReaderTempFile.exists()) {
                boolean mkdir = bsReaderTempFile.mkdir();

            }

            //加载文件
            Bundle localBundle = new Bundle();
            localBundle.putString("filePath", file.toString());

//            Log.e("SuperFileView", "file:" + file.toString());

            localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");

            if (this.mTbsReaderView == null)
                this.mTbsReaderView = getTbsReaderView(context);
            String filePath = file.getAbsolutePath();
            boolean bool = this.mTbsReaderView.preOpen(filePath.substring(filePath.lastIndexOf(".") + 1), false);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
            }
        }
    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    public interface OnGetFilePathListener {
        void onGetFilePath(SuperFileView superFileView);
    }

    public void setOnGetFilePathListener(OnGetFilePathListener mOnGetFilePathListener) {
        this.mOnGetFilePathListener = mOnGetFilePathListener;
    }

    public void onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }
}
