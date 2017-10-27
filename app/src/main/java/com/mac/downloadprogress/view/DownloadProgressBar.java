package com.mac.downloadprogress.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.mac.downloadprogress.R;
import com.mac.downloadprogress.utils.ScreenUtils;


/**
 * Created by mac on 2017/10/25.
 */

public class DownloadProgressBar extends View {
    private int barColor;
    private int barHeight;
    private int progressColor;
    private int progressTextSize;

    private int progress =0;
    private int height, width;
    private Paint paint;
    private Rect rect;

    private int textBarSpacing = 10;

    public DownloadProgressBar(Context context) {
        this(context, null);
    }

    public DownloadProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DownloadProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressBar);
        barColor = typedArray.getColor(R.styleable.DownloadProgressBar_barColor, Color.BLUE);
        barHeight = typedArray.getDimensionPixelSize(R.styleable.DownloadProgressBar_barHeight, 4);
        progressColor = typedArray.getColor(R.styleable.DownloadProgressBar_progressColor, Color.BLUE);
        progressTextSize = typedArray.getDimensionPixelSize(R.styleable.DownloadProgressBar_progressTextSize, 26);

        typedArray.recycle();

        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(progressTextSize);
        rect = new Rect();

    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(barColor);
        rect.set(0, height - barHeight, width * progress / 100, height);
        canvas.drawRect(rect, paint);
        paint.setColor(progressColor);
        canvas.drawText(progress+"%", width * progress / 100, height - (height - barHeight)/2 , paint );

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }else
        {
            height = progressTextSize + barHeight + textBarSpacing;
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else
        {
            width = ScreenUtils.getScreenWidth(getContext());
        }

        setMeasuredDimension(width, height);

    }
}
