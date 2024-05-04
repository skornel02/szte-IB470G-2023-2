package hu.notetaker.service;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.notetaker.models.NoteModel;

public class NotesService {
    private static final String TAG = "NotesService";

    private FirebaseFirestore db;

    public NotesService() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> addNote(NoteModel note) {
        Map<String, Object> noteProperties = new HashMap<>();
        noteProperties.put("user", note.getUser());
        noteProperties.put("title", note.getTitle());
        noteProperties.put("content", note.getContent());
        noteProperties.put("created", note.getCreated());
        noteProperties.put("finished", note.isFinished());

        if (note.getAlertAt() != null) {
            noteProperties.put("alertAt", note.getAlertAt());
        }

        return db.collection("notes")
                .add(noteProperties);
    }

    public Task<List<NoteModel>> getNotes(String user) {
        var notesTask = db.collection("notes")
                .whereEqualTo("user", user)
                .orderBy("created")
                .limit(100)
                .get();

        return notesTask.continueWith(task -> {
                    if (task.isSuccessful()) {
                        var notes = new ArrayList<NoteModel>();

                        task.getResult().forEach(document -> {
                            var note = new NoteModel();
                            note.setUser(document.getString("user"));
                            note.setTitle(document.getString("title"));
                            note.setContent(document.getString("content"));
                            note.setCreated(document.getTimestamp("created"));
                            note.setFinished(document.getBoolean("finished"));
                            note.setAlertAt(document.getTimestamp("alertAt"));

                            notes.add(note);
                        });

                        return notes;
                    } else {
                        return new ArrayList<>();
                    }
                });
    }

    public void updateNoteFinishedState(NoteModel note) {
        db.collection("notes")
                .whereEqualTo("title", note.getTitle())
                .whereEqualTo("user", note.getUser())
                .whereEqualTo("created", note.getCreated())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    queryDocumentSnapshots.forEach(documentSnapshot -> {
                        documentSnapshot.getReference()
                                .update("finished", !note.isFinished())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Note finished state updated " + note.getTitle());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error updating note finished state", e);
                                });
                    });
                    note.setFinished(!note.isFinished());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating note finished state", e);
                });
    }

    public void deleteNote(NoteModel note) {
        db.collection("notes")
                .whereEqualTo("title", note.getTitle())
                .whereEqualTo("user", note.getUser())
                .whereEqualTo("created", note.getCreated())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    queryDocumentSnapshots.forEach(documentSnapshot -> {
                        documentSnapshot.getReference()
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Note deleted " + note.getTitle());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error deleting note", e);
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting note", e);
                });
    }

    public Task<Long> countNotes() {
        var notesTask = db.collection("notes")
                .count()
                .get(AggregateSource.SERVER);

        return notesTask.continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().getCount();
            } else {
                return 0L;
            }
        });
    }
}
