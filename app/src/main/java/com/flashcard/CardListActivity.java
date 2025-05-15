package com.flashcard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flashcard.adapter.CardAdapter;
import com.flashcard.model.Card;
import com.flashcard.viewmodel.FlashCardViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CardListActivity extends AppCompatActivity {
    private FlashCardViewModel viewModel;
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private long deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        deckId = getIntent().getLongExtra("deck_id", -1);
        if (deckId == -1) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(FlashCardViewModel.class);

        recyclerView = findViewById(R.id.cardRecyclerView);
        adapter = new CardAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton addCardFab = findViewById(R.id.addCardFab);
        addCardFab.setOnClickListener(v -> showAddCardDialog());

        loadCards();
    }

    private void loadCards() {
        viewModel.getCardsByDeckId(deckId).observe(this, cards -> {
            adapter.submitList(cards);
        });
    }

    private void showAddCardDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_card, null);
        EditText questionInput = dialogView.findViewById(R.id.questionInput);
        EditText answerInput = dialogView.findViewById(R.id.answerInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Card")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String question = questionInput.getText().toString().trim();
                    String answer = answerInput.getText().toString().trim();
                    if (!question.isEmpty() && !answer.isEmpty()) {
                        Card card = new Card(question, answer, deckId);
                        viewModel.insertCard(card);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}