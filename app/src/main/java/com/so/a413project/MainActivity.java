package com.so.a413project;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import com.nex3z.fingerpaintview.FingerPaintView;

public class MainActivity extends AppCompatActivity {

    private FingerPaintView fingerPaintView;
    private DigitReader digitReader;
    private TextView digitDisplay;
    private Button clearButton;
    private Button detectButton;
    private Button newGameButton;  // New button for starting a new game

    private int correctGuesses = 0;
    private int incorrectGuesses = 0;
    private final int MAX_GUESSES = 10;
    private boolean gameEnded = false;  // Flag to check if the game has ended
    private int totalGuesses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //the views
        fingerPaintView = findViewById(R.id.fingerpaint_view);
        digitDisplay = findViewById(R.id.display);
        clearButton = findViewById(R.id.clear_button);
        detectButton = findViewById(R.id.detect_button);
        newGameButton = findViewById(R.id.new_game_button);  // Initialize new game button

        // Instantiating the DigitReader class
        digitReader = new DigitReader(this);

        // Clear board button logic
        clearButton.setOnClickListener(v -> fingerPaintView.clear());

        // Detect digit button logic
        detectButton.setOnClickListener(v -> detectDigit());

        // New game button logic
        if (newGameButton != null) {  // Null check before setting OnClickListener
            newGameButton.setOnClickListener(v -> startNewGame());  // Initialize new game button
        } else {
            Log.e("MainActivity", "New Game Button was not initialized.");
        }

    }

    private void detectDigit() {
        if (gameEnded) {
            Toast.makeText(MainActivity.this, "Game Over! You can start a new game.", Toast.LENGTH_SHORT).show();
            return;  // Prevent digit detection if the game is over
        }

        // bitmap of the drawn image
        Bitmap drawnBitmap = fingerPaintView.exportToBitmap();
        int detectedDigit = digitReader.detectDigit(drawnBitmap);

        totalGuesses++;  // Increment total guesses
        digitDisplay.setText(String.valueOf(detectedDigit));

        // Checking if the guess is correct or incorrect
        if (detectedDigit == expectedDigit()) {
            correctGuesses++;  // Increment correct guesses
            Toast.makeText(MainActivity.this, "Correct Guess!", Toast.LENGTH_SHORT).show();
        } else {
            incorrectGuesses++;  // Increment incorrect guesses
            Toast.makeText(MainActivity.this, "Incorrect Guess. Try Again!", Toast.LENGTH_SHORT).show();
        }

        // Checking if the game is over
        checkGameOver();
    }

    private int expectedDigit() {
        return (int) (Math.random() * 10);  // used mathrandom to generate a random digit between 0 and 9
    }

    private void checkGameOver() {
        // heres the actual game logic, also used toasts


        //logic for winning
        if (correctGuesses >= MAX_GUESSES) {
            Toast.makeText(MainActivity.this, "You Win! 10 Correct Guesses!", Toast.LENGTH_LONG).show();
            gameEnded = true;  // Marking the game as ended
            GoingToGameOver("You Win!");  // this takes it to GameOverActivity with the win result
        }
        // logic for losing
        else if (incorrectGuesses >= MAX_GUESSES) {
            Toast.makeText(MainActivity.this, "You Lose! 10 Incorrect Guesses!", Toast.LENGTH_LONG).show();
            gameEnded = true;  // Marking the game as ended
            GoingToGameOver("You Lose!");  //  GameOverActivity with the lose result
        }
    }

    // Navigate to GameOverActivity and pass the result (win/lose)
    private void GoingToGameOver(String result) {
        Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
        intent.putExtra("result", result);  // Pass the result as an extra
        startActivity(intent);  // Start GameOverActivity
    }

    // Start a new game by resetting counters and clearing the FingerPaintView
    private void startNewGame() {
        // Log to track the state
        Log.d("MainActivity", "New game started. Game state reset.");

        // Reset all game counters
        correctGuesses = 0;
        incorrectGuesses = 0;
        totalGuesses = 0;
        gameEnded = false;  // Mark game as not ended

        // Reset the display (clear the digit display)
        digitDisplay.setText("");

        // Clear the FingerPaintView
        fingerPaintView.clear();

        // Show a toast message
        Toast.makeText(MainActivity.this, "New Game Started! Draw a digit.", Toast.LENGTH_SHORT).show();
    }
}
