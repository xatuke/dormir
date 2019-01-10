package xyz.crowdedgeek.dormir;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        ImageView logo = findViewById(R.id.im);
        TextView title = findViewById(R.id.tv);

        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        logo.startAnimation(slide_up);
        title.startAnimation(slide_up);

        new Handler().postDelayed( ()-> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
        }, 2000);
    }
}
