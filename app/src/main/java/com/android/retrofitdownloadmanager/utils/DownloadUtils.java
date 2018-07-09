package com.android.retrofitdownloadmanager.utils;

import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.retrofitdownloadmanager.listener.DownloadListener;
import com.android.retrofitdownloadmanager.retrofit.RetrofitApi;
import com.android.retrofitdownloadmanager.retrofit.RetrofitRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 下载文件工具类
 */

public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    // 视频下载相关
    protected RetrofitApi mApi;
    private Handler mHandler;
    private Call<ResponseBody> mCall;
    private File mFile;
    private Thread mThread;
    // 下载到本地的文件目录
    private String mFileFolder = Environment.getExternalStorageDirectory() + "/DownloadFile";
    // 下载到本地的文件路径
    private String mFilePath;

    public DownloadUtils() {
        if (mApi == null) {
            mApi = RetrofitRequest.getInstance().getRetrofitApi();
        }
        mHandler = new Handler();
    }

//    public void downloadFile(String url, final DownloadListener downloadListener) {
//        //通过Url得到保存到本地的文件名
//        String name = url;
//        if (FileUtils.createOrExistsDir(mFileFolder)) {
//            int i = name.lastIndexOf('/');//一定是找最后一个'/'出现的位置
//            if (i != -1) {
//                name = name.substring(i);
//                mFilePath = mFileFolder + name;
//            }
//        }
//        if (TextUtils.isEmpty(mFilePath)) {
//            Log.e(TAG, "downloadVideo: 存储路径为空了");
//            return;
//        }
//        //建立一个文件
//        mFile = new File(mFilePath);
//        if (!FileUtils.isFileExists(mFile) && FileUtils.createOrExistsFile(mFile)) {
//            if (mApi == null) {
//                Log.e(TAG, "downloadVideo: 下载接口为空了");
//                return;
//            }
//            mCall = mApi.downloadFile(url);
//            mCall.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
//                    //下载文件放在子线程
//                    mThread = new Thread() {
//                        @Override
//                        public void run() {
//                            super.run();
//                            //保存到本地
//                            writeFile2Disk(response, mFile, downloadListener);
//                        }
//                    };
//                    mThread.start();
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    downloadListener.onFailure("网络错误！");
//                }
//            });
//        } else {
//            downloadListener.onFinish(mFilePath);
//        }
//    }

    public void downloadFile(String url, final DownloadListener downloadListener) {
        //通过Url得到保存到本地的文件名
        String name = url;
        if (FileUtils.createOrExistsDir(mFileFolder)) {
            int i = name.lastIndexOf('/');//一定是找最后一个'/'出现的位置
            if (i != -1) {
                name = name.substring(i);
                mFilePath = mFileFolder + name;
            }
        }
        if (TextUtils.isEmpty(mFilePath)) {
            Log.e(TAG, "downloadVideo: 存储路径为空了");
            return;
        }
        //建立一个文件
        mFile = new File(mFilePath);
        if (FileUtils.createOrExistsFile(mFile)) {
            if (mApi == null) {
                Log.e(TAG, "downloadVideo: 下载接口为空了");
                return;
            }
            mCall = mApi.downloadFile(url);
            mCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                    //下载文件放在子线程
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //保存到本地
                            writeFile2Disk(response, mFile, downloadListener);
                        }
                    };
                    mThread.start();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    downloadListener.onFailure("网络错误！");
                }
            });
        }
    }

    private void writeFile2Disk(Response<ResponseBody> response, File file, final DownloadListener downloadListener) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                downloadListener.onStart();
            }
        });
        long currentLength = 0;
        OutputStream os = null;

        if (response.body() == null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    downloadListener.onFailure("资源错误！");
                }
            });
            return;
        }
        InputStream is = response.body().byteStream();
        final long totalLength = response.body().contentLength();

        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                Log.e(TAG, "当前进度: " + currentLength);
                final long finalCurrentLength = currentLength;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadListener.onProgress((int) (100 * finalCurrentLength / totalLength));
                        if ((int) (100 * finalCurrentLength / totalLength) == 100) {
                            downloadListener.onFinish(mFilePath);
                        }
                    }
                });
            }
        } catch (FileNotFoundException e) {
            downloadListener.onFailure("未找到文件！");
            e.printStackTrace();
        } catch (IOException e) {
            downloadListener.onFailure("IO错误！");
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
