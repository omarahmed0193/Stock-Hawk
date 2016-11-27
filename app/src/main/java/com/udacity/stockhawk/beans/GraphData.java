package com.udacity.stockhawk.beans;


import android.os.Parcel;
import android.os.Parcelable;

public class GraphData implements Parcelable {
    private Float mDayOfTheMonth;
    private Float mStockValue;

    public GraphData(float dayOfTheMonth, float stockValue) {
        mDayOfTheMonth = dayOfTheMonth;
        mStockValue = stockValue;
    }

    public Float getDayOfTheMonth() {
        return mDayOfTheMonth;
    }

    public void setDayOfTheMonth(float dayOfTheMonth) {
        mDayOfTheMonth = dayOfTheMonth;
    }

    public Float getStockValue() {
        return mStockValue;
    }

    public void setStockValue(float stockValue) {
        mStockValue = stockValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mDayOfTheMonth);
        dest.writeValue(this.mStockValue);
    }

    protected GraphData(Parcel in) {
        this.mDayOfTheMonth = (Float) in.readValue(Float.class.getClassLoader());
        this.mStockValue = (Float) in.readValue(Float.class.getClassLoader());
    }

    public static final Parcelable.Creator<GraphData> CREATOR = new Parcelable.Creator<GraphData>() {
        @Override
        public GraphData createFromParcel(Parcel source) {
            return new GraphData(source);
        }

        @Override
        public GraphData[] newArray(int size) {
            return new GraphData[size];
        }
    };
}
