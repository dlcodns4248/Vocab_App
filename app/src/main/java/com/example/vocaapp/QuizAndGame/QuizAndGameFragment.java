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
    ConstraintLayout flashcardConstraintLayout; // 추가된 플래시카드 버튼

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_and_game, container, false);

        dictationConstraintLayout = view.findViewById(R.id.dictationConstraintLayout);
        flashcardConstraintLayout = view.findViewById(R.id.flashcardConstraintLayout);

        //  받아쓰기 버튼
        dictationConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizSettingBottomSheet bottomSheet = new QuizSettingBottomSheet();
                Bundle args = new Bundle();
                args.putString("quizType", "DICTATION");
                bottomSheet.setArguments(args);             //34,35,36 추가(게임이(목적지가) 여러개이기 때문에

                bottomSheet.show(getChildFragmentManager(), "QuizSettingTag");
            }
        });

        //  플래시카드(O/X) 버튼
        flashcardConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizSettingBottomSheet bottomSheet = new QuizSettingBottomSheet();
                Bundle args = new Bundle();
                args.putString("quizType", "FLASHCARD");
                bottomSheet.setArguments(args);
                bottomSheet.show(getChildFragmentManager(), "QuizSettingTag");
            }
        });

        return view;

    }
}
