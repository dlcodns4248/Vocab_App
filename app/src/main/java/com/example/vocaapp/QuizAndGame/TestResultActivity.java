package com.example.vocaapp.QuizAndGame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vocaapp.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestResultActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        Intent intent = getIntent();
        int pass = intent.getIntExtra("pass", 0);
        int fail = intent.getIntExtra("fail", 0);


        String userId = intent.getStringExtra("userId");
        String vocabularyId = intent.getStringExtra("vocabularyId");

        TextView passTextView = findViewById(R.id.passTextView);
        TextView failTextView = findViewById(R.id.failTextView);
        ProgressBar circularProgressBar = findViewById(R.id.circularProgressBar);
        TextView tvProgress = findViewById(R.id.tvProgress);
        TextView finishTextView = findViewById(R.id.finishTextView);
        TextView resultTextView = findViewById(R.id.resultTextView);


        passTextView.setText(String.valueOf(pass));
        failTextView.setText(String.valueOf(fail));

        int total = pass + fail;
        int progress = (int) ((double) pass / total * 100);
        String progressToString = String.valueOf(progress);
        circularProgressBar.setProgress(progress);
        tvProgress.setText(progressToString);

        if (progress >= 80){
            resultTextView.setText("오~~ 잘했어요! 합격이에요!");


            if (userId != null && vocabularyId != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // 1. 업데이트할 데이터를 준비합니다.
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                data.put("stampCount", FieldValue.increment(1));

                // 'lastTestTime'이라는 이름으로 현재 서버 시간을 저장합니다.
                data.put("lastTestTime", FieldValue.serverTimestamp());

                //  .update 대신 .set(data, SetOptions.merge())를 사용
                // 이 방식은 stampCount가 없으면 새로 만들고, 있으면 기존 값에 1을 더한다.
                db.collection("users").document(userId)
                        .collection("vocabularies").document(vocabularyId)
                        .set(data, com.google.firebase.firestore.SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "스탬프 1개 획득 성공!");
                            Toast.makeText(TestResultActivity.this, "✅ 스탬프가 찍혔습니다!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("DB_ERROR", "실패 원인: ", e);
                            Toast.makeText(TestResultActivity.this, "❌ 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        }
        else{
            resultTextView.setText("흑흑.. 아쉽게도 불합격이에요..");
        }

        finishTextView.setOnClickListener(v -> {
            finish();
        });

    }
}