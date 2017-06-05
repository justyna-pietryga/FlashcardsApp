package com.example.justyna.flashcards.databasemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "employeedb";
    private static final int DATABASE_VERSION = 2;

    public static final String CATEGORY_TABLE = "CATEGORY";

    public static final String ID_COLUMN = "ID";
    public static final String NAME_COLUMN = "NAME";
    public static final String TABLE_VOCABULARY = "VOCABULARY";
    public static final String CATEOGORY_ID = "CATEGORY_ID";
    public static final String COLUMN_FIRST_WORD ="FIRST_WORD";
    public static final String COLUMN_SECOND_WORD="SECOND_WORD";

    public static final String CREATE_CATEGORY_TABLE = "CREATE TABLE " + CATEGORY_TABLE + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY," + NAME_COLUMN + " TEXT" + ")";

    public static final String CREATE_VOCABULARY_TABLE = "CREATE TABLE "+TABLE_VOCABULARY + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY,"
            + COLUMN_FIRST_WORD + " TEXT,"
            + COLUMN_SECOND_WORD + " TEXT,"
            + CATEOGORY_ID + " INT, "
            + "FOREIGN KEY(" + CATEOGORY_ID + ") REFERENCES "
            + CATEGORY_TABLE + "(ID) ON DELETE CASCADE" + ")";

    private static DatabaseOpenHelper instance;

    public static synchronized DatabaseOpenHelper getHelper(Context context) {
        if (instance == null)
            instance = new DatabaseOpenHelper(context);
        return instance;
    }

    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_VOCABULARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
