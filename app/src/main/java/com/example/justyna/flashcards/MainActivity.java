package com.example.justyna.flashcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import databasemanager.Flash2DatabaseOpenHelper;
import databasemanager.FlashDatabaseOpenHelper;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "debugger";
    public static final String SHARED_PREFERENCES="FlashcardsPreferences";

    private static int whichRadioButtonMain=R.id.selectCategory_radioButton;
    private static Category categoryFromSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadContent();
    }

    private void loadContent(){
        RadioGroup mainRadiogroup = (RadioGroup) findViewById(R.id.main_radioGroup);
        final EditText createcategoryEditText = (EditText) findViewById(R.id.createCategory_editText);
        final Spinner categoriesSpinner = (Spinner) findViewById(R.id.categorySelecting_spinner);
        final Button deleteButton = (Button)findViewById(R.id.delete_button);
        final Button editButton = (Button) findViewById(R.id.editCategory_button);
        categoriesSpinner.setPrompt(getString(R.string.selecting_category_prompt));

        //RadioGroup
       mainRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) { // i=id
                switch(i){
                    case R.id.createCategory_radioButton:
                        createcategoryEditText.setVisibility(View.VISIBLE);
                        whichRadioButtonMain=i;
                        createcategoryEditText.setHint(R.string.create_new_category_hint);
                        deleteButton.setVisibility(View.INVISIBLE);
                        editButton.setVisibility(View.INVISIBLE);
                        categoriesSpinner.setEnabled(false);

                        break;

                    case R.id.selectCategory_radioButton:
                        deleteButton.setVisibility(View.VISIBLE);
                        editButton.setVisibility(View.VISIBLE);
                        createcategoryEditText.setVisibility(View.INVISIBLE);
                        whichRadioButtonMain=i;
                        categoriesSpinner.setEnabled(true);

                        break;
                }
            }
        });

        final FlashDatabaseOpenHelper db = new FlashDatabaseOpenHelper(MainActivity.this);
               // db.deleteAllFolders(); //

        Button tmpTestButton = (Button) findViewById(R.id.tmpTest_button);
        tmpTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteAllFolders();
                Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                startActivity(refresh);
                finish();
            }
        });

        // ArrayAdapter
        final List<Category> categories = db.getAllFolders();
//        Log.d(TAG, String.valueOf(categories.get(0).getId()));

                //for(int i=0; i<categories.size(); i++) Log.d(TAG, "categories: "+categories.get(i).getName());
        final ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, R.layout.simple_spinner, categories);
        categoriesSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();



       /* final List<Category> listReloaded = new ArrayList<>();
        for(int i=0; i<categories.size(); i++){
            listReloaded.add(new Category(categories.get(i).getId(),categories.get(i).getName(), i));
            Log.d(TAG, "listReloaded: "+ categories.get(i).getName());
        }  */

        //spinner
        SharedPreferences shared=getSharedPreferences(SHARED_PREFERENCES,0); ////////////////////
        categoriesSpinner.setSelection(shared.getInt("WhichIdFolder", 0)-1);//////////////////////
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long position) {
                categoryFromSpinner =db.getFolder(i+1);
                //categoryFromSpinner = checkTheCategoryOnThePosition(listReloaded, i);
                Log.d(TAG,"getFolder");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //next to flashcards lists
        Button startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences shared=getSharedPreferences(SHARED_PREFERENCES,0);

                //create category
                    if(whichRadioButtonMain==R.id.createCategory_radioButton) {
                        whichRadioButtonMain=R.id.selectCategory_radioButton;
                        String newCategoryName = createcategoryEditText.getText().toString();
                        List<Category> categories = db.getAllFolders();
                        Log.d(TAG, newCategoryName+"displayed");
                        if(newCategoryName.equals("")){
                            Toast.makeText(MainActivity.this, R.string.toast_null_category, Toast.LENGTH_LONG).show();
                            whichRadioButtonMain=R.id.createCategory_radioButton;
                        }

                        else if(isCategoryExisted(categories,newCategoryName)){
                            Toast.makeText(MainActivity.this, R.string.toast_name_exists, Toast.LENGTH_LONG).show();
                            whichRadioButtonMain=R.id.createCategory_radioButton;
                        }

                        else {
                            FlashDatabaseOpenHelper flashDatabaseOpenHelper = new FlashDatabaseOpenHelper(MainActivity.this);
                            //Category newCreatedCategory = new Category(newCategoryName,categories.size());   //
                            Category newCreatedCategory = new Category(newCategoryName);
                            flashDatabaseOpenHelper.addFolder(newCreatedCategory);
                            Log.d(TAG, "adding to database");

                            int sizeOfList = flashDatabaseOpenHelper.getAllFolders().size();

                            Intent intent = new Intent(MainActivity.this, VocabularyListActivity.class);
                            Intent refresh = new Intent(MainActivity.this, MainActivity.class);

                                //categories=db.getAllFolders(); //
                                //Category lastCategory = checkTheCategoryOnThePosition(categories, categories.size()-1); //

                            SharedPreferences.Editor sharedEditor = shared.edit();
                            sharedEditor.putInt("WhichIdFolder", sizeOfList).apply();
                            //sharedEditor.putInt("WhichIdFolder", lastCategory.getId()).apply(); //
                            sharedEditor.commit();

                            startActivity(refresh);
                            startActivity(intent);
                            finish();
                        }
                    }


                 //select category
                else if (whichRadioButtonMain==R.id.selectCategory_radioButton){
                             Log.d(TAG, categoryFromSpinner.toString());
                             Log.d(TAG, String.valueOf(categoryFromSpinner.getId()));
                        Intent intent=new Intent(MainActivity.this,VocabularyListActivity.class);


                        SharedPreferences.Editor sharedEditor = shared.edit();
                        sharedEditor.putInt("WhichIdFolder", categoryFromSpinner.getId()).apply();
                        sharedEditor.commit();

                        Log.d(TAG, "wyswietlam slowa");
                        startActivity(intent);
                    }
                //edit category
                else if(whichRadioButtonMain==R.id.editCategory_button){
                        whichRadioButtonMain=R.id.selectCategory_radioButton;

                        Flash2DatabaseOpenHelper db2 = new Flash2DatabaseOpenHelper(MainActivity.this);
                        List<Vocabulary> vocabularyList = new ArrayList<Vocabulary>();
                        vocabularyList=db2.getAllVocabulary(categoryFromSpinner.getName());

                        db2.deleteWordsFromCategory(categoryFromSpinner.getName());
                        for(int j=0; j<vocabularyList.size(); j++) {
                            db2.addWord(new Vocabulary(createcategoryEditText.getText().toString(),
                                    vocabularyList.get(j).getFirstLanguageWord(),
                                    vocabularyList.get(j).getSecondLanguageWord()));
                        }

                        Category oldCategory=db.getFolder(categoryFromSpinner.getId());
                       // db.deleteFolder(oldCategory);                                                 //
                      //  db.addFolder(new Category(createcategoryEditText.getText().toString()));      //

                        db.editFolder(oldCategory, createcategoryEditText.getText().toString());

                        createcategoryEditText.setHint(R.string.create_new_category_hint);
                        createcategoryEditText.setVisibility(View.INVISIBLE);

                        Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(refresh);
                        finish();

                    }
            }
        });

        //delete folder
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.delete_folder_alert_title)
                        .setMessage(getString(R.string.delete_folder_alert_message_part1)+categoryFromSpinner.getName()+
                                    getString(R.string.delete_folder_alert_message_part2))
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.deleteFolder(categoryFromSpinner);
                                Log.d(TAG,categoryFromSpinner.getName()+" deleted");
                                Flash2DatabaseOpenHelper db2 = new Flash2DatabaseOpenHelper(MainActivity.this);
                                db2.deleteWordsFromCategory(categoryFromSpinner.getName());
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        });

        //edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (editButton.getText()==getString(R.string.edit_category_button)) {
                    whichRadioButtonMain = R.id.editCategory_button;

                    createcategoryEditText.setVisibility(View.VISIBLE);
                    createcategoryEditText.setHint("Podaj nową nazwę kategorii");
                    editButton.setText(R.string.hide_adding_button);

                }

                else if(editButton.getText()==getString(R.string.hide_adding_button)){
                    whichRadioButtonMain=R.id.selectCategory_radioButton;
                    createcategoryEditText.setVisibility(View.INVISIBLE);
                    createcategoryEditText.setHint(R.string.create_new_category_hint);
                    editButton.setText(R.string.edit_category_button);
                }
                */
                /////////////////////////////////////////////////////


                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                final EditText edittext = new EditText(MainActivity.this);
                edittext.setText(categoryFromSpinner.getName());
                alert.setMessage("Podaj nową nazwę folderu");
                alert.setTitle("Edycja nazwy folderu");
                alert.setView(edittext);
                final Flash2DatabaseOpenHelper db2 = new Flash2DatabaseOpenHelper(MainActivity.this);
                final List<Vocabulary> vocabularyList = db2.getAllVocabulary(categoryFromSpinner.getName());

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String newName = edittext.getText().toString();

                        db2.deleteWordsFromCategory(categoryFromSpinner.getName());
                        for(int j = 0; j< vocabularyList.size(); j++) {
                            db2.addWord(new Vocabulary(newName,
                                    vocabularyList.get(j).getFirstLanguageWord(),
                                    vocabularyList.get(j).getSecondLanguageWord()));
                        }

                        Category oldCategory=db.getFolder(categoryFromSpinner.getId());
                        db.editFolder(oldCategory, newName);

                        SharedPreferences shared = getSharedPreferences(SHARED_PREFERENCES,0);
                        SharedPreferences.Editor sharedEditor = shared.edit();
                        sharedEditor.putInt("WhichIdFolder", categoryFromSpinner.getId()).apply();

                        Intent refresh = new Intent(MainActivity.this,MainActivity.class);
                        startActivity(refresh);
                        finish();



                    }
                });


                alert.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }

        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public boolean isCategoryExisted(List<Category> categories, String name){
        for(int i=0; i<categories.size(); i++){
            if(categories.get(i).getName().equals(name)) return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about_app:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setMessage("MOS - Apps\nJustyna Pietryga, 2016\nWersja 1.0");
                alert.setTitle("Flashcards");
                alert.setIcon(R.mipmap.ic_launcher);
                alert.setPositiveButton("OK", null);
                alert.show();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
       // SharedPreferences shared = getSharedPreferences(SHARED_PREFERENCES,0);
       // SharedPreferences.Editor sharedEditor = shared.edit();
        //sharedEditor.putInt("WhichIdFolder", 1).apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // Log.d(TAG, "onDestroy");
      //  SharedPreferences shared = getSharedPreferences(SHARED_PREFERENCES,0);
      //  SharedPreferences.Editor sharedEditor = shared.edit();
       // sharedEditor.putInt("WhichIdFolder", 1).apply();
    }*/

    /* public Category checkTheCategoryOnThePosition(List<Category> list, int position){
        for(int i=0; i<list.size(); i++){
            if(list.get(i).getIdPosition()==position) return list.get(i);
        }
        return null;
    } */
}
