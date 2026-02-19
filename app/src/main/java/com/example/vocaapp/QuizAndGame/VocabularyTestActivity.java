package com.example.vocaapp.QuizAndGame;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vocaapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VocabularyTestActivity extends AppCompatActivity {

    private TextView vocabularyTextView;
    private ImageView failImageView, passImageView;

    private List<Map<String, Object>> wordList = new ArrayList<>();
    private int currentIndex = 0;
    private int correctCount = 0;
    private String vocabularyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_test);

        // 1. 인텐트로 넘어온 단어장 ID 받기
        vocabularyId = getIntent().getStringExtra("vocabularyId");

        // 2. XML 뷰 연결
        vocabularyTextView = findViewById(R.id.vocabularyTextView);
        failImageView = findViewById(R.id.failImageView);   // X 버튼
        passImageView = findViewById(R.id.passImageView);   // O 버튼

        // 3. 파이어베이스에서 단어 가져오기
        loadWordsFromFirestore();

        // 4. X 버튼 클릭 리스너 (모르는 단어)
        failImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextWord();
            }
        });

        // 5. O 버튼 클릭 리스너 (아는 단어)
        passImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctCount++; // 맞은 개수 증가
                moveToNextWord();
            }
        });
    }

    private void loadWordsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. 현재 로그인한 사용자의 고유 ID(UID)를 가져옵니다.
        // (Firebase Auth가 설정되어 있어야 합니다.)
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String currentUserId = user.getUid();

        // 2. 정확한 경로: users(컬렉션) -> UID(문서) -> vocabularies(컬렉션) -> 단어장ID(문서) -> words(컬렉션)
        db.collection("users") // 'user'가 아니라 'users'로 수정 완료!
                .document(currentUserId)
                .collection("vocabularies")
                .document(vocabularyId)
                .collection("words")
                .get()
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onSuccess(com.google.firebase.firestore.QuerySnapshot queryDocumentSnapshots) {
                        wordList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            wordList.add(doc.getData());
                            Log.d("VocabularyTest", "단어 로드 성공: " + doc.getData().toString());
                        }

                        if (wordList.size() > 0) {
                            displayWord();
                        } else {
                            Toast.makeText(VocabularyTestActivity.this, "단어장에 단어가 없습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("VocabularyTest", "Firestore 연결 에러", e);
                        Toast.makeText(VocabularyTestActivity.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayWord() {
        if (currentIndex < wordList.size()) {
            // "word"는 DB 필드명입니다. 본인의 DB 필드명에 맞게 수정하세요!
            String word = (String) wordList.get(currentIndex).get("word");
            vocabularyTextView.setText(word);
        } else {
            // 모든 단어를 다 본 경우 결과 계산
            showFinalResult();
        }
    }

    private void moveToNextWord() {
        currentIndex++;
        displayWord();
    }

    private void showFinalResult() {
        int score = (int) (((double) correctCount / wordList.size()) * 100);
        boolean isPass = score >= 80;

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

        if (isPass) {
            // 1. 합격인 경우
            builder.setTitle("🎉 축하합니다! 합격입니다.");
            builder.setMessage("내 점수: " + score + "점\n정말 잘하셨어요!");

            builder.setPositiveButton("학습 완료", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish(); // 깔끔하게 종료하고 메인으로 이동
                }
            });
        } else {
            // 2. 불합격인 경우
            builder.setTitle("😢 조금 더 힘내볼까요?");
            builder.setMessage("내 점수: " + score + "점\n(합격 기준: 80점)");

            builder.setPositiveButton("한번 더 보자", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // 다시 시작하기 위해 데이터 초기화 후 처음부터 보여주기
                    currentIndex = 0;
                    correctCount = 0;
                    displayWord();
                }
            });

            builder.setNegativeButton("다음에 하기", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish(); // 일단은 나중에 하기 위해 종료
                }
            });
        }

        builder.setCancelable(false);
        builder.show();
    }
}