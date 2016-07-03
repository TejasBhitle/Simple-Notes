package tejas.recyclerview1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class About_Screen extends AppCompatActivity {

    private Toolbar mytoolbar;
    SharedPreferences sharedPreferences;
    boolean isDark;
    CardView card1,card2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("About");
        sharedPreferences=getSharedPreferences("prefs",MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("isDark",true);

        if(isDark)
            setTheme(R.style.DarkAppTheme);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.about_screen);


        card1=(CardView)findViewById(R.id.card1);
        card2=(CardView)findViewById(R.id.card2);




        if(isDark){
            card1.setBackgroundColor(getResources().getColor(R.color.dark_grey));
            card2.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        }
        else{
            card1.setBackgroundColor(getResources().getColor(R.color.light_theme_background));
            card2.setBackgroundColor(getResources().getColor(R.color.light_theme_background));
        }

        // Set a Toolbar to replace the ActionBar.
        mytoolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();

    }

    public void github_link(View view) {
        Uri uri =Uri.parse(getResources().getString(R.string.github_link));
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    public void email(View v){
        String TO = getResources().getString(R.string.developer_mail);
        String SUBJ = getResources().getString(R.string.app_name);

        Intent sendEmail = new Intent();
        sendEmail.setAction(Intent.ACTION_SENDTO);
        sendEmail.setType("text/plain");
        sendEmail.setData(Uri.parse("mailto:" + TO));

        sendEmail.putExtra(Intent.EXTRA_EMAIL,TO);
        sendEmail.putExtra(Intent.EXTRA_SUBJECT,SUBJ);

        try{
            startActivity(Intent.createChooser(sendEmail,"Send Mail"));
        }
        catch(android.content.ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.NoClientFound),Toast.LENGTH_SHORT).show();
        }
    }
}
