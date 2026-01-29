package com.example.vocaapp.VocabularyList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocaapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VocabularyActivity extends AppCompatActivity {

    private String vocabularyId;
    VocabularyListAdapter adapter;

    List<String> words = new ArrayList<>();
    List<String> meanings = new ArrayList<>();
    List<String> pronunciations = new ArrayList<>();
    List<String> comments = new ArrayList<>();

    FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_detail);

        // 뒤로가기 처리
        ImageView backImageView = findViewById(R.id.backImageView);
        backImageView.setOnClickListener(v -> {
            finish();
        });

        vocabularyId = getIntent().getStringExtra("vocabularyId");

        ImageView vocaRegisterImageView = findViewById(R.id.vocabularyBookRegisterImageView);

        // 단어 등록 창 띄우기
        vocaRegisterImageView.setOnClickListener(v -> {
            showWordRegisterBottomSheet();
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        String uid = user.getUid();

        VocabularyFirestore.listenWords(uid, vocabularyId, new VocabularyFirestore.OnWordsChanged() {
            @Override
            public void onChanged(QuerySnapshot snapshots) {
                words.clear();
                meanings.clear();
                pronunciations.clear();
                comments.clear();

                for (DocumentSnapshot document : snapshots) {
                    String word = document.getString("word");
                    String meaning = document.getString("mean");
                    String pronunciation = document.getString("pronunciation");
                    String comment = document.getString("explain");

                    if (word != null) words.add(word);
                    if (meaning != null) meanings.add(meaning);
                    if (pronunciation != null) pronunciations.add(pronunciation);
                    if (comment != null) comments.add(comment);
                }

                if (adapter == null) {
                    adapter = new VocabularyListAdapter(words, meanings, pronunciations, comments);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onError(Exception e) {
                Log.e("Firestore", "Listen failed.", e);
            }
        });


        // LayoutManager 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showWordRegisterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(VocabularyActivity.this);

        View view = getLayoutInflater()
                .inflate(R.layout.word_register_bottom_sheet, null);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        EditText wordEditText = view.findViewById(R.id.wordEditText);
        EditText meanEditText = view.findViewById(R.id.meanEditText);
        EditText pronunciationEditText = view.findViewById(R.id.pronunciationEditText);
        EditText explainEditText = view.findViewById(R.id.explainEditText);
        Button wordRegisterButton = view.findViewById(R.id.wordRegisterButton);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // 로그인 안 된 상태
            return;
        }

        String uid = user.getUid();


        wordRegisterButton.setOnClickListener(v -> {

            String word = wordEditText.getText().toString().trim();
            String mean = meanEditText.getText().toString().trim();
            String pronunciation = pronunciationEditText.getText().toString().trim();
            String explain = explainEditText.getText().toString().trim();

            // 유효성 검사
            if (word.isEmpty() || mean.isEmpty()) {
                Toast.makeText(this, "단어와 의미는 필수입니다", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firestore에 저장할 데이터
            Map<String, Object> wordData = new HashMap<>();
            wordData.put("word", word);
            wordData.put("mean", mean);
            wordData.put("pronunciation", pronunciation);
            wordData.put("explain", explain);

            // WordFirestore에서 실제로 데이터 삽입
            VocabularyFirestore.addWord(uid, vocabularyId, wordData, () -> {
                Toast.makeText(this, "단어 등록 완료!", Toast.LENGTH_SHORT).show();
                wordEditText.setText("");
                meanEditText.setText("");
                pronunciationEditText.setText("");
                explainEditText.setText("");
            }, () -> {
                Toast.makeText(this, "등록 실패", Toast.LENGTH_SHORT).show();
            });

        });
    }
}
