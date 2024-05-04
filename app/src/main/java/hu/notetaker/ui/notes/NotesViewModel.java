package hu.notetaker.ui.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import hu.notetaker.models.NoteModel;

public class NotesViewModel extends ViewModel {

    private final MutableLiveData<List<NoteModel>> _notes = new MutableLiveData<>(new ArrayList<>());

    public void setNotes(List<NoteModel> notes) {
        _notes.setValue(notes);
    }

    public LiveData<List<NoteModel>> getNotes() {
        return _notes;
    }
}
