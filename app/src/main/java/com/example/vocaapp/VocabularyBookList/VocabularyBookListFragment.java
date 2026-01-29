package com.example.vocaapp.VocabularyBookList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocaapp.OnItemClickListener;
import com.example.vocaapp.R;
import com.example.vocaapp.VocabularyList.VocabularyActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VocabularyBookListFragment extends Fragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private VocabularyBookListAdapter adapter;
    private final ArrayList<Map<String, String>> dataList = new ArrayList<>();
    private String uid;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary_book_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewVocabulary);

        // 현재 로그인한 사용자의 uid를 가져오는 처리
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        // 단어장 이름을 등록하는 bottomsheet를 띄우는 처리
        ImageView vocabularyBookRegisterImageView = view.findViewById(R.id.vocabularyBookRegisterImageView);
        vocabularyBookRegisterImageView.setOnClickListener(v -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

            View view2 = getLayoutInflater()
                    .inflate(R.layout.vocabulary_book_bottom_sheet, null);

            bottomSheetDialog.setContentView(view2);
            bottomSheetDialog.show();

            Button registerButton = view2.findViewById(R.id.registerButton);
            EditText bookNameEditText = view2.findViewById(R.id.bookNameEditText);


            registerButton.setOnClickListener( v2 -> {

                String bookName = bookNameEditText.getText().toString();

                // 단어장 이름 입력 안하면 처리
                if (bookName.isEmpty()){
                    Toast.makeText(getContext(), "단어장 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> inputVocabularyBookName = new HashMap<>();
                inputVocabularyBookName.put("title", bookName);

                VocabularyBookFirestore.addVocabularyBook(inputVocabularyBookName, uid, new VocabularyBookFirestore.VocabularyBookCallback() {
                    @Override
                    public void onSuccess() {
                        bottomSheetDialog.dismiss();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "등록 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        });

        // 단어장들 불러오기 처리
        VocabularyBookFirestore.listenVocabularies(uid, new VocabularyBookFirestore.VocabularyListCallback() {
            @Override
            public void onUpdate(List<Map<String, String>> dataList) {
                // 기존 데이터 갱신
                VocabularyBookListFragment.this.dataList.clear();
                VocabularyBookListFragment.this.dataList.addAll(dataList);

                // Adapter 갱신
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Failed to listen vocabularies", e);
            }
        });


        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new VocabularyBookListAdapter(dataList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    // recyclerview item 클릭 후 상세 페이지 이동 처리
    public void onItemClick(int position) {
        Map<String, String> selectedVocabulary = dataList.get(position);
        String selectedVocabularyId = selectedVocabulary.get("id");

        Intent intent = new Intent(requireContext(), VocabularyActivity.class);
        intent.putExtra("vocabularyId", selectedVocabularyId);
        startActivity(intent);
    }

}
