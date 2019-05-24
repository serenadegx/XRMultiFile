package com.example.multifile.entity;

import java.io.File;
import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class XRFile implements Serializable {

    public static final int FOLDER = 0;
    public static final int PICTURE = 1;
    public static final int VIDEO = 2;
    public static final int AUDIO = 3;
    public static final int PDF = 4;
    public static final int WORD = 5;
    public static final int EXCEL = 6;
    public static final int PPT = 7;
    public static final int TXT = 8;
    public static final int ZIP = 9;
    public static final int OTHER = 10;
    private int fileType;
    private String name;
    private String size;
    private String time;
    private File file;

    public XRFile() {
    }

    public XRFile(int fileType, String name, String size) {
        this.fileType = fileType;
        this.name = name;
        this.size = size;
    }

    public XRFile(File file) {
        this.file = file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getFileType() {
        return fileType;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean flag;
        if (obj instanceof XRFile) {
            XRFile xrFile = (XRFile) obj;
            flag = xrFile.getFile().getAbsolutePath().equals(this.getFile().getAbsolutePath());
        } else {
            flag = false;
        }
        return flag;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
