package edu.ntust.qa_ntust;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import edu.ntust.qa_ntust.data.QuestionContract;

public class ReplyQuestionActivity extends BasicActivity {
    private String correct_ans;
    private String mAnswer;
    private String id;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_qestion);

        TextView question = findViewById(R.id.textViewQuestionContent);
        TextView choice_A = findViewById(R.id.textViewChoiceA);
        TextView choice_B = findViewById(R.id.textViewChoiceB);
        TextView choice_C = findViewById(R.id.textViewChoiceC);
        TextView choice_D = findViewById(R.id.textViewChoiceD);
        TextView difficulty = findViewById(R.id.textViewDifficulty);
        ((RadioButton) findViewById(R.id.radioButtonAnswerA)).setChecked(true);
        mAnswer = "A";
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        question.setText(Objects.requireNonNull(bundle).getString("content"));
        choice_A.setText(bundle.getString("choice_A"));
        choice_B.setText(bundle.getString("choice_B"));
        choice_C.setText(bundle.getString("choice_C"));
        choice_D.setText(bundle.getString("choice_D"));
        difficulty.setText(bundle.getString("difficulty"));
        correct_ans = bundle.getString("answer");
        id = bundle.getString("_id");
        count = Integer.valueOf(Objects.requireNonNull(bundle.getString("count")));

        Button submit = findViewById(R.id.buttonSubmit);
        submit.setText(R.string.submit_button_text);
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

    public void onClickReplyQuestion(View view) {

        if (mAnswer.equals(correct_ans)) {
            Toast.makeText(getBaseContext(), "Correct", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Wrong", Toast.LENGTH_LONG).show();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionContract.QuestionEntry.COLUMN_COUNT, count + 1);

        String[] selectionArgs = {id};
        String selection = "_id" + " = ?";

        getContentResolver().update(QuestionContract.QuestionEntry.CONTENT_URI, contentValues, selection, selectionArgs);

        finish();
    }
}
