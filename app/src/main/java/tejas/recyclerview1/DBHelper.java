package tejas.recyclerview1;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "database.db";
    public static final String TABLE_NAME = "listdata";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT ="content";
    public static final String COL_DATE = "date";
    public static final String COL_COLOR = "color";
    public static final String COL_ID = "id";
    public static final String COL_IsLocked = "isLocked";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table listdata" +
                "(id integer primary key ," +
                "title text ,content text ," +
                "date text,color text," +
                " isLocked text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS listdata");
        onCreate(db);
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from listdata where id="+id+"", null );
        return res;
    }

    public Cursor getData(String title){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =db.rawQuery("select * from listdata where title = "+title+"",null);
        return res;
    }

    public ArrayList<MyData> getAllData(){

        ArrayList<MyData> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from listdata",null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false){
            String string1 = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            String string2 = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
            String string3 = cursor.getString(cursor.getColumnIndex(COL_ID));
            String string4 = cursor.getString(cursor.getColumnIndex(COL_DATE));
            String string5 = cursor.getString(cursor.getColumnIndex(COL_COLOR));
            String string6 = cursor.getString(cursor.getColumnIndex(COL_IsLocked));
            MyData obj = new MyData(string1,string2,string3,string4,string5,string6);
            arrayList.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;

    }

    public ArrayList<MyData> getAllExceptLocked(){
        ArrayList<MyData> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from listdata",null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            String string6 = cursor.getString(cursor.getColumnIndex(COL_IsLocked));
            if(string6.matches("false")){

                String string1 = cursor.getString(cursor.getColumnIndex(COL_TITLE));
                String string2 = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
                String string3 = cursor.getString(cursor.getColumnIndex(COL_ID));
                String string4 = cursor.getString(cursor.getColumnIndex(COL_DATE));
                String string5 = cursor.getString(cursor.getColumnIndex(COL_COLOR));
                MyData obj = new MyData(string1,string2,string3,string4,string5,string6);
                arrayList.add(obj);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<MyData> getReverseExceptLocked(){
        ArrayList<MyData> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from listdata",null);
        cursor.moveToLast();

        while (!cursor.isBeforeFirst()){
            String string6 = cursor.getString(cursor.getColumnIndex(COL_IsLocked));
            if(string6.matches("false")){

                String string1 = cursor.getString(cursor.getColumnIndex(COL_TITLE));
                String string2 = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
                String string3 = cursor.getString(cursor.getColumnIndex(COL_ID));
                String string4 = cursor.getString(cursor.getColumnIndex(COL_DATE));
                String string5 = cursor.getString(cursor.getColumnIndex(COL_COLOR));
                MyData obj = new MyData(string1,string2,string3,string4,string5,string6);
                arrayList.add(obj);
            }
            cursor.moveToPrevious();
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<MyData> getAllLockedData(){

        ArrayList<MyData> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from listdata",null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            String string6 = cursor.getString(cursor.getColumnIndex(COL_IsLocked));
            if (string6.matches("true")) {
                String string1 = cursor.getString(cursor.getColumnIndex(COL_TITLE));
                String string2 = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
                String string3 = cursor.getString(cursor.getColumnIndex(COL_ID));
                String string4 = cursor.getString(cursor.getColumnIndex(COL_DATE));
                String string5 = cursor.getString(cursor.getColumnIndex(COL_COLOR));
                MyData obj = new MyData(string1,string2,string3,string4,string5,string6);
                arrayList.add(obj);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;

    }

    public ArrayList<MyData> getReversedata(){

        ArrayList<MyData> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from listdata",null);
        cursor.moveToLast();

        while (!cursor.isBeforeFirst()){
            String string1 = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            String string2 = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
            String string3 = cursor.getString(cursor.getColumnIndex(COL_ID));
            String string4 = cursor.getString(cursor.getColumnIndex(COL_DATE));
            String string5 = cursor.getString(cursor.getColumnIndex(COL_COLOR));
            String string6 = cursor.getString(cursor.getColumnIndex(COL_IsLocked));
            MyData obj = new MyData(string1,string2,string3,string4,string5,string6);
            arrayList.add(obj);
            cursor.moveToPrevious();
        }

        cursor.close();
        return arrayList;

    }

    public ArrayList<MyData> getReverseLockedData(){

        ArrayList<MyData> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from listdata",null);
        cursor.moveToLast();

        while (!cursor.isBeforeFirst()){
            String string6 = cursor.getString(cursor.getColumnIndex(COL_IsLocked));
            if (string6.matches("true")) {
                String string1 = cursor.getString(cursor.getColumnIndex(COL_TITLE));
                String string2 = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
                String string3 = cursor.getString(cursor.getColumnIndex(COL_ID));
                String string4 = cursor.getString(cursor.getColumnIndex(COL_DATE));
                String string5 = cursor.getString(cursor.getColumnIndex(COL_COLOR));
                MyData obj = new MyData(string1,string2,string3,string4,string5,string6);
                arrayList.add(obj);
            }
            cursor.moveToPrevious();
        }
        cursor.close();
        return arrayList;

    }

    public ArrayList<String> getAllTitle(){
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from listdata",null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            arrayList.add(title);
        }
        cursor.close();
        //db.close();
        return arrayList;
    }

    public void insertData(String title ,String content,
                           String date,String color,String isLocked) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE,title);
        contentValues.put(COL_CONTENT,content);
        contentValues.put(COL_DATE,date);
        contentValues.put(COL_COLOR,color);
        contentValues.put(COL_IsLocked,isLocked);
        db.insert(TABLE_NAME,null,contentValues);
        db.close();

    }

    public boolean updateData(Integer id,String title ,String content,String date,String color,String isLocked){

        String[] update_me = new String[]{Integer.toString(id)};
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE,title);
        contentValues.put(COL_CONTENT,content);
        contentValues.put(COL_DATE,date);
        contentValues.put(COL_COLOR,color);
        contentValues.put(COL_IsLocked,isLocked);
        db.update(TABLE_NAME,contentValues,"id = ?",update_me);
        db.close();
        return true;
    }

    public void deleteData (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,COL_ID + " = ?",new String[]{String.valueOf(id)});
        db.close();

    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);

    }

}
