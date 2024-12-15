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
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import com.so.a413project.DigitReader;
import com.nex3z.fingerpaintview.FingerPaintView;

public class MainActivity extends AppCompatActivity {

    //gameplay variables
    private FingerPaintView fingerPaintView;
    private TextView digitDisplay, countersText, correctGuessesText, incorrectGuessesText;
    private Button clearButton, detectButton;
    private Button correctButton, incorrectButton;
    private DigitReader digitReader;
    private int correctGuesses = 0;
    private int incorrectGuesses = 0;
    private final int MAX_GUESSES = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing our views from xml
        fingerPaintView = findViewById(R.id.finger_paint_view);
        digitDisplay = findViewById(R.id.target_digit_text);
        clearButton = findViewById(R.id.clear_button);
        detectButton = findViewById(R.id.detect_button);
        correctButton = findViewById(R.id.correct_button);
        incorrectButton = findViewById(R.id.incorrect_button);
        correctGuessesText = findViewById(R.id.correct_guesses_text);  // Correct guesses counter
        incorrectGuessesText = findViewById(R.id.incorrect_guesses_text);  // Initialize incorrect guesses counter

        // Initializing the digit reader
        digitReader = new DigitReader(this);

        // starts the game with a random digit each time suing Math.Random 0-9
        randomDigit();

        // logic for the clear button
        clearButton.setOnClickListener(v -> {
            fingerPaintView.clear();
            digitDisplay.setText(""); // Clears the fingerpaint area
            randomDigit(); // Generates a new random digit after clearing
            userResponseButtons(); //this hides the incorrect/correct buttons
        });

        // logic for the detect button
        detectButton.setOnClickListener(v -> {
            Bitmap drawnBitmap = fingerPaintView.exportToBitmap();

            //if the user tries and press detect before drawing something a toast will pop up with instructions
            if (drawnBitmap == null) {
                Toast.makeText(MainActivity.this, "You have to draw before you detect!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Predict digit and display
            int detectedDigit = digitReader.detectDigit(drawnBitmap);
            digitDisplay.setText("Detected: " + detectedDigit);

            // Show Correct/Incorrect buttons
            correctButton.setVisibility(View.VISIBLE);
            incorrectButton.setVisibility(View.VISIBLE);
        });

        // logic for correct button
        correctButton.setOnClickListener(v -> {
            correctGuesses++; //counts the number of times the button is hit
            Toast.makeText(MainActivity.this, "Correct Guess!", Toast.LENGTH_SHORT).show();
            updateCounters();
            isGameOver();
            randomDigit(); // generates a new digit after each guess
            userResponseButtons();
        });

        // logic for the incorrect button
        incorrectButton.setOnClickListener(v -> {
            incorrectGuesses++; //counts the number of times the button is hit
            Toast.makeText(MainActivity.this, "Incorrect Guess. Try Again!", Toast.LENGTH_SHORT).show();
            updateCounters();
            isGameOver();
            userResponseButtons();
        });
    }

    // displays the counters while the uer is playing
    private void updateCounters() {
        correctGuessesText.setText("Correct: " + correctGuesses);
        incorrectGuessesText.setText("Incorrect: " + incorrectGuesses);
    }


    private void GoToGameOver() {
        // intent to go to the game over activity
        Intent intent = new Intent(MainActivity.this, GameOverActivity.class);

        // Passing the correct and incorrect guesses to the game over activity using putExtra
        intent.putExtra("correct_guesses", correctGuesses);
        intent.putExtra("incorrect_guesses", incorrectGuesses);

        // Starting the activity
        startActivity(intent);
        finish();
    }


    // Method to check if the game is over
    private void isGameOver() {
        if (correctGuesses >= MAX_GUESSES) {
            GoToGameOver();  // Call navigate with scores when the game ends
        } else if (incorrectGuesses >= MAX_GUESSES) {
            GoToGameOver();  // Call navigate with scores when the game ends
        }
    }

    // Generates a random target digit
    private void randomDigit() {
        int randomDigit = (int) (Math.random() * 10); //between 0-9
        digitDisplay.setText("Target Digit: " + randomDigit);
    }

    // Hides the Correct/Incorrect buttons
    private void userResponseButtons() {
        correctButton.setVisibility(View.GONE);
        incorrectButton.setVisibility(View.GONE);
    }
}