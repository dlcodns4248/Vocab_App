package com.example.vocaapp.QuizAndGame;

import android.util.Log;
import android.widget.Toast;

import com.example.vocaapp.VocabularyBookList.VocabularyBookFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    // words 컬렉션 안 단어와 뜻을 모두 가져오는 메서드
    public void getAllWords(String userId, String vocabularyId, OnWordsLoadedCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("users").document(userId)
                .collection("vocabularies").document(vocabularyId)
                .collection("words") // 하위 컬렉션으로 접근
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> wordList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 각 문서에서 word와 mean 필드 추출
                            Map<String, Object> wordData = new HashMap<>();
                            wordData.put("word", document.getString("word"));
                            wordData.put("mean", document.getString("mean"));
                            wordList.add(wordData);
                        }

                        // 데이터를 콜백으로 전달
                        callback.onCallback(wordList);
                    } else {
                        // 에러 처리 로직을 여기에 추가할 수 있습니다.
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }
    public interface OnWordsLoadedCallback {
        void onCallback(List<Map<String, Object>> words);
    }

    public void loadWordsFromFirestore(String userId, String vocabularyId, List<Map<String, Object>> wordList, loadWordsFromFirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userId = user.getUid(); // [추가] 유저 ID 저장

        db.collection("users")
                .document(userId)
                .collection("vocabularies")
                .document(vocabularyId)
                .collection("words")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    wordList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        wordList.add(doc.getData());
                    }
                    callback.onCallback(wordList);


                })
                .addOnFailureListener(e -> {
                    Log.e("OXTest", "Firestore 연결 에러", e);
                });
    }
    public interface loadWordsFromFirestoreCallback {
        void onCallback(List<Map<String, Object>> wordList);
    }
}
