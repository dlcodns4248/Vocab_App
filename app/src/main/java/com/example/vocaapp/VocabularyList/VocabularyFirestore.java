package com.example.vocaapp.VocabularyList;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class VocabularyFirestore {
    // 단어 추가하는 db 로직
    public static void addWord(String uid, String vocabularyId, Map<String, Object> wordData, Runnable onSuccess, Runnable onFailure) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("vocabularies")
                .document(vocabularyId)
                .collection("words")
                .add(wordData)
                .addOnSuccessListener(docRef -> {
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.run();
                });
    }
    // 단어 불러오는 db 로직
    public static void listenWords(String userId, String vocabularyId, OnWordsChanged listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("vocabularies")
                .document(vocabularyId)
                .collection("words")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) {
                        listener.onError(e);
                        return;
                    }

                    listener.onChanged(snapshots);
                });
    }

    public interface OnWordsChanged {
        void onChanged(QuerySnapshot snapshots);
        void onError(Exception e);
    }
}
