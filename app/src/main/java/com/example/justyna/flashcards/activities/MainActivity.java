package com.example.justyna.flashcards.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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

import com.example.justyna.flashcards.MyReceiver;
import com.example.justyna.flashcards.model.Category;
import com.example.justyna.flashcards.R;
import com.example.justyna.flashcards.databasemanager.CategoryDAO;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "debugger";
    public static final String SHARED_PREFERENCES = "FlashcardsPreferences";

    private static int whichRadioButtonMain = R.id.selectCategory_radioButton;
    private static Category categoryFromSpinner;
    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        categoryDAO = new CategoryDAO(this);
        loadContent();
        setUpNotificationAlarm(1);
    }

    private void setUpNotificationAlarm(int minutes) {
        int minute = 1000*60;
        Intent notifyIntent = new Intent(this,MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 0 , notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
                minute * minutes, pendingIntent);
    }

    private void setVisibilityOfButton(Button deleteButton, Button editButton){
        List<Category> c = categoryDAO.getAllFolders();
        if (c.size() == 0) {
            deleteButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.INVISIBLE);
        } else {
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
        }
    }

    private void radioButtonCreateCategory(EditText createCategoryEditText, Button deleteButton,
                                           Button editButton, Spinner categoriesSpinner, int id) {
        createCategoryEditText.setVisibility(View.VISIBLE);
        whichRadioButtonMain = id;
        createCategoryEditText.setHint(R.string.create_new_category_hint);
        deleteButton.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.INVISIBLE);
        categoriesSpinner.setEnabled(false);
    }

    private void radioButtonSelectCategory(EditText createCategoryEditText, Button deleteButton,
                                           Button editButton, Spinner categoriesSpinner, int id) {
        setVisibilityOfButton(deleteButton, editButton);
        createCategoryEditText.setVisibility(View.INVISIBLE);
        whichRadioButtonMain = id;
        categoriesSpinner.setEnabled(true);
    }

    private void loadContent() {
        RadioGroup mainRadioGroup = (RadioGroup) findViewById(R.id.main_radioGroup);
        final EditText createCategoryEditText = (EditText) findViewById(R.id.createCategory_editText);
        final Spinner categoriesSpinner = (Spinner) findViewById(R.id.categorySelecting_spinner);
        final Button deleteButton = (Button) findViewById(R.id.delete_button);
        final Button editButton = (Button) findViewById(R.id.editCategory_button);
        categoriesSpinner.setPrompt(getString(R.string.selecting_category_prompt));
        setVisibilityOfButton(deleteButton, editButton);

        //RadioGroup
        setUpMainRadioGroup(mainRadioGroup, createCategoryEditText, deleteButton, editButton, categoriesSpinner);

        // ArrayAdapter and Spinner
        setUpSpinnerWithAdapter(categoriesSpinner);

        //next to vocabulary list
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startButtonOnClick(createCategoryEditText);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteButtonOnClick();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonOnClick();
            }

        });
    }

    private void deleteButtonOnClick(){
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.delete_folder_alert_title)
                .setMessage(getString(R.string.delete_folder_alert_message_part1) + categoryFromSpinner.getName() +
                        getString(R.string.delete_folder_alert_message_part2))
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        categoryDAO.deleteFolder(categoryFromSpinner);
                        Log.d(TAG, categoryFromSpinner.getName() + " deleted");

                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void startButtonOnClick(EditText createCategoryEditText){
        SharedPreferences shared = getSharedPreferences(SHARED_PREFERENCES, 0);

        if (whichRadioButtonMain == R.id.createCategory_radioButton) {
            createCategory(createCategoryEditText, shared);
        } else if (whichRadioButtonMain == R.id.selectCategory_radioButton) {
            selectCategory(shared);
        }
    }

    private void editButtonOnClick(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        final EditText edittext = new EditText(MainActivity.this);
        edittext.setText(categoryFromSpinner.getName());
        alert.setMessage(R.string.set_folder_name);
        alert.setTitle(R.string.editing_folder_name);
        alert.setView(edittext);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String newName = edittext.getText().toString();

                Category oldCategory = categoryDAO.getFolder(categoryFromSpinner.getId());
                categoryDAO.editFolder(oldCategory, newName);

                SharedPreferences shared = getSharedPreferences(SHARED_PREFERENCES, 0);
                SharedPreferences.Editor sharedEditor = shared.edit();
                sharedEditor.putInt("WhichIdFolder", categoryFromSpinner.getId()).apply();

                Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                startActivity(refresh);
                finish();
            }
        });


        alert.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void selectCategory(SharedPreferences shared){
        Intent intent = new Intent(MainActivity.this, VocabularyListActivity.class);

        SharedPreferences.Editor sharedEditor = shared.edit();
        sharedEditor.putInt("WhichIdFolder", categoryFromSpinner.getId()).apply();
        sharedEditor.commit();

        startActivity(intent);
    }

    private void createCategory(EditText createCategoryEditText, SharedPreferences shared){
        whichRadioButtonMain = R.id.selectCategory_radioButton;
        String newCategoryName = createCategoryEditText.getText().toString();
        List<Category> categories = categoryDAO.getAllFolders();

        if (newCategoryName.equals("")) {
            Toast.makeText(MainActivity.this, R.string.toast_null_category, Toast.LENGTH_LONG).show();
            whichRadioButtonMain = R.id.createCategory_radioButton;
        } else if (isCategoryExisted(categories, newCategoryName)) {
            Toast.makeText(MainActivity.this, R.string.toast_name_exists, Toast.LENGTH_LONG).show();
            whichRadioButtonMain = R.id.createCategory_radioButton;
        } else {
            Category newCreatedCategory = new Category(newCategoryName);
            categoryDAO.addCateogry(newCreatedCategory);

            List<Category> categoriesAfterNewCreate = categoryDAO.getAllFolders();
            int id = categoriesAfterNewCreate.get(categoriesAfterNewCreate.size() - 1).getId();

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

    private void setUpMainRadioGroup(RadioGroup mainRadioGroup, final EditText createCategoryEditText,
                                     final Button deleteButton, final Button editButton, final Spinner categoriesSpinner){
        mainRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) { // i=id
                switch (i) {
                    case R.id.createCategory_radioButton:
                        radioButtonCreateCategory(createCategoryEditText, deleteButton, editButton, categoriesSpinner, i);
                        break;

                    case R.id.selectCategory_radioButton:
                        radioButtonSelectCategory(createCategoryEditText, deleteButton, editButton, categoriesSpinner, i);
                        break;
                }
            }
        });
    }

    private void setUpSpinnerWithAdapter(Spinner categoriesSpinner){
        final List<Category> categories = categoryDAO.getAllFolders();

        //adapter
        final ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, R.layout.simple_spinner, categories);
        categoriesSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //spinner
        SharedPreferences shared = getSharedPreferences(SHARED_PREFERENCES, 0);
        categoriesSpinner.setSelection(shared.getInt("WhichIdFolder", 0) - 1);
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long position) {
                List<Category> categoryList = categoryDAO.getAllFolders();
                int id = categoryList.get(i).getId();
                categoryFromSpinner = categoryDAO.getFolder(id);
                Log.d(TAG, "getFolder");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public boolean isCategoryExisted(List<Category> categories, String name) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equals(name)) return true;
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
        switch (item.getItemId()) {
            case R.id.about_app: {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setMessage("MOS - Apps\nJustyna Pietryga");
                alert.setTitle("Flashcards");
                alert.setIcon(R.mipmap.ic_launcher);
                alert.setPositiveButton("OK", null);
                alert.show();
                return true;
            }

            case R.id.dictionary: {
                Intent intent = new Intent(MainActivity.this, DictionaryActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
