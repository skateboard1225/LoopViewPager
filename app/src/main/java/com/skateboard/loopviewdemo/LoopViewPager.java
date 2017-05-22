package com.skateboard.loopviewdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by skateboard on 2017/5/21.
 */

public class LoopViewPager extends ViewGroup implements Animator.AnimatorListener
{

    private Adapter adapter;

    private Scroller scroller;

    private int currPos;

    private int lastPos;

    private int nextPos;

    private final int PAGE_WIDTH = getResources().getDisplayMetrics().widthPixels;

    private final int PAGE_HEIGHT = getResources().getDisplayMetrics().heightPixels;

    private final int PAGE_NUM = 3;

    private float touchX;

    private ObjectAnimator animator;

    public LoopViewPager(Context context)
    {
        this(context, null, 0);
    }

    public LoopViewPager(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LoopViewPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        animator=ObjectAnimator.ofInt(this,"scrollX",0,0);
        animator.addListener(this);
        animator.setDuration(200);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(PAGE_NUM * PAGE_WIDTH, MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int leftPage = -PAGE_WIDTH;
        for (int i = 0; i < getChildCount(); i++)
        {
            View child = getChildAt(i);
            child.layout(leftPage, 0, leftPage + PAGE_WIDTH, PAGE_HEIGHT);
            leftPage += PAGE_WIDTH;
        }
    }


    public void setAdapter(Adapter adapter)
    {
        this.adapter = adapter;
        notifyDataChanged();
    }

    private void notifyDataChanged()
    {

        if (adapter == null)
        {
            return;
        }
        clearViews();
        if (currPos == adapter.getItemCount() - 1)
        {
            nextPos = 0;
            lastPos = currPos - 1;
        } else if (currPos == 0)
        {
            nextPos = currPos + 1;
            lastPos = adapter.getItemCount() - 1;
        } else
        {
            nextPos = currPos + 1;
            lastPos = currPos - 1;
        }

        View lastView = adapter.getView(this, lastPos);
        View curView = adapter.getView(this, currPos);
        View nextView = adapter.getView(this, nextPos);
        addView(lastView);
        addView(curView);
        addView(nextView);
        invalidate();
    }

    private void clearViews()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            removeView(getChildAt(i));
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {

            case MotionEvent.ACTION_MOVE:
                scrollBy((int) (touchX - event.getX()), 0);
                break;

            case MotionEvent.ACTION_UP:
                if (getScrollX() >= PAGE_WIDTH/2)
                {
                    moveNext();
                }
                else if (getScrollX() <= -PAGE_WIDTH/2)
                {
                    movePre();
                }
                else
                {
                    startScroll(0,0);
                }
                break;
        }
        touchX = event.getX();
        return true;
    }

    private void startScroll(int endX,int endY)
    {
        scroller.startScroll(getScrollX(),getScrollY(),endX-getScrollX(),endY-getScrollY(),500);
        invalidate();
    }

    @Override
    public void computeScroll()
    {
        if(scroller.computeScrollOffset())
        {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }

    }

    private void movePre()
    {

        animator.setIntValues(getScrollX(),-PAGE_WIDTH);
        animator.start();
    }

    private void moveNext()
    {
        animator.setIntValues(getScrollX(),PAGE_WIDTH);
        animator.start();
    }

    private void dealPos()
    {
        if (currPos == adapter.getItemCount() - 1)
        {
            nextPos = 0;
            lastPos = currPos - 1;
        } else if (currPos == 0)
        {
            nextPos = currPos + 1;
            lastPos = adapter.getItemCount() - 1;
        } else
        {
            nextPos = currPos + 1;
            lastPos = currPos - 1;
        }
    }

    @Override
    public void onAnimationStart(Animator animation)
    {

    }

    @Override
    public void onAnimationEnd(Animator animation)
    {
        if(getScrollX()>0)
        {
            scrollNextAndReste();
        }
        else
        {
            scrollPreAndReset();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation)
    {

    }

    @Override
    public void onAnimationRepeat(Animator animation)
    {

    }

    private void scrollPreAndReset()
    {
        currPos=lastPos;
        dealPos();
        removeView(getChildAt(PAGE_NUM-1));
        View lastView = adapter.getView(this, lastPos);
        addView(lastView,0);
        invalidate();
        scrollTo(0,0);
    }

    private void scrollNextAndReste()
    {
        currPos = nextPos;
        dealPos();
        removeView(getChildAt(0));
        View nextView = adapter.getView(LoopViewPager.this, nextPos);
        addView(nextView);
        scrollTo(0, 0);
    }

    public abstract static class Adapter
    {

        public abstract int getItemCount();

        public abstract View getView(ViewGroup container, int position);

    }



}
