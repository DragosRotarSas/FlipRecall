package com.flashcard.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "folders")
public class Folder {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private long parentFolderId; // 0 for root folder
    
    public Folder(String title, long parentFolderId) {
        this.title = title;
        this.parentFolderId = parentFolderId;
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
    
    public long getParentFolderId() {
        return parentFolderId;
    }
    
    public void setParentFolderId(long parentFolderId) {
        this.parentFolderId = parentFolderId;
    }
} 