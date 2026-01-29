package com.example.vocaapp.VocabularyBookList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocaapp.OnItemClickListener;
import com.example.vocaapp.R;

import java.util.List;
import java.util.Map;

public class VocabularyBookListAdapter extends RecyclerView.Adapter<VocabularyBookListAdapter.VocabularyListViewHolder>{

    private List<Map<String, String>> dataList;
    private OnItemClickListener listener;

    public VocabularyBookListAdapter(List<Map<String,String>> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    // 각 아이템 뷰를 보관하고, 내부 위젯(TextView 등)에 접근할 수 있게 하는 ViewHolder
    public class VocabularyListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;

        public VocabularyListViewHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(pos);
                }
            });
        }
    }

    // 아이템 뷰를 생성하고 ViewHolder를 반환
    @NonNull
    @Override
    public VocabularyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocabulary_book, parent, false);
        return new VocabularyListViewHolder(view);
    }

    // ViewHolder의 뷰에 데이터를 설정
    @Override
    public void onBindViewHolder(@NonNull VocabularyListViewHolder holder, int position) {
        Map<String, String> vocab = dataList.get(position);
        holder.textViewItem.setText(vocab.get("title")); // 🔹 제목 표시
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    // 데이터 리스트의 크기를 반환
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
