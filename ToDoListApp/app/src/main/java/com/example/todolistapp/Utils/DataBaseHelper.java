package com.example.todolistapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolistapp.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TODO_DATABASE";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_NAME = "TODO_TABLE";

    private static final String COL_ID = "ID";
    private static final String COL_TASK = "TASK";
    private static final String COL_STATUS = "STATUS";
    private static final String COL_DATE = "DATE";
    private static final String COL_IMAGE_URI = "IMAGE_URI";
    private static final String COL_TIME = "TIME";
    private static final String COL_SOUND_URI = "SOUND_URI";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK + " TEXT, " +
                COL_STATUS + " INTEGER, " +
                COL_DATE + " TEXT, " +
                COL_IMAGE_URI + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_SOUND_URI + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_TIME + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_IMAGE_URI + " TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SOUND_URI + " TEXT");
        }
    }

    public long insertTask(ToDoModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK, task.getTask());
        values.put(COL_STATUS, 0);
        values.put(COL_DATE, task.getDate());
        values.put(COL_TIME, task.getTime());
        values.put(COL_IMAGE_URI, task.getImageUri());
        values.put(COL_SOUND_URI, task.getSoundUri());

        long id = db.insert(TABLE_NAME, null, values);
        return id;
    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ToDoModel task = new ToDoModel();
                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                task.setTask(cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK)));
                task.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COL_STATUS)));
                task.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
                task.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME)));
                task.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)));
                task.setSoundUri(cursor.getString(cursor.getColumnIndexOrThrow(COL_SOUND_URI)));
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return taskList;
    }

    public void updateTask(ToDoModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK, task.getTask());
        values.put(COL_DATE, task.getDate());
        values.put(COL_TIME, task.getTime());
        values.put(COL_IMAGE_URI, task.getImageUri());
        values.put(COL_SOUND_URI, task.getSoundUri());

        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(task.getId())});
    }

    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
    }
    public void markTaskAsDone(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", 1); // 1 = tamamlandı
        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(taskId)});
    }

    public ToDoModel getTaskById(int taskId) {
        return null;
    }

}