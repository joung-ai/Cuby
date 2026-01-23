package com.example.cuby;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CubyDatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "cuby.db";
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public CubyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // enable foreign keys
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // create tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // user profile
        db.execSQL(
                "CREATE TABLE userProfile (" +
                        "userID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL," +
                        "cubyName TEXT NOT NULL," +
                        "createdAt INTEGER NOT NULL" +
                        ");"
        );

        // daily plot
        db.execSQL(
                "CREATE TABLE dailyPlot (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "userID INTEGER NOT NULL," +
                        "date TEXT NOT NULL UNIQUE," +
                        "mood TEXT NOT NULL," +
                        "note TEXT," +
                        "plantImagePath TEXT," +
                        "createdAt INTEGER NOT NULL," +
                        "FOREIGN KEY (userID) REFERENCES userProfile(userID) ON DELETE CASCADE" +
                        ");"
        );

        // diary entry
        db.execSQL(
                "CREATE TABLE diaryEntry (" +
                        "diaryID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "dailyPlotID INTEGER NOT NULL," +
                        "content TEXT NOT NULL," +
                        "createdAt INTEGER NOT NULL," +
                        "FOREIGN KEY (dailyPlotID) REFERENCES dailyPlot(id) ON DELETE CASCADE" +
                        ");"
        );

        // daily task
        db.execSQL(
                "CREATE TABLE dailyTask (" +
                        "taskID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "dailyPlotID INTEGER NOT NULL," +
                        "taskType TEXT NOT NULL," +
                        "completed INTEGER DEFAULT 0," +
                        "createdAt INTEGER NOT NULL," +
                        "FOREIGN KEY (dailyPlotID) REFERENCES dailyPlot(id) ON DELETE CASCADE" +
                        ");"
        );

        // detox session
        db.execSQL(
                "CREATE TABLE detoxSession (" +
                        "detoxID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "userID INTEGER NOT NULL," +
                        "type TEXT NOT NULL," +
                        "durationMinutes INTEGER NOT NULL," +
                        "completed INTEGER DEFAULT 0," +
                        "createdAt INTEGER NOT NULL," +
                        "FOREIGN KEY (userID) REFERENCES userProfile(userID) ON DELETE CASCADE" +
                        ");"
        );

        // interaction
        db.execSQL(
                "CREATE TABLE interactionLog (" +
                        "interactionID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "userID INTEGER NOT NULL," +
                        "actionType TEXT NOT NULL," +
                        "createdAt INTEGER NOT NULL," +
                        "FOREIGN KEY (userID) REFERENCES userProfile(userID) ON DELETE CASCADE" +
                        ");"
        );
    }

    // for databse upgrades
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS interactionLog");
        db.execSQL("DROP TABLE IF EXISTS detoxSession");
        db.execSQL("DROP TABLE IF EXISTS dailyTask");
        db.execSQL("DROP TABLE IF EXISTS diaryEntry");
        db.execSQL("DROP TABLE IF EXISTS dailyPlot");
        db.execSQL("DROP TABLE IF EXISTS userProfile");
        onCreate(db);
    }
}
