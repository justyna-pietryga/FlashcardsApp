package com.example.justyna.flashcards.model;

import android.text.Html;
import android.text.Spanned;

public class DictionaryResult {
    private String sourceExpression;
    private String targetExpression;

    public DictionaryResult(String sourceExpression, String targetExpression) {
        this.sourceExpression = sourceExpression;
        this.targetExpression = targetExpression;
    }

    public String getSourceExpression() {
        return sourceExpression;
    }

    public void setSourceExpression(String sourceExpression) {
        this.sourceExpression = sourceExpression;
    }

    public String getTargetExpression() {
        return targetExpression;
    }

    public void setTargetExpression(String targetExpression) {
        this.targetExpression = targetExpression;
    }

    @Override
    public String toString() {
        return sourceExpression + '\n' + targetExpression;
    }
}
