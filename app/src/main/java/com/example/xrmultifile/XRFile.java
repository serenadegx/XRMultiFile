package com.example.xrmultifile;

import java.io.File;

import androidx.annotation.Nullable;

class XRFile {

    public static final int FOLDER = 0;
    public static final int video = 2;
    public static final int pdf = 3;
    public static final int word = 4;
    public static final int excel = 5;
    public static final int ppt = 6;
    public static final int zip = 7;
    public static final int OTHER = 8;
    private int fileType;
    private String name;
    private String size;
    private String time;
    private File file;

    public XRFile() {
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
}
