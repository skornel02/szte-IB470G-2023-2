package hu.notetaker.service;

import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import hu.notetaker.MainActivity;
import hu.notetaker.R;
import hu.notetaker.models.NoteModel;

public class NotificationService {

    public static final String CHANNEL_ID = "note_taker_channel";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Note Taker";
            String description = "Note Taker notifications for notes.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static Notification createNotification(Context context, NoteModel note) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(note.getTitle())
                .setContentText(note.getContent())
                .setSmallIcon(R.drawable.avatar_1)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

}
