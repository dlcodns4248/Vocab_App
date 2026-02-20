package com.example.vocaapp.VocabularyBookList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocaapp.R;

import java.util.List;
import java.util.Map;

public class VocabularyBookListAdapter extends RecyclerView.Adapter<VocabularyBookListAdapter.VocabularyListViewHolder>{

    // [수정] 데이터를 Object 타입으로 변경
    private List<Map<String, Object>> dataList;
    private VocabularyBookListFragment fragment;

    public VocabularyBookListAdapter(List<Map<String, Object>> dataList, VocabularyBookListFragment fragment) {
        this.dataList = dataList;
        this.fragment = fragment;
    }

    public static class VocabularyListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;
        ImageView[] stamps = new ImageView[6];

        public VocabularyListViewHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.textView);
            stamps[0] = itemView.findViewById(R.id.stamp1);
            stamps[1] = itemView.findViewById(R.id.stamp2);
            stamps[2] = itemView.findViewById(R.id.stamp3);
            stamps[3] = itemView.findViewById(R.id.stamp4);
            stamps[4] = itemView.findViewById(R.id.stamp5);
            stamps[5] = itemView.findViewById(R.id.stamp6);
        }
    }

    @NonNull
    @Override
    public VocabularyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vocabulary_book_list_item, parent, false);
        return new VocabularyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyListViewHolder holder, int position) {
        Map<String, Object> vocab = dataList.get(position);
        holder.textViewItem.setText(String.valueOf(vocab.get("title")));

        // [수정] 스탬프 개수를 안전하게 가져오는 로직
        int stampCount = 0;
        Object countObj = vocab.get("stampCount");

        if (countObj != null) {
            try {
                // 어떤 숫자 타입이 오든 안전하게 int로 변환
                stampCount = Integer.parseInt(String.valueOf(countObj));
            } catch (Exception e) {
                stampCount = 0;
            }
        }

        // [수정] 스탬프 개수만큼 빨간색 필터 적용
        for (int i = 0; i < 6; i++) {
            if (i < stampCount) {
                // 획득 시: 빨간색 (원하는 색상 코드로 변경 가능)
                holder.stamps[i].setColorFilter(Color.RED);
            } else {
                // 미획득 시: 회색
                holder.stamps[i].setColorFilter(Color.LTGRAY);
            }
        }

        holder.itemView.setOnClickListener(v -> fragment.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}