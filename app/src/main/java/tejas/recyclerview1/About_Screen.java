package tejas.recyclerview1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class About_Screen extends AppCompatActivity {

    private Toolbar mytoolbar;
    SharedPreferences sharedPreferences;
    CardView card1,card2,card3;
    Button github_button,gplus,email_id,linkedIn;
    String TO,SUBJ;
    View changeLog;
    ImageView appicon,profilepic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("About");
        sharedPreferences=getSharedPreferences("prefs",MODE_PRIVATE);

        setContentView(R.layout.about_screen);

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_primaryDark));
        }


        card1=(CardView)findViewById(R.id.card1);
        card2=(CardView)findViewById(R.id.card2);
        card3=(CardView)findViewById(R.id.card3);
        github_button =(Button)findViewById(R.id.github_button);
        gplus =(Button)findViewById(R.id.googleplus);
        changeLog =(View)findViewById(R.id.changeLog);
        email_id =(Button)findViewById(R.id.email_id);
        linkedIn =(Button)findViewById(R.id.linkedin);
        appicon =(ImageView)findViewById(R.id.app_icon);
        profilepic =(ImageView)findViewById(R.id.profile_pic);

        Picasso.with(this).load(R.drawable.my_photoshop_icon).into(appicon);
        Picasso.with(this).load(R.drawable.me).into(profilepic);

        TO = getResources().getString(R.string.developer_mail);
        SUBJ = getResources().getString(R.string.app_name);





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

        linkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link(view,2);
            }
        });

        email_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email(view,TO ,SUBJ,null);
            }
        });

        changeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLog(view);
            }
        });
    }

    private void showChangeLog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ChangeLog")
                .setMessage(R.string.changeLog)
                 .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         dialogInterface.dismiss();
                     }
                 });
        AlertDialog dialog = builder.create();
        dialog.show();

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
        else if(choose == 0) {
            uri = Uri.parse(getResources().getString(R.string.gplus_link));
        }
        else {
            uri = Uri.parse(getResources().getString(R.string.linkedIn_link));
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