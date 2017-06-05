package com.example.justyna.flashcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import com.example.justyna.flashcards.databasemanager.CategoryDAO;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "debugger";
    public static final String SHARED_PREFERENCES="FlashcardsPreferences";

    private static int whichRadioButtonMain=R.id.selectCategory_radioButton;
    private static Category categoryFromSpinner;
    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        categoryDAO= new CategoryDAO(this);

        loadContent();
    }

    private void loadContent(){
        RadioGroup mainRadiogroup = (RadioGroup) findViewById(R.id.main_radioGroup);
        final EditText createcategoryEditText = (EditText) findViewById(R.id.createCategory_editText);
        final Spinner categoriesSpinner = (Spinner) findViewById(R.id.categorySelecting_spinner);
        final Button deleteButton = (Button)findViewById(R.id.delete_button);
        final Button editButton = (Button) findViewById(R.id.editCategory_button);
        categoriesSpinner.setPrompt(getString(R.string.selecting_category_prompt));

        List<Category> c = categoryDAO.getAllFolders();
        if(c.size()==0){
            deleteButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.INVISIBLE);
        }
        else{
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
        }

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
                        List<Category> c = categoryDAO.getAllFolders();
                        if(c.size()==0){
                            deleteButton.setVisibility(View.INVISIBLE);
                            editButton.setVisibility(View.INVISIBLE);
                        }
                        else{
                            deleteButton.setVisibility(View.VISIBLE);
                            editButton.setVisibility(View.VISIBLE);
                        }
                        createcategoryEditText.setVisibility(View.INVISIBLE);
                        whichRadioButtonMain=i;
                        categoriesSpinner.setEnabled(true);

                        break;
                }
            }
        });



        // ArrayAdapter

        final List<Category> categories = categoryDAO.getAllFolders();

        final ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, R.layout.simple_spinner, categories);
        categoriesSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        //spinner
        SharedPreferences shared=getSharedPreferences(SHARED_PREFERENCES,0); ////////////////////
        categoriesSpinner.setSelection(shared.getInt("WhichIdFolder", 0)-1);//////////////////////
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long position) {
                List<Category> categoryList= categoryDAO.getAllFolders();
                int id=categoryList.get(i).getId();
                categoryFromSpinner=categoryDAO.getFolder(id);
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
                        List<Category> categories = categoryDAO.getAllFolders();

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

                            Category newCreatedCategory = new Category(newCategoryName);

                            categoryDAO.addCateogry(newCreatedCategory);

                            Log.d(TAG, "adding to database");

                            List<Category> categoriesAfterNewCreate = categoryDAO.getAllFolders();
                            int id= categoriesAfterNewCreate.get(categoriesAfterNewCreate.size()-1).getId();

                            Intent intent = new Intent(MainActivity.this, VocabularyListActivity.class);
                            Intent refresh = new Intent(MainActivity.this, MainActivity.class);

                            SharedPreferences.Editor sharedEditor = shared.edit();
                            sharedEditor.putInt("WhichIdFolder", id).apply();

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

                        Category oldCategory=categoryDAO.getFolder(categoryFromSpinner.getId());

                        categoryDAO.editFolder(oldCategory, createcategoryEditText.getText().toString());

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
                                categoryDAO.deleteFolder(categoryFromSpinner);
                                Log.d(TAG,categoryFromSpinner.getName()+" deleted");

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
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                final EditText edittext = new EditText(MainActivity.this);
                edittext.setText(categoryFromSpinner.getName());
                alert.setMessage("Podaj nową nazwę folderu");
                alert.setTitle("Edycja nazwy folderu");
                alert.setView(edittext);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String newName = edittext.getText().toString();

                        Category oldCategory=categoryDAO.getFolder(categoryFromSpinner.getId());
                        categoryDAO.editFolder(oldCategory, newName);

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
}
