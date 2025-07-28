package com.example.teachtrack.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "teachtrack.db";
    private static final int DATABASE_VERSION = 1;

    private static DbHelper instance;

    public static synchronized DbHelper getInstance(Context context){
        if(instance == null){
            instance = new DbHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int getStudentCountForClass(int classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Student WHERE class_id = ?", new String[]{String.valueOf(classId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys=ON");

        db.execSQL("CREATE TABLE Class(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE Student(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, roll_no TEXT, class_id INTEGER, FOREIGN KEY (class_id) REFERENCES Class(id) ON DELETE CASCADE) ");
        db.execSQL("CREATE TABLE Session(id INTEGER PRIMARY KEY AUTOINCREMENT, class_id INTEGER, date TEXT, topic TEXT,att_done INTEGER DEFAULT 0, FOREIGN KEY (class_id) REFERENCES Class(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE Attendance(id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, student_id INTEGER, is_present INTEGER CHECK (is_present IN (0, 1)), FOREIGN KEY (session_id) REFERENCES Session(id) ON DELETE CASCADE, FOREIGN KEY (student_id) REFERENCES Student(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop and recreate (simple but not ideal for real apps)
        db.execSQL("DROP TABLE IF EXISTS Class");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Session");
        db.execSQL("DROP TABLE IF EXISTS Attendance");
        onCreate(db);
    }
}
