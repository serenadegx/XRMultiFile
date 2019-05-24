package com.example.multifile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.bm.library.PhotoView;
import com.example.multifile.entity.XRFile;
import com.example.multifile.ui.SuperFileView;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

public class ShowActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private XRFile file;
    private FrameLayout fl;
    private SuperFileView fileView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        toolbar = findViewById(R.id.toolbar);
        fl = findViewById(R.id.container);
        file = (XRFile) getIntent().getSerializableExtra("data");
        initToolbar();
        iniData();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        setTitle(file.getName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void iniData() {
        if (file.getFileType() == XRFile.PICTURE) {
            PhotoView photoView = new PhotoView(this);
            photoView.enable();
            fl.addView(photoView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            Picasso.get()
                    .load(file.getFile())
                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .into(photoView);
        } else {
            fileView = new SuperFileView(this);
            fl.addView(fileView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            fileView.displayFile(file.getFile());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fileView != null)
            fileView.onStopDisplay();
    }

    public static void start2ShowActivity(Context context, XRFile file) {
        context.startActivity(new Intent(context, ShowActivity.class).putExtra("data", file));
    }
}
