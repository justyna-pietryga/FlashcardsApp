package com.example.justyna.flashcards.databasemanager;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.justyna.flashcards.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class VocabularyDAO extends FlashcardsDB_DAO {

    private static final String WHERE_ID_EQUALS = DatabaseOpenHelper.ID_COLUMN
            + " =?";

    public VocabularyDAO(Context context) {
        super(context);
    }

    public long addWord(Vocabulary vocabulary) {

        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.COLUMN_FIRST_WORD, vocabulary.getFirstLanguageWord());
        values.put(DatabaseOpenHelper.COLUMN_SECOND_WORD, vocabulary.getSecondLanguageWord());
        values.put(DatabaseOpenHelper.CATEOGORY_ID,vocabulary.getCategoryID());

        return database.insert(DatabaseOpenHelper.TABLE_VOCABULARY,null, values);

    }

    public Vocabulary getVocabulary(int id) {

        Cursor cursor = database.query(DatabaseOpenHelper.TABLE_VOCABULARY, new String[]{DatabaseOpenHelper.ID_COLUMN,
                DatabaseOpenHelper.COLUMN_FIRST_WORD, DatabaseOpenHelper.COLUMN_SECOND_WORD}, DatabaseOpenHelper.ID_COLUMN + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Vocabulary vocabulary = new Vocabulary(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),cursor.getString(2));

        return vocabulary;
    }

    public List<Vocabulary> getAllVocabulary(int category) {
        List<Vocabulary> vocabularyList = new ArrayList<Vocabulary>();

        String selectQuery = "SELECT "+"V."+DatabaseOpenHelper.ID_COLUMN+", "
                +"V."+DatabaseOpenHelper.COLUMN_FIRST_WORD+", "
                +"V."+DatabaseOpenHelper.COLUMN_SECOND_WORD
                +" FROM "+ DatabaseOpenHelper.TABLE_VOCABULARY+" V,"
                +DatabaseOpenHelper.CATEGORY_TABLE+" C"
                +" WHERE V.CATEGORY_ID=C.ID AND C.ID="+category;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.setId(Integer.parseInt(cursor.getString(0)));
                vocabulary.setFirstLanguageWord(cursor.getString(1));
                vocabulary.setSecondLanguageWord(cursor.getString(2));
                vocabularyList.add(vocabulary);
            } while(cursor.moveToNext());
        }

        return vocabularyList;
    }

    public void editVocabulary(Vocabulary vocabulary, String newFirstWord, String newSecondName){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseOpenHelper.COLUMN_FIRST_WORD,newFirstWord);
        cv.put(DatabaseOpenHelper.COLUMN_SECOND_WORD,newSecondName);

        database.update(DatabaseOpenHelper.TABLE_VOCABULARY, cv, "ID = "+vocabulary.getId(), null);
    }

    public void deleteAllWords(){
        database.delete(DatabaseOpenHelper.TABLE_VOCABULARY,null,null);
        database.close();
    }

    public void deleteOneRow(Vocabulary vocabulary){
        int id = vocabulary.getId();
        database.delete(DatabaseOpenHelper.TABLE_VOCABULARY, DatabaseOpenHelper.ID_COLUMN + " = ?",
                new String[] { String.valueOf(id) });

        database.close();
    }

}
