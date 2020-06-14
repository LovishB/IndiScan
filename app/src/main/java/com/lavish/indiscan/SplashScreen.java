package com.lavish.indiscan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private ImageView text;
    private Animation splashone;
    private int flag;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        text=findViewById(R.id.splash_text);
        splashone= AnimationUtils.loadAnimation(this,R.anim.spashone);
        text.startAnimation(splashone);
        flag=0;


        if(!getSharedPreferences("My prefs",Context.MODE_PRIVATE).contains("MyID")) {
            sharedpreferences = getSharedPreferences("My prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("MyID", "id-575757");
            editor.commit();

        }else{
            flag=1;

        }

        new CountDownTimer(1200,5000){

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if(flag==0){
                    Intent intent=new Intent(SplashScreen.this,Welcome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }


            }
        }.start();
    }
}
