package com.example.vocaapp.QuizAndGame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vocaapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultipleChoiceActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int currentIndex = 0;
    List<Map<String, Object>> allWords;
    String correctMean;
    int pass;
    int fail;

    TextView firstTextView, secondTextView, thirdTextView, forthTextView, wordTextView;
    TextView currentPageTextView, totalPageTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice);

        // 뷰 연결
        totalPageTextView = findViewById(R.id.totalPageTextView);
        currentPageTextView = findViewById(R.id.currentPageTextView);
        wordTextView = findViewById(R.id.wordTextView);
        firstTextView = findViewById(R.id.firstTextView);
        secondTextView = findViewById(R.id.secondTextView);
        thirdTextView = findViewById(R.id.thirdTextView);
        forthTextView = findViewById(R.id.forthTextView);
        ImageView cancelImageView = findViewById(R.id.cancelImageView);

        cancelImageView.setOnClickListener(v -> finish());

        String userId = user.getUid();
        Intent intent = getIntent();
        String vocabularyId = intent.getStringExtra("vocabularyId");

        QuizAndGameFirestore quizAndGameFirestore = new QuizAndGameFirestore();

        // 단어 전체 가져오기
        quizAndGameFirestore.getAllWords(userId, vocabularyId, new QuizAndGameFirestore.OnWordsLoadedCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> words) {
                if (words != null && !words.isEmpty()) {
                    allWords = words; // 데이터를 전역 변수에 보관
                    totalPageTextView.setText(String.valueOf(allWords.size()));
                    showNextQuiz(); // 첫 번째 퀴즈 표시
                }
            }
        });

        // 각 선택지 클릭 리스너 설정
        setOptionClickListeners();
    }

    // 2. 퀴즈를 화면에 표시하는 핵심 메서드
    private void showNextQuiz() {
        if (allWords == null || currentIndex >= allWords.size()) {
            // [수정] 모든 문제를 다 풀었을 때 결과 페이지로 이동
            Intent resultIntent = new Intent(MultipleChoiceActivity.this, TestResultActivity.class);

            // 결과 데이터 전달 (정답 수, 오답 수, 총 문제 수 등)
            resultIntent.putExtra("pass", pass);
            resultIntent.putExtra("fail", fail);

            startActivity(resultIntent);
            finish();
            return;
        }

        // 현재 페이지 표시 업데이트
        currentPageTextView.setText(String.valueOf(currentIndex + 1));

        // 정답 데이터 가져오기
        Map<String, Object> currentWordData = allWords.get(currentIndex);
        String word = (String) currentWordData.get("word");
        correctMean = (String) currentWordData.get("mean");

        wordTextView.setText(word);

        // 3. 보기 생성 (정답 + 랜덤 오답)
        List<String> options = new ArrayList<>();
        options.add(correctMean); // 정답 추가

        // 오답 3개 무작위 추출
        List<Map<String, Object>> tempWords = new ArrayList<>(allWords);
        tempWords.remove(currentIndex); // 정답 제외
        java.util.Collections.shuffle(tempWords); // 나머지 섞기

        for (int i = 0; i < Math.min(3, tempWords.size()); i++) {
            options.add((String) tempWords.get(i).get("mean"));
        }

        java.util.Collections.shuffle(options); // 보기 최종 섞기

        // 텍스트뷰에 세팅
        if (options.size() > 0) firstTextView.setText(options.get(0));
        if (options.size() > 1) secondTextView.setText(options.get(1));
        if (options.size() > 2) thirdTextView.setText(options.get(2));
        if (options.size() > 3) forthTextView.setText(options.get(3));
    }

    private void setOptionClickListeners() {
        View.OnClickListener listener = v -> {
            TextView selectedView = (TextView) v;
            String selectedText = selectedView.getText().toString();

            View parentLayout = (View) v.getParent();
            int color = androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_primary);


            // 정답 유무 체크
            if (selectedText.equals(correctMean)) {
                pass ++;
                parentLayout.getBackground().setTint(color);
            } else {
                fail ++;
                parentLayout.getBackground().setTint(color);
            }

            // 화면에 누른 옵션의 색을 잠시 보여준 후 이동
            v.postDelayed(() -> {
                parentLayout.getBackground().setTint(Color.WHITE);
                currentIndex++;
                showNextQuiz();
            }, 300);
        };

        firstTextView.setOnClickListener(listener);
        secondTextView.setOnClickListener(listener);
        thirdTextView.setOnClickListener(listener);
        forthTextView.setOnClickListener(listener);
    }
}
