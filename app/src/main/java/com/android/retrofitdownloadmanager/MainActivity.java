package com.android.retrofitdownloadmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.retrofitdownloadmanager.listener.DownloadListener;
import com.android.retrofitdownloadmanager.utils.DownloadUtils;
import com.android.retrofitdownloadmanager.utils.GifSizeFilter;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DOWNLOAD_THEME_PACKAGENAME = "com.a.b.c";
    private static final String DOWNLOAD_FILE_URL = "http://d.c-launcher.com/themes/cloud/c9c18d678d86c9760ec8ead191fad07ad4b1c855/out/googlePlay/assets/320.amr";
    private static final int REQUEST_CODE_CHOOSE = 0x001;

    private Button mDownloadFileBt;
    private ProgressBar mDownloadProgressBar;
    private TextView mDownloadProgressTv;

    private Button mUploadFileBt;
    private ProgressBar mUploadProgressBar;
    private TextView mUploadProgressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadFileBt = findViewById(R.id.downloadFileBt);
        mDownloadProgressTv = findViewById(R.id.downloadProgressTv);
        mDownloadProgressBar = findViewById(R.id.downloadProgressBar);
        mDownloadFileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });

        mUploadFileBt = findViewById(R.id.uploadFileBt);
        mUploadProgressBar = findViewById(R.id.uploadProgressBar);
        mUploadProgressTv = findViewById(R.id.uploadProgressTv);
        mUploadFileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<String> photos = Matisse.obtainPathResult(data);
            for (String photoPath : photos) {
                Log.d(TAG, "photo: " + photoPath);
            }
            // TODO: 18-11-1 retrofit上传逻辑

        }
    }

    private void downloadFile() {
        DownloadUtils downloadUtils = new DownloadUtils(MainActivity.this);
        downloadUtils.downloadFile(DOWNLOAD_THEME_PACKAGENAME, DOWNLOAD_FILE_URL, new DownloadListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart: ");
                mDownloadProgressTv.setText("开始下载");
                mDownloadFileBt.setEnabled(false);
                mDownloadFileBt.setTextColor(getResources().getColor(android.R.color.darker_gray));
                mDownloadProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(final int currentLength) {
                Log.e(TAG, "onLoading: " + currentLength);
                mDownloadProgressTv.setText("下载进度：" + currentLength);
                mDownloadProgressBar.setProgress(currentLength);
            }

            @Override
            public void onFinish(String localPath) {
                Log.e(TAG, "onFinish: " + localPath);
                mDownloadProgressTv.setText("下载完成");
                mDownloadFileBt.setEnabled(true);
                mDownloadFileBt.setTextColor(getResources().getColor(android.R.color.white));
                mDownloadProgressBar.setVisibility(View.GONE);
                mDownloadFileBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "点击下载完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(final String errorInfo) {
                Log.e(TAG, "onFailure: " + errorInfo);
                mDownloadProgressTv.setText("下载失败");
            }
        });
    }

    private void uploadFile() {
        AndPermission.with(MainActivity.this).runtime().permission(Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Matisse.from(MainActivity.this)
                                .choose(MimeType.ofImage())
                                .theme(R.style.Matisse_Zhihu)
                                .countable(false)
                                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                .maxSelectable(9)
                                .originalEnable(false)
                                .maxOriginalSize(10)
                                .imageEngine(new PicassoEngine())
                                .forResult(REQUEST_CODE_CHOOSE);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .start();
    }

}
