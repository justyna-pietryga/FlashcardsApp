package com.example.justyna.flashcards;

public class Vocabulary {
    private int id;
    private String category;
    private String firstLanguageWord;
    private String secondLanguageWord;

    public Vocabulary(int id, String category, String firstLanguageWord, String secondLanguageWord) {
        this.id = id;
        this.category=category;
        this.firstLanguageWord = firstLanguageWord;
        this.secondLanguageWord = secondLanguageWord;
    }

    public Vocabulary (String category, String firstLanguageWord, String secondLanguageWord) {
        this.category=category;
        this.firstLanguageWord = firstLanguageWord;
        this.secondLanguageWord = secondLanguageWord;
    }

    public Vocabulary() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstLanguageWord() {
        return firstLanguageWord;
    }

    public void setFirstLanguageWord(String firstLanguageWord) {
        this.firstLanguageWord = firstLanguageWord;
    }

    public String getSecondLanguageWord() {
        return secondLanguageWord;
    }

    public void setSecondLanguageWord(String secondLanguageWord) {
        this.secondLanguageWord = secondLanguageWord;
    }

    @Override
    public String toString() {
        return this.getClass().getName()+"[Id="+getId()+
                ", First language word="+getFirstLanguageWord()+
                ", Second Language Word="+getSecondLanguageWord()+"]";
    }
}
