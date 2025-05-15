package com.flashcard.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.flashcard.R;
import com.flashcard.model.Card;
import com.flashcard.viewmodel.FlashCardViewModel;

public class CardAdapter extends ListAdapter<Card, CardAdapter.CardViewHolder> {
    private final Context context;
    private final FlashCardViewModel viewModel;

    public CardAdapter(Context context, FlashCardViewModel viewModel) {
        super(new DiffUtil.ItemCallback<Card>() {
            @Override
            public boolean areItemsTheSame(@NonNull Card oldItem, @NonNull Card newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Card oldItem, @NonNull Card newItem) {
                return oldItem.getQuestion().equals(newItem.getQuestion()) &&
                        oldItem.getAnswer().equals(newItem.getAnswer());
            }
        });
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = getItem(position);
        holder.indexText.setText("Card " + (position + 1));
        holder.questionText.setText(card.getQuestion());
        holder.answerText.setText(card.getAnswer());

        holder.itemView.setOnClickListener(v -> showCardMenu(card));
    }

    private void showCardMenu(Card card) {
        String[] options = {"Edit card", "Delete card"};
        new AlertDialog.Builder(context)
                .setTitle("Card Options")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditCardDialog(card);
                    } else {
                        showDeleteCardConfirmation(card);
                    }
                })
                .show();
    }

    private void showEditCardDialog(Card card) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_card, null);
        EditText questionInput = dialogView.findViewById(R.id.questionInput);
        EditText answerInput = dialogView.findViewById(R.id.answerInput);

        questionInput.setText(card.getQuestion());
        answerInput.setText(card.getAnswer());

        new AlertDialog.Builder(context)
                .setTitle("Edit Card")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String question = questionInput.getText().toString().trim();
                    String answer = answerInput.getText().toString().trim();
                    if (!question.isEmpty() && !answer.isEmpty()) {
                        card.setQuestion(question);
                        card.setAnswer(answer);
                        viewModel.updateCard(card);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteCardConfirmation(Card card) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Card")
                .setMessage("Are you sure you want to delete this card?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteCard(card);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView indexText;
        TextView questionText;
        TextView answerText;

        CardViewHolder(View itemView) {
            super(itemView);
            indexText = itemView.findViewById(R.id.indexText);
            questionText = itemView.findViewById(R.id.questionText);
            answerText = itemView.findViewById(R.id.answerText);
        }
    }
}