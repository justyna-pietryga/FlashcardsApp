package com.example.justyna.flashcards.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.justyna.flashcards.model.DictionaryResult;
import com.example.justyna.flashcards.model.Language;
import com.example.justyna.flashcards.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DictionaryActivity extends AppCompatActivity {

    String sourceLanCode, destLanCode = "";
    List<DictionaryResult> results;
    ListView resultsListView;
    ArrayAdapter<DictionaryResult> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadContent();
    }

    private void loadContent(){
        final Spinner sourceLangSpinner = (Spinner) findViewById(R.id.sourceSpinner);
        final Spinner destLangSpinner = (Spinner) findViewById(R.id.destSpinner);
        final EditText wordToTranslate = (EditText) findViewById(R.id.wordToTranslateTextView);
        final Button translateButton = (Button) findViewById(R.id.translateButton);
        resultsListView = (ListView) findViewById(R.id.dictionaryListView);

        sourceLangSpinner.setPrompt("Choose the source language");
        destLangSpinner.setPrompt("Choose the destination language");

        List<Language> languages = generateLanguages();
        setUpSpinners(sourceLangSpinner, destLangSpinner, languages);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wordToTranslate.equals("")) {
                    Toast.makeText(DictionaryActivity.this, "No word to translate", Toast.LENGTH_LONG).show();
                }
                else{
                    new CallbackTask().execute(translations(wordToTranslate));
                }
            }
        });

    }

    private String translations(EditText wordToTranslate) {
        final String language = sourceLanCode;
        final String target_lang = destLanCode;
        final String word = wordToTranslate.getText().toString();
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id + "/translations=" + target_lang;
    }

    private void setUpSpinners(Spinner sourceLangSpinner, Spinner destLangSpinner, final List<Language> languages){

        final ArrayAdapter<Language> adapterS = new ArrayAdapter<>(this, R.layout.simple_spinner, languages);
        final ArrayAdapter<Language> adapterD = new ArrayAdapter<>(this, R.layout.simple_spinner, languages);

        sourceLangSpinner.setAdapter(adapterS);
        destLangSpinner.setAdapter(adapterD);
        adapterS.notifyDataSetChanged();
        adapterD.notifyDataSetChanged();
        sourceLangSpinner.setSelection(0);
        destLangSpinner.setSelection(1);

        sourceLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long position) {
                sourceLanCode = languages.get(i).getCode();
                Log.d("Debug", sourceLanCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        destLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long position) {
                destLanCode = languages.get(i).getCode();
                Log.d("Debug", destLanCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private List<Language> generateLanguages(){
        List<Language> languages = new ArrayList<>();
        languages.add(new Language("english", "en"));
        languages.add(new Language("espaniol", "es"));
        languages.add(new Language("germany", "de"));

        return languages;
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(DictionaryActivity.this, MainActivity.class);
        startActivity(back);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    public class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            final String app_id = "e6a7cc11";
            final String app_key = "f03a8d2b5fa35ed6d29ced9e0bd7973b";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            List<DictionaryResult> dictionaryResults = new ArrayList<>();
            try {
                JSONArray concreteResult = new JSONObject(result).getJSONArray("results").getJSONObject(0)
                        .getJSONArray("lexicalEntries").getJSONObject(0)
                        .getJSONArray("entries").getJSONObject(0)
                        .getJSONArray("senses").getJSONObject(0)
                        .getJSONArray("subsenses").getJSONObject(0).getJSONArray("examples");

                for(int i=0; i<concreteResult.length(); i++){
                    JSONObject row = concreteResult.getJSONObject(i);
                    String sourceExpression = row.getString("text");
                    String targetExpression = row.getJSONArray("translations").getJSONObject(0).getString("text");
                    dictionaryResults.add(new DictionaryResult(sourceExpression, targetExpression));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                dictionaryResults.add(new DictionaryResult("No expressions with this word in dictionary", "Try another one"));
                arrayAdapter = new ArrayAdapter<>(DictionaryActivity.this, R.layout.dictionary_result_row, dictionaryResults);
                resultsListView.setAdapter(arrayAdapter);
            }

            arrayAdapter = new ArrayAdapter<>(DictionaryActivity.this, R.layout.dictionary_result_row, dictionaryResults);
            resultsListView.setAdapter(arrayAdapter);

        }
    }
}
