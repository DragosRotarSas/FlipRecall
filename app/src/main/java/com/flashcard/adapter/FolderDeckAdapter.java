package com.flashcard.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flashcard.MainActivity;
import com.flashcard.R;
import com.flashcard.ReviewActivity;
import com.flashcard.dialog.DeckActionsDialog;
import com.flashcard.model.Deck;
import com.flashcard.model.Folder;
import com.flashcard.viewmodel.FlashCardViewModel;

import java.util.ArrayList;
import java.util.List;

public class FolderDeckAdapter extends RecyclerView.Adapter<FolderDeckAdapter.ViewHolder> {
    private List<Folder> folders = new ArrayList<>();
    private List<Deck> decks = new ArrayList<>();
    private FlashCardViewModel viewModel;

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
        notifyDataSetChanged();
    }

    public void setViewModel(FlashCardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_folder_deck, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < folders.size()) {
            Folder folder = folders.get(position);
            holder.titleText.setText(folder.getTitle());
            holder.iconView.setImageResource(android.R.drawable.ic_menu_more);
            holder.itemView.setOnClickListener(v -> {
                if (v.getContext() instanceof MainActivity) {
                    ((MainActivity) v.getContext()).navigateToFolder(folder.getId());
                }
            });
        } else {
            Deck deck = decks.get(position - folders.size());
            holder.titleText.setText(deck.getTitle());
            holder.iconView.setImageResource(android.R.drawable.ic_menu_edit);
            holder.itemView.setOnClickListener(v -> {
                DeckActionsDialog.show(v.getContext(), deck, viewModel);
            });
        }
    }

    @Override
    public int getItemCount() {
        return folders.size() + decks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView titleText;

        ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.iconView);
            titleText = itemView.findViewById(R.id.titleText);
        }
    }
}