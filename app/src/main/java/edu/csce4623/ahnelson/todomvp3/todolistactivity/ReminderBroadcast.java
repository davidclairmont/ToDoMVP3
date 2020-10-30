package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.csce4623.ahnelson.todomvp3.R;

// This class is used to build notifications.
public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        int id = intent.getIntExtra("id", -1);

        // create notification builder in current channel with task details
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyUser")
                .setSmallIcon(R.drawable.small_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setChannelId("notifyUser");

        Intent resultIntent = new Intent(context,ToDoListActivity.class);
        // create TaskStackBuilder to have multiple pending notifications
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ToDoListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "notifyUser";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "ReminderBroadcast",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        notificationManager.notify(id, builder.build());

    }
}
