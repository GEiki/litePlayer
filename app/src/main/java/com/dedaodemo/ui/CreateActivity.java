package com.dedaodemo.ui;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dedaodemo.R;

public class CreateActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tv_finish;
    private EditText et_title;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_song_list);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        tv_finish = (TextView)findViewById(R.id.tv_finish);
        constraintLayout = (ConstraintLayout) findViewById(R.id.cl_create);
        et_title = (EditText)findViewById(R.id.et_title);
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                Intent intent = new Intent(CreateActivity.this,MainActivity.class);
                String string = et_title.getText().toString();
                if(string.length()==0){
                    Snackbar.make(constraintLayout,"歌单标题不能为空",Snackbar.LENGTH_SHORT).show();
                    return;
                }else if(string.length()>4){
                    string = string.substring(0,4)+"...";
                }
                intent.putExtra("listname",string);
                setResult(RESULT_OK,intent);
                finish();
            }
        });




    }
}
