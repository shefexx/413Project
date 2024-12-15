package com.so.a413project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    private TextView resultTextView;
    private Button newGameButton;  // Button to start a new game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        resultTextView = findViewById(R.id.result_text);  // Find the TextView with ID result_text
        newGameButton = findViewById(R.id.new_game_button);  // Find the Button with ID new_game_button

        // passing the data from MainActivity
        String result = getIntent().getStringExtra("result");  // Retrieve the "result" string passed via Intent

        // will show "You Win!" or "You Lose!")
        resultTextView.setText(result);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting MainActivity again to restart the game
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);  // Create a new Intent
                startActivity(intent);  // Start MainActivity
                finish();  // Closing the current GameOverActivity so the user cannot go back to it
            }
        });
    }
}


