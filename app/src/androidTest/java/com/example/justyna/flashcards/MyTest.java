package com.example.justyna.flashcards;

import junit.framework.Assert;

import databasemanager.Flash2DatabaseOpenHelper;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;

import com.robotium.solo.Solo;
import com.example.justyna.flashcards.MainActivity;

import java.util.List;

import databasemanager.FlashDatabaseOpenHelper;

/**
 * Created by Justyna on 2016-12-08.
 */

public class MyTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    public MyTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    public void testName() throws Exception {
        solo.unlockScreen();
        Spinner categoryList = (Spinner) solo.getView(R.id.categorySelecting_spinner);
        solo.pressSpinnerItem(0,1);
        //FlashDatabaseOpenHelper db = new FlashDatabaseOpenHelper(MainActivity.this);

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo=new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }
}
