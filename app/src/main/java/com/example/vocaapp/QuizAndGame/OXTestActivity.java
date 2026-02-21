package com.example.vocaapp.QuizAndGame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vocaapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OXTestActivity extends AppCompatActivity {

    private TextView vocabularyTextView;
    private ImageView failImageView, passImageView;

    private List<Map<String, Object>> wordList = new ArrayList<>();
    private int currentIndex = 0;
    private int correctCount = 0;
    private String vocabularyId;
    private String userId;
    // 현재 페이지 수를 표시하기 위한 변수
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ox_test);

        // 1. 인텐트로 넘어온 단어장 ID 받기
        vocabularyId = getIntent().getStringExtra("vocabularyId");

        // 뷰 연결
        vocabularyTextView = findViewById(R.id.vocabularyTextView);
        failImageView = findViewById(R.id.failImageView);
        passImageView = findViewById(R.id.passImageView);
        ImageView cancelImageView = findViewById(R.id.cancelImageView);
        TextView currentPageTextView = findViewById(R.id.currentPageTextView);
        TextView totalPageTextView = findViewById(R.id.totalPageTextView);

        cancelImageView.setOnClickListener(v -> finish());

        QuizAndGameFirestore quizAndGameFirestore = new QuizAndGameFirestore();

        currentPageTextView.setText(currentPage);

        // 현재 로그인한 사용자의 id 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        quizAndGameFirestore.getWordCount(userId, vocabularyId, new QuizAndGameFirestore.OnWordCountCallback() {
            @Override
            public void onCallback(long wordCount) {
                totalPageTextView.setText(String.valueOf(wordCount));
            }
        });


        quizAndGameFirestore.loadWordsFromFirestore(userId, vocabularyId, wordList, new QuizAndGameFirestore.loadWordsFromFirestoreCallback(){
            @Override
            public void onCallback(List<Map<String, Object>> wordList) {
                if (wordList.size() > 0) {
                    displayWord();
                } else {
                    Toast.makeText(OXTestActivity.this, "단어장에 단어가 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        // X 버튼 클릭 리스너 (모르는 단어)
        failImageView.setOnClickListener(v -> {
            currentPage ++;
            moveToNextWord();

        });


        // O 버튼 클릭 리스너 (아는 단어)
        passImageView.setOnClickListener(v -> {
            correctCount++;
            currentPage ++;
            moveToNextWord();
        });
    }

    private void displayWord() {
        if (currentIndex < wordList.size()) {
            String word = (String) wordList.get(currentIndex).get("word");
            vocabularyTextView.setText(word);
        } else {
            // 모든 단어를 다 본 경우 결과 화면으로 이동
            showFinalResult();
        }
    }

    private void moveToNextWord() {
        currentIndex++;
        displayWord();
    }

    // [중요 수정] 이제 다이얼로그 대신 TestResultActivity로 데이터를 보냅니다.
    private void showFinalResult() {
        int pass = correctCount;
        int fail = wordList.size() - correctCount;

        // TestResultActivity로 전환하기 위한 인텐트 생성
        Intent intent = new Intent(OXTestActivity.this, TestResultActivity.class);

        // TestResultActivity에서 요구하는 키값들 그대로 넣어주기
        intent.putExtra("pass", pass);
        intent.putExtra("fail", fail);
        intent.putExtra("userId", userId);
        intent.putExtra("vocabularyId", vocabularyId);

        startActivity(intent);
        finish();
    }
}
