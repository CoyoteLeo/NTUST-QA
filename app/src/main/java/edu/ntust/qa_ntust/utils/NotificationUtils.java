package edu.ntust.qa_ntust.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;


import edu.ntust.qa_ntust.AddQuestionActivity;
import edu.ntust.qa_ntust.R;
import edu.ntust.qa_ntust.remind.QAReminderIntentService;
import edu.ntust.qa_ntust.remind.ReminderTasks;

/**
 * Utility class for creating hydration notifications
 */
public class NotificationUtils {
    private static final int QA_REMINDER_NOTIFICATION_ID = 1138;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int QA_REMINDER_PENDING_INTENT_ID = 3417;
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String QA_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";
    private static final int ACTION_REPLY_PENDING_INTENT_ID = 1;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 14;
    private static final int ACTION_ADD_QUESTION_PENDING_INTENT_ID = 15;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindQA(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    QA_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, QA_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_create_black_24dp)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.reminder_title))
                .setContentText(context.getString(R.string.reminder_text))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.reminder_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(replyPendingIntent(context))
                .addAction(ReplyAction(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(QA_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void remindAdd(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    QA_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, QA_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_create_black_24dp)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.reminder_title2))
                .setContentText(context.getString(R.string.reminder_text2))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.reminder_text2)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(addQuestionPendingIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(QA_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static NotificationCompat.Action ignoreReminderAction(Context context) {
        Intent intent = new Intent(context, QAReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);
        PendingIntent pending_intent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action(R.drawable.ic_cancel_black_24dp, "No, thanks.", pending_intent);
    }

    private static NotificationCompat.Action ReplyAction(Context context) {
        Intent intent = new Intent(context, QAReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_REPLY_QUESTION);
        PendingIntent pending_intent = PendingIntent.getService(
                context,
                ACTION_REPLY_PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        return new NotificationCompat.Action(R.drawable.ic_create_black_24dp, "Answer", pending_intent);
    }

    private static PendingIntent replyPendingIntent(Context context) {
        Intent intent = ReminderTasks.getRandomReplyIntent(context);
        return PendingIntent.getActivity(
                context,
                QA_REMINDER_PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent addQuestionPendingIntent(Context context) {
        Intent intent = new Intent(context, AddQuestionActivity.class);
        return PendingIntent.getActivity(
                context,
                ACTION_ADD_QUESTION_PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, R.drawable.ic_create_black_24dp);
    }
}
