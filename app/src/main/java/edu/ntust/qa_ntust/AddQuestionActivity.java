package edu.ntust.qa_ntust;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import edu.ntust.qa_ntust.data.QuestionContract;


public class AddQuestionActivity extends AppCompatActivity {
    private int mDifficulty;
    private String mAnswer;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_qestion);

        ((RadioButton) findViewById(R.id.radioButtonAnswerA)).setChecked(true);
        mAnswer = "A";

        ((RadioButton) findViewById(R.id.radioButtonDifficultyHard)).setChecked(true);
        mDifficulty = 3;


        Button submit = findViewById(R.id.buttonSubmit);
        submit.setText(R.string.create_button_text);
        submit.setOnClickListener(submitOnClick);
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
            contentValues.put(QuestionContract.QuestionEntry.COLUMN_COUNT, 0);
            Uri uri = getContentResolver().insert(QuestionContract.QuestionEntry.CONTENT_URI, contentValues);

            if (uri != null) {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            }

            finish();
        }
    };


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
}
