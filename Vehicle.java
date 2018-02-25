package com.example.patrick.myinventorylocator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Patrick on 2/8/2018.
 */

public class Vehicle implements Parcelable, Serializable {

    private String mStockNumber;
    private String mMake, mModel, mTrim, mColor, mBody;
    private int mYear, mRunPosition;
    private double mLat, mLong;
    private LatLng mVehicleLocation;
    private boolean mFavorite;
    transient private Thread myThread;

    public Vehicle() {
        mFavorite = false;
        this.myThread = new Thread();
    }


    // Creator
    public static final Creator CREATOR
            = new Creator() {
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        public Vehicle[] newArray(int theSize) {
            return new Vehicle[theSize];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mStockNumber);
        dest.writeString(mMake);
        dest.writeString(mModel);
        dest.writeString(mTrim);
        dest.writeString(mColor);
        dest.writeString(mBody);
        dest.writeInt(mYear);
    }

    // "De-parcel object
    public Vehicle(Parcel in) {
        mStockNumber = in.readString();
        mMake = in.readString();
        mModel = in.readString();
        mTrim = in.readString();
        mColor = in.readString();
        mBody = in.readString();
        mYear = in.readInt();
    }


    public String getStockNumber() {
        return mStockNumber;
    }

    public void setStockNumber(String mStockNumber) {
        this.mStockNumber = mStockNumber;
    }

    public String getMake() {
        return mMake;
    }

    public void setMake(String mMake) {
        this.mMake = mMake;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String mModel) {
        this.mModel = mModel;
    }

    public String getTrim() {
        return mTrim;
    }

    public void setTrim(String mTrim) {
        this.mTrim = mTrim;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String mColor) {
        this.mColor = mColor;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String mBody) {
        this.mBody = mBody;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int mYear) {
        this.mYear = mYear;
    }

    public int getRunPosition() {
        return mRunPosition;
    }

    public void setRunPosition(int mRunPosition) {
        this.mRunPosition = mRunPosition;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double Lat) {
        this.mLat = Lat;
    }

    public double getLong() {
        return mLong;
    }

    public void setLong(double Long) {
        this.mLong = Long;
    }

    public LatLng getVehicleLocation() {
        return mVehicleLocation;
    }

    public void setVehicleLocation(LatLng mVehicleLocation) {
        this.mVehicleLocation = mVehicleLocation;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean mFavorite) {
        this.mFavorite = mFavorite;
    }
}
