package tejas.recyclerview1;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class About_Screen extends AppCompatActivity {

    private Toolbar mytoolbar;
    SharedPreferences sharedPreferences;
    boolean isDark;
    CardView card1,card2;
    Button github_button,gplus,email_id;
    String TO,SUBJ;


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
        github_button =(Button)findViewById(R.id.github_button);
        gplus =(Button)findViewById(R.id.googleplus);
        email_id =(Button)findViewById(R.id.email_id);
        TO = getResources().getString(R.string.developer_mail);
        SUBJ = getResources().getString(R.string.app_name);


        if(isDark){
            card1.setCardBackgroundColor(getResources().getColor(R.color.dark_grey));
            card2.setCardBackgroundColor(getResources().getColor(R.color.dark_grey));
        }
        else{
            card1.setCardBackgroundColor(getResources().getColor(R.color.card_light));
            card2.setCardBackgroundColor(getResources().getColor(R.color.card_light));
        }

        // Set a Toolbar to replace the ActionBar.
        mytoolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link(view,0);
            }
        });

        github_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link(view,1);
            }
        });

        email_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email(view,TO ,SUBJ,null);
            }
        });
    }


    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();

    }

    public void link(View view,int choose) {
        Uri uri;
        if(choose == 1){
            uri =Uri.parse(getResources().getString(R.string.github_link));
        }
        else {
            uri = Uri.parse(getResources().getString(R.string.gplus_link));
        }

        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    public void email(View v,String TO ,String SUBJ,String MSG){
        //String TO = getResources().getString(R.string.developer_mail);
        //String SUBJ = getResources().getString(R.string.app_name);

        Intent sendEmail = new Intent();
        sendEmail.setAction(Intent.ACTION_SENDTO);
        sendEmail.setType("text/plain");
        sendEmail.setData(Uri.parse("mailto:" + TO));

        sendEmail.putExtra(Intent.EXTRA_EMAIL,TO);
        sendEmail.putExtra(Intent.EXTRA_SUBJECT,SUBJ);
        if(MSG != null)
           sendEmail.putExtra(Intent.EXTRA_TEXT,MSG);

        try{
            startActivity(Intent.createChooser(sendEmail,"Send Mail"));
        }
        catch(android.content.ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.NoClientFound),Toast.LENGTH_SHORT).show();
        }
    }

    public void rateApp(View view){
        Toast.makeText(getApplicationContext(),R.string.rateppmsg,Toast.LENGTH_SHORT).show();
    }

    public void feedback(View view){
        //Toast.makeText(getApplicationContext(),"Coming soon",Toast.LENGTH_SHORT).show();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.feedback_layout);

        final EditText description =(EditText)dialog.findViewById(R.id.feedback_edittext);



        Button send = (Button) dialog.findViewById(R.id.feedback_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RadioGroup radioGroup =(RadioGroup)dialog.findViewById(R.id.radiogroup);
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)dialog.findViewById(id);

                String msg1 = "Performance :"+ radioButton.getText().toString() +"\n\n";
                String msg2 = description.getText().toString();
                email(view,TO,SUBJ,msg1 + msg2);
            }
        });

        Button cancel =(Button)dialog.findViewById(R.id.feedback_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}