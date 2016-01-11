package com.denunciaty.denunciaty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class Splash extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "	XpsIxnsZy8AndmfqkGoANvxhh";
    private static final String TWITTER_SECRET = "wI61xZqnScb78BZxD6kAyWT3SxhXSqVGCtcJJ6QfQtmPFTUKZQ";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_splash);

        ImageView img = (ImageView) findViewById(R.id.imageView);
        TextView tV = (TextView) findViewById(R.id.textView);
        Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
        Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.move);

        //img.startAnimation(an);
        tV.startAnimation(an);
        img.startAnimation(an2);
        findViewById(R.id.loadingPanel).startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                finish();
                Intent i = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
