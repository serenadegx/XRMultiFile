package com.example.xrmultifile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
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
    private Spinner spinner;
    private RecyclerView rv;
    private FileAdapter adapter;
    private LinkedList<XRFile> backStack;
    private List<CustomFile> custom;
    private List<XRFile> mSelects;
    private String external;
    private Button btSure;
    private int limit;
    private TextView tvTotal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_file);
        tvTotal = findViewById(R.id.tv_total);
        spinner = findViewById(R.id.spinner);
        toolbar = findViewById(R.id.toolbar);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new EMDecoration(this, EMDecoration.VERTICAL_LIST, R.drawable.list_divider, 10));
        btSure = findViewById(R.id.bt_sure);
        btSure.setOnClickListener(this);
        initToolbar();
        initAdapter();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initFileData(Environment.getExternalStorageDirectory());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_sure) {
            if (mSelects != null && mSelects.size() > 0) {
                ArrayList<String> list = new ArrayList<>();
                for (XRFile xrFile : mSelects) {
                    list.add(xrFile.getFile().getAbsolutePath());
                }
                setResult(10001, new Intent().putStringArrayListExtra("data", list));
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                initFileData(Environment.getExternalStorageDirectory());
            } else {
                showPermissionDialog();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backStack != null && backStack.size() > 1) {
            //出栈
            backStack.pop();
            //获取栈中第一个数据，也就是顶层数据
            XRFile curFile = backStack.get(0);
            String path = curFile.getFile().getPath();
            toolbar.setSubtitle(path.substring(external.length()));
            initFileData(curFile.getFile());
        } else {
            finish();
        }
    }

    private void initData() {
        backStack = new LinkedList<>();
        custom = new ArrayList<>();
        external = Environment.getExternalStorageDirectory().getPath();
        CustomFile customPath = getIntent().getParcelableExtra("custom");
        if (customPath != null && new File(customPath.getPath()).exists()) {
            custom.add(customPath);
            backStack.push(new XRFile(new File(customPath.getPath())));
        } else {
            backStack.push(new XRFile(Environment.getExternalStorageDirectory()));
        }
        custom.add(new CustomFile("手机存储", external));

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);
    }

    private void initFileData(File rootFile) {
        List<XRFile> list = new ArrayList<>();
        if (rootFile != null && rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                if (!file.isHidden()) {
                    XRFile data = new XRFile();
                    data.setFile(file);
                    data.setName(file.getName());
                    if (file.isDirectory()) {
                        setFileType(file,data);
                        data.setFileType(XRFile.FOLDER);
                        data.setSize(getDirectorySize(file));
                    } else {
                        data.setFileType(XRFile.OTHER);
                        data.setSize(getPrintSize(getRealSize(file)));
                    }
                    data.setTime(getPrintTime(file.lastModified()));
                    list.add(data);
                }
            }
        }

        Collections.sort(list, new Comparator<XRFile>() {
            @Override
            public int compare(XRFile o1, XRFile o2) {
                if (o1.getFile().isDirectory() && o2.getFile().isFile())
                    return -1;
                if (o1.getFile().isFile() && o2.getFile().isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        adapter.setNewData(list);
    }

    private void setFileType(File file, XRFile data) {
        if (file.isDirectory()){
            data.setFileType(XRFile.FOLDER);
            return;
        }
        switch (file.getName().substring(file.getName().indexOf("."))){
            case "vid":

                break;
        }
    }

    private void initAdapter() {
        limit = getIntent().getIntExtra("limit", 1);
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, custom));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                backStack.clear();
                backStack.push(new XRFile(new File(custom.get(position).getPath())));
                toolbar.setTitle(custom.get(position).getName());
                toolbar.setSubtitle("");
                initFileData(new File(custom.get(position).getPath()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter = new FileAdapter(limit);
        rv.setAdapter(adapter);
        adapter.setItemClickListener(new FileAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View v, XRFile file, int position) {
                if (file.getFile().isDirectory()) {
                    backStack.push(file);
                    initFileData(file.getFile());
                    String path = file.getFile().getPath();
                    toolbar.setSubtitle(path.substring(external.length()));
                }
            }
        });
        adapter.setItemSelectListener(new FileAdapter.ItemSelectListener() {
            @Override
            public void onItemSelectListener(View v, List<XRFile> selects, int position) {
                mSelects = selects;
                btSure.setText(mSelects.size() > 0 ? "确定(" + mSelects.size() + "/" + limit + ")" : "确定");
                tvTotal.setText(mSelects.size() > 0 ? "已选：" + getTotal() : "");
            }
        });
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

    private String getTotal() {
        long totalSize = 0;
        for (XRFile file : mSelects) {
            totalSize += getRealSize(file.getFile());
        }
        return getPrintSize(totalSize);
    }

    private String getDirectorySize(File file) {
        String size = "文件：";
        int count = 0;
        if (file != null && file.isDirectory()) {
            count = file.list().length;
        }
        return size + count;
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

    public static void start2FileActivity(Context context, CustomFile data, int limit) {
        context.startActivity(new Intent(context, FileActivity.class)
                .putExtra("custom", data)
                .putExtra("limit", limit));
    }

    public static void startForResult2FileActivity(Activity activity, CustomFile data, int limit) {
        activity.startActivityForResult(new Intent(activity, FileActivity.class)
                .putExtra("custom", data)
                .putExtra("limit", limit), 715);
    }
}
