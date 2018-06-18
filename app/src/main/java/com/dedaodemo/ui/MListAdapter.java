package com.dedaodemo.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dedaodemo.model.Item;
import com.dedaodemo.R;
import com.dedaodemo.model.SongList;

import java.util.ArrayList;

/**
 * Created by asus on 2017/11/3.
 */

public class MListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<Item> itemData = null;
    private int ChooseItem;
    public MListAdapter(Context context){
        mContext=context;
    }

    public void setItemData(ArrayList<Item> data){
        itemData = data;
    }

    public void setChooseItem(int chooseItem) {
        ChooseItem = chooseItem;
    }

    public int getChooseItem() {
        return ChooseItem;
    }

    @Override
    public int getCount(){
       return itemData.size();
    }
    @Override
    public Item getItem(int position){
        return itemData.get(position);
    }
    @Override
    public long getItemId(int pos){
        return pos;
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        View v= LayoutInflater.from(mContext).inflate(R.layout.misc_item,parent,false);
        TextView tv_titile=(TextView)v.findViewById(R.id.tv_title);
        TextView tv_artist=(TextView)v.findViewById(R.id.tv_artist);
        LinearLayout linearLayout=(LinearLayout)v.findViewById(R.id.ll_item);

        tv_titile.setText(itemData.get(pos).getTitle());
        tv_artist.setText(itemData.get(pos).getAuthor());
//        TextView tv_time=(TextView)v.findViewById(R.id.tv_push_time);
//        TextView tv_author=(TextView)v.findViewById(R.id.tv_author);
//        TextView tv_title=(TextView)v.findViewById(R.id.tv_title);
//        TextView tv_content=(TextView)v.findViewById(R.id.tv_content);
//        Button btn_play=(Button) v.findViewById(R.id.btn_play);
//
//        tv_content.setText(data.get(pos).getContent());
//        tv_author.setText(data.get(pos).getAuthor());
//        tv_title.setText(data.get(pos).getTitle());
//        tv_time.setText(data.get(pos).getTime());
//        btn_play.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                mMediaPlayer=new MediaPlayer();
//                try{
//                    mMediaPlayer.setDataSource(Model.SRC);
//
//                    mMediaPlayer.prepareAsync();
//                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
//                    {
//                        @Override
//                        public void onPrepared(MediaPlayer mp)
//                        {
//                            mp.start();
//                            mOnPlayListener.onPlay(mMediaPlayer);
//                        }
//                    });
//                    Toast.makeText(mContext,"加载中",Toast.LENGTH_SHORT).show();
//                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
//                    {
//                        @Override
//                        public void onCompletion(MediaPlayer mp)
//                        {
//                            mp.release();
//                        }
//                    });
//
//                }catch (IllegalArgumentException e){
//                    e.printStackTrace();
//
//                }catch (IllegalStateException e){
//                    e.printStackTrace();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
        return v;
    }
}
