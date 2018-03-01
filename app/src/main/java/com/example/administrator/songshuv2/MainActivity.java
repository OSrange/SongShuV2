package com.example.administrator.songshuv2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button start;
    private Button ranking;
    private Button setting;
    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start=(Button)findViewById(R.id.start_bt);
        start.setOnClickListener(this);

        ranking=(Button)findViewById(R.id.ranking_bt);
        ranking.setOnClickListener(this);

        setting=(Button)findViewById(R.id.setting_bt);
        setting.setOnClickListener(this);

        exit=(Button)findViewById(R.id.exit_bt);
        exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_bt:
                Intent intent1=new Intent(this,PlayActivity.class);
                startActivity(intent1);
                break;
            case R.id.ranking_bt:
                Intent intent2=new Intent(this,RankingActivity.class);
                startActivity(intent2);
                break;
            case R.id.setting_bt:
                Intent intent3=new Intent(this,SettingActivity.class);
                startActivity(intent3);
                break;
            case R.id.exit_bt:
                finish();
                break;
            default:
                break;
        }
    }
}
