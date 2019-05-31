package edu.ntust.qa_ntust;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import edu.ntust.qa_ntust.data.QuestionContract;

public class EditQuestionActivity extends AppCompatActivity {
    private Button submit;
    private EditText question;
    private EditText choice_A;
    private EditText choice_B;
    private EditText choice_C;
    private EditText choice_D;
    private RadioGroup answer;
    private RadioGroup difficulty;
    private int mDifficulty;
    private String mAnswer;
    private  String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_qestion);

        question = (EditText) findViewById(R.id.editTextQuestionContent);
        choice_A = (EditText) findViewById(R.id.editTextChoiceA);
        choice_B = (EditText) findViewById(R.id.editTextChoiceB);
        choice_C = (EditText) findViewById(R.id.editTextChoiceC);
        choice_D = (EditText) findViewById(R.id.editTextChoiceD);
        answer = (RadioGroup)findViewById(R.id.radioGroupAnswer);
        difficulty = (RadioGroup)findViewById(R.id.radioGroupDifficulty) ;
        submit = (Button)findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(submitOnClick);

        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        question.setText(bundle.getString("content"));
        choice_A.setText(bundle.getString("choice_A"));
        choice_B.setText(bundle.getString("choice_B"));
        choice_C.setText(bundle.getString("choice_C"));
        choice_D.setText(bundle.getString("choice_D"));
        ((RadioButton)answer.getChildAt(bundle.getString("answer").charAt(0)-'A')).setChecked(true);
        ((RadioButton)difficulty.getChildAt(new Integer(bundle.getString("difficulty")))).setChecked(true);
        mDifficulty=new Integer(bundle.getString("difficulty"));
        mAnswer = bundle.getString("answer");
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
    private View.OnClickListener submitOnClick = new View.OnClickListener(){
        public void onClick(View v){
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
            Uri uri = QuestionContract.QuestionEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(id).build();

            String[] selectionArgs = { id.toString() };
            String selection = "_id" + " = ?";

            getContentResolver().update(QuestionContract.QuestionEntry.CONTENT_URI, contentValues,selection,selectionArgs);

            finish();
        }
    };
}
