package com.zhack.poskasir.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class POSData implements Parcelable {

    public String image;
    public String title;
    public int quantity;
    public int price;

    public POSData() { }

    protected POSData(Parcel in) {
        image = in.readString();
        title = in.readString();
        quantity = in.readInt();
        price = in.readInt();
    }

    public static final Creator<POSData> CREATOR = new Creator<POSData>() {
        @Override
        public POSData createFromParcel(Parcel in) {
            return new POSData(in);
        }

        @Override
        public POSData[] newArray(int size) {
            return new POSData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(title);
        dest.writeInt(quantity);
        dest.writeInt(price);
    }
}
