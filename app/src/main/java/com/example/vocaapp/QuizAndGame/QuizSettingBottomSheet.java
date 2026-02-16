package com.example.vocaapp.QuizAndGame;

import android.os.Bundle;
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

    // [1번 추가] 전달받은 제목을 담을 변수
    private String mTitle;

    // [1번 추가] 메인 화면에서 이 메서드를 통해 제목을 넘겨주게 됩니다.
    public static QuizSettingBottomSheet newInstance(String title) {
        QuizSettingBottomSheet fragment = new QuizSettingBottomSheet();
        Bundle args = new Bundle();
        args.putString("selected_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [1번 추가] 시작하자마자 메인에서 보낸 "택배"가 있는지 확인하고 가져옵니다.
        if (getArguments() != null) {
            mTitle = getArguments().getString("selected_title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dictation_bottom_sheet, container, false);

        Button startButton = view.findViewById(R.id.button);
        LinearLayout selectLinearLayout = view.findViewById(R.id.selectLinearLayout);
        TextView selectdWordBookTextView = view.findViewById(R.id.selectedWordBookTextView);

        // [2번 추가] 처음 창이 열릴 때, 받아온 mTitle이 있다면 텍스트뷰에 바로 보여줍니다.
        if (mTitle != null) {
            selectdWordBookTextView.setText(mTitle);
        }

        // 하위 BottomSheet로부터 결과를 받기 위한 리스너 설정
        getChildFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // 번들에서 데이터를 꺼내기
            String selectedTitle = bundle.getString("selectedTitle");
            String selectedDocId = bundle.getString("selectedId");
            if (selectedTitle != null) {
                selectdWordBookTextView.setText(selectedTitle);
                Toast.makeText(getContext(), "doc id는 " + selectedDocId, Toast.LENGTH_SHORT).show();
            }
        });

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
