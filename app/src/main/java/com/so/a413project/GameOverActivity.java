package com.so.a413project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    //these are our variables for displaying the user's final score
    // and for displaying the play agin and exit buttons
    private TextView finalScoreText;
    private Button playAgainButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // making sure that the textviews in the xml correspond to our buttons
        finalScoreText = findViewById(R.id.final_score_text);
        playAgainButton = findViewById(R.id.play_again_button);
        exitButton = findViewById(R.id.exit_button);

        // using intents to pass the data in the MainActivty to this activity
        //when the game over activity runs, it'll display whatever the user's score
        //was in the main activity as their final score
        int correctGuesses = getIntent().getIntExtra("correct_guesses", 0);
        int incorrectGuesses = getIntent().getIntExtra("incorrect_guesses", 0);

        // textview for the final score displaying
        finalScoreText.setText("Final Score: Correct " + correctGuesses + " Incorrect " + incorrectGuesses);

        // using a listener so that when the play again button is pressed it
        //returns to the Main Activity again
        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // this closes the game over activity
        });

        // using a listener so that the app closes when the user presses the button
        exitButton.setOnClickListener(v -> {
            finish(); // this closes the game over activity
            System.exit(0); // this forces the app to close
        });
    }
}

