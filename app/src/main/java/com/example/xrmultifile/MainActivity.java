package com.example.xrmultifile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.multifile.XRMultiFile;

import java.io.File;
import java.util.ArrayList;

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
                XRMultiFile.get()
                        .with(this)
                        .lookHiddenFile(false)
//                        .custom(new File(Environment.getExternalStorageDirectory().getPath() + "/新马达"))
                        .setFilter(".pdf")
                        .limit(5)
                        .select(this, 715);
                break;
            case R.id.bt_browse:
                XRMultiFile.get()
                        .with(this)
                        .lookHiddenFile(false)
                        .browse();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 715 && data != null) {
            ArrayList<String> list = XRMultiFile.getSelectResult(data);
            StringBuilder sb = new StringBuilder();
            for (String string : list) {
                sb.append(string + "\n");
            }
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
