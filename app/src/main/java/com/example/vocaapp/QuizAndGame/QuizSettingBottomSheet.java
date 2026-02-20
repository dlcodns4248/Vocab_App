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
            // 하위나 다른 곳에서 ID를 보내주면 변수에 저장
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

        // 시작 버튼을 누르면 처리
        // 시작 버튼을 누르면 처리
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 단어장 선택 여부 확인
                if (selectedDocId == null){
                    Toast.makeText(getContext(), "단어장을 선택해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                dismiss(); // 팝업 닫기
                Log.e("abcabc", "현재 단어장 ID: " + selectedDocId);

                // 2. 프래그먼트에서 보낸 '쪽지(quizType)' 확인하기
                // 어떤 버튼을 눌러서 이 팝업이 떴는지 확인하는 과정입니다.
                String quizType = "DICTATION"; // 기본값은 받아쓰기
                if (getArguments() != null) {
                    quizType = getArguments().getString("quizType", "DICTATION");
                }

                // 3. 목적지(Intent) 결정하기
                Intent intent;
                if ("FLASHCARD".equals(quizType)) {
                    // 플래시카드 버튼을 눌렀다면 O/X 테스트 화면으로!
                    intent = new Intent(getContext(), OXTestActivity.class);
                } else {
                    // 그 외(또는 받아쓰기 버튼)는 받아쓰기 화면으로!
                    intent = new Intent(getContext(), DictationActivity.class);
                }

                // 4. 선택한 단어장 ID를 들고 출발!
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
