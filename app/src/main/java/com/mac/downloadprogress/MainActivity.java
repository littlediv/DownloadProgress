package com.mac.downloadprogress;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mac.downloadprogress.download.ProgressDownloader;
import com.mac.downloadprogress.download.ProgressResponseBody;
import com.mac.downloadprogress.utils.DownloadUtils;
import com.mac.downloadprogress.view.DownloadProgressBar;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ProgressResponseBody.ProgressListener{

    private static final String url = "http://www.xinhong.net/app/qq.jpg";
    private DownloadProgressBar downloadProgressBar;

    private static final String TAG = "MainActivity";

    private boolean isDownloading = false;
    private long breakPoints =0L;
    private ProgressDownloader downloader;
    private File file;
    private long total;
    private long contentLength;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });
    private RelativeLayout rlDownloadItem;
    private DownloadProgressBar dpbDownload;
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadProgressBar = (DownloadProgressBar) findViewById(R.id.dpb);
        rlDownloadItem = (RelativeLayout) findViewById(R.id.rl_download_item);
        dpbDownload = (DownloadProgressBar) findViewById(R.id.dpb_download);
        btn2 = (Button) findViewById(R.id.btn2);

        file = new File(Environment.getExternalStorageDirectory(), "abc.jpg");
        downloader = new ProgressDownloader(url, file, this);

    }

    /**
     * 水平下载进度条
     * @param view
     */
    public void btn1(View view) {
        DownloadUtils.getInstance().download("http://www.xinhong.net/app/qq.jpg", "qq.jpg", new DownloadUtils.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Log.e(TAG, "onDownloadSuccess: " );
            }

            @Override
            public void onDownloading(final int progress) {
                Log.e(TAG, "onDownloading: "+ progress );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadProgressBar.setProgress(progress);
                    }
                });

            }

            @Override
            public void onDownloadFailure() {
                Log.e(TAG, "onDownloadFailure: " );
            }
        });
    }


    /**
     * 断点续传
     * @param view
     */
    public void btn2(View view) {
        if (!isDownloading) {
            downloader.download(breakPoints);
            isDownloading = true;
            btn2.setText("下载中");

        }else {
            downloader.pause();
            breakPoints = total;
            Log.e(TAG, "onRLClick: 下载暂停 "+ breakPoints );
            isDownloading = false;
            btn2.setText("下载暂停");
        }
    }

    @Override
    public void onPreExecute(long contentLength) {
        Log.e(TAG, "onPreExecute: " + contentLength);
        if (this.contentLength == 0L) {
            this.contentLength = contentLength;
        }
    }

    @Override
    public void update(long totalBytes, boolean down) {
        this.total = totalBytes + breakPoints;
        Log.e(TAG, "update: total "+ total + "  totalBytes "+totalBytes + "   breakPoints "+breakPoints );

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = (int) (total *100/ contentLength);
                Log.e(TAG, "run: "+i );
                dpbDownload.setProgress(i);
            }
        });

    }
}
