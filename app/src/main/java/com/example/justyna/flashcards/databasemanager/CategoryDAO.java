package com.example.justyna.flashcards.databasemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.justyna.flashcards.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends FlashcardsDB_DAO {

    private static final String WHERE_ID_EQUALS = DatabaseOpenHelper.ID_COLUMN + " =?";

    public CategoryDAO(Context context) {
        super(context);
    }

    public long addCateogry(Category category) {
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.NAME_COLUMN, category.getName());

        return database.insert(DatabaseOpenHelper.CATEGORY_TABLE, null, values);
    }

    public List<Category> getAllFolders() {
        List<Category> foldersList = new ArrayList<Category>();

        String selectQuery = "SELECT * FROM " + DatabaseOpenHelper.CATEGORY_TABLE;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(Integer.parseInt(cursor.getString(0)));
                category.setName(cursor.getString(1));
                foldersList.add(category);
            } while (cursor.moveToNext());
        }

        return foldersList;
    }

    public Category getFolder(int id) {
        Cursor cursor = database.query(DatabaseOpenHelper.CATEGORY_TABLE, new String[]{DatabaseOpenHelper.ID_COLUMN,
                        DatabaseOpenHelper.NAME_COLUMN}, DatabaseOpenHelper.ID_COLUMN + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Category category = new Category(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));

        return category;
    }

    public void editFolder(Category category, String newName) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseOpenHelper.NAME_COLUMN, newName);

        database.update(DatabaseOpenHelper.CATEGORY_TABLE, cv, "ID = " + category.getId(), null);
    }

    public void deleteFolder(Category category) {
        int id = category.getId();
        database.execSQL("DELETE FROM " + DatabaseOpenHelper.CATEGORY_TABLE + " WHERE " + DatabaseOpenHelper.ID_COLUMN + "= " + id);
    }

}
