package com.example.geoquiz_ranises;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView mQuestionTextView;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;

    private List<Question> mQuestionBank = new ArrayList<>();
    private int mCurrentIndex = 0;
    private boolean[] mQuestionAnswered;
    private boolean[] mIsCheater;
    private int mCorrectAnswerCount;
    private int mAnsweredQuestionCount;

    private static final String KEY_INDEX = "key_index";
    private static final String KEY_ANSWERED = "key_answered";
    private static final String KEY_CHEATER = "key_cheater";
    private static final String KEY_CORRECT_COUNT = "key_correct_count";
    private static final String KEY_ANSWERED_COUNT = "key_answered_count";
    private static final int REQUEST_CODE_CHEAT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize question bank
        initializeQuestions();

        // Get references to UI components
        mQuestionTextView = findViewById(R.id.question_text_view);
        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mCheatButton = findViewById(R.id.cheat_button);
        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);

        // Set up question text click listener
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size();
                updateQuestion();
            }
        });

        // Set up button click listeners
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank.get(mCurrentIndex).isAnswerTrue();
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size();
                updateQuestion();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.size()) % mQuestionBank.size();
                updateQuestion();
            }
        });

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mQuestionAnswered = savedInstanceState.getBooleanArray(KEY_ANSWERED);
            mIsCheater = savedInstanceState.getBooleanArray(KEY_CHEATER);
            mCorrectAnswerCount = savedInstanceState.getInt(KEY_CORRECT_COUNT, 0);
            mAnsweredQuestionCount = savedInstanceState.getInt(KEY_ANSWERED_COUNT, 0);
            if (mQuestionAnswered == null || mQuestionAnswered.length != mQuestionBank.size()) {
                mQuestionAnswered = new boolean[mQuestionBank.size()];
                mIsCheater = new boolean[mQuestionBank.size()];
                mCorrectAnswerCount = 0;
                mAnsweredQuestionCount = 0;
            }
        }

        // Display the current question
        updateQuestion();
    }

    private void initializeQuestions() {
        mQuestionBank.add(new Question(R.string.question_australia_capital, true));
        mQuestionBank.add(new Question(R.string.question_pacific_larger, true));
        mQuestionBank.add(new Question(R.string.question_suez_connects, true));
        mQuestionBank.add(new Question(R.string.question_machu_picchu, false));
        mQuestionBank.add(new Question(R.string.question_nile_longest, false));
        mQuestionBank.add(new Question(R.string.question_tokyo_capital, true));
        mQuestionBank.add(new Question(R.string.question_sahara_largest, true));
        mQuestionBank.add(new Question(R.string.question_everest_andes, false));
        mQuestionBank.add(new Question(R.string.question_venus_closest, false));
        mQuestionBank.add(new Question(R.string.question_barrier_reef, true));
        if (mQuestionAnswered == null) {
            mQuestionAnswered = new boolean[mQuestionBank.size()];
        }
        if (mIsCheater == null) {
            mIsCheater = new boolean[mQuestionBank.size()];
        }
    }

    private void updateQuestion() {
        int question = mQuestionBank.get(mCurrentIndex).getTextResId();
        String questionText = getString(question);
        String numberedQuestion = String.format(Locale.getDefault(), "%d. %s", mCurrentIndex + 1, questionText);
        mQuestionTextView.setText(numberedQuestion);
        updateButtonStates();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBooleanArray(KEY_ANSWERED, mQuestionAnswered);
        outState.putBooleanArray(KEY_CHEATER, mIsCheater);
        outState.putInt(KEY_CORRECT_COUNT, mCorrectAnswerCount);
        outState.putInt(KEY_ANSWERED_COUNT, mAnsweredQuestionCount);
    }

    private void setAnswerButtonsEnabled(boolean enabled) {
        mTrueButton.setEnabled(enabled);
        mFalseButton.setEnabled(enabled);
        float alpha = enabled ? 1f : 0.4f;
        mTrueButton.setAlpha(alpha);
        mFalseButton.setAlpha(alpha);
    }

    private void updateButtonStates() {
        boolean answered = mQuestionAnswered != null && mQuestionAnswered[mCurrentIndex];
        setAnswerButtonsEnabled(!answered);
        mNextButton.setEnabled(true);
        mPrevButton.setEnabled(true);
    }

    private void checkAnswer(boolean userPressedTrue) {
        if (mQuestionAnswered != null && mQuestionAnswered[mCurrentIndex]) {
            return;
        }
        
        boolean answerIsTrue = mQuestionBank.get(mCurrentIndex).isAnswerTrue();
        int messageResId;
        
        if (userPressedTrue == answerIsTrue) {
            android.util.Log.d("CHEAT_DEBUG", "Question " + mCurrentIndex + " mIsCheater: " + mIsCheater[mCurrentIndex]);
            mCorrectAnswerCount++;
            if (mIsCheater[mCurrentIndex]) {
                Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.correct_toast, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.incorrect_toast, Toast.LENGTH_SHORT).show();
        }
        
        mQuestionAnswered[mCurrentIndex] = true;
        mAnsweredQuestionCount++;
        updateButtonStates();
        
        if (mAnsweredQuestionCount == mQuestionBank.size()) {
            int cheatedCount = 0;
            for (boolean cheated : mIsCheater) {
                if (cheated) cheatedCount++;
            }
            String scoreText = getString(R.string.score_toast, mCorrectAnswerCount, mQuestionBank.size()) + 
                              " (Cheated: " + cheatedCount + ")";
            Toast.makeText(this, scoreText, Toast.LENGTH_LONG).show();
        }
    }

    private void showScore() {
        String scoreMessage = getString(R.string.score_toast, mCorrectAnswerCount, mQuestionBank.size());
        Toast.makeText(this, scoreMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            boolean didCheat = CheatActivity.wasAnswerShown(data);
            mIsCheater[mCurrentIndex] = didCheat;
            android.util.Log.d("CHEAT_DEBUG", "Question " + mCurrentIndex + " cheated: " + didCheat);
        }
    }
}