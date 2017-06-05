package com.example.justyna.flashcards;

public class Vocabulary {
    private int id;
    private String category;
    private String firstLanguageWord;
    private String secondLanguageWord;
    private int categoryID;

    public Vocabulary(int id, String firstLanguageWord, String secondLanguageWord) {
        this.id = id;
        this.firstLanguageWord = firstLanguageWord;
        this.secondLanguageWord = secondLanguageWord;
    }

    public Vocabulary(String firstLanguageWord, String secondLanguageWord, int categoryID) {
        this.firstLanguageWord = firstLanguageWord;
        this.secondLanguageWord = secondLanguageWord;
        this.categoryID = categoryID;
    }

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

    public Vocabulary(String firstLanguageWord, String secondLanguageWord) {
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

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public String toString() {
        return this.getClass().getName()+"[Id="+getId()+
                ", First language word="+getFirstLanguageWord()+
                ", Second Language Word="+getSecondLanguageWord()+", CategoryID="+getCategoryID()+"]";
    }
}
