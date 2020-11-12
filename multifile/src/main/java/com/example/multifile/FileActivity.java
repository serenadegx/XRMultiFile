package com.example.multifile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.multifile.adapter.FileAdapter;
import com.example.multifile.entity.CustomFile;
import com.example.multifile.entity.XRFile;
import com.example.multifile.ui.EMDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PERMISSION_REQUEST_CODE = 1001;
    public static final String EXTRA_RESULT = "data";

    private Toolbar toolbar;
    //    private Spinner spinner;
    private RecyclerView rv;
    private FileAdapter adapter;
    private LinkedList<XRFile> backStack;
    private List<CustomFile> custom;
    private List<XRFile> mSelects;
    private String external;
    private Button btSure;
    private int limit;
    private TextView tvTotal;
    private TextView tvNav;
    private View notDataView;
    private FrameLayout frameLayout;
    private LinearLayout llSelect;
    private LinearLayout llNav;
    private ListPopupWindow navWindow;
    private ListPopupWindow selectWindow;
    private boolean lookHidden;
    private boolean isBrowse;
    private List<XRFile> mFilterData = new ArrayList<>();
    private String mSuffix;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        tvTotal = findViewById(R.id.tv_total);
        toolbar = findViewById(R.id.toolbar);
        llSelect = findViewById(R.id.ll_select);
        llNav = findViewById(R.id.ll_nav);
        tvNav = findViewById(R.id.tv_nav);
        btSure = findViewById(R.id.bt_sure);
        rv = findViewById(R.id.rv);
        notDataView = getLayoutInflater().inflate(R.layout.layout_empty, (ViewGroup) rv.getParent(), false);
        initData();

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new EMDecoration(this, EMDecoration.VERTICAL_LIST, R.drawable.list_divider, 10));
        btSure.setOnClickListener(this);
        llNav.setOnClickListener(this);
        llSelect.setOnClickListener(this);
        initToolbar();
        initAdapter();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            change(0);
            if (TextUtils.isEmpty(mSuffix)) {
                initFileData(new File(custom.get(0).getPath()));
            } else {
                showFilterFile();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.bt_sure == v.getId()) {
            ArrayList<String> list = new ArrayList<>();
            for (XRFile xrFile : mSelects) {
                list.add(xrFile.getFile().getAbsolutePath());
            }
            setResult(10001, new Intent().putStringArrayListExtra(EXTRA_RESULT, list));
            finish();
        } else if (R.id.ll_nav == v.getId()) {
            if (navWindow != null && navWindow.isShowing()) {
                navWindow.dismiss();
            } else {
                showListPopUpWindow(v);
            }
        } else if (R.id.ll_select == v.getId()) {
            if (mSelects != null && mSelects.size() > 0) {
                if (selectWindow != null && selectWindow.isShowing()) {
                    selectWindow.dismiss();
                } else {
                    showSelectWindow(v);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                change(0);
                if (TextUtils.isEmpty(mSuffix)) {
                    initFileData(new File(custom.get(0).getPath()));
                } else {
                    showFilterFile();
                }
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
        isBrowse = getIntent().getBooleanExtra("isBrowse", false);
        lookHidden = getIntent().getBooleanExtra("lookHidden", false);
        mSuffix = getIntent().getStringExtra("suffix");
        custom.add(new CustomFile("手机存储", external));
        btSure.setVisibility(isBrowse ? View.GONE : View.VISIBLE);
        llSelect.setVisibility(isBrowse ? View.GONE : View.VISIBLE);
        if (customPath != null && new File(customPath.getPath()).exists()) {
            custom.add(customPath);
            backStack.push(new XRFile(new File(customPath.getPath())));
        } else if (customPath == null || !(new File(customPath.getPath()).exists())) {
            backStack.push(new XRFile(Environment.getExternalStorageDirectory()));
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter() {
        limit = getIntent().getIntExtra("limit", 1);

        adapter = new FileAdapter(limit);
        adapter.setBrowse(isBrowse);
        rv.setAdapter(adapter);
        adapter.setItemClickListener(new FileAdapter.ItemClickListener() {
            @Override
            public void onItemClickListener(View v, XRFile file, int position) {
                if (file.getFile().isDirectory()) {
                    backStack.push(file);
                    initFileData(file.getFile());
                    String path = file.getFile().getPath();
                    toolbar.setSubtitle(path.substring(external.length()));
                } else {
                    if (file.getFileType() == XRFile.PICTURE) {
                        ShowActivity.start2ShowActivity(FileActivity.this, file);
                    } else if (file.getFileType() == XRFile.VIDEO) {
                        start2Player("video/*", file.getFile());
                    } else if (file.getFileType() == XRFile.AUDIO) {
                        start2Player("audio/*", file.getFile());
                    } else if (file.getFileType() == XRFile.PDF || file.getFileType() == XRFile.WORD ||
                            file.getFileType() == XRFile.PPT || file.getFileType() == XRFile.EXCEL ||
                            file.getFileType() == XRFile.TXT) {
                        ShowActivity.start2ShowActivity(FileActivity.this, file);
                    }
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

    private void initFileData(File rootFile) {
        List<XRFile> list = new ArrayList<>();
        if (rootFile != null && rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                if (!file.isHidden() || lookHidden) {
                    XRFile data = new XRFile();
                    data.setFile(file);
                    data.setName(file.getName());
                    if (file.isDirectory()) {
                        data.setSize(getDirectorySize(file));
                    } else {
                        data.setSize(getPrintSize(getRealSize(file)));
                    }
                    setFileType(file, data);
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
        if (list.size() < 1) {
            adapter.setEmptyView(notDataView);
        }
    }

    private void setFileType(File file, XRFile data) {
        if (file.isDirectory()) {
            data.setFileType(XRFile.FOLDER);
            return;
        }
        switch (file.getName().substring(file.getName().indexOf(".") + 1)) {
            case "mp4":
                data.setFileType(XRFile.VIDEO);
                break;
            case "jpg":
            case "gif":
            case "png":
                data.setFileType(XRFile.PICTURE);
                break;
            case "mp3":
                data.setFileType(XRFile.AUDIO);
                break;
            case "pdf":
                data.setFileType(XRFile.PDF);
                break;
            case "docx":
                data.setFileType(XRFile.WORD);
                break;
            case "xlsx":
                data.setFileType(XRFile.EXCEL);
                break;
            case "pptx":
                data.setFileType(XRFile.PPT);
                break;
            case "zip":
                data.setFileType(XRFile.ZIP);
                break;
            default:
                data.setFileType(XRFile.OTHER);
                break;
        }
    }

    /**
     * 筛选文件根据类型
     *
     * @param rootFile
     * @param suffix
     */
    private void filterByFileType(File rootFile, String suffix) {
        if (rootFile != null && rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file :
                    files) {
                if (file.isFile() && suffix.equals(file.getName().substring(file.getName().lastIndexOf(".") + 1))) {
                    XRFile data = new XRFile();
                    data.setFile(file);
                    data.setName(file.getName());
                    data.setSize(getPrintSize(getRealSize(file)));
                    data.setTime(getPrintTime(file.lastModified()));
                    setFileType(file, data);
                    mFilterData.add(data);
                } else if (file.isDirectory()) {
                    filterByFileType(file, suffix);
                }
            }
        }
        Collections.sort(mFilterData, new Comparator<XRFile>() {
            @Override
            public int compare(XRFile o1, XRFile o2) {
                return (o1.getFile().lastModified() - o2.getFile().lastModified()) > 0 ? -1 : 1;
            }
        });
    }

    private void showFilterFile() {
        filterByFileType(Environment.getExternalStorageDirectory(), getIntent().getStringExtra("suffix"));
        adapter.setNewData(mFilterData);
        if (mFilterData.size() < 1) {
            adapter.setEmptyView(notDataView);
        }
    }


    private void showListPopUpWindow(View v) {
        navWindow = new ListPopupWindow(this);
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, custom);
        navWindow.setAdapter(adapter);
//        navWindow.setModal(false);
        navWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                change(position);
                initFileData(new File(custom.get(position).getPath()));
                navWindow.dismiss();
            }
        });
        navWindow.setAnchorView(v);
        navWindow.show();
    }

    private void change(int position) {
        backStack.clear();
        backStack.push(new XRFile(new File(custom.get(position).getPath())));
        toolbar.setTitle(custom.get(position).getName());
        toolbar.setSubtitle("");
        tvNav.setText(custom.get(position).getName());
    }

    private void showSelectWindow(View v) {
        selectWindow = new ListPopupWindow(this);
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mSelects);
        selectWindow.setAdapter(adapter);
        selectWindow.setModal(false);
        selectWindow.setAnchorView(v);
        selectWindow.show();
    }

    private void start2Player(String type, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = getPackageName() + ".provider";
            Uri contentUri = FileProvider.getUriForFile(this, authority, file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
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
        if (lookHidden) {
            if (file != null && file.isDirectory()) {
                count = file.list().length;
            }
        } else {
            for (File data : file.listFiles()) {
                if (!data.isHidden()) {
                    count++;
                }
            }
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
//        return data;
        return new SimpleDateFormat("yyyy年MM月dd日").format(new Date(time));
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

    public static void start2FileActivity(Context context, boolean lookHidden, CustomFile data, boolean isBrowse) {
        context.startActivity(new Intent(context, FileActivity.class)
                .putExtra("lookHidden", lookHidden)
                .putExtra("custom", data)
                .putExtra("isBrowse", isBrowse));
    }

    public static void startForResult2FileActivity(Activity activity, boolean lookHidden, CustomFile data, String suffix, int limit, int requestCode) {
        activity.startActivityForResult(new Intent(activity, FileActivity.class)
                .putExtra("custom", data)
                .putExtra("suffix", suffix)
                .putExtra("limit", limit), requestCode);
    }

    public static void start2FileActivity(Context context, boolean lookHidden, String suffix, boolean isBrowse) {
        context.startActivity(new Intent(context, FileActivity.class)
                .putExtra("lookHidden", lookHidden)
                .putExtra("suffix", suffix)
                .putExtra("isBrowse", isBrowse));
    }

}
