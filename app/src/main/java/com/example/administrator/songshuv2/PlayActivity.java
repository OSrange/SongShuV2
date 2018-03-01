package com.example.administrator.songshuv2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.songshuv2.bean.Person;
import com.example.administrator.songshuv2.util.PrefsUtil;

import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class PlayActivity extends AppCompatActivity{

    private MediaPlayer mediaPlayer=new MediaPlayer();

    private int width;
    private int height;


    private TextView countDownTv;
    private TextView songshuNumTv;

    private ImageView songshuIv;
    private ImageView geluomiIv;
    private ImageView backpackIv;
    private ImageView musicSwitchIv;

    private TextView increaseTv;

    private int count=4;
    private static final int COUNT_TIME=0;
    private static final int DO_NEXT=1;
    private static final int ALIVE=2;
    private static final int GELUOMI=3;

    private boolean IsGeluomi=false;
    private boolean IsSongshu=true;

    private long timeInterval=2100;
    private long animDuration=1000;
    private int songshuNum;
    private int ranking;
    private double beatPercentage;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        countDownTv=(TextView)findViewById(R.id.countDown_tv);
        songshuNumTv=(TextView)findViewById(R.id.songshuNum_tv);

        //获取屏幕宽高并做误差处理
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width=dm.widthPixels-200;
        height=dm.heightPixels-400;

        //图片点击事件处理
        songshuIv=(ImageView)findViewById(R.id.songshu_iv);
        backpackIv=(ImageView)findViewById(R.id.backpack_iv);
        increaseTv=(TextView)findViewById(R.id.increase_tv);
        songshuIv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                songshuIv.setEnabled(false);
                songshuNum++;
                IsSongshu=true;
                songshuAnim();
                songshuNumTv.setText("捕获嵩鼠"+songshuNum+"只");
                handler.sendEmptyMessageDelayed(DO_NEXT,timeInterval);
                return false;
            }
        });
        geluomiIv=(ImageView)findViewById(R.id.geluomi_iv);
        geluomiIv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                IsGeluomi=true;
                geluomiIv.setEnabled(false);
                gameover();
                return false;
            }
        });

        musicSwitchIv=(ImageView)findViewById(R.id.musicIcon_iv);
        musicSwitchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                musicSwitchIv.setImageResource(R.drawable.music_off);
                musicSwitchIv.setEnabled(false);
            }
        });
        //加载背景音乐
        initMusic();
        //开始游戏
        initPlay();
    }

    //捕获嵩鼠动画
    private void songshuAnim(){
        AnimationSet set=new AnimationSet(true);
        TranslateAnimation translateAnimation=
                new TranslateAnimation(0,backpackIv.getX()-songshuIv.getX(),0,backpackIv.getY()-songshuIv.getY());
        AlphaAnimation alphaAnimation=new AlphaAnimation(1,0.4f);
        set.addAnimation(translateAnimation);
        set.addAnimation(alphaAnimation);
        set.setDuration(800);
        songshuIv.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                songshuIv.setEnabled(true);
                increaseAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        songshuIv.setVisibility(View.INVISIBLE);
    }
    //+1动画
    private void increaseAnim() {
        increaseTv.setVisibility(View.VISIBLE);
        AnimationSet set=new AnimationSet(true);
        TranslateAnimation translateAnimation=new TranslateAnimation(0,90,0,-120);
        AlphaAnimation alphaAnimation=new AlphaAnimation(1,0);
        set.addAnimation(translateAnimation);
        set.addAnimation(alphaAnimation);
        set.setDuration(animDuration);
        set.setFillAfter(false);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                increaseTv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        increaseTv.startAnimation(set);
    }

    private void initMusic() {
        SharedPreferences prefs=getSharedPreferences(PrefsUtil.SETTING,MODE_PRIVATE);
        if (prefs.getBoolean(PrefsUtil.MUSIC_PLAY,true)){
            String musicPath=prefs.getString(PrefsUtil.SAVE_PATH,"");
            if (musicPath.equals("")){
                mediaPlayer=MediaPlayer.create(this,R.raw.yhbk);
                mediaPlayer.start();
            }else {
                try {
                    if (prefs.getBoolean(PrefsUtil.RANDOM_PLAY,false)){
                        String[] s=prefs.getString(PrefsUtil.DOWNLOAD_STRING,"").split("0");
                        if (s.length>1){
                            int i=(int) (1+Math.random()*(s.length-1));
                            musicPath=getApplicationContext().getExternalCacheDir()+"/bmob/"+s[i-1];
                        }
                    }
                    mediaPlayer.setDataSource(musicPath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //处理嵩鼠与格洛米View出现与消失的逻辑
    private void initPlay() {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case COUNT_TIME:
                        if (count<=0){
                            countDownTv.setText("开始！");
                            sendEmptyMessageDelayed(DO_NEXT,1000);
                            return;
                        }
                        count--;
                        countDownTv.setText(""+count);
                        sendEmptyMessageDelayed(COUNT_TIME,1000);
                        break;
                    case DO_NEXT:
                        countDownTv.setText("");
                        geluomiIv.setVisibility(View.INVISIBLE);
                        double i=Math.random()*4;
                        float x=(float) Math.random()*width;
                        float y=(float) Math.random()*height;
                        if (songshuNum<31){
                            animDuration=animDuration-20;
                            timeInterval=songshuNum*songshuNum-80*songshuNum+2100;
                        }else {
                            animDuration=600;
                            timeInterval=600;
                        }
                        if (i<3){
                            songshuIv.setX(x);
                            songshuIv.setY(y);
                            IsSongshu=false;
                            songshuIv.setVisibility(View.VISIBLE);
                            sendEmptyMessageDelayed(ALIVE,timeInterval);
                        }else{
                            geluomiIv.setX(x);
                            geluomiIv.setY(y);
                            geluomiIv.setVisibility(View.VISIBLE);
                            sendEmptyMessageDelayed(GELUOMI,timeInterval);
                        }
                        break;
                    case ALIVE:
                        if (!IsSongshu){
                            songshuIv.setVisibility(View.INVISIBLE);
                            gameover();
                            return;
                        }
                        break;
                    case GELUOMI:
                        if (IsGeluomi){
                            return;
                        }else {
                            sendEmptyMessage(DO_NEXT);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        handler.sendEmptyMessage(COUNT_TIME);
    }

    //游戏结束逻辑
    private void gameover() {
        //停止播放背景音乐
        try {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //计算本场成绩当前排名并显示对话框
        BmobQuery<Person> bmobQuery=new BmobQuery<Person>();
        bmobQuery.addQueryKeys("userResults");
        bmobQuery.order("userResults");
        bmobQuery.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if (e==null){
                    int i=0;
                    Person user=list.get(i);
                    while (songshuNum>user.getUserResults()&&!(i==list.size())){
                        i++;
                        user=list.get(i);
                    }
                    ranking=list.size()-i+1;
                    beatPercentage=i*100/list.size();
                    //显示对话框
                    AlertDialog.Builder dialog=new AlertDialog.Builder(PlayActivity.this);
                    if(IsGeluomi){
                        dialog.setIcon(R.drawable.geluomi);
                        dialog.setTitle("你打到了格洛米== ,游戏结束");
                    }else {
                        dialog.setIcon(R.drawable.songshu);
                        dialog.setTitle("嵩鼠溜了=-= ,游戏结束");
                    }
                    dialog.setMessage("捕获了"+songshuNum+"只嵩鼠\n"+"排名第"+ranking+"名，"+"共击败"+beatPercentage+"%的嵩鼠!");
                    dialog.setPositiveButton("再来一局", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            recreate();
                        }
                    });
                    dialog.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });

        //数据存储
        SharedPreferences pref=getSharedPreferences("UN",MODE_PRIVATE);
        int results=pref.getInt("userResults",0);
        String userId=pref.getString("userId","");
        if (results<songshuNum){
            //记录本地存储并上传服务器
            Person person=new Person();
            person.setUserResults(songshuNum);
            person.update(userId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e==null){
                        SharedPreferences.Editor editor=getSharedPreferences("UN",MODE_PRIVATE).edit();
                        editor.putInt("userResults",songshuNum);
                        editor.apply();
                        Toast.makeText(PlayActivity.this,"记录已更新！",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PlayActivity.this,"网络异常，成绩上传失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

}
