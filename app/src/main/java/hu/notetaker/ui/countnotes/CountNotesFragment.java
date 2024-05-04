package hu.notetaker.ui.countnotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import hu.notetaker.R;
import hu.notetaker.databinding.FragmentCountnotesBinding;
import hu.notetaker.tasks.TotalNotesTask;

public class CountNotesFragment extends Fragment {
    private FragmentCountnotesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCountnotesBinding.inflate(inflater, container, false);

        var root = (View) binding.getRoot();

        var fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        var request = new TotalNotesTask(total -> {
            binding.textCountontesValue.setText(String.format(Locale.getDefault(), "%d", total));
            binding.textCountontesValue.startAnimation(fadeInAnimation);
        });
        request.execute();

        return root;
    }
}
