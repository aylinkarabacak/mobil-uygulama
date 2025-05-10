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
    private static final int DATABASE_VERSION = 2; // Versiyon artışı
    private static final String TABLE_NAME = "TODO_TABLE";

    private static final String COL_ID = "ID";
    private static final String COL_TASK = "TASK";
    private static final String COL_STATUS = "STATUS";
    private static final String COL_DATE = "DATE";
    private static final String COL_IMAGE_URI = "IMAGE_URI";

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
                COL_IMAGE_URI + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eğer versiyon yükseltilirse, eski tabloyu silip yeniden oluştur
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Yeni görev eklemek
    public void insertTask(ToDoModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK, task.getTask());
        values.put(COL_STATUS, 0); // Yeni görev başlangıçta tamamlanmamış
        values.put(COL_DATE, task.getDate());
        values.put(COL_IMAGE_URI, task.getImageUri());

        db.insert(TABLE_NAME, null, values);
    }

    // Tüm görevleri almak
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
                task.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)));
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return taskList;
    }

    // Görev güncellemek
    public void updateTask(int id, String taskText, String date, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK, taskText);
        values.put(COL_DATE, date);
        values.put(COL_IMAGE_URI, imageUri);

        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    // Durum güncellemek (tamamlandı/tamamlanmadı)
    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);

        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    // Görev silmek
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
    }
}
