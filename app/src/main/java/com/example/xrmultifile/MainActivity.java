package com.example.xrmultifile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.Nullable;
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
        switch (v.getId()) {
            case R.id.bt_select:
//                FileActivity.start2FileActivity(this, new CustomFile("新马达", Environment.getExternalStorageDirectory().getPath() + "/新马达"), 3);
                FileActivity.startForResult2FileActivity(this, null, 3);
                break;
            case R.id.bt_browse:
                FileActivity.start2FileActivity(this, null, 3);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 715 && data != null) {
            ArrayList<String> list = data.getStringArrayListExtra("data");
            StringBuilder sb = new StringBuilder();
            for (String string : list) {
                sb.append(string + "\n");
            }
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
