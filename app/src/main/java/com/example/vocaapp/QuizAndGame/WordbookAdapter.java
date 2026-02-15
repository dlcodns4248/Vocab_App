package com.example.vocaapp.QuizAndGame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocaapp.R;

import java.util.List;
import java.util.Map;

public class WordbookAdapter extends RecyclerView.Adapter<WordbookAdapter.WordbookViewHolder> {

    private List<Map<String, Object>> wordbookList;

    // 생성자: 데이터를 전달받습니다.
    public WordbookAdapter(List<Map<String, Object>> list) {
        this.wordbookList = list;
    }

    // 1. 뷰홀더 생성: 아이템 레이아웃(XML)을 가져와서 뷰객체로 만듭니다.
    @NonNull
    @Override
    public WordbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_vocabulary_book_item, parent, false);
        return new WordbookViewHolder(view);
    }

    // 2. 데이터 바인딩: 생성된 뷰홀더에 실제 데이터를 넣습니다.
    @Override
    public void onBindViewHolder(@NonNull WordbookViewHolder holder, int position) {
        Map<String, Object> vocab = wordbookList.get(position);

        // 단어장 이름 설정
        holder.tvTitle.setText(String.valueOf(vocab.get("title")));

        // 단어 개수 설정 (숫자 + " 단어")
        Object count = vocab.get("wordCount");
        String countText = (count != null ? String.valueOf(count) : "0") + " 단어";
        holder.wordCountsTextView.setText(countText);
    }

    // 3. 아이템 개수 반환
    @Override
    public int getItemCount() {
        return wordbookList.size();
    }

    // 뷰홀더 클래스: 아이템 레이아웃 안에 있는 위젯들을 연결합니다.
    public static class WordbookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView wordCountsTextView;


        public WordbookViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tv_wordbook_name);
            this.wordCountsTextView = itemView.findViewById(R.id.wrodCountsTextView);
        }
    }
}
