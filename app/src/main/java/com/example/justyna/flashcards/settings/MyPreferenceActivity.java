package com.example.justyna.flashcards.settings;

import android.preference.PreferenceActivity;

import com.example.justyna.flashcards.R;
import com.example.justyna.flashcards.settings.MyPreferenceFragment;

import java.util.List;

public class MyPreferenceActivity extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return MyPreferenceFragment.class.getName().equals(fragmentName);
    }
}
