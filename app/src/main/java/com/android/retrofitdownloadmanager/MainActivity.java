package com.android.retrofitdownloadmanager;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DOWNLOAD_THEME_PACKAGENAME = "com.a.b.c";
    private static final String DOWNLOAD_FILE_URL = "http://d.c-launcher.com/themes/cloud/c9c18d678d86c9760ec8ead191fad07ad4b1c855/out/googlePlay/assets/320.amr";

    private Button mDownloadFileBt;
    private ProgressBar mProgressBar;
    private TextView mProgressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadFileBt = findViewById(R.id.downloadFileBt);
        mProgressTv = findViewById(R.id.progressTv);
        mProgressBar = findViewById(R.id.progressBar);
        mDownloadFileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });
    }

    private void downloadFile() {
        DownloadUtils downloadUtils = new DownloadUtils(MainActivity.this);
        downloadUtils.downloadFile(DOWNLOAD_THEME_PACKAGENAME, DOWNLOAD_FILE_URL, new DownloadListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart: ");
                mProgressTv.setText("开始下载");
                mDownloadFileBt.setEnabled(false);
                mDownloadFileBt.setTextColor(getResources().getColor(android.R.color.darker_gray));
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(final int currentLength) {
                Log.e(TAG, "onLoading: " + currentLength);
                mProgressTv.setText("下载进度：" + currentLength);
                mProgressBar.setProgress(currentLength);
            }

            @Override
            public void onFinish(String localPath) {
                Log.e(TAG, "onFinish: " + localPath);
                mProgressTv.setText("下载完成");
                mDownloadFileBt.setEnabled(true);
                mDownloadFileBt.setTextColor(getResources().getColor(android.R.color.white));
                mProgressBar.setVisibility(View.GONE);
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
                mProgressTv.setText("下载失败");
            }
        });
    }
}
