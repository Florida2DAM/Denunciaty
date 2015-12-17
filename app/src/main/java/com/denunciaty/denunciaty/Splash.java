package com.denunciaty.denunciaty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView img = (ImageView) findViewById(R.id.imageView);
        TextView tV = (TextView) findViewById(R.id.textView);
        Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);

        img.startAnimation(an);
        tV.startAnimation(an);
        findViewById(R.id.loadingPanel).startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                finish();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
