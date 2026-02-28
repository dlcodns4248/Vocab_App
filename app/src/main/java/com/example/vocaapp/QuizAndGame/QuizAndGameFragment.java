package com.example.vocaapp.QuizAndGame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.vocaapp.R;

public class QuizAndGameFragment extends Fragment {
    ConstraintLayout dictationConstraintLayout;
    ConstraintLayout flashcardConstraintLayout;
    ConstraintLayout multiplechoiceConstraintLayout;

    // 1. 모드 선택 레이아웃
    LinearLayout modeSelectionLayout;
    LinearLayout quizTypeSelectionLayout;
    Button btnOfficialMode;
    Button btnFreeMode;

    // 학습 모드인지 아닌지 기억하는 이름표
    boolean isOfficialMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_and_game, container, false);

        dictationConstraintLayout = view.findViewById(R.id.dictationConstraintLayout);
        flashcardConstraintLayout = view.findViewById(R.id.flashcardConstraintLayout);
        multiplechoiceConstraintLayout = view.findViewById(R.id.multiplechoiceConstraintLayout);

        // ★★★ [추가 2] 새 뷰들 연결 및 처음 화면 셋팅 ★★★
        modeSelectionLayout = view.findViewById(R.id.modeSelectionLayout);
        quizTypeSelectionLayout = view.findViewById(R.id.quizTypeSelectionLayout);
        btnOfficialMode = view.findViewById(R.id.btnOfficialMode);
        btnFreeMode = view.findViewById(R.id.btnFreeMode);

        // 처음 화면에 들어오면 모드 선택 창만 보이고, 퀴즈 선택 창은 숨김!
        modeSelectionLayout.setVisibility(View.VISIBLE);
        quizTypeSelectionLayout.setVisibility(View.GONE);
        // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★


        // ★★★ [추가 3] 모드 선택 버튼 눌렀을 때의 동작 ★★★
        btnOfficialMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOfficialMode = true; // "나 공식 모드야!" 라고 기억함
                modeSelectionLayout.setVisibility(View.GONE); // 모드 선택창 숨기고
                quizTypeSelectionLayout.setVisibility(View.VISIBLE); // 퀴즈 선택창 짠!
            }
        });

        btnFreeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOfficialMode = false; // "나 자율 모드야!" 라고 기억함
                modeSelectionLayout.setVisibility(View.GONE);
                quizTypeSelectionLayout.setVisibility(View.VISIBLE);
            }
        });


        //  받아쓰기 버튼
        dictationConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizSettingBottomSheet bottomSheet = new QuizSettingBottomSheet();
                Bundle args = new Bundle();
                args.putString("quizType", "DICTATION");

                args.putBoolean("isOfficial", isOfficialMode);

                bottomSheet.setArguments(args);
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

                args.putBoolean("isOfficial", isOfficialMode);

                bottomSheet.setArguments(args);
                bottomSheet.show(getChildFragmentManager(), "QuizSettingTag");
            }
        });

        // 객관식
        multiplechoiceConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizSettingBottomSheet bottomSheet = new QuizSettingBottomSheet();
                Bundle args = new Bundle();
                args.putString("quizType", "MULTIPLE_CHOICE");

                args.putBoolean("isOfficial", isOfficialMode);

                bottomSheet.setArguments(args);
                bottomSheet.show(getChildFragmentManager(), "QuizSettingTag");
            }
        });

        return view;

    }
}
