package com.ardityawahyu.autohiddencamera;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;

/**
 * Created by ardityawahyu on 12/13/17.
 */

public class Config implements Parcelable {
    private int cameraFacing;
    private int interval;
    private String host;
    private String username;
    private String password;
    private String albumName;
    private int isUpload;
    private int isDeleteAfterUpload;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cameraFacing);
        dest.writeInt(this.interval);
        dest.writeString(this.host);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.albumName);
        dest.writeInt(this.isUpload);
        dest.writeInt(this.isDeleteAfterUpload);
    }

    public Config() {
    }

    protected Config(Parcel in) {
        this.cameraFacing = in.readInt();
        this.interval = in.readInt();
        this.host = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.albumName = in.readString();
        this.isUpload = in.readInt();
        this.isDeleteAfterUpload = in.readInt();
    }

    public static final Creator<Config> CREATOR = new Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel source) {
            return new Config(source);
        }

        @Override
        public Config[] newArray(int size) {
            return new Config[size];
        }
    };

    public int getCameraFacing() {
        return cameraFacing;
    }

    public void setCameraFacing(int cameraFacing) {
        this.cameraFacing = cameraFacing;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public int getIsDeleteAfterUpload() {
        return isDeleteAfterUpload;
    }

    public void setIsDeleteAfterUpload(int isDeleteAfterUpload) {
        this.isDeleteAfterUpload = isDeleteAfterUpload;
    }
}
