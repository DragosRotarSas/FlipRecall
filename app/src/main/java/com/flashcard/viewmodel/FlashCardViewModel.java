package com.flashcard.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.flashcard.model.Card;
import com.flashcard.model.Deck;
import com.flashcard.model.Folder;
import com.flashcard.repository.FlashCardRepository;

import java.util.List;

public class FlashCardViewModel extends AndroidViewModel {
    private FlashCardRepository repository;
    
    public FlashCardViewModel(Application application) {
        super(application);
        repository = new FlashCardRepository(application);
    }
    
    // Folder operations
    public void insertFolder(Folder folder) {
        repository.insertFolder(folder);
    }
    
    public void updateFolder(Folder folder) {
        repository.updateFolder(folder);
    }
    
    public void deleteFolder(Folder folder) {
        repository.deleteFolder(folder);
    }
    
    public LiveData<List<Folder>> getFoldersByParentId(long parentId) {
        return repository.getFoldersByParentId(parentId);
    }
    
    public LiveData<Folder> getFolderById(long id) {
        return repository.getFolderById(id);
    }
    
    public LiveData<List<Folder>> getRootFolders() {
        return repository.getRootFolders();
    }
    
    // Deck operations
    public void insertDeck(Deck deck) {
        repository.insertDeck(deck);
    }
    
    public void updateDeck(Deck deck) {
        repository.updateDeck(deck);
    }
    
    public void deleteDeck(Deck deck) {
        repository.deleteDeck(deck);
    }
    
    public LiveData<List<Deck>> getDecksByFolderId(long folderId) {
        return repository.getDecksByFolderId(folderId);
    }
    
    // Card operations
    public void insertCard(Card card) {
        repository.insertCard(card);
    }
    
    public void updateCard(Card card) {
        repository.updateCard(card);
    }
    
    public void deleteCard(Card card) {
        repository.deleteCard(card);
    }
    
    public LiveData<List<Card>> getCardsByDeckId(long deckId) {
        return repository.getCardsByDeckId(deckId);
    }
    
    public LiveData<List<Card>> getCardsForReview(long deckId) {
        return repository.getCardsForReview(deckId);
    }

    public void deleteFolderRecursively(Folder folder) {
        // run on background thread
        new Thread(() -> deleteRecursively(folder.getId())).start();
    }

    private void deleteRecursively(long folderId) {
        // 1) delete all decks in this folder
        List<Deck> decks = repository.getDecksByFolderIdSync(folderId);
        for (Deck d : decks) {
            repository.deleteDeck(d);
        }

        // 2) recurse into subfolders
        List<Folder> children = repository.getFoldersByParentIdSync(folderId);
        for (Folder child : children) {
            deleteRecursively(child.getId());
        }

        // 3) finally delete this folder
        repository.deleteFolderById(folderId);
    }
} 