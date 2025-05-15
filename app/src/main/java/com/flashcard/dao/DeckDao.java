package com.flashcard.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.flashcard.model.Deck;

import java.util.List;

@Dao
public interface DeckDao {
    @Insert
    long insert(Deck deck);
    
    @Update
    void update(Deck deck);
    
    @Delete
    void delete(Deck deck);
    
    @Query("SELECT * FROM decks WHERE folderId = :folderId")
    LiveData<List<Deck>> getDecksByFolderId(long folderId);
    
    @Query("SELECT * FROM decks WHERE id = :id")
    LiveData<Deck> getDeckById(long id);

    @Query("SELECT * FROM decks WHERE folderId = :folderId")
    List<Deck> getDecksByFolderIdSync(long folderId);

    @Delete
    void deleteDeck(Deck deck);
} 