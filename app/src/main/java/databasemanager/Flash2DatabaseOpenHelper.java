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

public class Flash2DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME ="VocabularyDB";
    private static final int DB_VERSION =1;

    private static final String COLUMN_ID = "id";
    private static final String TABLE_VOCABULARY = "vocabularyTable";
    private static final String COLUMN_CATEFGORY = "categoryId";
    private static final String COLUMN_FIRST_WORD ="firstWord";
    private static final String COLUMN_SECOND_WORD="secondWord";

    public Flash2DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_VOCABULARY_TABLE = "CREATE TABLE "+TABLE_VOCABULARY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_CATEFGORY + " TEXT,"
                + COLUMN_FIRST_WORD + " TEXT,"
                + COLUMN_SECOND_WORD + " TEXT" + ")";

        sqLiteDatabase.execSQL(CREATE_VOCABULARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCABULARY);
        onCreate(sqLiteDatabase);
    }

    public void addWord(Vocabulary vocabulary) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEFGORY, vocabulary.getCategory());
        values.put(COLUMN_FIRST_WORD, vocabulary.getFirstLanguageWord());
        values.put(COLUMN_SECOND_WORD, vocabulary.getSecondLanguageWord());

        db.insert(TABLE_VOCABULARY,null, values);
        db.close();
    }

    public Vocabulary getVocabulary(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VOCABULARY, new String[]{COLUMN_ID,
                        COLUMN_CATEFGORY, COLUMN_FIRST_WORD, COLUMN_SECOND_WORD}, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Vocabulary vocabulary = new Vocabulary(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),cursor.getString(2),cursor.getString(3));

        return vocabulary;
    }

    public List<Vocabulary> getAllVocabulary(String category) {
        List<Vocabulary> vocabularyList = new ArrayList<Vocabulary>();

        String selectQuery = "SELECT * FROM "+ TABLE_VOCABULARY+
                            " WHERE TRIM("+COLUMN_CATEFGORY+") = '"+category+"'";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.setId(Integer.parseInt(cursor.getString(0)));
                vocabulary.setCategory(cursor.getString(1));
                vocabulary.setFirstLanguageWord(cursor.getString(2));
                vocabulary.setSecondLanguageWord(cursor.getString(3));
                vocabularyList.add(vocabulary);
            } while(cursor.moveToNext());
        }

        return vocabularyList;
    }

    public List<Vocabulary> getAllWordsFromAllCategories() {
        List<Vocabulary> vocabularyList = new ArrayList<Vocabulary>();

        String selectQuery = "SELECT * FROM "+ TABLE_VOCABULARY;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.setId(Integer.parseInt(cursor.getString(0)));
                vocabulary.setCategory(cursor.getString(1));
                vocabulary.setFirstLanguageWord(cursor.getString(2));
                vocabulary.setSecondLanguageWord(cursor.getString(3));
                vocabularyList.add(vocabulary);
            } while(cursor.moveToNext());
        }

        return vocabularyList;
    }

    public void editVocabulary(String categoryName, Vocabulary vocabulary, String newFirstWord, String newSecondName){
        SQLiteDatabase db = this.getWritableDatabase();

        List<Vocabulary> vocabularies = getAllWordsFromAllCategories();
        int id = vocabulary.getId();

        deleteAllWords();
        for(int i=0; i<id-1; i++){
            addWord(vocabularies.get(i));
        }

        addWord(new Vocabulary(categoryName, newFirstWord, newSecondName));

        for(int i=id; i<vocabularies.size(); i++){
            addWord(vocabularies.get(i));
        }

        db.close();
    }
    public void deleteAllWords(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VOCABULARY,null,null);
        db.close();
    }

    public void deleteOneRow(Vocabulary vocabulary){
        SQLiteDatabase db = this.getWritableDatabase();
        int id = vocabulary.getId();
        db.delete(TABLE_VOCABULARY, COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });

        db.close();
    }

    public void deleteWordsFromCategory(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_VOCABULARY + " WHERE TRIM("+COLUMN_CATEFGORY+") = '"+category+"'");
        db.close();
    }
}
