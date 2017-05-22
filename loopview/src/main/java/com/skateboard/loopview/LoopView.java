package com.skateboard.loopview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by skateboard on 2016/12/5.
 */

public class LoopView extends ViewGroup
{

    private final int LOAD_NEXT=999;

    private ViewPager viewPager;

    private View bottomView;

    private PagerAdapter adapter;

    private Handler handler;

    private OnPageChangeListener listener;

    private ViewPager.OnPageChangeListener pageChangeListener=new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

              if(listener!=null)
              {
                  listener.onPageScrolled(position,positionOffset,positionOffsetPixels);
              }
        }

        @Override
        public void onPageSelected(int position)
        {

           if(listener!=null)
           {
               listener.onPageSelected(position);
           }

        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
           if(listener!=null)
           {
               listener.onPageScrollStateChanged(state);
           }
        }
    };


    public LoopView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initHandler();
        initWidget(context, attrs);

    }

    private void initHandler()
    {
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if(msg.what==LOAD_NEXT)
                {
                    int dataSize=adapter.getCount();
                    if(viewPager.getCurrentItem()==dataSize-1)
                    {
                        viewPager.setCurrentItem(0,true);
                    }
                    else
                    {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                    requestLoadNext();
                }
            }
        };
    }

    private void requestLoadNext()
    {
        Message message=new Message();
        message.what=LOAD_NEXT;
        handler.sendMessageDelayed(message,3000);
    }

    private void initWidget(Context context, AttributeSet attrs)
    {
        viewPager = new ViewPager(getContext());
        viewPager.addOnPageChangeListener(pageChangeListener);
        addView(viewPager);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoopView);
        int bottomViewId = typedArray.getResourceId(R.styleable.LoopView_infoviewid, R.layout.bottom_view_layout);
        LayoutInflater inflater = LayoutInflater.from(context);
        bottomView = inflater.inflate(bottomViewId, null);
        addView(bottomView);
        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        viewPager.layout(0, 0, viewPager.getMeasuredWidth(), viewPager.getMeasuredHeight());
        bottomView.layout(0, viewPager.getMeasuredHeight() - bottomView.getMeasuredHeight(), bottomView.getMeasuredWidth(), viewPager.getMeasuredHeight());

    }



    public void setAdapter(PagerAdapter adapter)
    {
        this.adapter=adapter;
        viewPager.setAdapter(adapter);
        requestLoadNext();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        switch(ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                cancelLoadNext();
                break;
            case MotionEvent.ACTION_UP:
                requestLoadNext();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    private void cancelLoadNext()
    {
        handler.removeMessages(LOAD_NEXT);
    }

    public interface OnPageChangeListener
    {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);

    }

    public void setOnPageChangeListener(OnPageChangeListener listener)
    {
        this.listener=listener;
    }
}

