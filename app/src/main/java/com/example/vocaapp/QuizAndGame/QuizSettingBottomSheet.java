package com.example.vocaapp.QuizAndGame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vocaapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QuizSettingBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dictation_bottom_sheet, container, false);

        Button startButton = view.findViewById(R.id.button);
        LinearLayout selectLinearLayout = view.findViewById(R.id.selectLinearLayout);

        startButton.setOnClickListener(v -> {
            // "시작하기" 버튼 클릭 시 로직
            dismiss(); // 시트 닫기
        });

        selectLinearLayout.setOnClickListener(v -> {
            SelectVocabularyBookBottomSheet bottomSheet = new SelectVocabularyBookBottomSheet();
            bottomSheet.show(getChildFragmentManager(), "SelectVocabularyBookTag");
        });

        return view;
    }
}