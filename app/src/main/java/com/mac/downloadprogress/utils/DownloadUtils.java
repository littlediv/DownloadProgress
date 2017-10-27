package com.mac.downloadprogress.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mac on 2017/10/25.
 */

public class DownloadUtils {

    private static DownloadUtils downloadUtils;
    private static OkHttpClient okHttpClient;

    public static DownloadUtils getInstance() {
        if (downloadUtils == null) {
            downloadUtils = new DownloadUtils();
        }
        return downloadUtils;
    }

    private DownloadUtils() {
        okHttpClient = new OkHttpClient();
    }

    public void download(String url, final String fileName, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onDownloadFailure();
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                String savePath = isExistDir(fileName);

                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(savePath);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 100.0f / total);
                        if (listener != null) {
                            listener.onDownloading(progress);
                        }
                    }

                    fos.flush();
                    if (listener != null) {
                        listener.onDownloadSuccess();
                    }


                } catch (Exception e) {
                    if (listener != null) {
                        listener.onDownloadFailure();
                    }

                }finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception e) {
                    }

                    try {

                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception e) {
                    }


                }

            }
        });
    }

    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private String isExistDir(String fileName) throws IOException {
        File downloadFile = new File(Environment.getExternalStorageDirectory(), fileName);
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    public interface OnDownloadListener
    {
        void onDownloadSuccess();

        void onDownloading(int progress);

        void onDownloadFailure();
    }
}
