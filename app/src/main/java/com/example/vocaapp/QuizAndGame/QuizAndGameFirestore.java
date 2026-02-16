package com.example.vocaapp.QuizAndGame;

import android.util.Log;

import com.example.vocaapp.VocabularyBookList.VocabularyBookFirestore;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizAndGameFirestore {

    // 단어장 제목과 단어 개수 불러오는 db 로직
    public static void listenVocabularies(String uid, VocabularyListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(uid)
                .collection("vocabularies")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        if (callback != null) callback.onFailure(e);
                        return;
                    }

                    if (querySnapshot != null) {
                        List<Map<String, Object>> dataList = new ArrayList<>();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String title = doc.getString("title");
                            Long vocabularyCounts = doc.getLong("wordCount");

                            if (title != null) {
                                Map<String, Object> vocabData = new HashMap<>();
                                vocabData.put("id", doc.getId());
                                vocabData.put("title", title);
                                vocabData.put("wordCount", vocabularyCounts);
                                dataList.add(vocabData);
                            }
                        }

                        if (callback != null) callback.onUpdate(dataList);
                    }
                });
    }
    // 성공, 실패 인터페이스
    public interface VocabularyListCallback {
        void onUpdate(List<Map<String, Object>> dataList);
        void onFailure(Exception e);
    }

    // 단어장에 들어있는 단어의 개수를 가져오는 메서드
    public void getWordCount(String userId, String vocabularyId, OnWordCountCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("users").document(userId)
                .collection("vocabularies").document(vocabularyId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            long wordCount = document.getLong("wordCount") != null ? document.getLong("wordCount") : 0;

                            // 🔥 중요: 데이터를 콜백으로 넘겨줌
                            callback.onCallback(wordCount);
                        }
                    }
                });
    }
    public interface OnWordCountCallback {
        void onCallback(long wordCount);
    }
}
