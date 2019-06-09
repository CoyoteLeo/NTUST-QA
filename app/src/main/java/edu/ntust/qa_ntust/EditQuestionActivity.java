package edu.ntust.qa_ntust;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Objects;

import edu.ntust.qa_ntust.data.QuestionContract;

public class EditQuestionActivity extends BasicActivity {
    private int mDifficulty;
    private String mAnswer;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_qestion);

        EditText question = findViewById(R.id.editTextQuestionContent);
        EditText choice_A = findViewById(R.id.editTextChoiceA);
        EditText choice_B = findViewById(R.id.editTextChoiceB);
        EditText choice_C = findViewById(R.id.editTextChoiceC);
        EditText choice_D = findViewById(R.id.editTextChoiceD);
        RadioGroup answer = findViewById(R.id.radioGroupAnswer);
        RadioGroup difficulty = findViewById(R.id.radioGroupDifficulty);
        Button submit = findViewById(R.id.buttonSubmit);
        submit.setText(R.string.update_button_text);
        submit.setOnClickListener(submitOnClick);

        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        question.setText(Objects.requireNonNull(bundle).getString("content"));
        choice_A.setText(bundle.getString("choice_A"));
        choice_B.setText(bundle.getString("choice_B"));
        choice_C.setText(bundle.getString("choice_C"));
        choice_D.setText(bundle.getString("choice_D"));
        mDifficulty = Integer.parseInt(bundle.getString("difficulty", "1"));
        mAnswer = bundle.getString("answer");
        ((RadioButton) answer.getChildAt(bundle.getString("answer", "A").charAt(0) - 'A')).setChecked(true);
        ((RadioButton) difficulty.getChildAt(3 - mDifficulty)).setChecked(true);
        id = bundle.getString("_id");
    }

    public void onDifficultySelected(View view) {
        if (((RadioButton) findViewById(R.id.radioButtonDifficultyHard)).isChecked()) {
            mDifficulty = 3;
        } else if (((RadioButton) findViewById(R.id.radioButtonDifficultyMedium)).isChecked()) {
            mDifficulty = 2;
        } else if (((RadioButton) findViewById(R.id.radioButtonDifficultyEasy)).isChecked()) {
            mDifficulty = 1;
        }
    }

    public void onAnswerSelected(View view) {
        if (((RadioButton) findViewById(R.id.radioButtonAnswerA)).isChecked()) {
            mAnswer = "A";
        } else if (((RadioButton) findViewById(R.id.radioButtonAnswerB)).isChecked()) {
            mAnswer = "B";
        } else if (((RadioButton) findViewById(R.id.radioButtonAnswerC)).isChecked()) {
            mAnswer = "C";
        } else if (((RadioButton) findViewById(R.id.radioButtonAnswerD)).isChecked()) {
            mAnswer = "D";
        }
    }

    private View.OnClickListener submitOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            String content = ((EditText) findViewById(R.id.editTextQuestionContent)).getText().toString();
            if (content.length() == 0) {
                return;
            }

            String answer_A = ((EditText) findViewById(R.id.editTextChoiceA)).getText().toString();
            if (answer_A.length() == 0) {
                return;
            }

            String answer_B = ((EditText) findViewById(R.id.editTextChoiceB)).getText().toString();
            if (answer_B.length() == 0) {
                return;
            }

            String answer_C = ((EditText) findViewById(R.id.editTextChoiceC)).getText().toString();
            if (answer_C.length() == 0) {
                return;
            }

            String answer_D = ((EditText) findViewById(R.id.editTextChoiceD)).getText().toString();
            if (answer_D.length() == 0) {
                return;
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(QuestionContract.QuestionEntry.COLUMN_CONTENT, content);
            contentValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE_A, answer_A);
            contentValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE_B, answer_B);
            contentValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE_C, answer_C);
            contentValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE_D, answer_D);
            contentValues.put(QuestionContract.QuestionEntry.COLUMN_ANSWER, mAnswer);
            contentValues.put(QuestionContract.QuestionEntry.COLUMN_DIFFICULTY, mDifficulty);

            String[] selectionArgs = {id};
            String selection = "_id" + " = ?";

            getContentResolver().update(QuestionContract.QuestionEntry.CONTENT_URI, contentValues, selection, selectionArgs);

            finish();
        }
    };
}
