package com.dilimanlabs.formbase;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 4/27/2015.
 */
public class FormbaseParcelable implements Parcelable {
    private int mData;

    public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    public static final Parcelable.Creator<FormbaseParcelable> CREATOR
            = new Parcelable.Creator<FormbaseParcelable>() {
        public FormbaseParcelable createFromParcel(Parcel in) {
            return new FormbaseParcelable(in);
        }

        public FormbaseParcelable[] newArray(int size) {
            return new FormbaseParcelable[size];
        }
    };

    /** recreate object from parcel */
    private FormbaseParcelable(Parcel in) {
        mData = in.readInt();
    }
}
