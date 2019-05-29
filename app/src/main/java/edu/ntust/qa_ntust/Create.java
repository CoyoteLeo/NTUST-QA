package edu.ntust.qa_ntust;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Create extends AppCompatActivity {
    private Button  submit;
    private EditText question;
    private EditText choice_A;
    private EditText choice_B;
    private EditText choice_C;
    private EditText choice_D;
    private RadioGroup ans;
    private RadioGroup difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        question = (EditText) findViewById(R.id.question_edit);
        choice_A = (EditText) findViewById(R.id.choice_edit_A);
        choice_B = (EditText) findViewById(R.id.choice_edit_B);
        choice_C = (EditText) findViewById(R.id.choice_edit_C);
        choice_D = (EditText) findViewById(R.id.choice_edit_D);
        ans = (RadioGroup)findViewById(R.id.ans);
        difficulty = (RadioGroup)findViewById(R.id.difficulty) ;
        submit = (Button)findViewById(R.id.create_submit);
        submit.setOnClickListener(submitOnClick);
    }

    private View.OnClickListener submitOnClick = new View.OnClickListener(){
        public void onClick(View v){
            String question_content = question.getText().toString();
            String choice_A_content = question.getText().toString();
            String choice_B_content = question.getText().toString();
            String choice_C_content = question.getText().toString();
            String choice_D_content = question.getText().toString();

            String difficulty_secected ="";
            String ans_secected="";

            int temp = difficulty.getCheckedRadioButtonId();
            if ( temp!=-1){
                RadioButton selectedRadioButton = (RadioButton) findViewById(difficulty.getCheckedRadioButtonId());
                difficulty_secected = selectedRadioButton.getText().toString();
            }else{
                Toast.makeText(Create.this,"Select difficulty plz ", Toast.LENGTH_SHORT).show();
                return;
            }

            temp = ans.getCheckedRadioButtonId();
            if(ans.getCheckedRadioButtonId()!=-1){
                RadioButton selectedRadioButton = (RadioButton) findViewById(ans.getCheckedRadioButtonId());
                ans_secected = selectedRadioButton.getText().toString();
            }else{
                Toast.makeText(Create.this,"Select answer plz ", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(Create.this,"Create Successfully ", Toast.LENGTH_SHORT).show();

            Intent it = new Intent();
            it.setClass(Create.this,MainActivity.class);
            startActivity(it);
        }
    };
}
