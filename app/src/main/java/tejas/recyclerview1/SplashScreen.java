package tejas.recyclerview1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {

    int color;

    private final int splash_timer = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        color = R.color.dark_theme_background;
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(color));
        }

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                },splash_timer
        );
    }
}
