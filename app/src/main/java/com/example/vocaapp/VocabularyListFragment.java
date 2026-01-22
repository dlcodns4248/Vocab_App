package com.example.vocaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VocabularyListFragment extends Fragment {

    private RecyclerView recyclerView;
    private VocabularyListAdapter adapter;
    private final List<String> dataList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabulary_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewVocabulary);
        ImageView vocaRegisterImageView = view.findViewById(R.id.vocaRegisterImageView);

        vocaRegisterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), VocabularyRegisterActivity.class);
                startActivity(intent);
            }
        });


        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 3. 데이터 준비
        for (int i = 1; i <= 20; i++) {
            dataList.add("단어장 " + i);
        }

        // 4. Adapter 생성 및 연결
        adapter = new VocabularyListAdapter(dataList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
