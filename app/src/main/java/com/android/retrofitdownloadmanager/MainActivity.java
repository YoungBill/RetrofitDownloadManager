package com.android.retrofitdownloadmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.retrofitdownloadmanager.listener.DownloadListener;
import com.android.retrofitdownloadmanager.utils.DownloadUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DOWNLOAD_FILE_URL = "http://d.c-launcher.com/themes/cloud/c9c18d678d86c9760ec8ead191fad07ad4b1c855/out/googlePlay/assets/320.amr";

    private TextView mProgressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressTv = findViewById(R.id.progressTv);
        findViewById(R.id.downloadFileBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });
    }

    private void downloadFile() {
        DownloadUtils downloadUtils = new DownloadUtils(MainActivity.this);
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
