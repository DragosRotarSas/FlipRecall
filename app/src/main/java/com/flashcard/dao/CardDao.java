package com.flashcard.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.flashcard.model.Card;

import java.util.List;

@Dao
public interface CardDao {
    @Insert
    long insert(Card card);
    
    @Update
    void update(Card card);
    
    @Delete
    void delete(Card card);
    
    @Query("SELECT * FROM cards WHERE deckId = :deckId")
    LiveData<List<Card>> getCardsByDeckId(long deckId);
    
    @Query("SELECT * FROM cards WHERE id = :id")
    LiveData<Card> getCardById(long id);
    
    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY lastReviewDate ASC")
    LiveData<List<Card>> getCardsForReview(long deckId);
} 