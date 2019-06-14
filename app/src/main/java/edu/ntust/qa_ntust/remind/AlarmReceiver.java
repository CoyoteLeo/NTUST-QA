package edu.ntust.qa_ntust.remind;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {     //被作業系統alarm通知後執行
        ReminderTasks.executeTask(context, intent.getAction()); //呼叫ReminderTasks執行指定動作
    }
}