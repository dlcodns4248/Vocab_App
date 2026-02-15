package com.example.vocaapp.QuizAndGame;

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
}
