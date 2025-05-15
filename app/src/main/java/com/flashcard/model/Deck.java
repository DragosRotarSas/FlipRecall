package com.flashcard.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "decks",
        foreignKeys = @ForeignKey(entity = Folder.class,
                                parentColumns = "id",
                                childColumns = "folderId",
                                onDelete = ForeignKey.CASCADE))
public class Deck {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private long folderId;
    
    public Deck(String title, long folderId) {
        this.title = title;
        this.folderId = folderId;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public long getFolderId() {
        return folderId;
    }
    
    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }
} 