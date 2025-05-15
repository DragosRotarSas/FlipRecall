package com.flashcard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.flashcard.model.Card;
import com.flashcard.viewmodel.FlashCardViewModel;

import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    private FlashCardViewModel viewModel;
    private TextView questionText;
    private TextView answerText;
    private Button correctButton;
    private Button incorrectButton;
    private Button nextButton;
    private ImageButton endSessionButton;

    private List<Card> cards;
    private int currentCardIndex = 0;
    private long deckId;

    private boolean isAnswerShown = false;
    private boolean isAdvancing = false;
    private boolean hasLoadedCards = false;

    // Session counters
    private int sessionCorrectCount = 0;
    private int sessionIncorrectCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        deckId = getIntent().getLongExtra("deck_id", -1);
        if (deckId == -1) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(FlashCardViewModel.class);

        initializeViews();
        setupClickListeners();
        loadCards();
    }

    private void initializeViews() {
        questionText     = findViewById(R.id.questionText);
        answerText       = findViewById(R.id.answerText);
        correctButton    = findViewById(R.id.correctButton);
        incorrectButton  = findViewById(R.id.incorrectButton);
        nextButton       = findViewById(R.id.nextButton);
        endSessionButton = findViewById(R.id.endSessionButton);
    }

    private void setupClickListeners() {
        correctButton.setOnClickListener(v -> handleAnswer(true));
        incorrectButton.setOnClickListener(v -> handleAnswer(false));
        nextButton.setOnClickListener(v -> advance());
        endSessionButton.setOnClickListener(v -> finish());
    }

    private void loadCards() {
        viewModel.getCardsForReview(deckId).observe(this, cardList -> {
            if (cardList != null && !cardList.isEmpty()) {
                if (!hasLoadedCards) {
                    cards = cardList;
                    hasLoadedCards = true;
                    showCurrentCard();
                }
            } else {
                showAddCardDialog();
            }
        });
    }

    private void handleAnswer(boolean isCorrect) {
        if (isAnswerShown || isAdvancing) return;

        Card card = cards.get(currentCardIndex);
        if (isCorrect) {
            card.incrementCorrectCount();
            sessionCorrectCount++;
        } else {
            card.incrementIncorrectCount();
            sessionIncorrectCount++;
        }
        viewModel.updateCard(card);

        isAnswerShown = true;
        answerText.setVisibility(View.VISIBLE);
        correctButton.setVisibility(View.GONE);
        incorrectButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
    }

    private void advance() {
        if (isAdvancing) return;
        isAdvancing = true;

        currentCardIndex++;
        showCurrentCard();

        isAdvancing = false;
    }

    private void showCurrentCard() {
        if (cards == null || currentCardIndex >= cards.size()) {
            showReviewCompleteDialog();
            return;
        }

        Card card = cards.get(currentCardIndex);
        questionText.setText(card.getQuestion());
        answerText.setText(card.getAnswer());

        isAnswerShown = false;
        answerText.setVisibility(View.GONE);
        correctButton.setVisibility(View.VISIBLE);
        incorrectButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);
    }

    private void showReviewCompleteDialog() {
        String message = "Review Complete.\n" +
                "You have reviewed all cards in this deck.\n\n" +
                sessionCorrectCount + " answers were correct.\n" +
                sessionIncorrectCount + " answers were incorrect.";
        new AlertDialog.Builder(this)
                .setTitle("Review Complete")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showAddCardDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_card, null);
        EditText questionInput = dialogView.findViewById(R.id.questionInput);
        EditText answerInput   = dialogView.findViewById(R.id.answerInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Card")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String question = questionInput.getText().toString().trim();
                    String answer   = answerInput.getText().toString().trim();
                    if (!question.isEmpty() && !answer.isEmpty()) {
                        Card card = new Card(question, answer, deckId);
                        viewModel.insertCard(card);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Reset for clean state on recreation
        isAdvancing = false;
        hasLoadedCards = false;
        sessionCorrectCount = 0;
        sessionIncorrectCount = 0;
    }
}
