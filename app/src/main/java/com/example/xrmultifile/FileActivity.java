package com.example.xrmultifile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PERMISSION_REQUEST_CODE = 1001;

    private Toolbar toolbar;
    private RecyclerView rv;
    private FileAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        toolbar = findViewById(R.id.toolbar);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new EMDecoration(this, EMDecoration.VERTICAL_LIST, R.drawable.list_divider, 10));
        Button btSure = findViewById(R.id.bt_sure);
        initToolbar();
        initAdapter();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initData();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                initData();
            } else {
                showPermissionDialog();
            }
        }
    }

    private void initData() {
        List<XRFile> list = new ArrayList<>();
        File rootFile = Environment.getExternalStorageDirectory();
        if (rootFile != null && rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                XRFile data = new XRFile();
                data.setFile(file);
                data.setName(file.getName());
                if (file.isDirectory()) {
                    data.setSize(getDirectorySize(file));
                } else {
                    data.setSize(getPrintSize(getRealSize(file)));
                }
                data.setTime(getPrintTime(file.lastModified()));
                list.add(data);
            }
        }
        adapter.setNewData(list);
    }

    private void initAdapter() {
        adapter = new FileAdapter();
        rv.setAdapter(adapter);
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示信息")
                .setMessage("缺少存储权限，请单击【确定】按钮前往设置中心进行权限授权。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }).show();
    }

    private String getDirectorySize(File file) {
        String size = "文件：";
        int count = 0;
        if (file != null && file.isDirectory()) {
            count = file.list().length;
        }
        return size + count;
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);
    }

    private String getPrintTime(long time) {
        String data;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTimeInMillis(time);
        int _year = calendar.get(Calendar.YEAR);
        int _month = calendar.get(Calendar.MONTH) + 1;
        int _day = calendar.get(Calendar.DAY_OF_MONTH);
        if (_year == year && _month == month && _day == day) {  //在当天
            data = new SimpleDateFormat("HH:mm").format(new Date(time));
        } else if (_year == year) {                             //在当年
            data = new SimpleDateFormat("MM月dd日").format(new Date(time));
        } else {                                                //其他
            data = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(time));
        }
        return data;
    }

    private long getRealSize(File file) {
        FileInputStream fis = null;
        long size = 0;
        try {
            fis = new FileInputStream(file);
            size = fis.getChannel().size();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    public String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + " B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + " KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size * 100 / 1024 % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + " GB";
        }
    }

    public static void start2FileActivity(Context context) {
        context.startActivity(new Intent(context, FileActivity.class));
    }
}
