package neofusion.runmyscript.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import neofusion.runmyscript.model.ScriptItem;

public class ScriptItemDataSource {
    private DatabaseHelper mDatabaseHelper;

    public ScriptItemDataSource(Context context) {
        mDatabaseHelper = DatabaseHelper.getInstance(context);
    }

    public ArrayList<ScriptItem> getAll() {
        ArrayList<ScriptItem> listItems = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, new String[] {
                        DatabaseHelper.COLUMN_ID,
                        DatabaseHelper.COLUMN_NAME,
                        DatabaseHelper.COLUMN_PATH,
                        DatabaseHelper.COLUMN_TYPE,
                        DatabaseHelper.COLUMN_SU
                }, null, null, null, null, DatabaseHelper.COLUMN_NAME);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            String path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PATH));
            int type = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
            boolean su = (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SU)) == 1);
            listItems.add(new ScriptItem(id, name, path, type, su));
        }
        cursor.close();
        db.close();
        return listItems;
    }

    public void insert(ScriptItem scriptItem) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, scriptItem.getName());
        contentValues.put(DatabaseHelper.COLUMN_PATH, scriptItem.getPath());
        contentValues.put(DatabaseHelper.COLUMN_TYPE, scriptItem.getType());
        contentValues.put(DatabaseHelper.COLUMN_SU, scriptItem.getSu() ? 1 : 0);
        db.insertOrThrow(DatabaseHelper.TABLE_NAME, null, contentValues);
        db.close();
    }

    public void insert(ArrayList<ScriptItem> scriptItems) {
        if (scriptItems != null) {
            SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                for (ScriptItem item : scriptItems) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseHelper.COLUMN_NAME, item.getName());
                    contentValues.put(DatabaseHelper.COLUMN_PATH, item.getPath());
                    contentValues.put(DatabaseHelper.COLUMN_TYPE, item.getType());
                    contentValues.put(DatabaseHelper.COLUMN_SU, item.getSu() ? 1 : 0);
                    db.insertOrThrow(DatabaseHelper.TABLE_NAME, null, contentValues);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            db.close();
        }
    }

    public void update(ScriptItem scriptItem) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, scriptItem.getName());
        contentValues.put(DatabaseHelper.COLUMN_PATH, scriptItem.getPath());
        contentValues.put(DatabaseHelper.COLUMN_TYPE, scriptItem.getType());
        if (scriptItem.getSu()) {
            contentValues.put(DatabaseHelper.COLUMN_SU, 1);
        } else {
            contentValues.put(DatabaseHelper.COLUMN_SU, 0);
        }
        db.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.COLUMN_ID + "=" + Long.toString(scriptItem.getId()), null);
        db.close();
    }

    public void delete(long id) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + "=" + Long.toString(id), null);
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, "1", null);
        db.close();
    }
}