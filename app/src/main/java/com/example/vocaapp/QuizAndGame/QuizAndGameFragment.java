package com.example.vocaapp.QuizAndGame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.vocaapp.R;

public class QuizAndGameFragment extends Fragment {

    ConstraintLayout dictationConstraintLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_and_game, container, false);

        dictationConstraintLayout = view.findViewById(R.id.dictationConstraintLayout);

        dictationConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizSettingBottomSheet bottomSheet = new QuizSettingBottomSheet();
                bottomSheet.show(getChildFragmentManager(), "QuizSettingTag");
            }
        });

        return view;

    }
}
