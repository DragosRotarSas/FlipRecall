package com.flashcard.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.flashcard.dao.CardDao;
import com.flashcard.dao.DeckDao;
import com.flashcard.dao.FolderDao;
import com.flashcard.model.Card;
import com.flashcard.model.Deck;
import com.flashcard.model.Folder;

@Database(entities = {Folder.class, Deck.class, Card.class}, version = 1)
public abstract class FlashCardDatabase extends RoomDatabase {
    private static FlashCardDatabase instance;
    
    public abstract FolderDao folderDao();
    public abstract DeckDao deckDao();
    public abstract CardDao cardDao();
    
    public static synchronized FlashCardDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                FlashCardDatabase.class,
                "flashcard_database"
            ).build();
        }
        return instance;
    }
} 