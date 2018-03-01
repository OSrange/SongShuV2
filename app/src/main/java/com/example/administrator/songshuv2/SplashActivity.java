package com.example.administrator.songshuv2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.songshuv2.bean.Person;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SplashActivity extends AppCompatActivity {

    private EditText nameEt;
    private Button loginBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //第一：默认初始化
        Bmob.initialize(this, "203d1338262e83d7ac2bfc5e797fab6b");

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
        //若已有用户数据，则直接跳转
        SharedPreferences pref=getSharedPreferences("UN",MODE_PRIVATE);
        String name=pref.getString("userName","");
        if (!"".equals(name)){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        nameEt=(EditText)findViewById(R.id.name_et);
        loginBt=(Button)findViewById(R.id.login_bt);

        //注册逻辑
        register();
    }

    private void register() {
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameEt.getText().toString();
                if ("".equals(name.trim())){
                    Toast.makeText(SplashActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if ("Vae".equals(name.trim())||"vae".equals(name.trim())||"嵩哥".equals(name.trim())||"许嵩".equals(name.trim())){
                    Toast.makeText(SplashActivity.this,"只有许嵩才能使用此用户名哦=-=",Toast.LENGTH_SHORT).show();
                }else {
                    //SharedPreferences存储
                    final SharedPreferences.Editor editor=getSharedPreferences("UN",MODE_PRIVATE).edit();
                    editor.putString("userName",name);
                    editor.putInt("userResults",0);
                    editor.apply();
                    //上传服务器
                    final Person person=new Person();
                    person.setUserName(name);
                    person.setUserResults(0);
                    person.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e==null){
                                String userId=person.getObjectId();
                                editor.putString("userId",userId);
                                editor.apply();
                                Toast.makeText(SplashActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(SplashActivity.this,"该昵称已被使用",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
