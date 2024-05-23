package com.example.myapplication;

public class Word {
    private String arabicWord;
    private String englishMeaning;
    private String urduMeaning;

    public Word(String arabicWord, String englishMeaning, String urduMeaning) {
        this.arabicWord = arabicWord;
        this.englishMeaning = englishMeaning;
        this.urduMeaning = urduMeaning;
    }

    public String getArabicWord() {
        return arabicWord;
    }

    public void setArabicWord(String arabicWord) {
        this.arabicWord = arabicWord;
    }

    public String getEnglishMeaning() {
        return englishMeaning;
    }

    public void setEnglishMeaning(String englishMeaning) {
        this.englishMeaning = englishMeaning;
    }

    public String getUrduMeaning() {
        return urduMeaning;
    }

    public void setUrduMeaning(String urduMeaning) {
        this.urduMeaning = urduMeaning;
    }
}
