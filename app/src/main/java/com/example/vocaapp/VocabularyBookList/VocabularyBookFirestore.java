package com.example.vocaapp.VocabularyBookList;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    // [중요 수정] 단어장 불러오는 db 로직
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
                        // [수정] String 대신 Object를 사용하여 모든 타입의 데이터를 담습니다.
                        List<Map<String, Object>> dataList = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            // [수정] doc.getData()를 쓰면 title, stampCount 등 모든 필드를 한 번에 가져옵니다.
                            Map<String, Object> vocabData = doc.getData();
                            // ID 값도 나중에 필요하므로 함께 넣어줍니다.
                            vocabData.put("id", doc.getId());

                            dataList.add(vocabData);
                        }

                        if (callback != null) callback.onUpdate(dataList);
                    }
                });
    }

    // [중요 수정] 인터페이스 타입 변경
    public interface VocabularyListCallback {
        // [수정] 여기도 List<Map<String, Object>>로 변경하여 숫자 데이터를 허용합니다.
        void onUpdate(List<Map<String, Object>> dataList);
        void onFailure(Exception e);
    }
}