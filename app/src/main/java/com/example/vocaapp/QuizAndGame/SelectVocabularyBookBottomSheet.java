package com.example.vocaapp.QuizAndGame;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocaapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectVocabularyBookBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private WordbookAdapter adapter;
    private List<Map<String, Object>> dataList = new ArrayList<>();
    private String uid;
    private ImageView cancelImageView;
    private TextView registerTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_vocabulary_book_bottom_sheet, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        // 초기화 및 레이아웃 연결
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cancelImageView = view.findViewById(R.id.cancelImageView);
        registerTextView = view.findViewById(R.id.registerTextView);

        cancelImageView.setOnClickListener(v->{
            dismiss();
        });

        registerTextView.setOnClickListener(v -> {
            Map<String, Object> selectedData = adapter.getSelectedWordbook();


            if (selectedData != null) {
                // map에서 데이터 꺼내기
                String title = String.valueOf(selectedData.get("title"));
                String id = String.valueOf(selectedData.get("id"));

                QuizAndGameFirestore quizAndGameFirestore = new QuizAndGameFirestore();
                quizAndGameFirestore.getWordCount(uid, id, new QuizAndGameFirestore.OnWordCountCallback() {
                        @Override
                            public void onCallback(long wordCount) {
                                // 단어장에 단어가 없는 경우를 처리
                                if (wordCount == 0){
                                    Toast.makeText(requireContext(), "단어장에 단어가 없습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                // 단어장에 단어가 있는 경우를 처리
                                else{
                                    // [핵심] 부모(QuizSetting)에게 데이터를 전달합니다.
                                    Bundle result = new Bundle();
                                    result.putString("selectedTitle", title);
                                    result.putString("selectedId", id);

                                    // requestKey는 부모와 맞춘 약속된 키입니다.
                                    getParentFragmentManager().setFragmentResult("requestKey", result);

                                    // 본인만 닫습니다. 그러면 아래에 있던 QuizSetting이 다시 보입니다.
                                    dismiss();
                                }
                            }
                });
            } else {
                Toast.makeText(getContext(), "단어장을 선택해주세요!", Toast.LENGTH_SHORT).show();
            }

        });

        adapter = new WordbookAdapter(dataList);
        recyclerView.setAdapter(adapter);

        // 2. DB 데이터 불러오기
        loadVocabularies();

        return view;
    }

    private void loadVocabularies() {
        QuizAndGameFirestore.listenVocabularies(uid, new QuizAndGameFirestore.VocabularyListCallback() {
            @Override
            public void onUpdate(List<Map<String, Object>> newDataList) {
                dataList.clear();
                dataList.addAll(newDataList);
                adapter.notifyDataSetChanged(); // 리스트 갱신
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "데이터 로드 실패: ", e);
            }
        });
    }

}
