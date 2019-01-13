package xyz.crowdedgeek.dormir;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
        getWindow().setExitTransition(new Explode());
        getWindow().setEnterTransition(new Fade());
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
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                finish();
        }, 1500);
    }
}
