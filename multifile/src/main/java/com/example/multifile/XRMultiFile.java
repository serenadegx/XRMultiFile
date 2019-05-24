package com.example.multifile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.multifile.entity.CustomFile;

import java.io.File;
import java.util.ArrayList;

import static com.example.multifile.FileActivity.EXTRA_RESULT;
import static com.example.multifile.Utils.checkActivity;
import static com.example.multifile.Utils.checkContext;
import static com.example.multifile.Utils.checkFile;
import static com.example.multifile.Utils.checkIntent;
import static com.example.multifile.Utils.checkMain;


public class XRMultiFile {
    private static XRMultiFile instance;

    public static XRMultiFile get() {
        if (instance == null) {
            synchronized (XRMultiFile.class) {
                if (instance == null) {
                    instance = new XRMultiFile();
                }
            }
        }
        return instance;
    }

    public static ArrayList<String> getSelectResult(Intent data) {
        checkIntent(data);
        return data.getStringArrayListExtra(EXTRA_RESULT);
    }

    public FileCreator with(Context context) {
        return new FileCreator(context);
    }

    public class FileCreator {
        private Context context;
        private int limit;
        private CustomFile customFile;
        private boolean lookHidden;

        public FileCreator(Context context) {
            this.context = context;
            lookHidden = false;//默认不浏览隐藏文件
            this.limit = 3;//默认最多选 3 个
        }

        public FileCreator lookHiddenFile(boolean lookHidden) {
            this.lookHidden = lookHidden;
            return this;
        }

        public FileCreator custom(File file) {
            checkFile(file);
            customFile = new CustomFile(file.getName(), file.getAbsolutePath());
            return this;
        }

        public FileCreator custom(String path) {
            return custom(new File(path));
        }

        public FileCreator custom(File file, String label) {
            checkFile(file);
            customFile = new CustomFile(label, file.getAbsolutePath());
            return this;
        }

        public FileCreator limit(int limit) {
            this.limit = limit;
            return this;
        }

        public void browse() {
            checkMain();
            checkContext(context);
            FileActivity.start2FileActivity(context, lookHidden, customFile, true);
        }

        public void select(Activity activity, int requestCode) {
            checkMain();
            checkActivity(activity);
            FileActivity.startForResult2FileActivity(activity, lookHidden, customFile, limit, requestCode);
        }
    }

}
