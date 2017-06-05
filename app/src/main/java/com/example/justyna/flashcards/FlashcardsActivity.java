package com.example.justyna.flashcards;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.justyna.flashcards.databasemanager.CategoryDAO;
import com.example.justyna.flashcards.databasemanager.VocabularyDAO;

public class FlashcardsActivity extends AppCompatActivity {
    private static int i=0;
    private static int j=0;
    public static final String TAG = "debuggingVocabulary";
    private static String language;
    private VocabularyDAO vocabularyDAO;
    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);
      //  Log.d(TAG, "Flashcards");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vocabularyDAO=new VocabularyDAO(this);
        categoryDAO=new CategoryDAO(this);
        loadContent();
    }

    private void loadContent(){

        final TextView flashcardWord = (TextView) findViewById(R.id.vocabulary_flashcard_textView);
        Button invertWordButton = (Button) findViewById(R.id.invertWord_button);
        Button nextWordButton = (Button) findViewById(R.id.nextWord_button);

        final SharedPreferences shared = getSharedPreferences("FlashcardsPreferences", 0);
        int categoryIdFromMain = shared.getInt("WhichIdFolder", 1);

        Category categoryFromMain = categoryDAO.getFolder(categoryIdFromMain);

        final List<Vocabulary> vocabularyFromData = vocabularyDAO.getAllVocabulary(categoryIdFromMain);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FlashcardsActivity.this);
        final String settingsDisplayType = sharedPref.getString("displayType", "");
        String settingsOrderType = sharedPref.getString("orderType", "");

        final Vocabulary[] wordsReloadedInRandom = reloadedTableInRandom(vocabularyFromData);

        //which language first
        if(settingsOrderType.equals("Najpierw pierwszy język")) language="first";
        else language="second";

        //first flashcard
        if(language.equals("first")){
            if(settingsDisplayType.equals("Losowo"))flashcardWord.setText(wordsReloadedInRandom[i].getFirstLanguageWord());
            else flashcardWord.setText(vocabularyFromData.get(i).getFirstLanguageWord());
        }
        if(language.equals("second")){
            if(settingsDisplayType.equals("Losowo"))flashcardWord.setText(wordsReloadedInRandom[i].getSecondLanguageWord());
            else flashcardWord.setText(vocabularyFromData.get(i).getSecondLanguageWord());
        }

        nextWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FlashcardsActivity.this);
                String settingsOrderType = sharedPref.getString("orderType", "");
                String settingsDisplayType = sharedPref.getString("displayType", "");
                ///////////////////////////////////////////////////

                List<Vocabulary> vocabularies = new ArrayList<>();
                int size = wordsReloadedInRandom.length;

                /*if(settingsDisplayType.equals("Losowo")){
                    for(int i=0; i<wordsReloadedInRandom.length; i++)
                         vocabularies.add(wordsReloadedInRandom[i]);
                }

                else{
                    for(int i=0; i<vocabularyFromData.size(); i++)
                        vocabularies.add(vocabularyFromData.get(i));
                } */

                for(int i=0; i<size; i++){
                    if(settingsDisplayType.equals("Losowo"))  vocabularies.add(wordsReloadedInRandom[i]);
                    else vocabularies.add(vocabularyFromData.get(i));
                }

                ///////////////////////////////////////////////////

                if(settingsOrderType.equals("Najpierw pierwszy język")) language="first";
                if(settingsOrderType.equals("Najpierw drugi język")) language="second";

                    i++;
                    j = 0;
                    if (i < vocabularies.size()) {
                        if (language.equals("first"))
                            flashcardWord.setText(vocabularies.get(i).getFirstLanguageWord());
                        if (language.equals("second"))
                            flashcardWord.setText(vocabularies.get(i).getSecondLanguageWord());
                    }
                    else
                        Toast.makeText(FlashcardsActivity.this, "Koniec", Toast.LENGTH_LONG).show();

            }
        });

        invertWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FlashcardsActivity.this);
                String settingsOrderType = sharedPref.getString("orderType", "");

                ///////////////////////////////////////////////////

                List<Vocabulary> vocabularies = new ArrayList<>();

                if(settingsDisplayType.equals("Losowo")){
                    for(int i=0; i<wordsReloadedInRandom.length; i++)
                        vocabularies.add(wordsReloadedInRandom[i]);
                }

                else {
                    for(int i=0; i<vocabularyFromData.size(); i++)
                        vocabularies.add(vocabularyFromData.get(i));
                }


                ///////////////////////////////////////////////////

                if(settingsOrderType.equals("Najpierw pierwszy język")) language="first";
                if(settingsOrderType.equals("Najpierw drugi język")) language="second";

                    j++;
                    if(i>=vocabularies.size())i=vocabularies.size()-1;
                    //if (i < vocabularies.size()) {
                        if (j % 2 != 0) {
                            if(language=="first")flashcardWord.setText(vocabularies.get(i).getSecondLanguageWord());
                            if(language=="second")flashcardWord.setText(vocabularies.get(i).getFirstLanguageWord());
                        } else if (j % 2 == 0) {
                            if(language=="first")flashcardWord.setText(vocabularies.get(i).getFirstLanguageWord());
                            if(language=="second")flashcardWord.setText(vocabularies.get(i).getSecondLanguageWord());
                        }

                    //} else
                    //    Toast.makeText(FlashcardsActivity.this, "Koniec", Toast.LENGTH_LONG).show();
            }
        });

    }

    public Vocabulary[] reloadedTableInRandom(List<Vocabulary> list){
        List<Vocabulary> wordsList = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
           wordsList.add(list.get(i));
        }

        int size= wordsList.size();
        Random generator = new Random();
        Vocabulary [] table = new Vocabulary[size];
        int range = size;

        for (int i=0; i<size; i++){
            int random=generator.nextInt(range);
            table[i]=wordsList.get(random);

            Vocabulary temp = wordsList.get(random);
            wordsList.set(random, wordsList.get(range-1));
            wordsList.set(range-1, temp);

            range--;
        }

        return table;
    }

    @Override
    public boolean onSupportNavigateUp(){
        if(j%2!=0) j--;
        j=0;
        i=0;
        Log.d(TAG, "i= "+String.valueOf(i));
        Log.d(TAG, "j= "+String.valueOf(j));
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(j%2!=0) j--;
        j=0;
        i=0;
        Log.d(TAG, "i= "+String.valueOf(i));
        Log.d(TAG, "j= "+String.valueOf(j));

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
        switch (item.getItemId()){
            case R.id.preferences:
            {
                Intent intent = new Intent();
               // intent.setClassName(this,"com.example.justyna.flashcards.MyPreferenceActivity");
                intent.setClassName(this,"com.example.justyna.flashcards.SettingsActivity");
                startActivity(intent);
                return true;
            }

            case R.id.refreshFlashcards:
            {
                Intent refresh = new Intent(FlashcardsActivity.this, FlashcardsActivity.class);
                i=0; j=0;
                startActivity(refresh);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
