package tejas.recyclerview1;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity{

    Toolbar toolbar;
    SwitchCompat SortSwitch,DarkSwitch;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CardView card1,card2,card3;
    Boolean isOldestFirst,isDark;
    TextView Darktheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isDark= sharedPreferences.getBoolean("isDark",true);

        if(isDark)
            setTheme(R.style.DarkAppTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.setting_activity);

        SortSwitch =(SwitchCompat)findViewById(R.id.SortSwitch);

        card1=(CardView)findViewById(R.id.card1);
        card2=(CardView)findViewById(R.id.card2);



        //set toolbar
        toolbar =(Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if(sharedPreferences != null){
            isOldestFirst= sharedPreferences.getBoolean("isOldestFirst",true);
            isDark= sharedPreferences.getBoolean("isDark",true);
        }




        // SortSwitch
        if(isOldestFirst)
            SortSwitch.setChecked(true);
        else
            SortSwitch.setChecked(false);


        SortSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        SortSwitch(b);
                    }
                }
        );



        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearAllDialog();
            }
        });

    }

    private void DarkMode(boolean b) {
        if(b)
            isDark= true;
        else
            isDark=false;
        editor.putBoolean("isDark",isDark);
        editor.apply();
        recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(isDark){
            //settings_layout.setBackgroundColor(getResources().getColor(R.color.background_dark));
            card1.setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
            card2.setBackgroundColor(getResources().getColor(R.color.dark_theme_background));

        }
    }

    public void SortSwitch(boolean b){
        SortSwitch.setChecked(b);
        editor.putBoolean("isOldestFirst",b);
        editor.apply();
    }

    public void ClearAllDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All Notes ?")
                .setMessage("Notes in Trash will also be deleted")
                .setPositiveButton("Yes, Do It", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Delete All
                        DBHelper db = new DBHelper(getApplicationContext());
                        Delete_DBHelper delete_db = new Delete_DBHelper(getApplicationContext());
                        db.deleteAll();
                        delete_db.deleteAll();
                        Toast.makeText(getApplicationContext(),"Reset Successful",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No, Wait", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
