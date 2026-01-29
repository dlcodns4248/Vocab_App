package com.example.vocaapp.VocabularyList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocaapp.R;

import java.util.List;

public class VocabularyListAdapter extends RecyclerView.Adapter<VocabularyListAdapter.WordViewHolder> {

    // 4개의 문자열을 각각 담은 리스트들
    private List<String> words;
    private List<String> meanings;
    private List<String> pronunciations;
    private List<String> comments;

    // 생성자
    public VocabularyListAdapter(List<String> words, List<String> meanings,
                                 List<String> pronunciations, List<String> comments) {
        this.words = words;
        this.meanings = meanings;
        this.pronunciations = pronunciations;
        this.comments = comments;
    }

    // ViewHolder 정의
    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView, meanTextView, pronunciationTextView, commentTextView;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.wordTextView);
            meanTextView = itemView.findViewById(R.id.meanTextView);
            pronunciationTextView = itemView.findViewById(R.id.pronunciationTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_list_item, parent, false); // XML 이름에 맞게
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        // 리스트에서 각각 꺼내서 TextView에 세팅
        holder.wordTextView.setText(words.get(position));
        holder.meanTextView.setText(meanings.get(position));
        holder.pronunciationTextView.setText(pronunciations.get(position));
        holder.commentTextView.setText(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return words.size(); // 모든 리스트는 같은 길이여야 함
    }
}
