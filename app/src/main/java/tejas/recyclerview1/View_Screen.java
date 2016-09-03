package tejas.recyclerview1;


import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class View_Screen extends AppCompatActivity
implements TextToSpeech.OnInitListener{

    private DBHelper db;
    private Delete_DBHelper delete_dbHelper;
    private TextToSpeech tts;
    TextView title,content,date;
    Integer id;
    View viewscreen;
    String title_string,content_string,date_string,color_string,time;
    Toolbar toolbar;
    SharedPreferences preferences;
    Boolean isLocked_boolean, isSpeechOn;
    String isLocked,PASSWORD;
    FloatingActionButton fab;
    Cursor cursor;
    Menu local_menu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        PASSWORD = preferences.getString("PASSWORD","");

        setContentView(R.layout.viewscreen);


        title =(TextView)findViewById(R.id.viewScreen_title);
        content=(TextView)findViewById(R.id.viewScreen_content);
        date= (TextView)findViewById(R.id.viewScreen_date);
        viewscreen =(View)findViewById(R.id.viewscreeen);

        tts = new TextToSpeech(this,this);
        isSpeechOn=false;

        toolbar =(Toolbar)findViewById(R.id.viewscreeen_toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.clear_black_36px);


        db = new DBHelper(this);
        delete_dbHelper= new Delete_DBHelper(this);
        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");

        cursor = db.getData(id);
        cursor.moveToFirst();
        title_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_TITLE));
        content_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_CONTENT));
        date_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE));
        time = cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE));
        color_string= cursor.getString(cursor.getColumnIndex(DBHelper.COL_COLOR));
        isLocked = cursor.getString(cursor.getColumnIndex(DBHelper.COL_IsLocked));
        cursor.close();

        title.setText(title_string);
        content.setText(content_string);
        date.setText(date_string);
        toolbar.setBackgroundColor(Integer.parseInt(color_string));
        viewscreen.setBackgroundColor(Integer.parseInt(color_string));



        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Integer.parseInt(color_string));
        }

        fab =(FloatingActionButton)findViewById(R.id.view_fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isSpeechOn){
                            tts.stop();
                            fab.setImageResource(R.drawable.create_black_36px);
                            isSpeechOn=false;
                        }
                        else
                          edit_pressed();
                    }
                }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();

        cursor = db.getData(id);
        cursor.moveToFirst();
        isLocked = cursor.getString(cursor.getColumnIndex(DBHelper.COL_IsLocked));
        cursor.close();

        if(isLocked.matches("true")){
            isLocked_boolean=true;
        }
        else{
            isLocked_boolean=false;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        local_menu=menu;
        getMenuInflater().inflate(R.menu.viewscreen_menu,menu);

        MenuItem item = (MenuItem)menu.findItem(R.id.viewscreen_lock);
        if(isLocked_boolean){
            //unlock icon
            item.setIcon(R.drawable.lock_open_black_36px);
        }
        else{
            //lock icon
            item.setIcon(R.drawable.lock_black_closed_36px);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.viewscreen_delete:
                ShowDeleteDialog();
                break;
            case R.id.viewscreen_share:
                ShareData(content_string,title_string);
                break;
            case R.id.viewscreen_notify:
                BuildNotification(title_string,content_string);
                break;
            case R.id.viewscreen_clipboard:
                CopyToClipboard(content_string,title_string);
                Toast.makeText(getApplicationContext(),R.string.clipboard,Toast.LENGTH_SHORT).show();
                break;
            case R.id.viewscreen_lock:
                lockedPressed();
                break;
            case R.id.viewscreen_speak:
                speak(content.getText().toString());
                break;
        }
        return true;
    }

    private void speak(String speaktext) {

        isSpeechOn=true;
        tts.speak(speaktext,TextToSpeech.QUEUE_FLUSH,null);
        fab.setImageResource(R.drawable.ic_stop_white_36px);

    }

    @Override
    protected void onDestroy() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.ENGLISH);
            tts.setSpeechRate((float)0.8);
            if(result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","This language is Not Supported");
            }

        }else
            Log.e("TTS","Intialization Failed");
    }

    private void lockedPressed() {
        if(PASSWORD.matches("")){
            Toast.makeText(getApplicationContext(),"PinCode not set",Toast.LENGTH_SHORT).show();
        }
        else{
            if(isLocked_boolean){
                isLocked="false";
                local_menu.findItem(R.id.viewscreen_lock).setIcon(R.drawable.lock_black_closed_36px);
                Toast.makeText(getApplicationContext(),"Unlocked",Toast.LENGTH_SHORT).show();
            }
            else{
                isLocked="true";
                local_menu.findItem(R.id.viewscreen_lock).setIcon(R.drawable.lock_open_black_36px);
                Toast.makeText(getApplicationContext(),"Locked",Toast.LENGTH_SHORT).show();
            }
            db.updateData(id,title_string,content_string,time,color_string,isLocked);
            onResume();
        }
    }

    private void CopyToClipboard(String content_string, String title_string) {
        ClipboardManager clipboard =(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text",title_string +"\n" + content_string);
        clipboard.setPrimaryClip(clip);
    }

    public void edit_pressed(){
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        Intent i = new Intent(View_Screen.this,Add_Screen.class);
        i.putExtras(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            Pair< View,String> p1 = Pair.create((View)fab,"trans_add_fab");
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(View_Screen.this);
            finish();
            startActivity(i, options.toBundle());
        }
        else
            startActivity(i);
    }

    public void ShowDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_prompt)
                .setTitle("DELETE")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void  onClick(DialogInterface dialogInterface, int i) {
                        Add_to_Trash();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nothing
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //alertDialog.getWindow().setBackgroundDrawableResource(R.color.tangerine);
    }

    public void Add_to_Trash(){
        Calendar calendar =Calendar.getInstance();
        time = calendar.getTime().toString().substring(0,16);
        delete_dbHelper.insertData(title_string,content_string,time,color_string,isLocked);

        db.deleteData(id);
       Toast.makeText(View_Screen.this,R.string.trash_move,Toast.LENGTH_SHORT).show();
        supportFinishAfterTransition();
    }

    public void ShareData(String content,String title){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, title+"\n" +content);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i,"Choose App to Share"));
    }

    public void BuildNotification(String title,String Content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.my_photoshop_icon)
                .setContentTitle(title)
                .setContentText(Content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Content));

        /*Intent i =new Intent(this,MainActivity.class);
       PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);*/

        NotificationManager manager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

    @Override
    public void onBackPressed() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            supportFinishAfterTransition();
        else
        finish();
        //overridePendingTransition(R.anim.slide_out_up,R.anim.slide_in_up);
        super.onBackPressed();
    }


}
