package com.example.vocaapp.QuizAndGame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vocaapp.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DictationActivity extends AppCompatActivity {

    ImageView cancelImageView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView currentPageTextView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);

        // 데이터 연결
        cancelImageView = findViewById(R.id.cancelImageView);
        currentPageTextView = findViewById(R.id.currentPageTextView);

        String userId = user.getUid();

        // intent로 받은 데이터 꺼내기
        Intent intent = getIntent();
        String vocabularyId = intent.getStringExtra("vocabularyId");

        // Firestore 메서드 실행
        QuizAndGameFirestore quizAndGameFirestore = new QuizAndGameFirestore();
        // 메서드 호출 시 세 번째 인자로 콜백 구현
        quizAndGameFirestore.getWordCount(userId, vocabularyId, new QuizAndGameFirestore.OnWordCountCallback() {
            @Override
            public void onCallback(long wordCount) {

                int currentPage = 1;

                currentPageTextView.setText(currentPage + "/" +  wordCount);

            }
        });



        cancelImageView.setOnClickListener(v -> {
            finish();
        });
    }
}
