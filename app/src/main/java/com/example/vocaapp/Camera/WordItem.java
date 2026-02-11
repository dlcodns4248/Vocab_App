package com.example.vocaapp.Camera;

public class WordItem {
    public String word;
    public String meaning;
    public String pronunciation;
    public String comment;

    // 기본 생성자 (GSON 파싱에 필요)
    public WordItem() {
    }

    public WordItem(String word, String meaning, String pronunciation, String comment) {
        this.word = word;
        this.meaning = meaning;
        this.pronunciation = pronunciation;
        this.comment = comment;
    }
}
