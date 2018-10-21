package com.example.justyna.flashcards;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import com.example.justyna.flashcards.activities.MainActivity;
import com.example.justyna.flashcards.databasemanager.VocabularyDAO;
import com.example.justyna.flashcards.model.Vocabulary;

public class RemindWordService extends IntentService {
    private static final int NOTIFICATION_ID = 3;
    private VocabularyDAO vocabularyDAO;

    public RemindWordService() {
        super("RemindWordService");
        vocabularyDAO = new VocabularyDAO(this);
    }

    private String prepareNotificationBody() {
        Vocabulary vocabulary = vocabularyDAO.getRandomVocabulary();
        return vocabulary.getFirstLanguageWord() + " - " + vocabulary.getSecondLanguageWord();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getResources().getString(R.string.word_of_day));
        builder.setContentText(prepareNotificationBody());
        builder.setSmallIcon(R.drawable.ic_note_add_white);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}
