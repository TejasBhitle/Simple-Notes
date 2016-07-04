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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class View_Screen extends AppCompatActivity{

    private DBHelper db;
    private Delete_DBHelper delete_dbHelper;
    TextView title,content,date;
    Integer id;
    View top_layout,bottom_layout;
    public String title_string,content_string,date_string,color_string;
    Toolbar toolbar;
    SharedPreferences preferences;
    Boolean isDark;
    FloatingActionButton fab;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        isDark= preferences.getBoolean("isDark",true);
        if(isDark){
            setTheme(R.style.DarkAppTheme);
        }
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.new_viewscreen);


        title =(TextView)findViewById(R.id.viewScreen_title);
        content=(TextView)findViewById(R.id.viewScreen_content);
        date= (TextView)findViewById(R.id.viewScreen_date);
        bottom_layout=(View)findViewById(R.id.viewscreen_bottom_layout);
        top_layout=(View)findViewById(R.id.viewScreen_toplayout);

        toolbar =(Toolbar)findViewById(R.id.viewscreeen_toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(isDark){
            bottom_layout.setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
        }

        db = new DBHelper(this);
        delete_dbHelper= new Delete_DBHelper(this);
        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");

        Cursor cursor = db.getData(id);
        cursor.moveToFirst();
        title_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_TITLE));
        content_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_CONTENT));
        date_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE));
        color_string= cursor.getString(cursor.getColumnIndex(DBHelper.COL_COLOR));
        cursor.close();

        title.setText(title_string);
        content.setText(content_string);
        date.setText(date_string);
        toolbar.setBackgroundColor(Integer.parseInt(color_string));
        top_layout.setBackgroundColor(Integer.parseInt(color_string));

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Integer.parseInt(color_string));
        }

        fab =(FloatingActionButton)findViewById(R.id.view_fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edit_pressed();
                    }
                }
        );



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewscreen_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
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
        }
        return true;
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
                    .makeSceneTransitionAnimation(View_Screen.this,p1);
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
        delete_dbHelper.insertData(title_string,content_string,java.text.DateFormat.getDateTimeInstance().format(new Date()),color_string);
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
        builder.setSmallIcon(R.drawable.my_app_icon)
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
