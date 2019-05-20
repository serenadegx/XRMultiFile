package com.example.xrmultifile;

import java.io.File;

class XRFile {
    private int fileType;
    private String name;
    private String size;
    private String time;
    private File file;

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
}
