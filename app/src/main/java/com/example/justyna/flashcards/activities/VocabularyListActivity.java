package com.example.justyna.flashcards.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.example.justyna.flashcards.model.Category;
import com.example.justyna.flashcards.R;
import com.example.justyna.flashcards.RowAdapter;
import com.example.justyna.flashcards.RowBean;
import com.example.justyna.flashcards.model.Vocabulary;
import com.example.justyna.flashcards.databasemanager.CategoryDAO;
import com.example.justyna.flashcards.databasemanager.VocabularyDAO;


public class VocabularyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    RowAdapter adapter;
    public VocabularyListActivity CustomListView = null;
    public ArrayList<RowBean> CustomListViewValuesArr = new ArrayList<RowBean>();
    private VocabularyDAO vocabularyDAO;
    private CategoryDAO categoryDAO;
    private Category categoryFromMain;
    SharedPreferences shared;

    public static final String TAG = "debuggingVocabulary";
    private static int showOrHideButton = 0;
    private static String categoryFromMainStaticToAddingNewWordInAlert;
    private static int categoryIdFromMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        shared = getSharedPreferences("FlashcardsPreferences", 0);
        vocabularyDAO = new VocabularyDAO(this);
        categoryDAO = new CategoryDAO(this);
        loadContent();
    }

    private void setUpAtStartAndSetCategoryText(TextView categoryTextView) {
        final SharedPreferences shared = getSharedPreferences("FlashcardsPreferences", 0);
        categoryIdFromMain = shared.getInt("WhichIdFolder", 1);
        categoryFromMain = categoryDAO.getFolder(categoryIdFromMain);
        categoryFromMainStaticToAddingNewWordInAlert = categoryFromMain.getName();

        categoryTextView.setText(getString(R.string.category) + categoryFromMain.getName());
    }

    private void configureListView(List<Vocabulary> vocabularyFromData) {
        for (int i = 0; i < vocabularyFromData.size(); i++) {
            CustomListViewValuesArr.add(new RowBean(vocabularyFromData.get(i).getFirstLanguageWord(),
                    vocabularyFromData.get(i).getSecondLanguageWord(), i, vocabularyFromData.get(i).getId()));
        }

        Resources res = getResources();
        list = (ListView) findViewById(R.id.vocabulary_listView);
        if (vocabularyFromData.size() > 0) {
            adapter = new RowAdapter(CustomListView, CustomListViewValuesArr, res, VocabularyListActivity.this);
            list.setAdapter(adapter);
        }

        list.setOnItemClickListener(VocabularyListActivity.this);
    }

    @SuppressLint("SetTextI18n")
    private void loadContent() {

        TextView categoryTextView = (TextView) findViewById(R.id.category_textView);
        final ImageButton editWord = (ImageButton) findViewById(R.id.editWord_button);
        final ImageButton deleteWord = (ImageButton) findViewById(R.id.deleteWord_button);
        final ImageButton returnButton = (ImageButton) findViewById(R.id.return_button);
        Button openAsFlashcards = (Button) findViewById(R.id.openAsFlashcards_button);
        setUpAtStartAndSetCategoryText(categoryTextView);
        List<Vocabulary> vocabularyFromData = vocabularyDAO.getAllVocabularyByCategory(categoryIdFromMain);

        configureListView(vocabularyFromData);

        editWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editWordOnClick();
            }
        });

        deleteWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWordOnClick();
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
                List<Vocabulary> list = vocabularyDAO.getAllVocabularyByCategory(categoryFromMain.getId());

                if (list.size() == 0)
                    Toast.makeText(VocabularyListActivity.this, R.string.no_vocabulary, Toast.LENGTH_LONG).show();

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
        switch (item.getItemId()) {
            case R.id.addRow_item: {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText firstWordEtidText = new EditText(VocabularyListActivity.this);
                final EditText secondWordEditText = new EditText(VocabularyListActivity.this);
                alert.setMessage(R.string.setFirstWord);
                alert.setTitle(R.string.addingWordTitle);
                alert.setView(firstWordEtidText);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        oKAfterAddWordOnClick(firstWordEtidText, secondWordEditText);
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

    private void deleteWordOnClick(){
        new AlertDialog.Builder(VocabularyListActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.delete_word_alert_title)
                .setMessage(R.string.delete_word_alert_message)
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int rowBeanIdInDatabase = shared.getInt("WhichRowBeanIdInDatabase", 0);

                        Vocabulary vocabularyToDelete = vocabularyDAO.getVocabulary(rowBeanIdInDatabase);
                        vocabularyDAO.deleteOneRow(vocabularyToDelete);

                        Intent intent = new Intent(VocabularyListActivity.this, VocabularyListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void editWordOnClick() {
        AlertDialog.Builder alert = new AlertDialog.Builder(VocabularyListActivity.this);
        final EditText editFirstWordText = new EditText(VocabularyListActivity.this);
        final EditText editSecondWordText = new EditText(VocabularyListActivity.this);
        int rowBeanIdInDatabase = shared.getInt("WhichRowBeanIdInDatabase", 0);
        final Vocabulary vocabularyToEdit = vocabularyDAO.getVocabulary(rowBeanIdInDatabase);

        alert.setMessage(R.string.setFirstWord);
        alert.setTitle(R.string.editingWordTitle);

        alert.setView(editFirstWordText);
        editFirstWordText.setText(vocabularyToEdit.getFirstLanguageWord());

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                okAfterFirstWordForEditWordProccess(editFirstWordText, editSecondWordText, vocabularyToEdit);
            }
        });

        alert.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    private void okAfterFirstWordForEditWordProccess(final EditText editFirstWordText, final EditText editSecondWordText, final Vocabulary vocabularyToEdit) {
        AlertDialog.Builder alert2 = new AlertDialog.Builder(VocabularyListActivity.this);
        alert2.setView(editSecondWordText);
        editSecondWordText.setText(vocabularyToEdit.getSecondLanguageWord());
        alert2.setTitle(R.string.editingWordTitle);
        alert2.setMessage(R.string.setSecondWord);
        alert2.setNegativeButton("ANULUJ", null);

        alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                okAfterSecondWordForEditAndHandleTheProcess(editFirstWordText, editSecondWordText, vocabularyToEdit);
            }
        });
        alert2.create();
        alert2.show();
    }

    private void okAfterSecondWordForEditAndHandleTheProcess(EditText editFirstWordText,
                                                             EditText editSecondWordText, Vocabulary vocabularyToEdit) {
        String firstWord = editFirstWordText.getText().toString();
        String secondWord = editSecondWordText.getText().toString();
        if (secondWord.equals("") && firstWord.equals(""))
            Toast.makeText(VocabularyListActivity.this, R.string.alert_both_field_empty, Toast.LENGTH_LONG).show();
        else {
            if (secondWord.equals("") || firstWord.equals(""))
                Toast.makeText(VocabularyListActivity.this, R.string.alert_one_field_empty, Toast.LENGTH_LONG).show();

            Intent refresh = new Intent(VocabularyListActivity.this, VocabularyListActivity.class);
            vocabularyDAO.editVocabulary(vocabularyToEdit, firstWord, secondWord);

            startActivity(refresh);
            finish();
        }
    }

    private void oKAfterAddWordOnClick(EditText firstWordEtidText, final EditText secondWordEditText){
        final String firstWord = firstWordEtidText.getText().toString();

        AlertDialog.Builder alert2 = new AlertDialog.Builder(VocabularyListActivity.this);
        alert2.setView(secondWordEditText);
        alert2.setTitle(R.string.addingWordTitle);
        alert2.setMessage(R.string.setSecondWord);
        alert2.setNegativeButton("ANULUJ", null);
        alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String secondWord = secondWordEditText.getText().toString();
                if (secondWord.equals("") && firstWord.equals(""))
                    Toast.makeText(VocabularyListActivity.this, R.string.alert_both_field_empty, Toast.LENGTH_LONG).show();
                else {
                    if (secondWord.equals("") || firstWord.equals(""))
                        Toast.makeText(VocabularyListActivity.this, R.string.alert_one_field_empty, Toast.LENGTH_LONG).show();
                    vocabularyDAO.addWord(new Vocabulary(firstWord, secondWord, categoryIdFromMain));
                    Intent refresh = new Intent(VocabularyListActivity.this, VocabularyListActivity.class);
                    startActivity(refresh);
                    finish();
                }
            }
        });
        alert2.create();
        alert2.show();
    }

    @Override
    public void onBackPressed() {
        showOrHideButton--;
        Intent back = new Intent(VocabularyListActivity.this, MainActivity.class);
        startActivity(back);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        final ImageButton editWord = (ImageButton) findViewById(R.id.editWord_button);
        final ImageButton deleteWord = (ImageButton) findViewById(R.id.deleteWord_button);
        ImageButton returnButton = (ImageButton) findViewById(R.id.return_button);
        SharedPreferences shared = getSharedPreferences("FlashcardsPreferences", 0);

        editWord.setVisibility(View.VISIBLE);
        deleteWord.setVisibility(View.VISIBLE);
        returnButton.setVisibility(View.VISIBLE);

        RowBean rowBeanItem = checkTheRowBeanOnThePosition(CustomListViewValuesArr, position);

        SharedPreferences.Editor sharedEditor = shared.edit();
        sharedEditor.putInt("WhichRowBeanIdInDatabase", rowBeanItem.getIDinDatabase()).apply();
        sharedEditor.commit();


        Toast.makeText(VocabularyListActivity.this, rowBeanItem.getFirstWord() + " - " + rowBeanItem.getSecondWord(), Toast.LENGTH_LONG).show();
    }

    public RowBean checkTheRowBeanOnThePosition(List<RowBean> list, int position) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPositionOnListViewId() == position) return list.get(i);
        }
        return null;
    }

}
