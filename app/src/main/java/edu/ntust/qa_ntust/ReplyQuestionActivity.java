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
import android.widget.TextView;
import android.widget.Toast;

import edu.ntust.qa_ntust.data.QuestionContract;

public class ReplyQuestionActivity extends AppCompatActivity {
    private Button submit;
    private TextView question;
    private TextView choice_A;
    private TextView choice_B;
    private TextView choice_C;
    private TextView choice_D;
    private RadioGroup answer;
    private TextView difficulty;
    private  String correct_ans;
    private String mAnswer;
    private String id;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_qestion);

        question = (TextView) findViewById(R.id.textViewQuestionContent);
        choice_A = (TextView) findViewById(R.id.textViewChoiceA);
        choice_B = (TextView) findViewById(R.id.textViewChoiceB);
        choice_C = (TextView) findViewById(R.id.textViewChoiceC);
        choice_D = (TextView) findViewById(R.id.textViewChoiceD);
        answer = (RadioGroup)findViewById(R.id.radioGroupAnswer);
        difficulty = (TextView)findViewById(R.id.textViewDifficulty) ;
        submit = (Button)findViewById(R.id.buttonSubmit);

        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        question.setText(bundle.getString("content"));
        choice_A.setText(bundle.getString("choice_A"));
        choice_B.setText(bundle.getString("choice_B"));
        choice_C.setText(bundle.getString("choice_C"));
        choice_D.setText(bundle.getString("choice_D"));
        difficulty.setText(bundle.getString("difficulty"));
        correct_ans = bundle.getString("answer");
        id = bundle.getString("_id");
        count = new Integer(bundle.getString("count"));
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

        if(mAnswer.equals(correct_ans)){
            Toast.makeText(getBaseContext(), "Correct", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(getBaseContext(), "Wrong", Toast.LENGTH_LONG).show();
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(QuestionContract.QuestionEntry.COLUMN_COUNT, count+1);

        String[] selectionArgs = { id.toString() };
        String selection = "_id" + " = ?";

        getContentResolver().update(QuestionContract.QuestionEntry.CONTENT_URI, contentValues,selection,selectionArgs);

        finish();
    }
}
