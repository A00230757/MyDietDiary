package com.example.app.dietdiary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.mydietdiary.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddDietActivity extends AppCompatActivity {
String diettype="";
    LinearLayout nutrientLinearlayout;
    ArrayList<TextView> altv;
    ArrayList<EditText> alet;
    ArrayList<FoodItemDetail> alfid=new ArrayList<>();
    ArrayList<FoodItem> alfi=new ArrayList<>();
    ArrayList<String> al=new ArrayList<>();
    SQLiteDatabase db=null;
    AutoCompleteTextView auto;
    ArrayAdapter<String> ad;
    String selectedfin="";
    int selectedfid;
    AlertDialog.Builder alert1;
    NavigationView nav1;
    DrawerLayout d1;
    ActionBarDrawerToggle actionBarDrawerToggle;
    long sd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_diet);
        setTitle("Add Diet");
        nav1 = (NavigationView) (findViewById(R.id.nav1));
        d1 = (DrawerLayout) (findViewById(R.id.d1));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, d1, null, 0, 0);
        d1.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        nav1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.i1) {
                    Intent in = new Intent(AddDietActivity.this, AddNutrientActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i2) {
                    Intent in = new Intent(AddDietActivity.this, AddFoodItemActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task2", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.i3) {
                    Intent in = new Intent(AddDietActivity.this, AddFoodDetailActivity.class);
                    startActivity(in);
                    // Toast.makeText(MainActivity.this, "Task3", Toast.LENGTH_SHORT).show();

                }

                else if (item.getItemId() == R.id.i4){
                    Intent in = new Intent(AddDietActivity.this, AddDietFirstActivity.class);
                    startActivity(in);
                    //Toast.makeText(MainActivity.this, "Task4", Toast.LENGTH_SHORT).show();

                }
                else if(item.getItemId() == R.id.i5){
                    Intent in = new Intent(AddDietActivity.this, DietHistoryNewActivity.class);
                    startActivity(in);
                }
                else if(item.getItemId() == R.id.i6){
                    String message = "Whether you're trying to lose weight or just attempting to eat healthier, Download a DietDiary App(keeping calorie records)  can help you make positive changes. ";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
                }
                else{
                    Intent in = new Intent(AddDietActivity.this, MainActivity.class);
                    startActivity(in);
                }

                d1.closeDrawer(GravityCompat.START);


                return true;
            }
        });
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        nutrientLinearlayout = (LinearLayout) (findViewById(R.id.answerLinearlayout));
        altv = new ArrayList<>();
        alet = new ArrayList<>();
        nutrientLinearlayout.removeAllViews();
        Intent in=getIntent();
        diettype=in.getStringExtra("diettype");
        sd=in.getLongExtra("sd",0);
        auto = (AutoCompleteTextView) (findViewById(R.id.auto));
        ad  = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,al);

        auto.setAdapter(ad);
        alert1 = new AlertDialog.Builder(this);
        alert1.setTitle("Alert");
        alert1.setIcon(R.drawable.ic_launcher_background);
        alert1.setMessage("Add Food Item Nutrients In Master List First?");
        alert1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in =new Intent(AddDietActivity.this,AddFoodDetailActivity.class);
                startActivity(in);
            }
        });
        alert1.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in =new Intent(AddDietActivity.this,MainActivity.class);
                startActivity(in);
            }
        });
        alert1.create();
        fetchfooditems();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {

            if (d1.isDrawerOpen(GravityCompat.START)) {
                d1.closeDrawer(GravityCompat.START);
            } else {
                d1.openDrawer(GravityCompat.START);
            }

        }

        return true;
    }
    public void cancel(View view){
        Intent in=new Intent(AddDietActivity.this,MainActivity.class);
        startActivity(in);
    }
public void search(View view)
{
    selectedfin = auto.getText().toString();
    //Toast.makeText(getApplication(),selectedfin,Toast.LENGTH_LONG).show();
    for(int i=0;i<alfi.size();i++){
        if(selectedfin.equals(alfi.get(i).name)){
            selectedfid=alfi.get(i).foodid;
            fetchnutrients();
            break;
        }
    }
}
public void chkdataoffitems() {
    if (checkForTableExists(db, "fooditemsdetail")) {

        int count = 0;
        Cursor c = db.rawQuery("select * from fooditemsdetail ", null);
        while (c.moveToNext()) {
            count = count + 1;
        }
        if (count == 0) {
            alert1.show();
        }

    } else {
   alert1.show();
    }

    }
    private void fetchfooditems() {
        if(checkForTableExists(db,"fooditems"))
        {
            al.clear();
            alfi.clear();
            int count=0;
            Cursor c = db.rawQuery("select * from fooditems", null);
            while (c.moveToNext()) {
                count=count+1;
                int icon = c.getInt(c.getColumnIndex("icon"));
                int foodid = c.getInt(c.getColumnIndex("foodid"));
                String fname = c.getString(c.getColumnIndex("fname"));
                String dname = c.getString(c.getColumnIndex("dname"));
                alfi.add(new FoodItem(foodid,fname,dname,icon));
                al.add(fname);
            }
            ad.notifyDataSetChanged();
            if(count==0){
                alert1.show();

            }
            else{
                chkdataoffitems();
            }
        }
        else {
         alert1.show();

        }
    }
    private boolean checkForTableExists(SQLiteDatabase db, String table){
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            return true;
        }
        mCursor.close();
        return false;
    }

    public void fetchnutrients(){
        if(checkForTableExists(db,"fooditemsdetail"))
        {
          alfid.clear();
          nutrientLinearlayout.removeAllViews();
          int count=0;
            Cursor c = db.rawQuery("select * from fooditemsdetail where foodid="+selectedfid+"", null);
            while (c.moveToNext()) {
                count=count+1;
                int fooddetailid = c.getInt(c.getColumnIndex("fooddetailid"));
                int foodid = c.getInt(c.getColumnIndex("foodid"));
                String nutrition = c.getString(c.getColumnIndex("nutrition"));
                String unit = c.getString(c.getColumnIndex("unit"));
                float quantity = c.getFloat(c.getColumnIndex("quantity"));
                alfid.add(new FoodItemDetail(fooddetailid,foodid,nutrition,unit,quantity));

            }
            if(count==0){
                alert1.show();
            }
           else  if(alfid.size()>0){
                android.view.Display display = ((android.view.WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                for (int m = 0; m < alfid.size(); m++) {
                    Log.d("TEST",m+"");
                    final TextView textView = new TextView(getApplicationContext());
                    final EditText editText=new EditText(getApplicationContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth(), 90));
                    textView.setPadding(5, 0, 5, 0);
                    textView.setGravity(Gravity.BOTTOM);
                    textView.setTextSize(25);
                    textView.setText(alfid.get(m).nutritionname+"("+alfid.get(m).unit+")");
                    textView.setTextColor(Color.BLACK);
                    altv.add(textView);
                    editText.setLayoutParams(new LinearLayout.LayoutParams(display.getWidth(), 150));
                    editText.setHint("enter calorie quantiy");
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setText(alfid.get(m).quantity+"");
                    alet.add( editText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nutrientLinearlayout.addView(textView);
                            nutrientLinearlayout.addView(editText);
                        }
                    });
                }
            }
            else{
                Toast.makeText(AddDietActivity.this,"Add Nutrients in food items",Toast.LENGTH_SHORT).show();
            }


        }
        else {
         alert1.show();
                  }

    }
    public void adddiet(View view) {
        try {
            boolean flag = true;
            for (int t = 0; t < alet.size(); t++) {
                Float v=Float.parseFloat(alet.get(t).getText().toString());
                if (alet.get(t).getText().toString().equals("")) {
                    flag = false;
                    Toast.makeText(getApplicationContext(), "enter quantity value of all nutrients", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if (flag) {


    if (checkForTableExists(db, "diet")) {
//                long millis=System.currentTimeMillis();
//                java.sql.Date date=new java.sql.Date(millis);
//                String dt=date.toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date="";
        if(sd!=0){
            date = sdf.format(sd);
        }
        else{
            date = sdf.format(new Date());
        }

        //String date = "2018-10-05";
        //Toast.makeText(getApplicationContext(),date+"  td ",Toast.LENGTH_SHORT).show();
        Date d1 = sdf.parse(date);
        long timeInMilliseconds = d1.getTime();
//        Cursor cust = db.rawQuery("select * from diet where dt="+timeInMilliseconds+"", null);
//        while (cust.moveToNext()) {
//          int id = cust.getInt(cust.getColumnIndex("dietid"));
//            db.execSQL("delete from diet where dietid="+id+"");
//                    Cursor c = db.rawQuery("select * from dietdetail where dietid="+id+"", null);
//        while (c.moveToNext()) {
//                      int ddid = c.getInt(c.getColumnIndex("dietdetailid"));
//            db.execSQL("delete from dietdetail where dietdetailid="+ddid+"");
//
//        }
//        }
        db.execSQL("insert into diet (diettype,dt) values('" + diettype + "',"+timeInMilliseconds+")");
        int dietid = 0;
        Cursor cus = db.rawQuery("select * from diet", null);
        int total=0;
        while (cus.moveToNext()) {
            total=total+1;
            dietid = cus.getInt(cus.getColumnIndex("dietid"));
            long d = cus.getLong(cus.getColumnIndex("dt"));
            Log.d("MYDTT",dietid+" "+total+"  "+d);
           // Toast.makeText(getApplicationContext(),"  total diettable "+total,Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < altv.size(); i++) {
            String s = altv.get(i).getText().toString();
            int index1 = s.indexOf("(");
            int index2 = s.indexOf(")");
            String nut = s.substring(0, index1);
            String unit = s.substring(index1 + 1, index2);
          String test=  alet.get(i).getText().toString();
            Log.d("MYDDTT",test+"  values dietdetailtable"+total);
            String currenttime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
           //Toast.makeText(getApplicationContext(),test+"  values dietdetailtable",Toast.LENGTH_SHORT).show();
            db.execSQL("insert into dietdetail (dietid,fname,nutrient,unit,quantity,currenttime,entrydate) values(" + dietid + ",'" + selectedfin + "','" + nut + "','" + unit + "'," + Float.parseFloat(alet.get(i).getText().toString()) + ",'"+currenttime+"',"+timeInMilliseconds+")");
        }
//        Cursor c = db.rawQuery("select * from dietdetail ", null);
//        while (c.moveToNext()) {
//
//            float quantity = c.getFloat(c.getColumnIndex("quantity"));
//            int ddid = c.getInt(c.getColumnIndex("dietdetailid"));
//            int did = c.getInt(c.getColumnIndex("dietid"));
//            String nutrient = c.getString(c.getColumnIndex("nutrient"));
//            String unit = c.getString(c.getColumnIndex("unit"));
//            String fname = c.getString(c.getColumnIndex("fname"));
//            //Toast.makeText(getApplicationContext(), " qn " + quantity + " ddid " + ddid + " did " + did + " nt " + nutrient + " un " + unit + " fn " + fname, Toast.LENGTH_LONG).show();
//            Log.d("MYTTTT", "qn   " + quantity + " ddid  " + ddid + "  did  " + did + "  nt  " + nutrient + "  un  " + unit + "  fn  " + fname);
//
//        }

        if(sd==0) {
            Toast.makeText(getApplicationContext(), "Diet Added Successfully", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(AddDietActivity.this, MainActivity.class);
            startActivity(in);
        }
        else{
            Toast.makeText(getApplicationContext(), "Diet Updated Successfully", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(AddDietActivity.this, DietHistoryNewActivity.class);
            in.putExtra("sd",sd);
            startActivity(in);
        }
    } else {
        db.execSQL("create table if not exists diet (dietid INTEGER PRIMARY KEY AUTOINCREMENT,diettype VARCHAR(500), dt LONG)");
        db.execSQL("create table if not exists dietdetail (dietdetailid INTEGER PRIMARY KEY AUTOINCREMENT,dietid INTEGER,fname VARCHAR(500),nutrient VARCHAR(500),unit VARCHAR(500),quantity FLOAT,currenttime VARCHER(500), entrydate LONG , FOREIGN KEY ('dietid') REFERENCES 'diet' ('dietid'))");
        db.execSQL("insert into diet (diettype) values('" + diettype + "')");

    }

            }
        }catch(Exception ex){
            //ex.printStackTrace();
          Toast.makeText(getApplicationContext(),"enter  numeric values for nutrient quantity ",Toast.LENGTH_SHORT).show();
        }
        }
        @Override
        public void onBackPressed () {
            super.onBackPressed();
            Intent in = new Intent(AddDietActivity.this, AddDietFirstActivity.class);
            startActivity(in);
        }

}
