package com.example.todolistapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.example.todolistapp.Model.ToDoModel;
import com.example.todolistapp.Utils.DataBaseHelper;

public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_TITLE = "notification-title";
    public static String NOTIFICATION_TEXT = "notification-text";
    public static String NOTIFICATION_SOUND_URI = "notification-sound-uri";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:wakelock");
        wl.acquire(3000);

        String title = intent.getStringExtra(NOTIFICATION_TITLE);
        String text = intent.getStringExtra(NOTIFICATION_TEXT);
        String soundUri = intent.getStringExtra(NOTIFICATION_SOUND_URI);
        int taskId = intent.getIntExtra("taskId", -1);

        if (taskId != -1) {
            DataBaseHelper db = new DataBaseHelper(context.getApplicationContext());
            ToDoModel task = db.getTaskById(taskId);
            if (task != null && task.getStatus() == 1) {
                wl.release();
                return;
            }
            db.markTaskAsDone(taskId);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "todo_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel oldChannel = notificationManager.getNotificationChannel(channelId);
            if (oldChannel != null) {
                notificationManager.deleteNotificationChannel(channelId);
            }
            NotificationChannel channel = new NotificationChannel(channelId, "ToDo Bildirim Kanalı", NotificationManager.IMPORTANCE_HIGH);
            Uri sound = soundUri != null ? Uri.parse(soundUri) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            channel.setSound(sound, null); // 🔊 Ses ayarı burada
            notificationManager.createNotificationChannel(channel);
        }

        Uri sound = soundUri != null ? Uri.parse(soundUri) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSound(sound)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        wl.release();
    }
}

