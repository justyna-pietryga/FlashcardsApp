package databasemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.justyna.flashcards.Category;
import com.example.justyna.flashcards.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class FlashDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME ="FoldersDB";
    private static final int DB_VERSION =1;

    private static final String TABLE_FOLDERS = "folders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    private static final String TABLE_VOCABULARY = "vocabularyTable";
    private static final String COLUMN_CATEFGORY = "categoryId";
    private static final String COLUMN_FIRST_WORD ="firstWord";
    private static final String COLUMN_SECOND_WORD="secondWord";

    public FlashDatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_FOLDERS_TABLE = "CREATE TABLE " + TABLE_FOLDERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT" + ")";

        /*String CREATE_VOCABULARY_TABLE = "CREATE TABLE "+TABLE_VOCABULARY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_CATEFGORY + " TEXT,"
                + COLUMN_FIRST_WORD + " TEXT,"
                + COLUMN_SECOND_WORD + " TEXT" + ")"; */

        sqLiteDatabase.execSQL(CREATE_FOLDERS_TABLE);
        //sqLiteDatabase.execSQL(CREATE_VOCABULARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
       // sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCABULARY);
        onCreate(sqLiteDatabase);
    }

    public void addFolder(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, category.getName()); // Contact Name

        db.insert(TABLE_FOLDERS, null, values);

        db.close();
    }

    public Category getFolder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FOLDERS, new String[]{COLUMN_ID,
                        COLUMN_NAME}, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Category category = new Category(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));

        return category;
    }

    public List<Category> getAllFolders() {
        List<Category> foldersList = new ArrayList<Category>();

        String selectQuery = "SELECT * FROM " + TABLE_FOLDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(Integer.parseInt(cursor.getString(0)));
                category.setName(cursor.getString(1));
                foldersList.add(category);
            } while (cursor.moveToNext());
        }

        // return contact list
        return foldersList;
    }

    public int updateFolder(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, category.getName());

        // updating row
        return db.update(TABLE_FOLDERS, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(category.getId()) });
    }

    public void editFolder(Category category, String newName){
        SQLiteDatabase db = this.getWritableDatabase();

        List<Category> categories = getAllFolders();
        int id = category.getId();

        deleteAllFolders();
        for(int i=0; i<id-1; i++){
            addFolder(categories.get(i));
        }

        addFolder(new Category(newName));

        for(int i=id; i<categories.size(); i++){
            addFolder(categories.get(i));
        }

        db.close();
    }

    public void deleteFolder(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id=category.getId();
       /* db.delete(TABLE_FOLDERS, COLUMN_ID + " = ?",
                new String[] { String.valueOf(category.getId()) });
        List<Category> categories=getAllFolders();
        for(int i=id+1; i<categories.size();i++){
            categories.set(i, categories.get(i+1));
        }

        deleteAllFolders();

        for(int i=0; i<categories.size();i++){
            addFolder(categories.get(i));
        } */

        List<Category> categories = getAllFolders();
        deleteAllFolders();
        for(int i=0; i<id-1; i++){
            addFolder(categories.get(i));
        }

        for(int i=id; i<categories.size(); i++){
            addFolder(categories.get(i));
        }

        db.close();
    }


    public void deleteAllFolders(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOLDERS,null,null);
        db.close();
    }

    public int getFoldersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FOLDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
