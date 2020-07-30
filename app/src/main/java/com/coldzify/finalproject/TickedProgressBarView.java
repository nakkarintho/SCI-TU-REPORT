package com.coldzify.finalproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class TickedProgressBarView extends ProgressBar {
    private static final float DEFAULT_lineWidth = 10.0f;
    private float lineWidth;

    private int textSize = 12;
    private Paint mTickPaint,textPaint;
    private Paint backgroundCirclePaint,progressLinePaint,backgroundLinePaint;
    private float INDICATOR_RADIUS = 5f;
    private float width_ratio = 0.8f;
    private String text1,text2,text3,text4;
    private Rect bound1 = new Rect();
    public TickedProgressBarView(Context context) {
        super(context);
        setup(null);
    }

    public TickedProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public TickedProgressBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(attrs);
    }

    private void setup(AttributeSet attrs){
        setupStyleable(attrs);

        text1 = getResources().getString(R.string.waiting_status_th);
        text2 = getResources().getString(R.string.accepted_status_th);
        text3 = getResources().getString(R.string.inprogress_status_th);
        text4 = getResources().getString(R.string.finished_status_th);

        mTickPaint = new Paint();
        mTickPaint.setColor(ContextCompat.getColor(getContext(), R.color.statusBar_color));
        mTickPaint.setStyle(Paint.Style.FILL);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundCirclePaint = new Paint();
        backgroundCirclePaint.setColor(ContextCompat.getColor(getContext(),R.color.statusBar_second_color));
        backgroundCirclePaint.setStyle(Paint.Style.FILL);
        backgroundCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        progressLinePaint = new Paint();
        progressLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.statusBar_color));

        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(getContext(),R.color.statusBar_color));
        textPaint.setTextSize(dpTopx(textSize));
        textPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.my_font));
        backgroundLinePaint = new Paint();
        backgroundLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.statusBar_second_color));

    }


    private void setupStyleable(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TickedProgressBarView);
        lineWidth = typedArray.getDimension(R.styleable.TickedProgressBarView_lineWidth,DEFAULT_lineWidth);
        width_ratio = typedArray.getFloat(R.styleable.TickedProgressBarView_percentWidth,0.8f);
        if(width_ratio> 1)
            width_ratio = 0.8f;
        INDICATOR_RADIUS = lineWidth*2;
        typedArray.recycle();

    }
    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), (int)lineWidth*4*3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        float ratioStart = (1-width_ratio)/2;
        float ratioEnd = width_ratio+ratioStart;
        float width = getWidth()*width_ratio;
        float distance_point = (width-2*INDICATOR_RADIUS)/getMax();
        float xStart = getWidth()*ratioStart;
        float xEnd = getWidth()*ratioEnd;
        float y = getHeight()-INDICATOR_RADIUS;

        progressLinePaint.setStrokeWidth(lineWidth);
        backgroundLinePaint.setStrokeWidth(lineWidth);

        drawStatusText(canvas);

        canvas.drawLine(xStart,y,xStart+width*getProgress()/getMax(),y,progressLinePaint);
        canvas.drawLine(xStart+width*getProgress()/getMax(),y,xEnd,y,backgroundLinePaint);
        //canvas.drawCircle(INDICATOR_RADIUS,y,INDICATOR_RADIUS,mTickPaint);
        float start = xStart+INDICATOR_RADIUS;
        for(int i = 0 ; i < getMax()+1 ; i++){

            if(i <= getProgress()){
                canvas.drawCircle(start,y,INDICATOR_RADIUS,mTickPaint);
            }
            else{
                canvas.drawCircle(start,y,INDICATOR_RADIUS, backgroundCirclePaint);
            }
            start += distance_point;
        }
        //System.out.println("width : "+getWidth());

    }
    private void drawStatusText(Canvas canvas){
        float ratioStart = (1-width_ratio)/2;
        float xStart = getWidth()*ratioStart;
        float width = getWidth()*width_ratio;
        float distance_point = (width-2*INDICATOR_RADIUS)/getMax();
        float start = xStart+INDICATOR_RADIUS;
        float y = getHeight()-6f*lineWidth;

        textPaint.getTextBounds(text1, 0, text1.length(), bound1);

        int w1 = bound1.width();
        canvas.drawText(text1,start-w1/2f,y, textPaint);

        start += distance_point;
        textPaint.getTextBounds(text2, 0, text2.length(), bound1);

        w1 = bound1.width();
        canvas.drawText(text2,start-w1/2f,y, textPaint);

        start += distance_point;
        textPaint.getTextBounds(text3, 0, text3.length(), bound1);

        w1 = bound1.width();
        canvas.drawText(text3,start-w1/2f,y, textPaint);

        start += distance_point;
        textPaint.getTextBounds(text4, 0, text4.length(), bound1);

        w1 = bound1.width();
        canvas.drawText(text4,start-w1/2f,y, textPaint);

    }
    public int dpTopx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,   dp,getResources().getDisplayMetrics());
    }
    public  float pxTodp( float px)  {
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,getResources().getDisplayMetrics());
    }

}