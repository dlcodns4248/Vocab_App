package com.example.vocaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VocabularyListAdapter extends RecyclerView.Adapter<VocabularyListAdapter.VocabularyListViewHolder>{

    private final List<String> dataList;

    public VocabularyListAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    // 각 아이템 뷰를 보관하고, 내부 위젯(TextView 등)에 접근할 수 있게 하는 ViewHolder
    public static class VocabularyListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;

        public VocabularyListViewHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.textView);
        }
    }

    // 아이템 뷰를 생성하고 ViewHolder를 반환
    @NonNull
    @Override
    public VocabularyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vocabulary_list_item, parent, false);
        return new VocabularyListViewHolder(view);
    }

    // ViewHolder의 뷰에 데이터를 설정
    @Override
    public void onBindViewHolder(@NonNull VocabularyListViewHolder holder, int position) {
        String data = dataList.get(position);
        holder.textViewItem.setText(data);
    }

    // 데이터 리스트의 크기를 반환
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
