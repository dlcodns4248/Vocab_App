package com.example.vocaapp;

import com.example.vocaapp.QuizAndGame.QuizAndGameFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.widget.Toast; //토스트 메세지 출력용
import android.util.Log; // 로그 출력용

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.vocaapp.VocabularyBookList.VocabularyBookListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new VocabularyBookListFragment())
                .commit();

        // 하단 네비게시연 선택에 따른 화면 이동
        bottomNavigationView.setOnItemSelectedListener(item -> {

            // 기본 화면을 단어장 화면으로
            Fragment selectedFragment = new VocabularyBookListFragment();

            int id = item.getItemId();

            if (id == R.id.vocabularylist) {
                selectedFragment = new VocabularyBookListFragment();
            } else if (id == R.id.quizandgame) {
                selectedFragment = new QuizAndGameFragment();
            } else if (id == R.id.setting) {
                selectedFragment = new SettingFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });
    }
    //  단어 공부 완료 처리 함수
    public void studyWord(String userId, String vocabId, String wordId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. 경로 설정
        DocumentReference wordRef = db.collection("users").document(userId)
                .collection("vocabularies").document(vocabId)
                .collection("words").document(wordId);

        // 2. 현재 데이터 가져오기
        wordRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                //A. 현재 단계 확인
                Long currentStageLong = documentSnapshot.getLong("stage");
                int currentStage = (currentStageLong != null) ? currentStageLong.intValue() : 0;
                // B. 다음 단계 계산
                int nextStage = currentStage + 1;

                // C. 다음 복습 시간 계산
                int waitMinutes = 0;
                if (nextStage == 1) waitMinutes = 10;
                else if (nextStage == 2) waitMinutes = 60;
                else if (nextStage == 3) waitMinutes = 1440;
                else if (nextStage == 4) waitMinutes = 10080;
                else waitMinutes = 43200;
                // D. 날짜 계산
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, waitMinutes);
                Date nextReviewDate = cal.getTime();

                // E. 업데이트할 데이터 준비
                Map<String, Object> updates = new HashMap<>();
                updates.put("stage", nextStage);
                updates.put("nextReviewDate", new Timestamp(nextReviewDate)); //파이어베이스용 시간 포멧
                updates.put("isStudying", true);
                updates.put("lastStudiedAt", new Timestamp(new Date()));

                final int finalNextStage = nextStage;
                final int finalWaitMinutes = waitMinutes;

                // F. DB 업데이트 실행
                wordRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            //성공 시 실행
                            String msg = "성공이다" + finalNextStage + "단계로 상승. (" + finalWaitMinutes + "분 뒤 복습)";
                            Log.d("StudyLogic", msg);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            //실패 시 실행될 코드
                            Log.w("StudyLogic", "업데이트 실패", e);
                            Toast.makeText(getApplicationContext(), "에러 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getApplicationContext(),"단어를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}