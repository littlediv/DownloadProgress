package com.mac.downloadprogress.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by mac on 2017/10/26.
 */

public class ProgressDownloader {

    private static final String TAG = "ProgressDownloader";

    private ProgressResponseBody.ProgressListener progressListener;
    private String url;
    private OkHttpClient client;
    private File destination;
    private Call call;

    public ProgressDownloader(String url, File destination, ProgressResponseBody.ProgressListener progressListener) {
        this.url = url;
        this.destination = destination;
        this.progressListener = progressListener;
        client = getProgressClient();
    }

    private Call newCall(long startPoints) {
        Request request = new Request.Builder()
                .url(url)
                .header("RANGE", "bytes=" + startPoints + "-")
                .build();

        return client.newCall(request);
    }


    public void download(final long startsPoint) {
        call = newCall(startsPoint);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                save(response, startsPoint);
            }
        });
    }

    private void save(Response response, long startsPoint) {
        ResponseBody body = response.body();
        InputStream inputStream = body.byteStream();
        FileChannel channelOut = null;
        RandomAccessFile randomAccessFile = null;

        try {
            randomAccessFile = new RandomAccessFile(destination, "rwd");
            channelOut = randomAccessFile.getChannel();
            MappedByteBuffer mappedByteBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, startsPoint, body.contentLength());
            byte[] buffer = new byte[2048];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                mappedByteBuffer.put(buffer, 0, len);
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (channelOut != null) {
                    channelOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        if (call != null) {
            call.cancel();
        }
    }
    public OkHttpClient getProgressClient() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        };
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }
}
