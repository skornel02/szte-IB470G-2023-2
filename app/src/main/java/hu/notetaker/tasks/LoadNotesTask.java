package hu.notetaker.tasks;

import android.os.AsyncTask;

import java.util.List;
import java.util.function.Consumer;

import hu.notetaker.models.NoteModel;
import hu.notetaker.service.NotesService;

public class LoadNotesTask extends AsyncTask<Void, Void, List<NoteModel>> {
    private final String user;
    private final Consumer<List<NoteModel>> callback;

    private final NotesService service;

    public LoadNotesTask(String user, Consumer<List<NoteModel>> callback) {
        this.user = user;
        this.callback = callback;

        service = new NotesService();
    }


    @Override
    protected List<NoteModel> doInBackground(Void... voids) {
        var task = service.getNotes(user);

        while (!task.isComplete()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task.getResult();
    }

    @Override
    protected void onPostExecute(List<NoteModel> notes) {
        super.onPostExecute(notes);

        callback.accept(notes);
    }
}
