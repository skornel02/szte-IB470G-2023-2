package hu.notetaker.ui.notes;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import hu.notetaker.NotificationReceiver;
import hu.notetaker.R;
import hu.notetaker.databinding.FragmentNotesBinding;
import hu.notetaker.databinding.ItemNoteBinding;
import hu.notetaker.models.NoteModel;
import hu.notetaker.service.NotificationService;
import hu.notetaker.tasks.DeleteNoteTask;
import hu.notetaker.tasks.LoadNotesTask;
import hu.notetaker.tasks.UpdateNoteFinishedStatusTask;

public class NotesFragment extends Fragment {
    private NotesViewModel vm;
    private FragmentNotesBinding binding;

    private Animation animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);

        this.vm = new ViewModelProvider(this).get(NotesViewModel.class);
        var root = (View) binding.getRoot();

        this.animation = AnimationUtils.loadAnimation(getContext(), R.anim.extranimation);


        var adapter = new NoteAdapter(new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull NoteModel oldItem, @NonNull NoteModel newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull NoteModel oldItem, @NonNull NoteModel newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getContent().equals(newItem.getContent()) &&
                        oldItem.isFinished() == newItem.isFinished();
            }
        });
        binding.recyclerviewTransform.setAdapter(adapter);

        vm.getNotes().observe(getViewLifecycleOwner(), adapter::submitList);

        loadNotes();

        return root;
    }

    private void loadNotes() {
        var mAuth = FirebaseAuth.getInstance();
        var task = new LoadNotesTask(mAuth.getCurrentUser().getEmail(), notes -> {
            vm.setNotes(notes);
        });
        task.execute();
    }

    private void handleUpdateFinished(NoteModel note) {
        var request = new UpdateNoteFinishedStatusTask(note);
        request.execute();
    }

    private void handleDeleteFinished(NoteModel note) {
        var request = new DeleteNoteTask(note);
        request.execute();

        Toast.makeText(getContext(), "Note deleted " + note.getTitle(), Toast.LENGTH_SHORT).show();

        var notes = vm.getNotes().getValue();
        if (notes == null) {
            return;
        }
        notes = new ArrayList<>(notes);

        notes.removeIf(n -> n.equals(note));
        vm.setNotes(notes);
    }

    class NoteAdapter extends ListAdapter<NoteModel, NoteViewHolder> {

        protected NoteAdapter(@NonNull DiffUtil.ItemCallback<NoteModel> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            var binding = ItemNoteBinding.inflate(LayoutInflater.from(getContext()));
            return new NoteViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            holder.bind(getItem(position));
        }
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private ItemNoteBinding binding;

        public NoteViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(NoteModel note) {
            binding.textViewTitle.setText(note.getTitle());
            binding.textViewContent.setText(note.getContent());
            binding.textViewCreated.setText(note.getCreated().toDate().toLocaleString());
            binding.checkBoxFinished.setChecked(note.isFinished());

            binding.checkBoxFinished.setOnCheckedChangeListener((buttonView, isChecked) -> {
                handleUpdateFinished(note);
            });
            binding.buttonDelete.setOnClickListener(v -> {
                handleDeleteFinished(note);
            });

            binding.getRoot().startAnimation(animation);
        }
    }
}
