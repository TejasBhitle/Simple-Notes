package tejas.recyclerview1;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView myrecyclerView;
    private RecyclerView.Adapter Myadapter;
    private RecyclerView.LayoutManager MylayoutManager;
    private DrawerLayout myDrawer;
    private NavigationView myNavigationDrawer;
    private ActionBarDrawerToggle mydrawerToggle;
    private Toolbar mytoolbar;
    DBHelper db;
    private boolean backPressedToExitOnce = false;
    private boolean isOldestFirst,isGrid,isPasswordSet,isHidden;
    private Toast toast = null;
    private ArrayList<MyData> main_arrayList;
    MyData data;
    String id,isLocked,PASSWORD;
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
        setTitle("NOTES");
        setTitleColor(R.color.amoled_dark_theme_background);

        setContentView(R.layout.mainactivity);

        db = new DBHelper(this);
        main_Rela_layout = (View)findViewById(R.id.main_rela_layout);


        // Set a Toolbar to replace the ActionBar.
        mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        // Setup drawer view
        myNavigationDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(myNavigationDrawer);

        //for hamburger icon
        myDrawer =(DrawerLayout)findViewById(R.id.drawer_layout);
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
        isHidden = sharedPreferences.getBoolean("isHidden",false);
        PASSWORD = sharedPreferences.getString("PASSWORD","");


        if(PASSWORD.matches(""))
            isPasswordSet=false;
        else
           isPasswordSet=true;

        //inflating recyclerView
        if(isOldestFirst){
            if(isHidden)
                main_arrayList= db.getAllExceptLocked();
            else
                main_arrayList = db.getAllData();
        }
        else{
            if(isHidden)
                main_arrayList= db.getReverseExceptLocked();
            else
                main_arrayList = db.getReversedata();

        }


        myrecyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        if(isGrid){
            myrecyclerView.setLayoutManager( new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }
        else{
            MylayoutManager = new LinearLayoutManager(this);
            myrecyclerView.setLayoutManager(MylayoutManager);
        }
        Myadapter = new CustomAdapter(main_arrayList,getApplicationContext(),isPasswordSet);
        myrecyclerView.setAdapter(Myadapter);
        myrecyclerView.scrollToPosition(0);

        if(main_arrayList.size()==0)
            findViewById(R.id.blank_textview1).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.blank_textview1).setVisibility(View.GONE);

        myrecyclerView.addOnItemTouchListener(
                new MyRecyclerItemListener(getApplicationContext(), new MyRecyclerItemListener.OnClickItemInterface() {
                    @Override
                    public void onLongPress(View v, int position) {
                        Toast.makeText(getApplicationContext(),"long press",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemClick(View view, int position) {

                        data = main_arrayList.get(position);
                        id = data.getData_id();
                        isLocked= data.getData_isLocked();

                        if(isLocked.matches("true") && isPasswordSet){
                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.setContentView(R.layout.password_dialog);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                            final EditText EditText = (EditText)dialog.findViewById(R.id.password_dialog_editText);
                            final TextView titletext = (TextView)dialog.findViewById(R.id.password_title);
                            titletext.setText("Password");

                            Button ok = (Button)dialog.findViewById(R.id.password_dialog_ok);
                            ok.setOnClickListener(
                                    new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View view) {
                                                           String entered_data= EditText.getText().toString();
                                                           if(entered_data.equals(PASSWORD))
                                                               callViewSceen(view);
                                                           else
                                                               Toast.makeText(getApplicationContext(),"Password invalid",Toast.LENGTH_SHORT).show();
                                                           dialog.cancel();
                                                       }
                                                   }
                            );
                            Button cancel = (Button)dialog.findViewById(R.id.password_dialog_cancel);
                            cancel.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.cancel();
                                        }
                                    }
                            );
                            dialog.show();
                        }
                        else
                            callViewSceen(view);
                    }
                })
        );

        /*myrecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy>0 )
                    fab.hide();
                else
                    fab.show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        local_menu=menu;
        getMenuInflater().inflate(R.menu.mainscreen_menu,menu);
        if(isGrid){
            local_menu.findItem(R.id.changeListGrid).setIcon(R.drawable.view_stream_white_36px);
        }
        else{
            local_menu.findItem(R.id.changeListGrid).setIcon(R.drawable.dashboard_white_36px);
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
            case R.id.changeListGrid:
                isGrid = !isGrid;
                if(isGrid){
                    local_menu.findItem(R.id.changeListGrid).setIcon(R.drawable.view_stream_white_36px);
                }
                else{
                    local_menu.findItem(R.id.changeListGrid).setIcon(R.drawable.dashboard_white_36px);
                }
                editor.putBoolean("isGrid",isGrid);
                editor.apply();
                onResume();
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
        return new ActionBarDrawerToggle(this, myDrawer, mytoolbar,
                R.string.drawer_open,R.string.drawer_close);
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
                Intent i = new Intent(getApplicationContext() ,About_Screen.class);
                startActivity(i);
                break;
            case R.id.menu_LOCKED:
                if(isPasswordSet)
                  LockedPressed();
                else
                  Toast.makeText(MainActivity.this,"No PinCode Set",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_TRASH:
                Intent intent = new Intent(getApplicationContext(),Trash_Screen.class);
                startActivity(intent);
                break;
            case R.id.menu_SETTINGS:
                Intent a = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(a);
                break;
        }

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);

        myDrawer.closeDrawers();
    }

    private void LockedPressed() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.password_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        final EditText enteredText = (EditText)dialog.findViewById(R.id.password_dialog_editText);
        final TextView title_text = (TextView)dialog.findViewById(R.id.password_title);
        title_text.setText("PinCode Required");


        Button ok = (Button)dialog.findViewById(R.id.password_dialog_ok);
        ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String enteredPassword = enteredText.getText().toString();
                        if(enteredPassword.matches("")){
                            Toast.makeText(getApplicationContext(),"No PinCode entered",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            boolean b = password_verify(enteredPassword);
                            if(b){
                                callLockedScreen();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"PinCode incorrect",Toast.LENGTH_SHORT).show();
                            }
                        }
                        onResume();
                        dialog.dismiss();

                    }
                }
        );
        Button cancel = (Button)dialog.findViewById(R.id.password_dialog_cancel) ;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private boolean password_verify(String string){
        if(string.equals(PASSWORD))
            return true;
        else
            return false;

    }

    public void callAddScreen(View view){
        Intent i = new Intent(MainActivity.this,Add_Screen.class);
        startActivity(i);
    }

    public void callViewSceen(View view){
        Intent i = new Intent(MainActivity.this,View_Screen.class);
        Bundle bundle = new Bundle();
        int id = Integer.parseInt(data.getData_id());
        bundle.putInt("id",id);
        i.putExtras(bundle);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            Pair<View,String> p2 = Pair.create((View)fab,"fab");
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(MainActivity.this,p2);
            startActivity(i, options.toBundle());
        }
        else
        {
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
        }

    }

    public void callLockedScreen(){
        Intent k = new Intent(getApplicationContext(),Locked_Screen.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(MainActivity.this);
            startActivity(k, options.toBundle());
        }
        else
            startActivity(k);
    }

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

        /*public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }*/


}