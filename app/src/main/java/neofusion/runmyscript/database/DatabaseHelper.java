package neofusion.runmyscript.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import neofusion.runmyscript.model.ScriptItem;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;
    private static final String DATABASE_NAME = "runmyscript.db";
    private static final int DATABASE_VERSION = 4;
    public static final String TABLE_NAME = "scriptitems";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_SU = "su";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
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