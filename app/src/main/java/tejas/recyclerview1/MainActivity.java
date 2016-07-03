package tejas.recyclerview1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public RecyclerView myrecyclerView;
    public RecyclerView.Adapter Myadapter;
    public RecyclerView.LayoutManager MylayoutManager;

    private DrawerLayout myDrawer;
    private NavigationView myNavigationDrawer;
    private ActionBarDrawerToggle mydrawerToggle;
    private Toolbar mytoolbar;
    DBHelper db;

    private boolean backPressedToExitOnce = false;
    private boolean isOldestFirst,isDark,isGrid;//if true then list view else grid view
    private Toast toast = null;

    public ArrayList<MyData> main_arrayList;
    MyData data;
    String id;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView blank_textview1,title;
    Menu local_menu;
    FloatingActionButton fab;
    View main_Rela_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isDark = sharedPreferences.getBoolean("isDark",true);
        if(isDark)
            setTheme(R.style.DarkAppTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.mainactivity);

        db = new DBHelper(this);
        main_Rela_layout = (View)findViewById(R.id.main_rela_layout);


        // Set a Toolbar to replace the ActionBar.
        mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);



        // Setup drawer view
        myNavigationDrawer = (NavigationView) findViewById(R.id.nvView);//  list of items
        setupDrawerContent(myNavigationDrawer);

        //for hamburger icon
        myDrawer =(DrawerLayout)findViewById(R.id.drawer_layout);//drawer_layout in activity_main
        mydrawerToggle = setupDrawerToggle();
        myDrawer.addDrawerListener(mydrawerToggle);


        blank_textview1=(TextView)findViewById(R.id.blank_textview1);
        fab = (FloatingActionButton)findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddScreen(view);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //refreshing themes and looks
        isGrid = sharedPreferences.getBoolean("isGrid",true);
        isOldestFirst = sharedPreferences.getBoolean("isOldestFirst",true);


        //inflating recyclerView
        if(isOldestFirst)
            main_arrayList = db.getAllData();
        else
            main_arrayList = db.getReversedata();


        myrecyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        if(isGrid){
            myrecyclerView.setLayoutManager( new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }
        else{
            MylayoutManager = new LinearLayoutManager(this);
            myrecyclerView.setLayoutManager(MylayoutManager);
        }
        Myadapter = new CustomAdapter(main_arrayList,getApplicationContext());
        myrecyclerView.setAdapter(Myadapter);
        myrecyclerView.scrollToPosition(0);

        if(main_arrayList.size()==0)
            findViewById(R.id.blank_textview1).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.blank_textview1).setVisibility(View.GONE);

        myrecyclerView.addOnItemTouchListener(
                new MyRecyclerItemListener(getApplicationContext(), new MyRecyclerItemListener.OnClickItemInterface() {
                    @Override
                    public boolean onLongPress(View v, int position) {
                        Toast.makeText(getApplicationContext(),"long press",Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    @Override
                    public void onItemClick(View view, int position) {

                        data = main_arrayList.get(position);
                        id = data.getData_id();
                        Intent i = new Intent(MainActivity.this,View_Screen.class);
                        Bundle bundle = new Bundle();
                        int id = Integer.parseInt(data.getData_id());
                        bundle.putInt("id",id);
                        i.putExtras(bundle);

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
                            view.setTransitionName("card");

                            Pair<View,String> p4 = Pair.create(view,"card");
                            ActivityOptionsCompat options = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation(MainActivity.this,p4);
                            startActivity(i, options.toBundle());
                        }
                        else
                            startActivity(i);
                        //overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);

                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        local_menu=menu;
        getMenuInflater().inflate(R.menu.mainscreen_menu,menu);
        if(isGrid){
            local_menu.findItem(R.id.changeListGrid).setIcon(R.drawable.svg_view_stream_white_36px);
        }
        else{
            local_menu.findItem(R.id.changeListGrid).setIcon(R.drawable.svg_view_quilt_white_36px);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                //To open the drawer
                myDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_SETTINGS:
                Intent i = new Intent(MainActivity.this ,SettingsActivity.class);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(i, options.toBundle());
                }
                else
                startActivity(i);

                break;
            case R.id.changeListGrid:
                isGrid = !isGrid;
                if(isGrid){
                    local_menu.findItem(R.id.changeListGrid).setIcon(R.drawable.svg_view_stream_white_36px);
                }
                else{
                    local_menu.findItem(R.id.changeListGrid).setIcon(R.drawable.svg_view_quilt_white_36px);
                }
                editor.putBoolean("isGrid",isGrid);
                editor.apply();
                onResume();
                break;
            case R.id.darkMode:
                isDark =! isDark;
                editor.putBoolean("isDark",isDark);
                editor.apply();
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mydrawerToggle.syncState();
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
        switch (menuItem.getItemId())
        {
            case R.id.menu_LISTS:
                //Do Nothing
                break;
            case R.id.menu_ABOUT:
                Intent i = new Intent(MainActivity.this ,About_Screen.class);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(i, options.toBundle());
                }
                else
                    startActivity(i);
                break;
            case R.id.menu_TRASH:
                Intent intent = new Intent(MainActivity.this,Trash_Screen.class);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(intent, options.toBundle());
                }
                else
                    startActivity(intent);
                break;
            case R.id.menu_SETTINGS:
                Intent a = new Intent(MainActivity.this,SettingsActivity.class);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(a, options.toBundle());
                }
                else
                    startActivity(a);
                break;
        }

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);

        myDrawer.closeDrawers();
    }


    public void callAddScreen(View view){
        Intent i = new Intent(MainActivity.this,Add_Screen.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Pair<View,String> p1 = Pair.create((View)fab,"trans_add_fab");
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(MainActivity.this,p1);
            startActivity(i, options.toBundle());
        }
        else startActivity(i);
    }

    /*public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }*/

    @Override
    public void onBackPressed() {
        if(myDrawer.isDrawerOpen(GravityCompat.START))
        {
            myDrawer.closeDrawers();
        }
        else{
            if (backPressedToExitOnce) {
                moveTaskToBack(true);
            } else {
                this.backPressedToExitOnce = true;
                showToast("Press again to exit");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        backPressedToExitOnce = false;
                    }
                }, 2000);
            }
        }
    }

    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }

    @Override
    protected void onPause() {
        if(this.toast != null)
            this.toast.cancel();
        super.onPause();
    }

}