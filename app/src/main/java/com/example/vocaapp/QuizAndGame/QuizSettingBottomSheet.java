package com.example.vocaapp.QuizAndGame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vocaapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QuizSettingBottomSheet extends BottomSheetDialogFragment {
    private String selectedDocId;
    private String selectedDocTitle;
    private TextView selectdWordBookTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getChildFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            this.selectedDocId = bundle.getString("selectedId");
            this.selectedDocTitle = bundle.getString("selectedTitle");
            selectdWordBookTextView = getView().findViewById(R.id.selectedWordBookTextView);
            selectdWordBookTextView.setText(selectedDocTitle);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dictation_bottom_sheet, container, false);

        Button startButton = view.findViewById(R.id.button);
        LinearLayout selectLinearLayout = view.findViewById(R.id.selectLinearLayout);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDocId == null){
                    Toast.makeText(getContext(), "단어장을 선택해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                dismiss();

                String quizType = "DICTATION";
                if (getArguments() != null) {
                    quizType = getArguments().getString("quizType", "DICTATION");
                }

                // 게임 선택에 따라 다른 화면 이동
                Intent intent;
                if ("FLASHCARD".equals(quizType)) {
                    intent = new Intent(getContext(), OXTestActivity.class);
                } else if ("MULTIPLE_CHOICE".equals(quizType)) {
                    intent = new Intent(getContext(), MultipleChoiceActivity.class);
                } else{
                    intent = new Intent(getContext(), DictationActivity.class);
                }


                intent.putExtra("vocabularyId", selectedDocId);
                startActivity(intent);
            }
        });

        selectLinearLayout.setOnClickListener(v -> {
            SelectVocabularyBookBottomSheet bottomSheet = new SelectVocabularyBookBottomSheet();
            bottomSheet.show(getChildFragmentManager(), "SelectVocabularyBookTag");
        });

        return view;
    }
}
