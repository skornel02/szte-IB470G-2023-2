package hu.notetaker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import hu.notetaker.models.NoteModel;
import hu.notetaker.service.NotificationService;
import hu.notetaker.tasks.CreateNoteTask;

public class CreateNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var context = this;

        setContentView(R.layout.task_creator);

        var title = (EditText) findViewById(R.id.editTextTitle);
        var content = (EditText) findViewById(R.id.editTextContent);
        var alertAt = (EditText) findViewById(R.id.editDateTiemAlertAt);
        var createButton = findViewById(R.id.buttonNoteCreate);
        var createAndContinueButton = findViewById(R.id.buttonNoteCreateContinue);

        var alertAtTimestamp = new AtomicReference<Timestamp>();

        var mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(CreateNoteActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }

        Button alertAtButton = findViewById(R.id.buttonNoteDateSet);
        alertAtButton.setOnClickListener(v -> {
            try {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, monthOfYear);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            // Now that we have the date, show the TimePickerDialog
                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                            int mMinute = c.get(Calendar.MINUTE);

                            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                                    (view1, hourOfDay, minute) -> {
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);

                                        // Now that we have the date and time, set it to the note
                                        alertAtTimestamp.set(new Timestamp(c.getTime()));
                                        alertAt.setText(alertAtTimestamp.get().toDate().toLocaleString());
                                    }, mHour, mMinute, false);
                            timePickerDialog.show();
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CreateNoteActivity", "Failed to set alert date", e);
                Toast.makeText(context, "Failed to set alert date", Toast.LENGTH_SHORT).show();
            }
        });

        createButton.setOnClickListener(v -> {
            var titleText = title.getText().toString();
            var contentText = content.getText().toString();

            if (titleText.isEmpty() || contentText.isEmpty()) {
                Snackbar.make(v, "Please fill in all fields!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(com.google.android.material.R.color.design_default_color_error))
                        .show();

                return;
            }

            var note = new NoteModel();
            note.setTitle(titleText);
            note.setContent(contentText);
            note.setUser(mAuth.getCurrentUser().getEmail());
            note.setCreated(new Timestamp(new Date()));
            note.setFinished(false);

            if (alertAtTimestamp.get() != null) {
                note.setAlertAt(alertAtTimestamp.get());
            }

            var task = new CreateNoteTask(note, result -> {
                if (result) {
                    Toast.makeText(CreateNoteActivity.this, "Note created successfully", Toast.LENGTH_SHORT)
                            .show();

                    if (alertAtTimestamp.get() != null) {
                        createNotification(this, note);
                    }

                    Intent intent = new Intent(CreateNoteActivity.this, MainActivity.class);
                    startActivity(intent);
                    this.finish();
                } else {
                    Snackbar.make(v, "Failed to create note!", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(com.google.android.material.R.color.design_default_color_error))
                            .show();
                }
            });

            task.execute();
        });

        createAndContinueButton.setOnClickListener(v -> {
            var titleText = title.getText().toString();
            var contentText = content.getText().toString();
            var alertAtText = alertAt.getText().toString();

            if (titleText.isEmpty() || contentText.isEmpty()) {
                Snackbar.make(v, "Please fill in all fields!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(com.google.android.material.R.color.design_default_color_error))
                        .show();

                return;
            }

            var note = new NoteModel();
            note.setTitle(titleText);
            note.setContent(contentText);
            note.setUser(mAuth.getCurrentUser().getEmail());
            note.setCreated(new Timestamp(new Date()));

            if (!alertAtText.isEmpty()) {
                note.setAlertAt(new Timestamp(new Date(alertAtText)));
            }

            var task = new CreateNoteTask(note, result -> {
                if (result) {
                    Toast.makeText(CreateNoteActivity.this, "Note created successfully", Toast.LENGTH_SHORT)
                            .show();

                    if (alertAtTimestamp.get() != null) {
                        createNotification(this, note);
                    }
                } else {
                    Snackbar.make(v, "Failed to create note!", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(com.google.android.material.R.color.design_default_color_error))
                            .show();
                }
            });

            task.execute();
        });
    }

    private void createNotification(Context context, NoteModel note) {
        NotificationService.createNotificationChannel(context);
        var notification = NotificationService.createNotification(context, note);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        } else {
            Intent notificationIntent = new Intent(context, NotificationReceiver.class);
            notificationIntent.putExtra("notification", notification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, note.getAlertAt().toDate().getTime(), pendingIntent);
        }
    }
}
