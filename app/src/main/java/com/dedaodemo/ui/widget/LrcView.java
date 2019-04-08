package com.dedaodemo.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import com.dedaodemo.R;
import com.dedaodemo.bean.LrcBean;
import com.dedaodemo.util.Util;

import java.util.ArrayList;

public class LrcView extends View {

    private ArrayList<LrcBean> beans;
    private long currentTime;
    private Paint paint = new Paint();
    private Paint hPaint = new Paint();
    private Scroller scroller;
    private int lrcIndex = 0;
    private static final int OFFSET = 160;


    public LrcView(Context context) {
        this(context,null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        paint.setTextSize(Util.dip2px(context,20));
        paint.setColor(getContext().getResources().getColor(R.color.black,null));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        hPaint.setTextSize(Util.dip2px(context,20));
        hPaint.setColor(getContext().getResources().getColor(R.color.colorPrimary,null));
        hPaint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
    }


    public void setBeans(ArrayList<LrcBean> beans) {
        this.beans = beans;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int index = lrcIndex;
        if (beans != null) {
            for(int i=0;i<beans.size();i++) {
                if (currentTime >= beans.get(i).getStartTime() && currentTime <= beans.get(i).getEndTime() ) {
                    canvas.drawText(beans.get(i).getLrc(),width/2,height/2+OFFSET*i,hPaint);
                    index = i;
                } else {
                    canvas.drawText(beans.get(i).getLrc(),width/2,height/2+OFFSET*i,paint);
                }
            }

            if(index != lrcIndex) {
                int offset = OFFSET;
                scroller.startScroll(0,getScrollY(),0,offset,1000);
                if (getScrollY() != OFFSET*index) {
                    scroller.startScroll(0,getScrollY(),0,OFFSET*index-getScrollY(),1000);
                }
                lrcIndex = index;
            }


        } else {
           canvas.drawText("暂无歌词",width/2,height/2+80,paint);
           setScrollY(0);
        }
        postInvalidateDelayed(100);

    }



    @Override
    public void computeScroll() {
       if(scroller.computeScrollOffset()) {
           scrollTo(scroller.getCurrX(),scroller.getCurrY());
           postInvalidate();
       }
    }
}
