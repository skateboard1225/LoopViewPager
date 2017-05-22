package com.skateboard.loopviewdemo;

import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
{

    private LoopViewPager loopViewPager;
    private List<Integer> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataList.add(R.drawable.pic1);
        dataList.add(R.drawable.pic2);
        dataList.add(R.drawable.pic3);
        dataList.add(R.drawable.pic4);
        loopViewPager = (LoopViewPager) findViewById(R.id.loop_view);
        loopViewPager.setAdapter(new DataAdapter(dataList));
    }


    private class DataAdapter extends LoopViewPager.Adapter
    {

        List<Integer> dataList;

        DataAdapter(List<Integer> dataList)
        {
            this.dataList = dataList;
        }


        @Override
        public int getItemCount()
        {
            return dataList.size();
        }

        @Override
        public View getView(ViewGroup container, int position)
        {
            View view=LayoutInflater.from(container.getContext()).inflate(R.layout.item_layout,container,false);
            ImageView pic= (ImageView) view.findViewById(R.id.pic);
            pic.setImageResource(dataList.get(position));
            return view;
        }


    }
}
