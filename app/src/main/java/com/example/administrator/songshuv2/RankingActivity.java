package com.example.administrator.songshuv2;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.songshuv2.bean.Person;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class RankingActivity extends AppCompatActivity {

    private ListView rankingLv;

    private TextView userNameTv;
    private TextView userScoreTv;
    private TextView userRankingTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        userNameTv=(TextView)findViewById(R.id.userName_tv);
        userScoreTv=(TextView)findViewById(R.id.userScore_tv);
        userRankingTv=(TextView)findViewById(R.id.userRanking_tv);

        rankingLv=(ListView)findViewById(R.id.ranking_lv);
        //加载排行榜
        initRanking();

    }

    private void initRanking() {

        //加载用户记录
        SharedPreferences pref=getSharedPreferences("UN",MODE_PRIVATE);
        final String name=pref.getString("userName","");
        int record=pref.getInt("userResults",0);
        String userId=pref.getString("userId","");
        userNameTv.setText(name);
        userScoreTv.setText("我的记录: "+record+"只");

        BmobQuery<Person> bmobQuery=new BmobQuery<Person>();
        bmobQuery.addQueryKeys("userName,userResults");
        bmobQuery.order("-userResults");
        bmobQuery.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if (e==null){
                    rankingLv.setAdapter(new ArrayAdapter<Person>(RankingActivity.this,R.layout.ranking_item,list){
                        @NonNull
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Person person=getItem(position);
                            View view= LayoutInflater.from(getContext()).inflate(R.layout.ranking_item,parent,false);
                            TextView playerNameTv=(TextView)view.findViewById(R.id.playerName_tv);
                            TextView playerScoreTv=(TextView)view.findViewById(R.id.playerScore_tv);
                            TextView playerRankingTv=(TextView)view.findViewById(R.id.playerRanking_tv);
                            playerNameTv.setText(person.getUserName());
                            playerNameTv.setTextSize(17);
                            playerScoreTv.setText(Integer.toString(person.getUserResults())+"只");
                            playerScoreTv.setTextSize(17);
                            playerRankingTv.setText(Integer.toString(position+1));
                            playerRankingTv.setTextSize(17);
                            if (position==0){
                                playerNameTv.setTextColor(getResources().getColor(R.color.gold));
                                playerScoreTv.setTextColor(getResources().getColor(R.color.gold));
                                playerRankingTv.setTextColor(getResources().getColor(R.color.gold));
                            }
                            if (position==1){
                                playerNameTv.setTextColor(getResources().getColor(R.color.silver));
                                playerScoreTv.setTextColor(getResources().getColor(R.color.silver));
                                playerRankingTv.setTextColor(getResources().getColor(R.color.silver));
                            }
                            if (position==2){
                                playerNameTv.setTextColor(getResources().getColor(R.color.wood));
                                playerScoreTv.setTextColor(getResources().getColor(R.color.wood));
                                playerRankingTv.setTextColor(getResources().getColor(R.color.wood));
                            }
                            if (name.equals(person.getUserName())){
                                userRankingTv.setText("第"+(position+1)+"名");
                            }
                            return view;
                        }
                    });
                }else {
                    Toast.makeText(RankingActivity.this,"网络异常，查询排名失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
