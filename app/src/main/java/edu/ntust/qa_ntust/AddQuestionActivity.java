package edu.ntust.qa_ntust;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import edu.ntust.qa_ntust.data.QuestionContract;


public class AddQuestionActivity extends AppCompatActivity {
    private int mDifficulty;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_qestion);

        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
        mDifficulty = 1;
    }


    /**
     * onClickAddQuestion is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddQuestion(View view) {
        String input = ((EditText) findViewById(R.id.editTextQuestionContent)).getText().toString();
        if (input.length() == 0) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_CONTENT, input);
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE_A, input);
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE_B, input);
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE_C, input);
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE_D, input);
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_ANSWER, "A");
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_DIFFICULTY, mDifficulty);
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_COUNT, 5);
        Uri uri = getContentResolver().insert(QuestionContract.QuestionEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        finish();
    }


    /**
     * onDifficultySelected is called whenever a priority button is clicked.
     * It changes the value of mDifficulty based on the selected button.
     */
    public void onDifficultySelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            mDifficulty = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            mDifficulty = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            mDifficulty = 3;
        }
    }
}
