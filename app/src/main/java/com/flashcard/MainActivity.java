package com.flashcard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flashcard.adapter.FolderDeckAdapter;
import com.flashcard.model.Deck;
import com.flashcard.model.Folder;
import com.flashcard.viewmodel.FlashCardViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FlashCardViewModel viewModel;
    private FolderDeckAdapter adapter;
    private long currentFolderId = 0; // root

    private TextView folderTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(FlashCardViewModel.class);

        folderTitleText = findViewById(R.id.folderTitleText);
        RecyclerView rv  = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.addButton);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FolderDeckAdapter();
        adapter.setViewModel(viewModel);
        rv.setAdapter(adapter);

        fab.setOnClickListener(v -> showAddDialog());

        loadCurrentFolder();
    }

    private void loadCurrentFolder() {
        updateFolderTitle();

        if (currentFolderId == 0) {
            viewModel.getRootFolders().observe(this, folders -> {
                adapter.setFolders(folders);
                viewModel.getDecksByFolderId(0)
                        .observe(this, decks -> adapter.setDecks(decks));
            });
        } else {
            viewModel.getFoldersByParentId(currentFolderId).observe(this, folders -> {
                adapter.setFolders(folders);
                viewModel.getDecksByFolderId(currentFolderId)
                        .observe(this, decks -> adapter.setDecks(decks));
            });
        }
    }

    private void updateFolderTitle() {
        if (currentFolderId == 0) {
            folderTitleText.setText("Home");
        } else {
            viewModel.getFolderById(currentFolderId).observe(this, new Observer<Folder>() {
                @Override
                public void onChanged(Folder folder) {
                    // remove this one-time observer
                    viewModel.getFolderById(currentFolderId).removeObserver(this);
                    if (folder != null) {
                        folderTitleText.setText(folder.getTitle());
                    }
                }
            });
        }
    }

    private void showAddDialog() {
        List<String> options = new ArrayList<>();
        options.add("Add Folder");
        if (currentFolderId != 0) {
            options.add("Add Deck");
            options.add("Rename Folder");
            options.add("Delete Folder");
        }
        String[] items = options.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Choose action")
                .setItems(items, (dialog, which) -> {
                    switch (items[which]) {
                        case "Add Folder":    showAddFolderDialog();    break;
                        case "Add Deck":      showAddDeckDialog();      break;
                        case "Rename Folder": showRenameFolderDialog(); break;
                        case "Delete Folder": showDeleteFolderConfirmation(); break;
                    }
                })
                .show();
    }

    private void showAddFolderDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_folder, null);
        EditText input = v.findViewById(R.id.folderNameInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Folder")
                .setView(v)
                .setPositiveButton("Add", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        viewModel.insertFolder(new Folder(name, currentFolderId));
                        loadCurrentFolder();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddDeckDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_deck, null);
        EditText input = v.findViewById(R.id.deckNameInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Deck")
                .setView(v)
                .setPositiveButton("Add", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        viewModel.insertDeck(new Deck(name, currentFolderId));
                        loadCurrentFolder();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRenameFolderDialog() {
        if (currentFolderId == 0) return;

        // one-time observer on this folder LiveData
        LiveData<Folder> live = viewModel.getFolderById(currentFolderId);
        Observer<Folder> obs = new Observer<Folder>() {
            @Override
            public void onChanged(Folder folder) {
                live.removeObserver(this);
                if (folder == null) return;

                View v = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.dialog_add_folder, null);
                EditText input = v.findViewById(R.id.folderNameInput);
                input.setText(folder.getTitle());

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Rename Folder")
                        .setView(v)
                        .setPositiveButton("Rename", (d, w) -> {
                            String newName = input.getText().toString().trim();
                            if (!newName.isEmpty()) {
                                folder.setTitle(newName);
                                viewModel.updateFolder(folder);
                                loadCurrentFolder();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        };
        live.observe(this, obs);
    }

    private void showDeleteFolderConfirmation() {
        if (currentFolderId == 0) return;

        viewModel.getFolderById(currentFolderId).observe(this, new Observer<Folder>() {
            @Override
            public void onChanged(Folder folder) {
                viewModel.getFolderById(currentFolderId).removeObserver(this);
                if (folder == null) return;
                long parentId = folder.getParentFolderId();

                viewModel.getDecksByFolderId(currentFolderId)
                        .observe(MainActivity.this, new Observer<List<Deck>>() {
                            @Override
                            public void onChanged(List<Deck> decks) {
                                viewModel.getDecksByFolderId(currentFolderId).removeObserver(this);
                                String msg = decks.isEmpty()
                                        ? "Are you sure you want to delete this folder?"
                                        : "This folder contains decks. Delete anyway?";

                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Delete Folder")
                                        .setMessage(msg)
                                        .setPositiveButton("Delete", (d, w) -> {
                                            // navigate up immediately
                                            currentFolderId = parentId;
                                            loadCurrentFolder();
                                            // deep-delete in background
                                            viewModel.deleteFolderRecursively(folder);
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        });
            }
        });
    }

    public void navigateToFolder(long folderId) {
        currentFolderId = folderId;
        loadCurrentFolder();
    }

    @Override
    public void onBackPressed() {
        if (currentFolderId != 0) {
            viewModel.getFolderById(currentFolderId).observe(this, new Observer<Folder>() {
                @Override
                public void onChanged(Folder folder) {
                    viewModel.getFolderById(currentFolderId).removeObserver(this);
                    if (folder != null) {
                        currentFolderId = folder.getParentFolderId();
                        loadCurrentFolder();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }
}
