package com.hynet.heebit.components.widget.lock;

import android.os.Parcel;
import android.os.Parcelable;

import com.hynet.heebit.components.constant.Regex;

public class Point implements Parcelable {

    public int x;
    public int y;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point src) {
        this.x = src.x;
        this.y = src.y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final void negate() {
        x = -x;
        y = -y;
    }

    public final void offset(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public final boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point point = (Point) o;
            return this.x == point.x && this.y == point.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x * 32713 + y;
    }

    @Override
    public String toString() {
        return x + Regex.COMMA.getRegext() + y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(x);
        out.writeInt(y);
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {

        @Override
        public Point createFromParcel(Parcel source) {
            Point point = new Point();
            point.readFromParcel(source);
            return point;
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public void readFromParcel(Parcel in) {
        x = in.readInt();
        y = in.readInt();
    }
}
