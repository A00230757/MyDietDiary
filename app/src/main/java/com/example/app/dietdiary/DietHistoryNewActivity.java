package com.example.app.dietdiary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.mydietdiary.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class DietHistoryNewActivity extends AppCompatActivity {
   static ArrayList<diethistory> al = new ArrayList<>();
    NavigationView nav1;
    DrawerLayout d1;
    RecyclerView rcv;
    MyRecyclerAdapter myad;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView tv1;
    TextView tv2;
    static TextView tvpath;
    ArrayList<nutrients> alnutrients = new ArrayList<>();
    ArrayList<diet> aldiet = new ArrayList<>();
    ArrayList<dietdetail> aldietdetail = new ArrayList<>();
    SQLiteDatabase db = null;
    RadioButton rb1, rb2, rb3, rb4;
    RadioGroup rbg;
    long selecteddate;
    String diettype = "All";
    AlertDialog.Builder ad,ad1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefnutrients;
    DatabaseReference nutrientsref;
    DatabaseReference mainrefdiet;
    DatabaseReference dietref;
    DatabaseReference mainrefdietdetail;
    DatabaseReference dietdetailref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_history_new);
        tv1 = (TextView) (findViewById(R.id.tv1));
        tv2 = (TextView) (findViewById(R.id.tv2));
        tvpath = (TextView) (findViewById(R.id.tvpath));
        rb1 = (RadioButton) (findViewById(R.id.rb1));
        rb2 = (RadioButton) (findViewById(R.id.rb2));
        rb4 = (RadioButton) (findViewById(R.id.rb4));
        rb3 = (RadioButton) (findViewById(R.id.rb3));
        rbg = (RadioGroup) (findViewById(R.id.rg1));
        ad = new AlertDialog.Builder(this);
        ad1=new AlertDialog.Builder(this);
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefnutrients = firebaseDatabase.getReference();
        nutrientsref =mainrefnutrients.child("nutrients");
        mainrefdiet = firebaseDatabase.getReference();
        dietref =mainrefdiet.child("diet");
        mainrefdietdetail = firebaseDatabase.getReference();
        dietdetailref =mainrefdietdetail.child("dietdetail");

        String currenttime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb1) {
                    al.clear();
                    myad.notifyDataSetChanged();
                    // totalcalories=0;
                    tv2.setText("Total Calories :");
                    diettype = "Breakfast";
                    fetchdiettabledata(diettype, selecteddate);

                } else if (checkedId == R.id.rb2) {
                    al.clear();
                    myad.notifyDataSetChanged();
                    // totalcalories=0;
                    tv2.setText("Total Calories :");
                    diettype = "Lunch";
                    fetchdiettabledata(diettype, selecteddate);
                } else if (checkedId == R.id.rb3) {
                    al.clear();
                    myad.notifyDataSetChanged();
                    // totalcalories=0;
                    tv2.setText("Total Calories :");
                    diettype = "Dinner";
                    fetchdiettabledata(diettype, selecteddate);
                } else if (checkedId == R.id.rb4) {
                    al.clear();
                    myad.notifyDataSetChanged();
                    tv2.setText("Total Calories :");
                    // totalcalories=0;
                    diettype = "All";
                    fetchdiettabledata(diettype, selecteddate);
                }
            }
        });
        nav1 = (NavigationView) (findViewById(R.id.nav1));
        d1 = (DrawerLayout) (findViewById(R.id.d1));
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
//        Cursor c = db.rawQuery("select * from diet ", null);
//        while (c.moveToNext()) {
//            int dietdetailid = c.getInt(c.getColumnIndex("dietid"));
//            db.execSQL("delete from diet  where dietid="+dietdetailid+"");
//        }

        rcv = (RecyclerView) (findViewById(R.id.rcv1));
        myad = new MyRecyclerAdapter();
        rcv.setAdapter(myad);
        al.clear();
        myad.notifyDataSetChanged();
        //Specifying Layout Manager to RecyclerView is Compulsary for Proper Rendering
        LinearLayoutManager simpleverticallayout = new LinearLayoutManager(this);
        rcv.setLayoutManager(simpleverticallayout);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
//        rcv.setLayoutManager(gridLayoutManager);
        android.view.Display display = ((android.view.WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        bt1.setTextSize((float) (display.getHeight() * 0.01));
//
//        tv1.setTextSize((float) (display.getHeight() * 0.02));
//        tv2.setTextSize((float) (display.getHeight() * 0.01));
        // Toast.makeText(getApplicationContext(),(int)(display.getHeight())+"",Toast.LENGTH_LONG).show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, d1, null, 0, 0);
        d1.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        nav1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.i1) {
                    Intent in = new Intent(DietHistoryNewActivity.this, AddNutrientActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i2) {
                    Intent in = new Intent(DietHistoryNewActivity.this, AddFoodItemActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task2", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i3) {
                    Intent in = new Intent(DietHistoryNewActivity.this, AddFoodDetailActivity.class);
                    startActivity(in);
                    // Toast.makeText(MainActivity.this, "Task3", Toast.LENGTH_SHORT).show();

                } else if (item.getItemId() == R.id.i4) {
                    Intent in = new Intent(DietHistoryNewActivity.this, AddDietFirstActivity.class);
                    startActivity(in);
                    //Toast.makeText(MainActivity.this, "Task4", Toast.LENGTH_SHORT).show();

                } else if (item.getItemId() == R.id.i5) {
                    Intent in = new Intent(DietHistoryNewActivity.this, DietHistoryNewActivity.class);
                    startActivity(in);
                } else if (item.getItemId() == R.id.i6) {
                    String message = "Whether you're trying to lose weight or just attempting to eat healthier, Download a DietDiary App(keeping calorie records)  can help you make positive changes. ";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
                } else {
                    Intent in = new Intent(DietHistoryNewActivity.this, MainActivity.class);
                    startActivity(in);
                }

                d1.closeDrawer(GravityCompat.START);


                return true;
            }
        });
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("Diet History");
            supportActionBar.setHomeAsUpIndicator(R.drawable.lines);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        initiallogic();
Intent in=getIntent();
long sd;
 sd=in.getLongExtra("sd",0);
if(sd!=0){
    al.clear();
    myad.notifyDataSetChanged();
    tv2.setText("Total Calories :");
    // totalcalories=0;
    selecteddate=sd;
    diettype = "All";
    fetchdiettabledata(diettype, sd);
}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mytoolbarmenu, menu);
        return true;
    }

    private void initiallogic() {
        nutrientsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alnutrients.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    nutrients nutrienttemp = singlesnapshot.getValue(nutrients.class);
                    try {
                        alnutrients.add(nutrienttemp);

                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                myad.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        if (checkForTableExists(db, "nutrients")) {
//            alnutrients.clear();
//            int count = 0;
//            Cursor c = db.rawQuery("select * from nutrients", null);
//            while (c.moveToNext()) {
//                count = count + 1;
//                String nut = c.getString(c.getColumnIndex("nutrient"));
//                String unit = c.getString(c.getColumnIndex("unit"));
//                alnutrients.add(new nutrients(nut, unit));
//
//            }
//            if (count == 0) {
//                //Toast.makeText(getApplicationContext(),"no any nutrient present",Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            // Toast.makeText(getApplicationContext(),"no any nutrient present ",Toast.LENGTH_SHORT).show();
//        }

    }

    private void fetchdiettabledata(final String dtype, final long dtt) {
       // Toast.makeText(getApplicationContext(),dtype+""+dtt,Toast.LENGTH_SHORT).show();
       // if (checkForTableExists(db, "diet")) {
            aldiet.clear();
            aldietdetail.clear();

            try {
                if (dtype.equals("All")) {
                    dietref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = 0;

                            //Log.d("MYESSAGE",dataSnapshot.toString());
                            for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                            {
                               diet diettemp = singlesnapshot.getValue(diet.class);

                                try {
                                   if(diettemp.date==dtt){
                                        count = count + 1;
                                        aldiet.add(diettemp);
                                       fetchdietdetailtabledata(diettemp.dietid);
                                    }



                                }
                                catch (Exception ex){
                                    ex.printStackTrace();
                                }

                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//                    Cursor c = db.rawQuery("select * from diet where dt=" + dtt + " ", null);
//                    while (c.moveToNext()) {
//                        count = count + 1;
//                        int dietid = c.getInt(c.getColumnIndex("dietid"));
//                        long dt = c.getLong(c.getColumnIndex("dt"));
//                        String diettype = c.getString(c.getColumnIndex("diettype"));
//                        aldiet.add(new diet(dietid+"", diettype, dt));
//                        fetchdietdetailtabledata(dietid);
//                    }
//                    if (count == 0) {
//                        Toast.makeText(getApplicationContext(), "No any diet history found for selected date", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    dietref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int count = 0;

                            //Log.d("MYESSAGE",dataSnapshot.toString());
                            for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                            {
                                diet diettemp = singlesnapshot.getValue(diet.class);
                                try {
                                    if(dtype.equals(diettemp.diettype)&&diettemp.date==dtt) {
                                        count = count + 1;
                                        aldiet.add(diettemp);
                                        Thread.sleep(2000);
                                        fetchdietdetailtabledata(diettemp.dietid);
                                    }


                                }
                                catch (Exception ex){
                                    ex.printStackTrace();
                                }

                            }
                            //fetchdietdetailtabledata("diet09:27:331639026000000");

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//                    Cursor c = db.rawQuery("select * from diet where dt=" + dtt + " and diettype='" + dtype + "'", null);
//                    while (c.moveToNext()) {
//                        count = count + 1;
//                        int dietid = c.getInt(c.getColumnIndex("dietid"));
//                        long dt = c.getLong(c.getColumnIndex("dt"));
//                        String diettype = c.getString(c.getColumnIndex("diettype"));
//                        aldiet.add(new diet(dietid+"", diettype, dt));
//                        fetchdietdetailtabledata(dietid);
//                    }
//                    if (count == 0) {
//                        tv2.setText("Total Calories :");
//                        Toast.makeText(getApplicationContext(), "No any diet history found for selected  diettype on this date/Misssing not added", Toast.LENGTH_SHORT).show();
//
//                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

//        } else {
//            Toast.makeText(getApplicationContext(), "No any diet history found for selected date/Missing not added", Toast.LENGTH_SHORT).show();
//        }

    }

    private void fetchdietdetailtabledata(final String did) {
        dietdetailref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    dietdetail dietdetailtemp = singlesnapshot.getValue(dietdetail.class);
                    try {
                        if(did.equals(dietdetailtemp.dietid)) {
                            count = count + 1;
                            aldietdetail.add(dietdetailtemp);
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
               // myad.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),aldiet.size()+"--"+aldietdetail.size(),Toast.LENGTH_SHORT).show();
                calculatecalories();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        if (checkForTableExists(db, "dietdetail")) {
//
//            int count = 0;
//            Cursor c = db.rawQuery("select * from dietdetail where dietid=" + did + "", null);
//            while (c.moveToNext()) {
//                count = count + 1;
//                int dietdetailid = c.getInt(c.getColumnIndex("dietdetailid"));
//                int dietid = c.getInt(c.getColumnIndex("dietid"));
//                float quantity = c.getFloat(c.getColumnIndex("quantity"));
//                Log.d("NNNNNNNN", quantity + "");
//                String fname = c.getString(c.getColumnIndex("fname"));
//                String nutrient = c.getString(c.getColumnIndex("nutrient"));
//                String unit = c.getString(c.getColumnIndex("unit"));
//                String currenttime = c.getString(c.getColumnIndex("currenttime"));
//                aldietdetail.add(new dietdetail(dietdetailid+"", dietid+"", fname, nutrient, unit, quantity, currenttime));
//
//            }
//            if (count == 0) {
//                tv2.setText("Total Calories :");
//                Toast.makeText(getApplicationContext(), "No any diet history found for selected diettype on this  date/Missing not added", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "No any diet history found for selected date/Missing not added", Toast.LENGTH_SHORT).show();
//        }

    }


    public void nextscreen(View view) {
        if(al.size()>0) {
            Intent in = new Intent(DietHistoryNewActivity.this, AddDietFirstActivity.class);
            in.putExtra("sd",selecteddate);
            startActivity(in);
        }
        else{
            Toast.makeText(getApplicationContext(),"select  any  diet history first",Toast.LENGTH_SHORT);
        }
    }
    private void calculatecalories() {
        al.clear();
        myad.notifyDataSetChanged();
        float totalcalories = 0;
        for (int i = 0; i < aldietdetail.size(); i++) {
            float cal = 0;
            float q1 = aldietdetail.get(i).quantity;
            String n = aldietdetail.get(i).nutrient;

            n = n.toUpperCase();
            String u = aldietdetail.get(i).unit;
            String fi = aldietdetail.get(i).fname;
            String dietdetailid = aldietdetail.get(i).dietid;
            String currenttime = aldietdetail.get(i).currenttime;
            boolean myflag = false;
            String ddtt = "";
            try {
//                Cursor c = db.rawQuery("select * from diet where dietid=" + aldietdetail.get(i).dietid + " ", null);
//                if (c.moveToNext()) {
//                    ddtt = c.getString(c.getColumnIndex("diettype"));
//                    myflag=true;
//                }
                for (int x = 0; x < aldiet.size(); x++) {//es nu mai comment kiti code chalan lyi
                    if (aldietdetail.get(i).dietid.equals(aldiet.get(x).dietid)) {
                        ddtt = aldiet.get(x).diettype;
                        myflag = true;
                    }
                }
               // myflag =true;// mai app likhi eh line pehla ni si likhi
                //Thread.sleep((int)0.90);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (myflag) {
                String n1 = "CARBOHYDRATE";
                String n2 = "PROTEIN";
                String n3 = "FAT";
                String n4 = "VITAMIN";
                String n5 = "MINERAL";
                String n6 = "CALCIUM";
                if (n.contains(n1)) {
                    int v = 4;
                    //calories=calories+((float)(v)*q1);
                    cal = cal + ((float) (v) * q1);
                    //Log.d("MMMMMM", n + " calories " + totalcalories + "  cal " + cal + "  q1" + q1);
                } else if (n.contains(n2)) {
                    int v = 4;
                    //calories=calories+((float)(v)*q1);
                    cal = cal + ((float) (v) * q1);
                    // Log.d("MMMMMM", n + " calories " + totalcalories + "  cal " + cal + "  q1" + q1);
                } else if (n.contains(n3)) {
                    int v = 9;
                    //calories=calories+((float)(v)*q1);
                    cal = cal + ((float) (v) * q1);
                    //Log.d("MMMMMM", n + " calories " + totalcalories + "  cal " + cal + "  q1" + q1);
                } else if (n.contains(n4)) {
                    int v = 9;
                    //calories=calories+((float)(v)*q1);
                    cal = cal + ((float) (v) * q1);
                    //Log.d("MMMMMM", n + " calories " + totalcalories + "  cal " + cal + "  q1" + q1);
                } else if (n.contains(n5)) {
                    int v = 4;
                    //calories=calories+((float)(v)*q1);
                    cal = cal + ((float) (v) * q1);
                    //Log.d("MMMMMM", n + " calories " + totalcalories + "  cal " + cal + "  q1" + q1);
                } else if (n.contains(n6)) {
                    int v = 4;
                    //calories=calories+((float)(v)*q1);
                    cal = cal + ((float) (v) * q1);
                    // Log.d("MMMMMM", n + " calories " + totalcalories + "  cal " + cal + "  q1" + q1);
                } else {
                    int v = 4;
                    //calories=calories+((float)(v)*q1);
                    cal = cal + ((float) (v) * q1);
                    //Log.d("MMMMMM", n + " calories " + totalcalories + "  cal " + cal + "  q1" + q1);
                }
                totalcalories = totalcalories + cal;
                boolean flag = true;
                for (int r = 0; r < al.size(); r++) {
                    String fn = al.get(r).fooditem;
                    if (fn.equals(fi) && currenttime.equals(al.get(r).currenttime)) { //es nu mai comment keeta code chaan lyi
                        al.get(r).calories = al.get(r).calories + cal;
                        al.get(r).count = al.get(r).count + 1;
                        flag = false;
                    }
                }
                if (flag) {

                    al.add(new diethistory(fi, ddtt, cal, 1, currenttime, dietdetailid));
                }
            }

        }
        Toast.makeText(getApplicationContext(),"alsize "+al.size()+" cal total:"+totalcalories,Toast.LENGTH_SHORT).show();
        myad.notifyDataSetChanged();
        tv2.setText("Total Calories :" + totalcalories);
        Date date = new Date(selecteddate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String exactdate = sdf.format(date);
        tv1.setText("Diet History " + exactdate);
    }

    private boolean checkForTableExists(SQLiteDatabase db, String table) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            return true;
        }
        mCursor.close();
        return false;
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
        else if (item.getItemId() == R.id.m3) {
            ad.setTitle("Alert");
            ad.setIcon(R.drawable.ic_launcher_background);
            ad.setMessage("Do you really want to reset. This action delete all diet data?");
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.execSQL("DROP TABLE IF EXISTS 'nutrients'");
                    db.execSQL("DROP TABLE IF EXISTS 'fooditems'");
                    db.execSQL("DROP TABLE IF EXISTS 'fooditemsdetail'");
                    db.execSQL("DROP TABLE IF EXISTS 'diet'");
                    db.execSQL("DROP TABLE IF EXISTS 'dietdetail'");
                    Toast.makeText(getApplicationContext(), "App Rest Successfully. Add New Data Now.", Toast.LENGTH_LONG).show();
                    Intent in = new Intent(DietHistoryNewActivity.this, MainActivity.class);
                    startActivity(in);
                }
            });
            ad.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            ad.create();
            ad.show();
            //Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (item.getItemId() == R.id.m4) {
            Intent in = new Intent(DietHistoryNewActivity.this, HelpActivity.class);
            startActivity(in);
            return true;
        }
        else if (item.getItemId() == R.id.m5) {
            if(al.size()>0){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    String exactdate="";
                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = sdf.format(selecteddate);
                        Date d1 = sdf.parse(date);
                        exactdate = sdf.format(d1);
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        saveExcelFile(getApplicationContext(),"caloriereport"+exactdate+".xls");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"select any diet history first",Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId() == R.id.m6) {
            if(al.size()>0) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //Toast.makeText(getApplicationContext(),"permission not granted",Toast.LENGTH_SHORT).show();
                        // Check Permissions Now
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    } else {
                        String exactdate = "";
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String date = sdf.format(selecteddate);
                            Date d1 = sdf.parse(date);
                            exactdate = sdf.format(d1);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        readExcelFile(getApplicationContext(), "caloriereport" + exactdate + ".xls");
                    }

            }
            else{
                Toast.makeText(getApplicationContext(),"First Add Today's Diet",Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId() == R.id.m2) {
            ad.setTitle("Alert");
            ad.setIcon(R.drawable.ic_launcher_background);
            ad.setMessage("Do you really want to Delete Overall Diet History. ?");
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.execSQL("DROP TABLE IF EXISTS 'diet'");
                    db.execSQL("DROP TABLE IF EXISTS 'dietdetail'");
                    // Toast.makeText(getApplicationContext(),"Diet History Deleted Successfully.",Toast.LENGTH_LONG).show();
                    Intent in = new Intent(DietHistoryNewActivity.this, MainActivity.class);
                    startActivity(in);
                }
            });
            ad.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad.create();
            ad.show();
            //Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show();
            return true;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            String exactdate = "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());
                Date d1 = sdf.parse(date);
                exactdate = sdf.format(d1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                saveExcelFile(getApplicationContext(),"caloriereport"+exactdate+".xls");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==101&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            String exactdate = "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());
                Date d1 = sdf.parse(date);
                exactdate = sdf.format(d1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

                readExcelFile(getApplicationContext(),"caloriereport"+exactdate+".xls");

        }
        else{

        }

    }
    private void readExcelFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            // Log.e(TAG, "Storage not available or read only");
            return;
        }

        try {
            // Creating Input Stream
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "DietDiary/" + filename);
            if (!file.exists()) {
                Toast.makeText(context, "First Export History to excelfile then share", Toast.LENGTH_SHORT).show();
            } else {
                FileInputStream myInput = new FileInputStream(file);
                //  Uri uri = Uri.fromFile(file);
                // Create a POIFSFileSystem object
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                // Create a workbook using the File System
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                // Get the first sheet from workbook
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                /** We now need something to iterate through the cells.**/
                Iterator rowIter = mySheet.rowIterator();
                String diethistory = "";
                while (rowIter.hasNext()) {
                    HSSFRow myRow = (HSSFRow) rowIter.next();
                    Iterator cellIter = myRow.cellIterator();
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        diethistory = diethistory + myCell.toString() + "      ";
                    }
                    diethistory = diethistory + "\n";
                }

                if (!file.exists() || file.length() <= 0) {
                    Toast.makeText(context,"error" , Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent result = new Intent(Intent.ACTION_SEND);
                // result.setType("text/*");
                //result.setType("application/vnd.ms-excel");
                result.setType("application/*");
                try {
                    Uri fileUri = FileProvider.getUriForFile(DietHistoryNewActivity.this, "com.example.harpreet.mydietdiary.provider", file);
                    result.addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    result.putExtra(Intent.EXTRA_STREAM, fileUri);
                    context.startActivity(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context,"error", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    /////// Inner Class  ////////
    // Create ur own RecyclerAdapter
    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

        // Define ur own View Holder (Refers to Single Row)
        class MyViewHolder extends RecyclerView.ViewHolder {
            CardView singlecardview;

            // We have Changed View (which represent single row) to CardView in whole code
            public MyViewHolder(CardView itemView) {
                super(itemView);
                singlecardview = (itemView);
            }
        }

        // Inflate ur Single Row / CardView from XML here
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View viewthatcontainscardview = inflater.inflate(R.layout.cadviewdesigndiethistory, parent, false);

            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.cardview1));

            // This will call Constructor of MyViewHolder, which will further copy its reference
            // to customview (instance variable name) to make its usable in all other methods of class
            // Log.d("MYMESSAGE", "On CreateView Holder Done");
            return new MyViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            CardView localcardview = holder.singlecardview;
            localcardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), position + " clicked", Toast.LENGTH_LONG).show();
                }
            });
            final TextView tv1, tv2, tv3, tv4, tv5;
            tv1 = (TextView) (localcardview.findViewById(R.id.tv111));
            tv2 = (TextView) (localcardview.findViewById(R.id.tv222));
            tv3 = (TextView) (localcardview.findViewById(R.id.tv333));
            tv4 = (TextView) (localcardview.findViewById(R.id.tv444));
            tv5 = (TextView) (localcardview.findViewById(R.id.tv555));
            final diethistory ni = al.get(position);
            tv1.setText(ni.calories + "");
            tv4.setText("(cal)");
//            tv2.setText(ni.fooditem+"("+ni.count/2+")");
            tv2.setText(ni.fooditem);
            tv3.setText(ni.currenttime + "(" + ni.diettype + ")");
            // Picasso.with(getApplicationContext()).load(st.photo).resize(200, 100).into(imv1);
            tv5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad1.setTitle("Alert");
                    ad1.setIcon(R.drawable.ic_launcher_background);
                    ad1.setMessage("Do you really want to delete ??");
                    ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.execSQL("delete  from diet where dietid=" + ni.dietid + "");

                            db.execSQL("delete  from dietdetail where dietid=" + ni.dietid + "");
                            al.clear();
                            myad.notifyDataSetChanged();
                            tv2.setText("Total Calories :");
                            fetchdiettabledata(diettype, selecteddate);

                        }
                    });
                    ad1.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    ad1.create();
                    ad1.show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return al.size();
        }
    }

    public void showdpf(View view) {
        al.clear();
tvpath.setText("");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String date = sdf.format();
//        Date date1 = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);

        int dd = calendar.get(Calendar.DAY_OF_MONTH);


        // Log.d("VmmEducation", yy + " " + mm + " " + dd);

        myad.notifyDataSetChanged();
        tv2.setText("Total Calories :");
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                //Toast.makeText(DietHistoryActivity.this, date, Toast.LENGTH_SHORT).show();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d1 = sdf.parse(date);
                    selecteddate = d1.getTime();
                    String exactdate = sdf.format(d1);
                    tv1.setText("Diet History " + exactdate);
                    fetchdiettabledata(diettype, selecteddate);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, yy, mm, dd);
        dpd.show();
    }

    public void seegraph(View view) {
        Intent in = new Intent(DietHistoryNewActivity.this, PieGraphHistoryActivity.class);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(DietHistoryNewActivity.this, MainActivity.class);
        startActivity(in);
    }
    private static boolean saveExcelFile(Context context, String fileName) throws IOException {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            // Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.GREEN.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        CellStyle cs1 = wb.createCellStyle();
        cs1.setFillForegroundColor(HSSFColor.WHITE.index);
        cs1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myOrder");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Food Item");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Calories");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Type");
        c.setCellStyle(cs);
        c = row.createCell(3);
        c.setCellValue("Time");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Date");
        c.setCellStyle(cs);
        String exactdate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date());
            Date d1 = sdf.parse(date);
            exactdate = sdf.format(d1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int j = 0;
        float tc = 0;
        for (int i = 0; i < al.size(); i++) {
            j = j + 1;
            Row row1 = sheet1.createRow(j);
            c = row1.createCell(0);
            c.setCellValue(al.get(i).fooditem);
            c.setCellStyle(cs1);

            c = row1.createCell(1);
            c.setCellValue(al.get(i).calories + "");
            c.setCellStyle(cs1);
            tc = tc + al.get(i).calories;
            c = row1.createCell(2);
            c.setCellValue(al.get(i).diettype);
            c.setCellStyle(cs1);
            c = row1.createCell(3);
            c.setCellValue(al.get(i).currenttime + "");
            c.setCellStyle(cs1);

            c = row1.createCell(4);
            c.setCellValue(exactdate);
            c.setCellStyle(cs1);
        }
        j = j + 1;
        Row row1 = sheet1.createRow(j);

        c = row1.createCell(1);
        c.setCellValue("");
        c.setCellStyle(cs1);
        j = j + 1;
        row1 = sheet1.createRow(j);
        c = row1.createCell(1);
        c.setCellValue("Total Calories: " + tc);
        c.setCellStyle(cs1);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "DietDiary");
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "DietDiary/" + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            tvpath.setText("File saved at " + file.getPath());
            Toast.makeText(context, "File saved at " + file.getPath(), Toast.LENGTH_LONG).show();
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
