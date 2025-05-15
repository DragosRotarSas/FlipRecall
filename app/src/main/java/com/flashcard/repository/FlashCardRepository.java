package com.flashcard.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.flashcard.dao.CardDao;
import com.flashcard.dao.DeckDao;
import com.flashcard.dao.FolderDao;
import com.flashcard.database.FlashCardDatabase;
import com.flashcard.model.Card;
import com.flashcard.model.Deck;
import com.flashcard.model.Folder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlashCardRepository {
    private FolderDao folderDao;
    private DeckDao deckDao;
    private CardDao cardDao;
    private ExecutorService executorService;
    
    public FlashCardRepository(Application application) {
        FlashCardDatabase database = FlashCardDatabase.getInstance(application);
        folderDao = database.folderDao();
        deckDao = database.deckDao();
        cardDao = database.cardDao();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    // Folder operations
    public void insertFolder(Folder folder) {
        executorService.execute(() -> folderDao.insert(folder));
    }
    
    public void updateFolder(Folder folder) {
        executorService.execute(() -> folderDao.update(folder));
    }
    
    public void deleteFolder(Folder folder) {
        executorService.execute(() -> folderDao.delete(folder));
    }
    
    public LiveData<List<Folder>> getFoldersByParentId(long parentId) {
        return folderDao.getFoldersByParentId(parentId);
    }
    
    public LiveData<Folder> getFolderById(long id) {
        return folderDao.getFolderById(id);
    }
    
    public LiveData<List<Folder>> getRootFolders() {
        return folderDao.getRootFolders();
    }
    
    // Deck operations
    public void insertDeck(Deck deck) {
        executorService.execute(() -> deckDao.insert(deck));
    }
    
    public void updateDeck(Deck deck) {
        executorService.execute(() -> deckDao.update(deck));
    }
    
    public void deleteDeck(Deck deck) {
        executorService.execute(() -> deckDao.delete(deck));
    }
    
    public LiveData<List<Deck>> getDecksByFolderId(long folderId) {
        return deckDao.getDecksByFolderId(folderId);
    }
    
    // Card operations
    public void insertCard(Card card) {
        executorService.execute(() -> cardDao.insert(card));
    }
    
    public void updateCard(Card card) {
        executorService.execute(() -> cardDao.update(card));
    }
    
    public void deleteCard(Card card) {
        executorService.execute(() -> cardDao.delete(card));
    }
    
    public LiveData<List<Card>> getCardsByDeckId(long deckId) {
        return cardDao.getCardsByDeckId(deckId);
    }
    
    public LiveData<List<Card>> getCardsForReview(long deckId) {
        return cardDao.getCardsForReview(deckId);
    }
} 