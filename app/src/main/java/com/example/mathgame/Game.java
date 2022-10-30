package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mathgame.helper.Validation;

import java.util.Locale;
import java.util.Random;

public class Game extends AppCompatActivity {
    private static final long START_TIMER_IN_MILLIS = 20_000;
    private long timeLeftInMillis = START_TIMER_IN_MILLIS;
    private Boolean isTimerRunning;

    private TextView score;
    private TextView life;
    private TextView time;
    private TextView question;
    private EditText answer;
    private Button ok;
    private Button nextQuest;

    private int correctAnswer;
    private int userScore = 0;
    private int userLife = 3;

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        loadWidgets();

        gameContinue();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation validation = Validation.validateNumber(answer.getText().toString());

                if(!validation.isCorrect()) {
                    setValidationError(validation.getValue());
                    return;
                }

                if(userLife <= 0) {
                    endGame();
                    return;
                }

                checkAnswer(validation.getValue());
            }
        });

        nextQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableWidget();
                resetTimer();

                if(userLife <= 0) {
                    endGame();
                    return;
                }

                gameContinue();
            }
        });
    }

    private void loadWidgets() {
        score = findViewById(R.id.textViewScore);
        life = findViewById(R.id.textViewLife);
        time = findViewById(R.id.textViewTime);
        question = findViewById(R.id.textViewQuestion);
        answer = findViewById(R.id.editTextAnswer);
        ok = findViewById((R.id.buttonOk));
        nextQuest = findViewById((R.id.buttonNextQuest));
    }

    private void gameContinue() {
        Random random = new Random();
        int number = random.nextInt(10);
        int secondNumber = random.nextInt(10);
        correctAnswer = number + secondNumber;

        question.setText(String.format("%s + %s", number, secondNumber));
        resetHint();
        startTimer();
    }

    private void endGame() {
        Toast.makeText(getApplicationContext(), R.string.game_over, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Game.this, Result.class);
        intent.putExtra("score", userScore);
        startActivity(intent);
        finish();
    }

    private void startTimer() {
        if(timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                pauseTimer();
                resetTimer();
                updateTimer();
                updateUserLife();
                disableWidget();
                question.setText(R.string.times_up);
            }
        }.start();

        isTimerRunning = true;
    }

    private void checkAnswer(String answerText) {
        int userAnswer = Integer.parseInt(answerText);
        pauseTimer();

        if(userAnswer != correctAnswer) {
            updateUserLife();
            question.setText(R.string.incorrect_answer);
            return;
        }

        updateUserScore();
        disableWidget();
        resetHint();

        question.setText(R.string.correct_answer);
    }

    private void updateTimer() {
        int second = (int)(timeLeftInMillis / 1000) % 60;
        String timeLeft =  String.format(Locale.getDefault(), "%02d", second);
        time.setText(timeLeft);
    }

    private void pauseTimer() {
        timer.cancel();
        isTimerRunning = false;
    }

    private void resetTimer() {
        timeLeftInMillis = START_TIMER_IN_MILLIS;
        updateTimer();
    }

    private void setValidationError(String error) {
        answer.setText("");
        answer.setHint(error);
        answer.setHintTextColor(Color.RED);
    }

    private void updateUserLife() {
        userLife = userLife - 1;
        life.setText(String.valueOf(userLife));

        if(userLife <= 0) {
            endGame();
        }
    }

    private void updateUserScore() {
        userScore = userScore + 10;
        score.setText(String.valueOf(userScore));
    }

    private void disableWidget() {
        answer.setEnabled(false);
        ok.setEnabled(false);
    }

    private void enableWidget() {
        answer.setText("");
        answer.setEnabled(true);
        ok.setEnabled(true);
    }

    private void resetHint() {
        answer.setHint(R.string.please_write_an_answer);
        answer.setHintTextColor(Color.GRAY);
    }
}