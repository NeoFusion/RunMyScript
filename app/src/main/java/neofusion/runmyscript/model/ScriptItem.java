/*
 * Copyright 2013 Evgeniy NeoFusion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neofusion.runmyscript.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

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

    @NonNull
    @Override
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