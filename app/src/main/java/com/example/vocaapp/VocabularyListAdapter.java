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

    public static class VocabularyListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;

        public VocabularyListViewHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.textView);
        }
    }

    @NonNull
    @Override
    public VocabularyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vocabulary_list_item, parent, false);
        return new VocabularyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyListViewHolder holder, int position) {
        String data = dataList.get(position);
        holder.textViewItem.setText(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
