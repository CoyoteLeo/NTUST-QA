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

    public static final String ACTION_REPLY_QUESTION = "reply-QA";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-QA";
    public static final String ACTION_SEND_NOTIFICATION = "issue-notification";

    public static Intent getRandomReplyIntent(Context context) {    //隨機撈一個題目出來
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
        if (ACTION_SEND_NOTIFICATION.equals(action)) {
            Uri uri = QuestionContract.QuestionEntry.CONTENT_URI;   //撈問題'
            uri = uri.buildUpon().build();
            String[] projection = {
                    "*"
            };
            String[] selections = {};
            Cursor cursor = context.getContentResolver().query(uri, projection, "", selections, "RANDOM() LIMIT 1");
            Objects.requireNonNull(cursor).moveToFirst();
            if (cursor.getCount() == 0) {   //資料庫沒問題'
                NotificationUtils.remindAdd(context);   //提醒user去興曾問題
            } else {
                NotificationUtils.remindQA(context);    //提醒user回答問題
            }
            cursor.close();

        } else if (ACTION_REPLY_QUESTION.equals(action)) {  //user選擇回答問題，隨機選ㄍ題目給他玩
            context.startActivity(getRandomReplyIntent(context));
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {    //user不回答問題，清空所有的notificaiton
            NotificationUtils.clearAllNotifications(context);
        }

    }
}