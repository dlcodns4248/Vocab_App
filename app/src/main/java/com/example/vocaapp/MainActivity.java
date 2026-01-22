package com.example.vocaapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new VocabularyListFragment())
                .commit();

        // 하단 네비게시연 선택에 따른 화면 이동
        bottomNavigationView.setOnItemSelectedListener(item -> {

            // 기본 화면을 단어장 화면으로
            Fragment selectedFragment = new VocabularyListFragment();

            int id = item.getItemId();

            if (id == R.id.vocabularylist) {
                selectedFragment = new VocabularyListFragment();
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
}