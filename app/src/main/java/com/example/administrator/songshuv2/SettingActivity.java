package com.example.administrator.songshuv2;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.songshuv2.bean.Music;
import com.example.administrator.songshuv2.util.PrefsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CHOOSEFILE = 0;
    private ImageView musicSwitchIv;
    private Button musicListBt;
    private Button backBt;
    private ListView musicLv;
    private TextView selectedMusicTv;
    private ImageView randomPlayIv;
    private ImageView localSelectIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        backBt=(Button)findViewById(R.id.back_bt);
        backBt.setOnClickListener(this);
        musicSwitchIv=(ImageView) findViewById(R.id.musicSwitch_iv);
        musicSwitchIv.setOnClickListener(this);
        musicListBt=(Button)findViewById(R.id.musicList_bt);
        musicListBt.setOnClickListener(this);
        randomPlayIv=(ImageView) findViewById(R.id.randomPlay_iv);
        randomPlayIv.setOnClickListener(this);
        localSelectIv=(ImageView)findViewById(R.id.localSelect_iv);
        localSelectIv.setOnClickListener(this);
        selectedMusicTv=(TextView)findViewById(R.id.selectedMusic_tv);

        //加载用户设置
        initSettings();

        musicLv=(ListView)findViewById(R.id.music_lv);
        //music item点击事件，点击下载并更换背景音乐
        musicLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final TextView musicNameTv=(TextView)view.findViewById(R.id.musicName_tv);
                final ImageView downloadIv=(ImageView)view.findViewById(R.id.download_iv);
                if (downloadIv.getVisibility()==View.VISIBLE){
                    AlertDialog.Builder dialog=new AlertDialog.Builder(SettingActivity.this);
                    dialog.setTitle("是否下载该曲目");
                    dialog.setMessage(musicNameTv.getText());
                    dialog.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            BmobQuery<Music> bmobQuery=new BmobQuery<Music>();
                            bmobQuery.addWhereEqualTo("musicName",musicNameTv.getText());
                            bmobQuery.findObjects(new FindListener<Music>() {
                                @Override
                                public void done(List<Music> list, BmobException e) {
                                    final BmobFile musicFile=list.get(0).getMusicFile();
                                    if (musicFile!=null){
                                        final File saveFile=new File(getApplicationContext().getExternalCacheDir()+"/bmob/",musicFile.getFilename());
                                        musicFile.download(saveFile,new DownloadFileListener() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                musicNameTv.setTextColor(getResources().getColor(R.color.black));
                                                downloadIv.setVisibility(View.INVISIBLE);
                                                selectedMusicTv.setText(musicNameTv.getText().toString());
                                                final SharedPreferences.Editor editor=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE).edit();
                                                final SharedPreferences prefs=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE);
                                                editor.putString(PrefsUtil.DOWNLOAD_STRING,prefs.getString(PrefsUtil.DOWNLOAD_STRING,"")+musicFile.getFilename()+"0");
                                                editor.putString(musicNameTv.getText().toString(),musicFile.getFilename());
                                                editor.putString(PrefsUtil.SELECTED_MUSIC_NAME,musicNameTv.getText().toString());
                                                editor.putString(PrefsUtil.SAVE_PATH,s);
                                                editor.apply();
                                                Toast.makeText(SettingActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onProgress(Integer integer, long l) {

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setCancelable(true);
                    dialog.show();
                }else{
                    selectedMusicTv.setText(musicNameTv.getText());

                    final SharedPreferences prefs=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE);
                    final SharedPreferences.Editor editor=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE).edit();
                    Toast.makeText(SettingActivity.this,getApplicationContext().getExternalCacheDir()+"/bmob/"+
                            prefs.getString(musicNameTv.getText().toString(),""),Toast.LENGTH_SHORT).show();
                    editor.putString(PrefsUtil.SAVE_PATH,getApplicationContext().getExternalCacheDir()+"/bmob/"+
                            prefs.getString(musicNameTv.getText().toString(),""));
                    editor.putString(PrefsUtil.SELECTED_MUSIC_NAME,musicNameTv.getText().toString());
                    editor.apply();
                    musicLv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initSettings() {
        final SharedPreferences prefs=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE);

        if (prefs.getBoolean(PrefsUtil.MUSIC_PLAY,true)){
            musicSwitchIv.setImageResource(R.drawable.switch_on);
        }else {
            musicSwitchIv.setImageResource(R.drawable.switch_off);
        }

        if (prefs.getBoolean(PrefsUtil.RANDOM_PLAY,false)){
            randomPlayIv.setImageResource(R.drawable.switch_on);
        }else {
            randomPlayIv.setImageResource(R.drawable.switch_off);
        }

        final String selectedMusicName=prefs.getString(PrefsUtil.SELECTED_MUSIC_NAME,"");
        if (!selectedMusicName.equals("")){
            selectedMusicTv.setText(selectedMusicName);
        }
    }

    //加载音乐列表方法
    private void initMusicList() {
        final SharedPreferences.Editor editor=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE).edit();
        final SharedPreferences prefs=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE);
        if (prefs.getString(PrefsUtil.MUSIC_NAME_STRING,"").equals("")){
            BmobQuery<Music> bmobQuery=new BmobQuery<>();
            bmobQuery.addQueryKeys("musicName");
            bmobQuery.findObjects(new FindListener<Music>() {
                @Override
                public void done(List<Music> list, BmobException e) {
                    if (e==null){
                        String MUSIC_NAME_STRING="";
                        for (int i=0;i<list.size();i++){
                            MUSIC_NAME_STRING+=list.get(i).getMusicName()+"0";
                        }
                        editor.putString(PrefsUtil.MUSIC_NAME_STRING,MUSIC_NAME_STRING);
                        editor.apply();
                        musicLv.setAdapter(new ArrayAdapter<Music>(SettingActivity.this,R.layout.music_item, list){
                            @NonNull
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                Music music=getItem(position);
                                View view= LayoutInflater.from(getContext()).inflate(R.layout.music_item,parent,false);
                                TextView musicNameTv=(TextView)view.findViewById(R.id.musicName_tv);
                                musicNameTv.setText(music.getMusicName());
                                return view;
                            }
                        });
                    }else {
                        Toast.makeText(SettingActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            List<String> musicNameList=new ArrayList<>();
            String[] s=prefs.getString(PrefsUtil.MUSIC_NAME_STRING,"").split("0");
            for (int i=0;i<s.length;i++){
                musicNameList.add(s[i]);
            }
            musicLv.setAdapter(new ArrayAdapter<String>(SettingActivity.this,R.layout.music_item, musicNameList){
                @NonNull
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    String musicName=getItem(position);
                    View view=LayoutInflater.from(getContext()).inflate(R.layout.music_item,parent,false);
                    TextView musicNameTv=(TextView)view.findViewById(R.id.musicName_tv);
                    ImageView downloadIv=(ImageView)view.findViewById(R.id.download_iv);
                    musicNameTv.setText(musicName);
                    if (!prefs.getString(musicName,"").equals("")){
                        musicNameTv.setTextColor(getResources().getColor(R.color.black));
                        downloadIv.setVisibility(View.INVISIBLE);
                    }
                    return view;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case REQUEST_CHOOSEFILE:
                    Uri uri=data.getData();
                    String chooseFilePath=null;
                    if (Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT){
                        //4.4以后
                        chooseFilePath=getRealFilePath(getBaseContext(),uri);
                    }
                    File file=new File(chooseFilePath);
                    Toast.makeText(SettingActivity.this,chooseFilePath,Toast.LENGTH_LONG).show();
                    selectedMusicTv.setText(file.getName());
                    SharedPreferences.Editor editor=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE).edit();
                    editor.putString(PrefsUtil.SELECTED_MUSIC_NAME,file.getName());
                    editor.putString(PrefsUtil.SAVE_PATH,chooseFilePath);
                    editor.apply();
                    break;
            }
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Audio.AudioColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Audio.AudioColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    public void onClick(View v) {
        final SharedPreferences.Editor editor=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE).edit();
        final SharedPreferences prefs=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE);
        switch (v.getId()){
            case R.id.musicSwitch_iv:
                editor.putBoolean(PrefsUtil.MUSIC_PLAY,!prefs.getBoolean(PrefsUtil.MUSIC_PLAY,true));
                editor.apply();
                if (prefs.getBoolean(PrefsUtil.MUSIC_PLAY,true)){
                    musicSwitchIv.setImageResource(R.drawable.switch_on);
                    Toast.makeText(this,"游戏音乐已开启",Toast.LENGTH_SHORT).show();
                }else {
                    musicSwitchIv.setImageResource(R.drawable.switch_off);
                    Toast.makeText(this,"游戏音乐已关闭",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.musicList_bt:
                if (musicLv.getVisibility()==View.GONE){
                    initMusicList();
                    musicLv.setVisibility(View.VISIBLE);
                }else {
                    musicLv.setVisibility(View.GONE);
                }
                break;
            case R.id.randomPlay_iv:
                editor.putBoolean(PrefsUtil.RANDOM_PLAY,!prefs.getBoolean(PrefsUtil.RANDOM_PLAY,false));
                editor.apply();
                if (prefs.getBoolean(PrefsUtil.RANDOM_PLAY,false)){
                    randomPlayIv.setImageResource(R.drawable.switch_on);
                }else {
                    randomPlayIv.setImageResource(R.drawable.switch_off);
                }
                break;
            case R.id.localSelect_iv:
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,REQUEST_CHOOSEFILE);
                break;
            case R.id.back_bt:
                finish();
                break;
            default:
                break;
        }
    }
}
