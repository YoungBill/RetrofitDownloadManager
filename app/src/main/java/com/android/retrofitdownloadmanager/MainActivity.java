package com.android.retrofitdownloadmanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.retrofitdownloadmanager.listener.DownloadListener;
import com.android.retrofitdownloadmanager.utils.DownloadUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DOWNLOAD_FILE_URL = "http://d.c-launcher.com/themes/cloud/c9c18d678d86c9760ec8ead191fad07ad4b1c855/out/googlePlay/assets/320.amr";
    private static final int REQUEST_CODE_EXTERNAL_STORAGE = 0x001;

    private TextView mProgressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressTv = findViewById(R.id.progressTv);
        findViewById(R.id.downloadFileBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_EXTERNAL_STORAGE);
                } else {
                    downloadFile();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_CODE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile();
        } else {
            Toast.makeText(MainActivity.this, "请授予文件读写无权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile() {
        DownloadUtils downloadUtils = new DownloadUtils();
        downloadUtils.downloadFile(DOWNLOAD_FILE_URL, new DownloadListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart: ");
                mProgressTv.setText("开始下载");
            }

            @Override
            public void onProgress(final int currentLength) {
                Log.e(TAG, "onLoading: " + currentLength);
                mProgressTv.setText("下载进度：" + currentLength);
            }

            @Override
            public void onFinish(String localPath) {
                Log.e(TAG, "onFinish: " + localPath);
                mProgressTv.setText("下载完成");
            }

            @Override
            public void onFailure(final String errorInfo) {
                Log.e(TAG, "onFailure: " + errorInfo);
                mProgressTv.setText("下载失败");
            }
        });
    }
}
