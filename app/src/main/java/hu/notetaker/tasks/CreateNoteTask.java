package hu.notetaker.tasks;

import android.os.AsyncTask;

import java.util.function.Consumer;

import hu.notetaker.models.NoteModel;
import hu.notetaker.service.NotesService;

public class CreateNoteTask extends AsyncTask<Void, Void, Boolean> {
    private final NoteModel note;
    private final Consumer<Boolean> callback;

    private final NotesService service;

    public CreateNoteTask(NoteModel note, Consumer<Boolean> callback) {
        this.note = note;
        this.callback = callback;

        service = new NotesService();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        var task = service.addNote(note);

        while (!task.isComplete()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task.isSuccessful();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        callback.accept(success);
    }
}
