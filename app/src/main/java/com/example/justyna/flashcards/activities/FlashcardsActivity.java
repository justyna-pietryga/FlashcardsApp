package com.example.justyna.flashcards.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.example.justyna.flashcards.R;
import com.example.justyna.flashcards.model.Vocabulary;
import com.example.justyna.flashcards.databasemanager.VocabularyDAO;

import static com.example.justyna.flashcards.activities.FlashcardsActivity.FlashcardSide.BACK;
import static com.example.justyna.flashcards.activities.FlashcardsActivity.FlashcardSide.FRONT;

public class FlashcardsActivity extends AppCompatActivity {
    private static int flashcardIterator = 0;
    private static FlashcardSide flashcardSide = FRONT;
    public static final String TAG = "debuggingVocabulary";
    private static String language;
    private VocabularyDAO vocabularyDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vocabularyDAO = new VocabularyDAO(this);
        loadContent();
    }

    private void loadContent() {

        final TextView flashcardWord = (TextView) findViewById(R.id.vocabulary_flashcard_textView);
        Button invertWordButton = (Button) findViewById(R.id.invertWord_button);
        Button nextWordButton = (Button) findViewById(R.id.nextWord_button);

        final SharedPreferences shared = getSharedPreferences("FlashcardsPreferences", 0);
        int categoryIdFromMain = shared.getInt("WhichIdFolder", 1);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FlashcardsActivity.this);
        final String settingsDisplayType = sharedPref.getString("displayType", "");
        String settingsOrderType = sharedPref.getString("orderType", "");

        final List<Vocabulary> vocabularyFromData = vocabularyDAO.getAllVocabularyByCategory(categoryIdFromMain);
        final Vocabulary[] wordsReloadedInRandom = reloadedTableInRandom(vocabularyFromData);

        setSettingTextOfAnOptionForFirstFlashcard(settingsDisplayType, settingsOrderType, flashcardWord, wordsReloadedInRandom, vocabularyFromData);

        nextWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWordButtonOnClick(wordsReloadedInRandom, vocabularyFromData, flashcardWord);
            }
        });

        invertWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invertWordButtonOnClick(wordsReloadedInRandom, vocabularyFromData, flashcardWord);
            }
        });

    }

    private void invertWordButtonOnClick(Vocabulary[] wordsReloadedInRandom, List<Vocabulary> vocabularyFromData, TextView flashcardWord) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FlashcardsActivity.this);
        String settingsDisplayType = sharedPref.getString("displayType", "");

        List<Vocabulary> vocabularies = new ArrayList<>();

        if (settingsDisplayType.equals(getResources().getString(R.string.random_option))) {
            vocabularies.addAll(Arrays.asList(wordsReloadedInRandom));
        } else {
            vocabularies.addAll(vocabularyFromData);
        }

        setLanguageFirsOptInVariables();
        flashcardSide = flashcardSide.changeSide();

        if (flashcardIterator >= vocabularies.size()) flashcardIterator = vocabularies.size() - 1;

        setTextInTextViewAfterInvert(flashcardWord, vocabularies.get(flashcardIterator));
    }

    private void nextWordButtonOnClick(Vocabulary[] wordsReloadedInRandom, List<Vocabulary> vocabularyFromData, TextView flashcardWord) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FlashcardsActivity.this);
        String settingsDisplayType = sharedPref.getString("displayType", "");

        List<Vocabulary> vocabularies = new ArrayList<>();
        int size = wordsReloadedInRandom.length;

        for (int i = 0; i < size; i++) {
            if (settingsDisplayType.equals(getResources().getString(R.string.random_option)))
                vocabularies.add(wordsReloadedInRandom[i]);
            else vocabularies.add(vocabularyFromData.get(i));
        }

        setLanguageFirsOptInVariables();

        flashcardIterator++;
        flashcardSide = FRONT;

        if (flashcardIterator < vocabularies.size()) {
            if (language.equals("first"))
                flashcardWord.setText(vocabularies.get(flashcardIterator).getFirstLanguageWord());
            else flashcardWord.setText(vocabularies.get(flashcardIterator).getSecondLanguageWord());
        } else
            Toast.makeText(FlashcardsActivity.this, "Finish", Toast.LENGTH_LONG).show();
    }

    private void setLanguageFirsOptInVariables() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FlashcardsActivity.this);
        String settingsOrderType = sharedPref.getString("orderType", "");
        if (settingsOrderType.equals(getResources().getString(R.string.first_language_option)))
            language = "first";
        else language = "second";
    }

    private void setSettingTextOfAnOptionForFirstFlashcard(String settingsDisplayType, String settingsOrderType,
                                                           TextView flashcardWord, Vocabulary[] wordsReloadedInRandom,
                                                           List<Vocabulary> vocabularyFromData) {
        //which language first
        if (settingsOrderType.equals(getResources().getString(R.string.first_language_option)))
            language = "first";
        else language = "second";

        //first flashcard
        if (language.equals("first")) {
            setTextInTextView(flashcardWord, settingsDisplayType, wordsReloadedInRandom[flashcardIterator].getFirstLanguageWord(), vocabularyFromData.get(flashcardIterator).getFirstLanguageWord());
        }
        if (language.equals("second")) {
            setTextInTextView(flashcardWord, settingsDisplayType, wordsReloadedInRandom[flashcardIterator].getSecondLanguageWord(), vocabularyFromData.get(flashcardIterator).getSecondLanguageWord());
        }
    }

    private void setTextInTextViewAfterInvert(TextView flashcardWord, Vocabulary vocabulary) {

        String frontWord, backWord;

        frontWord = language.equals("first") ? vocabulary.getSecondLanguageWord() : vocabulary.getFirstLanguageWord();
        backWord = language.equals("second") ? vocabulary.getSecondLanguageWord() : vocabulary.getFirstLanguageWord();

        if (flashcardSide == BACK)
            flashcardWord.setText(frontWord);
        else
            flashcardWord.setText(backWord);
    }

    private void setTextInTextView(TextView flashcardWord, String settingsDisplayType,
                                   String randomOptText, String sequenceOptText) {
        if (settingsDisplayType.equals(getResources().getString(R.string.random_option)))
            flashcardWord.setText(randomOptText);
        else flashcardWord.setText(sequenceOptText);
    }

    public Vocabulary[] reloadedTableInRandom(List<Vocabulary> list) {
        List<Vocabulary> wordsList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            wordsList.add(list.get(i));
        }

        int size = wordsList.size();
        Random generator = new Random();
        Vocabulary[] table = new Vocabulary[size];
        int range = size;

        for (int i = 0; i < size; i++) {
            int random = generator.nextInt(range);
            table[i] = wordsList.get(random);

            Vocabulary temp = wordsList.get(random);
            wordsList.set(random, wordsList.get(range - 1));
            wordsList.set(range - 1, temp);

            range--;
        }

        return table;
    }

    private void resetVariables() {
        flashcardSide = FRONT;
        flashcardIterator = 0;
    }

    @Override
    public boolean onSupportNavigateUp() {
        resetVariables();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        resetVariables();
        Intent back = new Intent(FlashcardsActivity.this, VocabularyListActivity.class);
        startActivity(back);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences: {
                Intent intent = new Intent();
                intent.setClassName(this, "com.example.justyna.flashcards.settings.SettingsActivity");
                startActivity(intent);
                return true;
            }

            case R.id.refreshFlashcards: {
                Intent refresh = new Intent(FlashcardsActivity.this, FlashcardsActivity.class);
                resetVariables();
                startActivity(refresh);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    enum FlashcardSide {
        FRONT, BACK;

        FlashcardSide changeSide() {
            return this == FRONT ? BACK : FRONT;
        }
    }
}


