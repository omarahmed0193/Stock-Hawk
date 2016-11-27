
package com.udacity.stockhawk.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Quote extends RealmObject implements Parcelable {

    @PrimaryKey
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("Bid")
    @Expose
    private String bid;
    @SerializedName("Change")
    @Expose
    private String change;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("ChangeinPercent")
    @Expose
    private String changeinPercent;

    public Quote() {

    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChangeinPercent() {
        return changeinPercent;
    }

    public void setChangeinPercent(String changeinPercent) {
        this.changeinPercent = changeinPercent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.symbol);
        dest.writeString(this.bid);
        dest.writeString(this.change);
        dest.writeString(this.name);
        dest.writeString(this.changeinPercent);
    }

    protected Quote(Parcel in) {
        this.symbol = in.readString();
        this.bid = in.readString();
        this.change = in.readString();
        this.name = in.readString();
        this.changeinPercent = in.readString();
    }

    public static final Parcelable.Creator<Quote> CREATOR = new Parcelable.Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel source) {
            return new Quote(source);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };
}
