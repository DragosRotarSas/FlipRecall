package com.flashcard.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "cards",
        foreignKeys = @ForeignKey(entity = Deck.class,
                                parentColumns = "id",
                                childColumns = "deckId",
                                onDelete = ForeignKey.CASCADE))
public class Card {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String question;
    private String answer;
    private long deckId;
    private int correctCount;
    private int incorrectCount;
    private long lastReviewDate;
    
    public Card(String question, String answer, long deckId) {
        this.question = question;
        this.answer = answer;
        this.deckId = deckId;
        this.correctCount = 0;
        this.incorrectCount = 0;
        this.lastReviewDate = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public long getDeckId() {
        return deckId;
    }
    
    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }
    
    public int getCorrectCount() {
        return correctCount;
    }
    
    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }
    
    public int getIncorrectCount() {
        return incorrectCount;
    }
    
    public void setIncorrectCount(int incorrectCount) {
        this.incorrectCount = incorrectCount;
    }
    
    public long getLastReviewDate() {
        return lastReviewDate;
    }
    
    public void setLastReviewDate(long lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
    }
    
    public void incrementCorrectCount() {
        this.correctCount++;
        this.lastReviewDate = System.currentTimeMillis();
    }
    
    public void incrementIncorrectCount() {
        this.incorrectCount++;
        this.lastReviewDate = System.currentTimeMillis();
    }
} 