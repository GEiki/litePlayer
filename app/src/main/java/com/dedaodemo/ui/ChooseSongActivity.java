package com.dedaodemo.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dedaodemo.adapter.ChooseAdapter;
import com.dedaodemo.bean.Item;
import com.dedaodemo.MyDatabaseHelper;
import com.dedaodemo.R;
import com.dedaodemo.bean.SongList;
import com.dedaodemo.ViewModel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChooseSongActivity extends AppCompatActivity {

    private static final int LOAD_FINISH =1;
    public static final int REQUEST_CODE = 123;

    private ListView listView;
    private TextView tv_finish;
    private ArrayList<Item> items = new ArrayList<>();
    private SongViewModel songViewModel;
    MyDatabaseHelper databaseHelper;
    Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_FINISH:{
                    ChooseAdapter chooseAdapter = new ChooseAdapter(ChooseSongActivity.this,items);
                    listView.setAdapter(chooseAdapter);
                    chooseAdapter.notifyDataSetChanged();
                    break;
                }
                default:break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_song);
        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);
        tv_finish = (TextView)findViewById(R.id.tv_finish);
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Item> arrayList = ((ChooseAdapter)listView.getAdapter()).getChooseItems();
                if(arrayList.isEmpty()){
                    Intent intent = new Intent(ChooseSongActivity.this,MainActivity.class);
                    setResult(RESULT_CANCELED,intent);
                    finish();
                }
                int songListIndex = getIntent().getIntExtra("song_list_index",0);

                List<SongList> songLists = songViewModel.getSongLists();
                songViewModel.addSongToSongList(arrayList,songLists.get(songListIndex));

                Intent intent = new Intent(ChooseSongActivity.this,MainActivity.class);
                setResult(RESULT_OK,intent);
                finish();


            }
        });
        databaseHelper =new MyDatabaseHelper(this,"misc_db",null,1);

        List<Item>  songs = (List<Item>) (songViewModel.getSongLists().get(0).getSongList().getValue());
        items = (ArrayList<Item>) songs;
        mhandler.sendEmptyMessage(LOAD_FINISH);


        listView = (ListView)findViewById(R.id.list_view);
        ChooseAdapter adapter = new ChooseAdapter(this,items);
        listView.setAdapter(adapter);

    }

    private void loadDataFromDB() {
        if (databaseHelper != null && items.isEmpty()) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            SongList songList = new SongList();
            songList.setTitle("全部歌曲");
            Cursor cursor = db.query(songList.getTableName(), null, null, null, null, null, "id", null);
            while (cursor.moveToNext()) {
                Item a = new Item();
                a.setTitle(cursor.getString(1));//title
                a.setAuthor(cursor.getString(2));//Author
                a.setTime(cursor.getString(3));//time
                a.setPath(cursor.getString(4));//path
                a.setSize(String.valueOf(cursor.getInt(5)));//size
                items.add(a);
            }
            cursor.close();


            db.close();
        }
    }
}
