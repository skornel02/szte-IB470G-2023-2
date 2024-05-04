package hu.notetaker.tasks;

import android.os.AsyncTask;

import hu.notetaker.models.NoteModel;
import hu.notetaker.service.NotesService;

public class UpdateNoteFinishedStatusTask extends AsyncTask<Void, Void, Void> {

    private NoteModel note;
    private NotesService service;

    public UpdateNoteFinishedStatusTask(NoteModel note) {
        this.note = note;

        service = new NotesService();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        service.updateNoteFinishedState(note);
        return null;
    }
}
