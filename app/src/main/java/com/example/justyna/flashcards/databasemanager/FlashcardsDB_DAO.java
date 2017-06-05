package com.example.justyna.flashcards.databasemanager;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FlashcardsDB_DAO {

    protected SQLiteDatabase database;
    private DatabaseOpenHelper dbHelper;
    private Context mContext;

    public FlashcardsDB_DAO(Context context) {
        this.mContext = context;
        dbHelper = DatabaseOpenHelper.getHelper(mContext);
        open();

    }

    public void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = DatabaseOpenHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }

    /*public void close() {
        dbHelper.close();
        database = null;
    }*/
}
