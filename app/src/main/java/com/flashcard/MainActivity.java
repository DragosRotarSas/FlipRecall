package com.flashcard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flashcard.adapter.FolderDeckAdapter;
import com.flashcard.model.Deck;
import com.flashcard.model.Folder;
import com.flashcard.viewmodel.FlashCardViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private FlashCardViewModel viewModel;
    private FolderDeckAdapter adapter;
    private long currentFolderId = 0; // 0 represents root folder

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(FlashCardViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FolderDeckAdapter();
        adapter.setViewModel(viewModel);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showAddDialog());

        loadCurrentFolder();
    }

    private void loadCurrentFolder() {
        if (currentFolderId == 0) {
            viewModel.getRootFolders().observe(this, folders -> {
                adapter.setFolders(folders);
                viewModel.getDecksByFolderId(currentFolderId).observe(this, decks -> {
                    adapter.setDecks(decks);
                });
            });
        } else {
            viewModel.getFoldersByParentId(currentFolderId).observe(this, folders -> {
                adapter.setFolders(folders);
                viewModel.getDecksByFolderId(currentFolderId).observe(this, decks -> {
                    adapter.setDecks(decks);
                });
            });
        }
    }

    private void showAddDialog() {
        // Only show Delete Folder option when not in root
        String[] options = currentFolderId == 0 ?
                new String[]{"Add Folder", "Add Deck"} :
                new String[]{"Add Folder", "Add Deck", "Delete Folder"};

        new AlertDialog.Builder(this)
                .setTitle("Choose action")
                .setItems(options, (dialog, which) -> {
                    if (currentFolderId == 0 && which >= 2) {
                        return; // Safety check
                    }
                    switch (which) {
                        case 0:
                            showAddFolderDialog();
                            break;
                        case 1:
                            showAddDeckDialog();
                            break;
                        case 2:
                            showDeleteFolderConfirmation();
                            break;
                    }
                })
                .show();
    }

    private void showAddFolderDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_folder, null);
        EditText folderNameInput = dialogView.findViewById(R.id.folderNameInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Folder")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String folderName = folderNameInput.getText().toString().trim();
                    if (!folderName.isEmpty()) {
                        Folder folder = new Folder(folderName, currentFolderId);
                        viewModel.insertFolder(folder);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddDeckDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_deck, null);
        EditText deckNameInput = dialogView.findViewById(R.id.deckNameInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Deck")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String deckName = deckNameInput.getText().toString().trim();
                    if (!deckName.isEmpty()) {
                        Deck deck = new Deck(deckName, currentFolderId);
                        viewModel.insertDeck(deck);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteFolderConfirmation() {
        if (currentFolderId == 0) return; // Safety check

        viewModel.getFolderById(currentFolderId).observe(this, folder -> {
            if (folder == null) return;

            long parentFolderId = folder.getParentFolderId();
            viewModel.getDecksByFolderId(currentFolderId).observe(this, decks -> {
                String message = decks.isEmpty() ?
                        "Are you sure you want to delete this folder?" :
                        "This folder contains decks. Are you sure you want to delete it?";

                new AlertDialog.Builder(this)
                        .setTitle("Delete Folder")
                        .setMessage(message)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // First navigate up
                            currentFolderId = parentFolderId;
                            loadCurrentFolder();

                            // Then delete the folder
                            viewModel.deleteFolder(folder);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });
    }

    public void navigateToFolder(long folderId) {
        currentFolderId = folderId;
        loadCurrentFolder();
    }

    @Override
    public void onBackPressed() {
        if (currentFolderId != 0) {
            // Get parent folder ID and navigate to it
            viewModel.getFolderById(currentFolderId).observe(this, folder -> {
                if (folder != null) {
                    currentFolderId = folder.getParentFolderId();
                    loadCurrentFolder();
                }
            });
        } else {
            super.onBackPressed();
        }
    }
} 