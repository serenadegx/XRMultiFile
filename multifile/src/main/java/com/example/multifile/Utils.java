package com.example.multifile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import java.io.File;

public class Utils {

    static void checkFile(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalStateException("File is null or not exists.");
        }
    }

    static void checkContext(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context is null.");
        }
    }

    static void checkActivity(Activity activity) {
        if (activity == null) {
            throw new IllegalStateException("Context is null.");
        }
    }

    static void checkIntent(Intent data) {
        if (data == null) {
            throw new IllegalStateException("Intent is null.");
        }
    }

    static void checkMain(){
        if (!isMain()){
            throw new IllegalStateException("Method call should happen from the main thread.");
        }
    }

    static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
