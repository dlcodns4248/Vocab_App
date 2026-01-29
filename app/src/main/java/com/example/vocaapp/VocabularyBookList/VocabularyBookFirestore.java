package com.example.vocaapp.VocabularyBookList;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VocabularyBookFirestore {
    // 단어장 db에 추가하는 로직
    public static void addVocabularyBook(Map<String, Object> inputVocabularyBookName, String uid, VocabularyBookCallback callback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(uid)
                .collection("vocabularies")
                .add(inputVocabularyBookName)
                .addOnSuccessListener(documentReference -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }
    // 성공, 실패 인터페이스
    public interface VocabularyBookCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    // 단어 불러오는 db 로직
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
                        List<Map<String, String>> dataList = new ArrayList<>();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String title = doc.getString("title");
                            if (title != null) {
                                Map<String, String> vocabData = new HashMap<>();
                                vocabData.put("id", doc.getId());
                                vocabData.put("title", title);
                                dataList.add(vocabData);
                            }
                        }

                        if (callback != null) callback.onUpdate(dataList);
                    }
                });
    }
    // 성공, 실패 인터페이스
    public interface VocabularyListCallback {
        void onUpdate(List<Map<String, String>> dataList);
        void onFailure(Exception e);
    }
}
