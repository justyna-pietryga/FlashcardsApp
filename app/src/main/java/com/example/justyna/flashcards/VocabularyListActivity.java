package com.example.justyna.flashcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.justyna.flashcards.databasemanager.CategoryDAO;
import com.example.justyna.flashcards.databasemanager.VocabularyDAO;


public class VocabularyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    RowAdapter adapter;
    public  VocabularyListActivity CustomListView = null;
    public  ArrayList<RowBean> CustomListViewValuesArr = new ArrayList<RowBean>();
    private VocabularyDAO vocabularyDAO;
    private CategoryDAO categoryDAO;


    public static final String TAG = "debuggingVocabulary";
    private static int showOrHideButton=0;
    private static String categoryFromMainStaticToAddingNewWordInAlert;
    private static int categoryIdFromMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vocabularyDAO=new VocabularyDAO(this);
        categoryDAO= new CategoryDAO(this);
        loadContent();
    }

    private void loadContent(){


        TextView categoryTextView = (TextView) findViewById(R.id.category_textView);
        final ImageButton editWord = (ImageButton) findViewById(R.id.editWord_button);
        final ImageButton deleteWord = (ImageButton) findViewById(R.id.deleteWord_button);
        final ImageButton returnButton = (ImageButton) findViewById(R.id.return_button);
        Button openAsFlashcards = (Button) findViewById(R.id.openAsFlashcards_button);

        final SharedPreferences shared = getSharedPreferences("FlashcardsPreferences", 0);
        categoryIdFromMain = shared.getInt("WhichIdFolder", 1);


        final Category categoryFromMain=categoryDAO.getFolder(categoryIdFromMain);

        categoryFromMainStaticToAddingNewWordInAlert = categoryFromMain.getName();
       Log.d(TAG, "Vocabulary for "+categoryFromMain.getName());

        categoryTextView.setText("Kategoria: "+categoryFromMain.getName());


        List<Vocabulary>vocabularyFromData = vocabularyDAO.getAllVocabulary(categoryIdFromMain);

        Log.d(TAG, "getAllVocabularies" );


        for(int i=0; i<vocabularyFromData.size(); i++){
            CustomListViewValuesArr.add(new RowBean(vocabularyFromData.get(i).getFirstLanguageWord(),
                                        vocabularyFromData.get(i).getSecondLanguageWord(), i, vocabularyFromData.get(i).getId()));
        }


        Resources res =getResources();
        list= ( ListView )findViewById( R.id.vocabulary_listView );
        if(vocabularyFromData.size()>0) {
            adapter = new RowAdapter(CustomListView, CustomListViewValuesArr, res, VocabularyListActivity.this);
            list.setAdapter(adapter);

        }

        list.setOnItemClickListener(VocabularyListActivity.this);


        //edit and delete

        editWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                AlertDialog.Builder alert = new AlertDialog.Builder(VocabularyListActivity.this);
                final EditText edittext = new EditText(VocabularyListActivity.this);
                final EditText edittext2 = new EditText(VocabularyListActivity.this);
                int rowBeanIdInDatabase = shared.getInt("WhichRowBeanIdInDatabase", 0);
                final Vocabulary vocabularyToDelete=vocabularyDAO.getVocabulary(rowBeanIdInDatabase);

                alert.setMessage("Podaj pierwsze słowo");
                alert.setTitle("Edycja słowa");

                alert.setView(edittext);
                edittext.setText(vocabularyToDelete.getFirstLanguageWord());

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String firstWord = edittext.getText().toString();

                        AlertDialog.Builder alert2 = new AlertDialog.Builder(VocabularyListActivity.this);
                        alert2.setView(edittext2);
                        edittext2.setText(vocabularyToDelete.getSecondLanguageWord());
                        alert2.setTitle("Edycja słowa");
                        alert2.setMessage("Podaj drugie słowo");
                        alert2.setNegativeButton("ANULUJ", null);

                            alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String secondWord = edittext2.getText().toString();
                                    if (secondWord.equals("")&& firstWord.equals(""))
                                        Toast.makeText(VocabularyListActivity.this, "Oba pola nie mogą być puste!", Toast.LENGTH_LONG).show();
                                    else {
                                        if (secondWord.equals("")|| firstWord.equals(""))
                                            Toast.makeText(VocabularyListActivity.this, "Uwaga! Jedno pole jest puste!", Toast.LENGTH_LONG).show();
                                        Intent refresh = new Intent(VocabularyListActivity.this, VocabularyListActivity.class);

                                        vocabularyDAO.editVocabulary(vocabularyToDelete, firstWord,secondWord);

                                        startActivity(refresh);
                                        finish();

                                    }
                                }
                            });
                            alert2.create();
                            alert2.show();

                    }
                });


                alert.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

        deleteWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(VocabularyListActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.delete_word_alert_title)
                        .setMessage(R.string.delete_word_alert_message)
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int rowBeanIdInDatabase = shared.getInt("WhichRowBeanIdInDatabase", 0);


                                Vocabulary vocabularyToDelete=vocabularyDAO.getVocabulary(rowBeanIdInDatabase);
                                vocabularyDAO.deleteOneRow(vocabularyToDelete);

                                Intent intent = new Intent(VocabularyListActivity.this, VocabularyListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWord.setVisibility(View.INVISIBLE);
                editWord.setVisibility(View.INVISIBLE);
                returnButton.setVisibility(View.INVISIBLE);
            }
        });

        openAsFlashcards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "przegladaj fiszki- przycisk");

                List<Vocabulary> list = vocabularyDAO.getAllVocabulary(categoryFromMain.getId());

                if(list.size()==0) Toast.makeText(VocabularyListActivity.this,"Brak słownictwa!", Toast.LENGTH_LONG).show();

                else {
                    Intent intent = new Intent(VocabularyListActivity.this, FlashcardsActivity.class);
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vocabulary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addRow_item:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText edittext = new EditText(VocabularyListActivity.this);
                final EditText edittext2 = new EditText(VocabularyListActivity.this);
                alert.setMessage("Podaj pierwsze słowo");
                alert.setTitle("Tworzenie słowa");

                alert.setView(edittext);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String firstWord = edittext.getText().toString();

                        AlertDialog.Builder alert2= new AlertDialog.Builder(VocabularyListActivity.this);
                        alert2.setView(edittext2);
                            alert2.setTitle("Tworzenie słowa");
                            alert2.setMessage("Podaj drugie słowo");
                            alert2.setNegativeButton("ANULUJ", null);
                            alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                       String secondWord=edittext2.getText().toString();
                                        if (secondWord.equals("")&& firstWord.equals(""))
                                            Toast.makeText(VocabularyListActivity.this, "Oba pola nie mogą być puste!", Toast.LENGTH_LONG).show();
                                        else {
                                            if (secondWord.equals("")|| firstWord.equals(""))
                                                Toast.makeText(VocabularyListActivity.this, "Uwaga! Jedno pole jest puste!", Toast.LENGTH_LONG).show();
                                            vocabularyDAO.addWord(new Vocabulary(firstWord,secondWord, categoryIdFromMain));
                                            Intent refresh = new Intent(VocabularyListActivity.this, VocabularyListActivity.class);
                                            startActivity(refresh);
                                            finish();
                                        }
                                    }
                                });
                             alert2.create();
                             alert2.show();
                    }
                });


                alert.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showOrHideButton--;
        Intent back = new Intent(VocabularyListActivity.this, MainActivity.class);
        startActivity(back);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        final ImageButton editWord = (ImageButton) findViewById(R.id.editWord_button);
        final ImageButton deleteWord = (ImageButton) findViewById(R.id.deleteWord_button);
        ImageButton returnButton = (ImageButton) findViewById(R.id.return_button);
        SharedPreferences shared=getSharedPreferences("FlashcardsPreferences",0);

        editWord.setVisibility(View.VISIBLE); deleteWord.setVisibility(View.VISIBLE); returnButton.setVisibility(View.VISIBLE);

        RowBean rowBeanItem = checkTheRowBeanOnThePosition(CustomListViewValuesArr,position);

            SharedPreferences.Editor sharedEditor = shared.edit();
            sharedEditor.putInt("WhichRowBeanIdInDatabase", rowBeanItem.getIDinDatabase()).apply();
            sharedEditor.commit();


        Toast.makeText(VocabularyListActivity.this, rowBeanItem.getFirstWord()+" - "+rowBeanItem.getSecondWord(), Toast.LENGTH_LONG).show();
    }

    public RowBean checkTheRowBeanOnThePosition(List<RowBean> list, int position){
        for(int i=0; i<list.size(); i++){
            if(list.get(i).getPositionOnListViewId()==position) return list.get(i);
            }
        return null;
    }

}
