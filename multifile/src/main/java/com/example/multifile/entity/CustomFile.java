package com.example.multifile.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CustomFile implements Parcelable {
    private String name;
    private String path;

    public CustomFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    protected CustomFile(Parcel in) {
        name = in.readString();
        path = in.readString();
    }

    public static final Creator<CustomFile> CREATOR = new Creator<CustomFile>() {
        @Override
        public CustomFile createFromParcel(Parcel in) {
            return new CustomFile(in);
        }

        @Override
        public CustomFile[] newArray(int size) {
            return new CustomFile[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
