package com.example.xrmultifile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btSelect = findViewById(R.id.bt_select);
        Button btBrowse = findViewById(R.id.bt_browse);
        btSelect.setOnClickListener(this);
        btBrowse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_select:
                FileActivity.start2FileActivity(this);
                break;
            case R.id.bt_browse:
                FileActivity.start2FileActivity(this);
                break;
        }
    }
}
