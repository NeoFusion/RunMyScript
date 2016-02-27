package neofusion.runmyscript.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ScriptItem implements Parcelable {
    public static final int TYPE_SINGLE_COMMAND = 1;
    public static final int TYPE_PATH_TO_FILE = 2;

    private long mId;
    private String mName;
    private String mPath;
    private int mType;
    private boolean mSu;

    public ScriptItem(long id, String name, String path, int type, boolean su) {
        mId = id;
        mName = name;
        mPath = path;
        mType = type;
        mSu = su;
    }

    public ScriptItem(String name, String path, int type, boolean su) {
        mId = -1;
        mName = name;
        mPath = path;
        mType = type;
        mSu = su;
    }

    private ScriptItem(Parcel parcel) {
        mId = parcel.readLong();
        mName = parcel.readString();
        mPath = parcel.readString();
        mType = parcel.readInt();
        mSu = parcel.readByte() != 0;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setSu(boolean su) {
        mSu = su;
    }

    public boolean getSu() {
        return mSu;
    }

    public String toString() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mPath);
        dest.writeInt(mType);
        dest.writeByte((byte) (mSu ? 1 : 0));
    }

    public static final Parcelable.Creator<ScriptItem> CREATOR = new Parcelable.Creator<ScriptItem>() {
        @Override
        public ScriptItem createFromParcel(Parcel source) {
            return new ScriptItem(source);
        }

        @Override
        public ScriptItem[] newArray(int size) {
            return new ScriptItem[size];
        }
    };
}