package tejas.recyclerview1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
    SharedPreferences preferences;
    boolean isDark;

    private static int splash_timer = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        isDark=preferences.getBoolean("isDark",false);
        if(isDark)
            setTheme(R.style.DarkAppTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.splash_screen);

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
