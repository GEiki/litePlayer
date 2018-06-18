package com.dedaodemo.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by guoss on 2017/11/17.
 * 只能用于ListView
 * 只能有一个footView
 * 使用时需设置LoadMoreListener和FootView
 * 请用该部件提供的setFootView方法设置footView
 * 通过setLoadMore来移除footView
 */

public class LoadMoreRefreshLayout extends SwipeRefreshLayout
{
   private ListView mListView;
    private LoadMoreListener mListener;
    private LinearLayout ll_test=null;
    private View footView;
    private int last_index;
    private int total_index;
    private float curor;
    private float parentWidth;
    private float childWidth;
    private boolean isFirst=true;
    private boolean isLoadMore=false;
    private boolean isFirstScroll=true;
    private boolean isUp=true;

    public interface LoadMoreListener{
        public void loadMore();
    }
    public LoadMoreRefreshLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    /*
    * 获取ListView
    * */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {

        if(mListView==null){
            if(getChildCount()>0){
                if(getChildAt(0) instanceof ListView){
                    mListView=(ListView)getChildAt(0);
                    setListViewOnScrollListener();
                }
            }
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    /*
    * 设置ListView滑动监听
    *
    * */
    private void setListViewOnScrollListener(){
        mListView.setOnTouchListener(new OnTouchListener() {//监听是上滑还是下滑
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isFirstScroll){
                    curor=event.getY();
                    isFirstScroll=false;
                }
                switch (event.getAction()){
                    case MotionEvent.ACTION_MOVE:{
                        if(event.getY()<curor){//上滑
                            isUp=true;
                            curor=event.getY();
                        }else if(event.getY()>curor){//下滑
                            isUp=false;
                            curor=event.getY();
                        }
                    }
                }
                return false;//防止事件被消费，导致滑动冲突
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener()//监听滑动状态
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                if(last_index==total_index&&mListView.getFooterViewsCount()==0&&scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE){

                    if(!isLoadMore()){

                        if(footView!=null&&mListener!=null){

                            mListener.loadMore();
                        }
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                last_index=firstVisibleItem+visibleItemCount;
                total_index=totalItemCount;
                if(ll_test!=null) {
                    if (isFirst) {//onCreate结束前控件仍未进行测量，因此需要在此进行赋值
                        parentWidth = view.getWidth();
                        childWidth = ll_test.getWidth();

                    }
                }


                if(ll_test!=null) {
                    if (firstVisibleItem == 0) {
                        if (isUp) {//上滑
                            Log.i("onScroll", "上滑");
                            Log.i("onScroll", String.valueOf(ll_test.getWidth()) + "______" + String.valueOf(parentWidth));

                            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ll_test, "scaleX", parentWidth / childWidth);
                            objectAnimator.setDuration(400);
                            objectAnimator.start();

                        } else if (!isUp) {//下滑
                            Log.i("onScroll", "下滑");
                            Log.i("onScroll", String.valueOf(ll_test.getWidth()) + "______" + String.valueOf(childWidth));
                            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ll_test, "scaleX", childWidth / parentWidth);
                            objectAnimator.setDuration(400);
                            objectAnimator.start();

                        }
                    }
                }


            }
        });
    }

    public void setLoadMoreListener(LoadMoreListener listener)
    {
        mListener = listener;
    }

    public void setFootView(View footView)
    {
        this.footView = footView;
    }

    public boolean isLoadMore()
    {
        return isLoadMore;
    }

    public void setLl_test(LinearLayout ll_test) {
        this.ll_test = ll_test;
    }

    public void setLoadMore(boolean loadMore)
    {
        isLoadMore = loadMore;
        if(isLoadMore){
            if(footView!=null){
                mListView.addFooterView(footView);
            }
        }else {
            if(footView!=null){
                mListView.removeFooterView(footView);
            }
        }
    }

}
