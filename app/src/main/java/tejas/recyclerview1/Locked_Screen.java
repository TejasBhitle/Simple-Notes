package tejas.recyclerview1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Locked_Screen  extends AppCompatActivity{


    RecyclerView myrecyclerView;
    RecyclerView.Adapter Myadapter;
    RecyclerView.LayoutManager MylayoutManager;
    Toolbar mytoolbar;
    DBHelper db;
    String id,PASSWORD;
    MyData data;
    FloatingActionButton fab;
    ArrayList<MyData> main_arrayList;
    SharedPreferences sharedPreferences;
    View main_Rela_layout;
    boolean isGrid,isOldestFirst;
    TextView blank_textview1;
    DrawerLayout drawerLayout;
    NavigationView nview;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);

        setTitle("Protected Notes");
        setContentView(R.layout.mainactivity);

        drawerLayout =(DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawerLayout !=null;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        nview =(NavigationView)findViewById(R.id.nvView);
        assert nview != null;
        nview.setVisibility(View.GONE);

        db= new DBHelper(this);


        // Set a Toolbar to replace the ActionBar.
        mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        main_Rela_layout = (View)findViewById(R.id.main_rela_layout);
        blank_textview1=(TextView)findViewById(R.id.blank_textview1);

        fab =(FloatingActionButton)findViewById(R.id.main_fab);
        fab.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        PASSWORD= sharedPreferences.getString("PASSWORD","");

        if(isOldestFirst)
            main_arrayList = db.getAllLockedData();
        else
            main_arrayList = db.getReverseLockedData();


        myrecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        MylayoutManager = new LinearLayoutManager(this);
        myrecyclerView.setLayoutManager( new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        Myadapter = new CustomAdapter(main_arrayList,getApplicationContext(),false);
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
                        callViewSceen(view);
                    }

                    @Override
                    public void onLongPress(View v, int position) {
                        //do nothing
                    }
                })
        );
    }

    public void callViewSceen(View view){
        Intent i = new Intent(Locked_Screen.this,View_Screen.class);
        Bundle bundle = new Bundle();
        int id = Integer.parseInt(data.getData_id());
        bundle.putInt("id",id);
        i.putExtras(bundle);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            view.setTransitionName("card");

            Pair<View,String> p4 = Pair.create(view,"card");
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(Locked_Screen.this,p4);
            startActivity(i, options.toBundle());
        }
        else
            startActivity(i);
        //overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
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
