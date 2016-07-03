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

    private DrawerLayout myDrawer;
    private NavigationView myNavigationDrawer;
    private ActionBarDrawerToggle mydrawerToggle;
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



        db= new DBHelper(this);
        delete_db = new Delete_DBHelper(this);

        // Set a Toolbar to replace the ActionBar.
        mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        // Setup drawer view
        myNavigationDrawer = (NavigationView) findViewById(R.id.nvView);// in activity main . also contains list of items
        setupDrawerContent(myNavigationDrawer);


        //for hamburger icon
        myDrawer =(DrawerLayout)findViewById(R.id.drawer_layout);//drawer_layout in activity_main
        mydrawerToggle = setupDrawerToggle();
        myDrawer.addDrawerListener(mydrawerToggle);


        main_Rela_layout = (View)findViewById(R.id.main_rela_layout);
        blank_textview1=(TextView)findViewById(R.id.blank_textview1);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.main_fab);

        //refreshing themes and looks
        if(sharedPreferences != null){
            isGrid = sharedPreferences.getBoolean("isGrid",true);
            isDark = sharedPreferences.getBoolean("isDark",true);
            isOldestFirst = sharedPreferences.getBoolean("isOldestFirst",true);
        }
        /*if(isDark){
            blank_textview1.setTextColor(getResources().getColor(R.color.background_light));
        }
        else{
            blank_textview1.setTextColor(getResources().getColor(R.color.background_dark));
        }*/

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
                    public boolean onLongPress(View v, int position) {
                        //do nothing
                        return true;
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


    //for hamburger icon
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, myDrawer, mytoolbar,R.string.drawer_open,R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        //Add your switch Case
        switch (menuItem.getItemId())
        {
            case R.id.menu_LISTS:
                Intent i = new Intent(Trash_Screen.this,MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(Trash_Screen.this);
                supportFinishAfterTransition();
                startActivity(i, options.toBundle());
                break;

            case R.id.menu_ABOUT:
                Intent intent = new Intent(Trash_Screen.this,About_Screen.class);
                ActivityOptionsCompat option2 = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(Trash_Screen.this);
                supportFinishAfterTransition();
                startActivity(intent, option2.toBundle());
                break;

            case R.id.menu_TRASH:
                //Do Nothing
                break;

            case R.id.menu_SETTINGS:
                Intent a = new Intent(Trash_Screen.this,SettingsActivity.class);
                ActivityOptionsCompat option3 = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(Trash_Screen.this);
                supportFinishAfterTransition();
                startActivity(a, option3.toBundle());
                break;
        }
        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);

        // Set action bar title
        //setTitle(menuItem.getTitle());

        // Close the navigation drawer
        myDrawer.closeDrawers();
    }



    //To open the drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                myDrawer.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mydrawerToggle.syncState();
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
