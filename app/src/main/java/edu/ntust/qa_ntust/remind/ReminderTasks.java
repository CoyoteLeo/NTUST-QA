package edu.ntust.qa_ntust.remind;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;


import java.util.Objects;

import edu.ntust.qa_ntust.ReplyQuestionActivity;
import edu.ntust.qa_ntust.data.QuestionContract;
import edu.ntust.qa_ntust.utils.NotificationUtils;

public class ReminderTasks {

    public static final String REPLY_QUESTION = "reply-QA";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-QA";

    public static Intent getRandomReplyIntent(Context context) {
        Intent intent = new Intent(context, ReplyQuestionActivity.class);
        Bundle bundle = new Bundle();
        Uri uri = QuestionContract.QuestionEntry.CONTENT_URI;
        uri = uri.buildUpon().build();
        String[] projection = {
                "*"
        };
        String[] selections = {};
        Cursor cursor = context.getContentResolver().query(uri, projection, "", selections, "RANDOM() LIMIT 1");
        Objects.requireNonNull(cursor).moveToFirst();
        bundle.putString("_id", cursor.getString(0));
        bundle.putString("content", cursor.getString(1));
        bundle.putString("choice_A", cursor.getString(2));
        bundle.putString("choice_B", cursor.getString(3));
        bundle.putString("choice_C", cursor.getString(4));
        bundle.putString("choice_D", cursor.getString(5));
        bundle.putString("answer", cursor.getString(6));
        bundle.putString("count", cursor.getString(7));
        bundle.putString("difficulty", cursor.getString(8));
        intent.putExtras(bundle);
        cursor.close();
        return intent;
    }

    static void executeTask(Context context, String action) {
        if (REPLY_QUESTION.equals(action))
            context.startActivity(getRandomReplyIntent(context));
        NotificationUtils.clearAllNotifications(context);
    }
}