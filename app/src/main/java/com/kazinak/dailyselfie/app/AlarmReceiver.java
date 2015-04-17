package com.kazinak.dailyselfie.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private PendingIntent pendingIntent;
    private Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isVibrate = intent.getBooleanExtra("vibrate", false);
        String ringtonePath = intent.getStringExtra("ringtone");

        intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        notification = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setContentTitle("Daily Selfie")
                .setContentText("Время для нового селфи")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

        if (isVibrate) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }

        if (ringtonePath != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(context, Uri.parse(ringtonePath));
            ringtone.play();
        }
    }
}
