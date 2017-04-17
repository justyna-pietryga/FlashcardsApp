package com.example.justyna.flashcards;


public class RowBean {
    private String firstWord;
    private String secondWord;
    private int positionOnListViewId;
    private int IDinDatabase;

    public int getIDinDatabase() {
        return IDinDatabase;
    }

    public void setIDinDatabase(int IDinDatabase) {
        this.IDinDatabase = IDinDatabase;
    }

    public void setFirstWord(String firstWord) {
        this.firstWord = firstWord;
    }

    public void setSecondWord(String secondWord) {
        this.secondWord = secondWord;
    }

    public String getFirstWord() {
        return firstWord;
    }

    public String getSecondWord() {
        return secondWord;
    }

    public int getPositionOnListViewId() {
        return positionOnListViewId;
    }

    public void setPositionOnListViewId(int positionOnListViewId) {
        this.positionOnListViewId = positionOnListViewId;
    }

    public RowBean() {
    }

    public RowBean(String firstWord, String secondWord) {
        this.firstWord = firstWord;
        this.secondWord = secondWord;
    }

    public RowBean(String firstWord, String secondWord, int positionOnListViewId, int IDinDatabase) {
        this.firstWord = firstWord;
        this.secondWord = secondWord;
        this.positionOnListViewId = positionOnListViewId;
        this.IDinDatabase = IDinDatabase;
    }

    @Override
    public String toString() {
        return "RowBean{" +
                "firstWord='" + firstWord + '\'' +
                ", secondWord='" + secondWord + '\'' +
                '}';
    }
}
