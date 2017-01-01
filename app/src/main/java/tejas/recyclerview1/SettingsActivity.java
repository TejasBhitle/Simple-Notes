package tejas.recyclerview1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity{

    Toolbar toolbar;
    Switch PasswordSwitch,SortSwitch,HideSwitch;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CardView card1,card2,card3;
    Boolean isOldestFirst,isPasswordSet,isHidden;
    String PASSWORD;
    TextView ChangePassword_textview;
    DBHelper db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");

        sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isHidden =sharedPreferences.getBoolean("isHidden",false);
        isOldestFirst= sharedPreferences.getBoolean("isOldestFirst",false);
        PASSWORD=sharedPreferences.getString("PASSWORD","");

        db = new DBHelper(this);

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_primaryDark));
        }

        setContentView(R.layout.setting_activity);

        SortSwitch =(Switch)findViewById(R.id.SortSwitch);
        PasswordSwitch=(Switch)findViewById(R.id.password_switch);
        HideSwitch =(Switch)findViewById(R.id.HideSwitch) ;
        ChangePassword_textview=(TextView)findViewById(R.id.changePassword) ;

        card1=(CardView)findViewById(R.id.card1);
        card2=(CardView)findViewById(R.id.card2);
        card3=(CardView)findViewById(R.id.card3);

        //set toolbar
        toolbar =(Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(PASSWORD.matches("")){
            isPasswordSet=false;
        }
        else{
            isPasswordSet=true;
        }
        SortSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortSwitch();
            }
        });

        HideSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideSwitch();
            }
        });

        PasswordSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordSwitch();
            }
        });

        ChangePassword_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword("Enter Current PinCode");
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetAllDialog();
            }
        });


        card1.setCardBackgroundColor(getResources().getColor(R.color.dark_theme_background));
        card2.setCardBackgroundColor(getResources().getColor(R.color.dark_theme_background));
        card3.setCardBackgroundColor(getResources().getColor(R.color.dark_theme_background));
    }

    @Override
    protected void onResume() {
        super.onResume();


        // SortSwitch_initial
        if(isOldestFirst)
            SortSwitch.setChecked(true);
        else
            SortSwitch.setChecked(false);


        //Password_inital
        if(isPasswordSet){
            PasswordSwitch.setChecked(true);
            ChangePassword_textview.setClickable(true);
            HideSwitch.setClickable(true);
        }
        else{
            PasswordSwitch.setChecked(false);
            ChangePassword_textview.setClickable(false);
            HideSwitch.setClickable(false);
        }

        //hidden_initial
        if(isHidden)
            HideSwitch.setChecked(true);
        else
            HideSwitch.setChecked(false);


    }

    private boolean password_verify(String string){
        if(string.equals(PASSWORD))
            return true;
        else
            return false;

    }

    public void HideSwitch(){
        isHidden =!isHidden;
        HideSwitch.setChecked(isHidden);
        editor.putBoolean("isHidden",isHidden);
        editor.apply();
    }

    public void SortSwitch(){
        isOldestFirst = !isOldestFirst;
        SortSwitch.setChecked(isOldestFirst);
        editor.putBoolean("isOldestFirst",isOldestFirst);
        editor.apply();
    }

    public void passwordSwitch(){
        if(isPasswordSet){
            //remove if valid
            removePassword("PinCode Required");
        }
        else{
            //add
            addPassword("Add PinCode");
        }
    }

    public void addPassword(String title){

        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.password_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        final EditText enteredText = (EditText)dialog.findViewById(R.id.password_dialog_editText);
        final TextView titletext = (TextView)dialog.findViewById(R.id.password_title);
        titletext.setText(title);

        Button ok = (Button)dialog.findViewById(R.id.password_dialog_ok);
        ok.setOnClickListener( new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       String enteredPassword = enteredText.getText().toString();
                                       if(enteredPassword.matches("")){
                                           Toast.makeText(getApplicationContext(),"No PinCode entered",Toast.LENGTH_SHORT).show();
                                           isPasswordSet=false;
                                       }
                                       else{
                                           PASSWORD=enteredPassword;
                                           editor.putString("PASSWORD",PASSWORD);
                                           editor.apply();
                                           isPasswordSet= true;
                                           Toast.makeText(getApplicationContext(),"Pincode entered",Toast.LENGTH_SHORT).show();

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
                PasswordSwitch.setChecked(false);
            }
        });
        dialog.show();
    }

    private void removePassword( String title) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.password_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        final EditText enteredText = (EditText)dialog.findViewById(R.id.password_dialog_editText);
        final TextView title_text = (TextView)dialog.findViewById(R.id.password_title);
        title_text.setText(title);

        Button ok = (Button)dialog.findViewById(R.id.password_dialog_ok);
        ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String enteredPassword = enteredText.getText().toString();
                        if(enteredPassword.matches("")){
                            Toast.makeText(getApplicationContext(),"No PinCode entered",Toast.LENGTH_SHORT).show();
                            isPasswordSet=true;
                        }
                        else{
                            boolean b = password_verify(enteredPassword);
                            if(b){
                                // delete password
                                editor.putString("PASSWORD","");
                                editor.apply();
                                isPasswordSet=false;
                                isHidden=false;
                                editor.putBoolean("isHidden",isHidden);
                                editor.apply();
                                Toast.makeText(getApplicationContext(),"PinCode removed",Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(getApplicationContext(),"PinCode incorrect",Toast.LENGTH_SHORT).show();
                                isPasswordSet= true;
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
                PasswordSwitch.setChecked(true);
            }
        });
        dialog.show();
    }

    public void changePassword(String title){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.password_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        final EditText enteredText = (EditText)dialog.findViewById(R.id.password_dialog_editText);
        final TextView titletext = (TextView)dialog.findViewById(R.id.password_title);
        titletext.setText(title);

        Button ok = (Button)dialog.findViewById(R.id.password_dialog_ok);
        ok.setOnClickListener( new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       String enteredPassword = enteredText.getText().toString();
                                       if(enteredPassword.matches("")){
                                           Toast.makeText(getApplicationContext(),"No PinCode entered",Toast.LENGTH_SHORT).show();
                                       }
                                       else{
                                           boolean b = password_verify(enteredPassword);
                                           if(b){
                                               addPassword("Enter New PinCode");
                                           }
                                           else{
                                               Toast.makeText(getApplicationContext(),"PinCode Incorrect",Toast.LENGTH_SHORT).show();
                                           }
                                       }
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

    public void ResetAllDialog(){
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
