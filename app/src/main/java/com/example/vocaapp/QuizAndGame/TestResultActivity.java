package com.example.vocaapp.QuizAndGame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vocaapp.R;

public class TestResultActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        Intent intent = getIntent();
        int pass = intent.getIntExtra("pass", 0);
        int fail = intent.getIntExtra("fail", 0);

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
        }
        else{
            resultTextView.setText("흑흑.. 아쉽게도 불합격이에요..");
        }

        finishTextView.setOnClickListener(v -> {
            finish();
        });


    }
}
