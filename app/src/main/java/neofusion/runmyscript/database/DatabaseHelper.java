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

package neofusion.runmyscript.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import neofusion.runmyscript.model.ScriptItem;

class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;
    private static final String DATABASE_NAME = "runmyscript.db";
    private static final int DATABASE_VERSION = 4;
    static final String TABLE_NAME = "scriptitems";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_PATH = "path";
    static final String COLUMN_TYPE = "type";
    static final String COLUMN_SU = "su";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PATH + " TEXT,"
                + COLUMN_TYPE + " INTEGER,"
                + COLUMN_SU + " INTEGER"
                + ");");
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        ScriptItem[] scriptItems = {
                new ScriptItem("List root directory contents", "ls /", ScriptItem.TYPE_SINGLE_COMMAND, false),
                new ScriptItem("Current processes", "ps", ScriptItem.TYPE_SINGLE_COMMAND, false),
                new ScriptItem("Uptime", "uptime", ScriptItem.TYPE_SINGLE_COMMAND, false)
        };
        db.beginTransaction();
        try {
            for (ScriptItem item : scriptItems) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAME, item.getName());
                contentValues.put(COLUMN_PATH, item.getPath());
                contentValues.put(COLUMN_TYPE, item.getType());
                contentValues.put(COLUMN_SU, item.getSu() ? 1 : 0);
                db.insertOrThrow(TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}