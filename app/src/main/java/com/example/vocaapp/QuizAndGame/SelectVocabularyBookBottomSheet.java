package com.example.vocaapp.QuizAndGame;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_vocabulary_book_bottom_sheet, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        // 1. RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView); // XML에 선언한 ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
