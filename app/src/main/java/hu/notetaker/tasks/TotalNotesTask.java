package hu.notetaker.tasks;

import android.os.AsyncTask;

import java.util.function.Consumer;

import hu.notetaker.service.NotesService;

public class TotalNotesTask extends AsyncTask<Void, Void, Long> {
    private final Consumer<Long> callback;

    private final NotesService service;

    public TotalNotesTask(Consumer<Long> callback) {
        this.callback = callback;

        service = new NotesService();
    }

    @Override
    protected Long doInBackground(Void... voids) {
        var total = service.countNotes();

        while (!total.isComplete()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return total.getResult();
    }

    @Override
    protected void onPostExecute(Long total) {
        super.onPostExecute(total);

        callback.accept(total);
    }
}
