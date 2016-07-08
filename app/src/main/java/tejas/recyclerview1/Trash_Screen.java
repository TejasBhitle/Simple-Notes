package tejas.recyclerview1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Trash_Screen extends AppCompatActivity {

    public RecyclerView myrecyclerView;
    public RecyclerView.Adapter Myadapter;
    public RecyclerView.LayoutManager MylayoutManager;

    private Toolbar mytoolbar;
    DBHelper db;
    Delete_DBHelper delete_db;
    String id;
    MyData data;
    public ArrayList<MyData> main_arrayList;
    SharedPreferences sharedPreferences;
    View main_Rela_layout;
    boolean isDark,isGrid,isOldestFirst;
    TextView blank_textview1;

    DrawerLayout drawerLayout;
    NavigationView nview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Trash Screen");
        sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("isDark",true);
        if(isDark)
            setTheme(R.style.DarkAppTheme);
        else
            setTheme(R.style.AppTheme);


        setContentView(R.layout.mainactivity);

        drawerLayout =(DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawerLayout !=null;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        nview =(NavigationView)findViewById(R.id.nvView);
        assert nview != null;
        nview.setVisibility(View.GONE);



        db= new DBHelper(this);
        delete_db = new Delete_DBHelper(this);

        // Set a Toolbar to replace the ActionBar.
        mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        main_Rela_layout = (View)findViewById(R.id.main_rela_layout);
        blank_textview1=(TextView)findViewById(R.id.blank_textview1);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.main_fab);

        //refreshing themes and looks
        if(sharedPreferences != null){
            isGrid = sharedPreferences.getBoolean("isGrid",true);
            isDark = sharedPreferences.getBoolean("isDark",true);
            isOldestFirst = sharedPreferences.getBoolean("isOldestFirst",true);
        }

        fab.setImageResource(R.drawable.svg_delete_forever_white_36px);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Trash_Screen.this);
                        builder.setTitle("CLEAR ALL")
                                .setMessage("This Action cannot be undone")
                                .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        delete_db.deleteAll();
                                        onResume();
                                    }
                                })
                                .setNegativeButton("No, Wait", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Do nothing
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
        );
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(isOldestFirst)
            main_arrayList = delete_db.getAllData();
        else
            main_arrayList = delete_db.getReversedata();


        myrecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        MylayoutManager = new LinearLayoutManager(this);
        myrecyclerView.setLayoutManager( new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        Myadapter = new CustomAdapter(main_arrayList,getApplicationContext());
        myrecyclerView.setAdapter(Myadapter);

        if(main_arrayList.size()==0)
            findViewById(R.id.blank_textview1).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.blank_textview1).setVisibility(View.GONE);

        myrecyclerView.addOnItemTouchListener(
                new MyRecyclerItemListener(getApplicationContext(), new MyRecyclerItemListener.OnClickItemInterface() {
                    @Override
                    public void onItemClick(View view, int position) {
                        data = main_arrayList.get(position);
                        id = data.getData_id();
                        ShowRestoreDialog(id);
                    }

                    @Override
                    public void onLongPress(View v, int position) {
                        //do nothing
                    }
                })
        );
    }
    private void ShowRestoreDialog(String id) {
        final int int_id = Integer.parseInt(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_prompt)
                .setMessage(R.string.restoreMessage)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete_db.deleteData(int_id);
                        onResume();
                        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Restore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.insertData(data.getData_Title(),data.getData_Content(),data.getData_date(),data.getData_color());
                        delete_db.deleteData(int_id);
                        onResume();
                        Toast.makeText(getApplicationContext(),"Restored",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            supportFinishAfterTransition();
        }
        return super.onKeyDown(keyCode, event);
    }


}
