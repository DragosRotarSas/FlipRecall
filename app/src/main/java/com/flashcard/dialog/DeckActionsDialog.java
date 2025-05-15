package com.flashcard.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.flashcard.CardListActivity;
import com.flashcard.R;
import com.flashcard.ReviewActivity;
import com.flashcard.model.Card;
import com.flashcard.model.Deck;
import com.flashcard.viewmodel.FlashCardViewModel;

public class DeckActionsDialog {
    public static void show(Context context, Deck deck, FlashCardViewModel viewModel) {
        String[] options = {
                "View and modify cards",
                "Start review",
                "Delete deck"
        };

        new AlertDialog.Builder(context)
                .setTitle(deck.getTitle())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // View and modify cards
                            Intent viewIntent = new Intent(context, CardListActivity.class);
                            viewIntent.putExtra("deck_id", deck.getId());
                            context.startActivity(viewIntent);
                            break;
                        case 1: // Start review
                            Intent reviewIntent = new Intent(context, ReviewActivity.class);
                            reviewIntent.putExtra("deck_id", deck.getId());
                            context.startActivity(reviewIntent);
                            break;
                        case 2: // Delete deck
                            showDeleteDeckConfirmation(context, deck, viewModel);
                            break;
                    }
                })
                .show();
    }

    private static void showDeleteDeckConfirmation(Context context, Deck deck, FlashCardViewModel viewModel) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Deck")
                .setMessage("Are you sure you want to delete this deck?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the deck using the repository
                    viewModel.deleteDeck(deck);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}