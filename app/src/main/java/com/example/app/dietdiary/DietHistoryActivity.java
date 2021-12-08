package com.example.app.dietdiary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.mydietdiary.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DietHistoryActivity extends AppCompatActivity {

    NavigationView nav1;
    DrawerLayout d1;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView tv1;
long startdate=0;
long enddate=0;
ArrayList<Integer> alcal=new ArrayList<>();

    SQLiteDatabase db=null;
    TextView tvh;
    ArrayList<nutrients> alnutrients = new ArrayList<>();
    ArrayList<diet> aldiet = new ArrayList<>();
    ArrayList<dietdetail> aldietdetail = new ArrayList<>();
    LineChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_history);
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        mChart = findViewById(R.id.chart1);
        mChart.setPinchZoom(false);
        mChart.setTouchEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.getAxisRight().setEnabled(false);
        tv1 = (TextView) (findViewById(R.id.tv1));
        tvh=(TextView)(findViewById(R.id.tvh)) ;
        nav1 = (NavigationView) (findViewById(R.id.nav1));
        d1 = (DrawerLayout) (findViewById(R.id.d1));
        setTitle("My Diet Diary");
        android.view.Display display = ((android.view.WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, d1, null, 0, 0);
        d1.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        nav1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.i1) {
                    Intent in = new Intent(DietHistoryActivity.this, AddNutrientActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i2) {
                    Intent in = new Intent(DietHistoryActivity.this, AddFoodItemActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task2", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i3) {
                    Intent in = new Intent(DietHistoryActivity.this, AddFoodDetailActivity.class);
                    startActivity(in);
                    // Toast.makeText(MainActivity.this, "Task3", Toast.LENGTH_SHORT).show();

                }


                else if (item.getItemId() == R.id.i4){
                    Intent in = new Intent(DietHistoryActivity.this, AddDietFirstActivity.class);
                    startActivity(in);
                    //Toast.makeText(MainActivity.this, "Task4", Toast.LENGTH_SHORT).show();

                }
                else if(item.getItemId() == R.id.i5){
                    Intent in = new Intent(DietHistoryActivity.this, DietHistoryNewActivity.class);
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
                    Intent in = new Intent(DietHistoryActivity.this, MainActivity.class);
                    startActivity(in);
                }
                d1.closeDrawer(GravityCompat.START);


                return true;
            }
        });
        initiallogic();

    }
    private void initiallogic() {
        if(checkForTableExists(db,"nutrients"))
        {
            alnutrients.clear();
            int count=0;
            Cursor c = db.rawQuery("select * from nutrients", null);
            while (c.moveToNext()) {
                count=count+1;
                String nut = c.getString(c.getColumnIndex("nutrient"));
                String unit= c.getString(c.getColumnIndex("unit"));
                alnutrients.add(new nutrients(nut,unit));

            }
            if(count==0){
                Toast.makeText(getApplicationContext(),"no any nutrient present",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"no any nutrient present ",Toast.LENGTH_SHORT).show();
        }


    }

    private void fetchdiettabledata(Long sd,Long ed) {
        if(checkForTableExists(db,"diet"))
        {
            aldiet.clear();
            int count=0;
            try {

                Cursor c = db.rawQuery("select * from diet WHERE dt BETWEEN "+sd+" AND "+ed+"", null);
                aldietdetail.clear();
                while (c.moveToNext()) {
                    count=count+1;
                    int dietid = c.getInt(c.getColumnIndex("dietid"));
                    long dt= c.getLong(c.getColumnIndex("dt"));
                    String diettype= c.getString(c.getColumnIndex("diettype"));
                    aldiet.add(new diet(dietid,diettype,dt));
                    fetchdietdetailtabledata(dietid);
                }
                calculatecalories();
                if(count==0){
                    Toast.makeText(getApplicationContext(),"no any diet present",Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

        }
        else {
            Toast.makeText(getApplicationContext(),"no any diet present ",Toast.LENGTH_SHORT).show();
        }

    }
    private void setData(int count, float range)
    {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++)
        {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.cross)));
        }

        LineDataSet set1 = new LineDataSet(values, "DataSet 1");

        set1.setDrawIcons(false);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);


        set1.setFillColor(Color.BLACK);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(dataSets);

        // set data
        mChart.setData(data);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void fetchdietdetailtabledata(int did) {
        if(checkForTableExists(db,"diet"))
        {

            int count=0;
            Cursor c = db.rawQuery("select * from dietdetail where dietid="+did+"", null);
            while (c.moveToNext()) {
                count=count+1;
                int dietdetailid = c.getInt(c.getColumnIndex("dietdetailid"));
                int dietid = c.getInt(c.getColumnIndex("dietid"));
                float quantity= c.getFloat(c.getColumnIndex("quantity"));
                String fname= c.getString(c.getColumnIndex("fname"));
                String nutrient= c.getString(c.getColumnIndex("nutrient"));
                String unit= c.getString(c.getColumnIndex("unit"));
                String currenttime= c.getString(c.getColumnIndex("currenttime"));
                aldietdetail.add(new dietdetail(dietdetailid,dietid,fname,nutrient,unit,quantity,currenttime));

            }
            if(count==0){
                Toast.makeText(getApplicationContext(),"no any dietdetail present",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"no any dietdetail present ",Toast.LENGTH_SHORT).show();
        }

    }

    private void calculatecalories() {
        tvh.setText("");
        for(int k=0;k<aldiet.size();k++){
            Long dt=aldiet.get(k).date;
            int mainid=aldiet.get(k).dietid;
            String type=aldiet.get(k).diettype;
            Date date = new Date(dt);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String exactdate= sdf.format(date);
            Log.d("MYDTTD",dt+" "+exactdate);
            float calories=0;
            for(int i=0;i<aldietdetail.size();i++)
            {
                if(aldietdetail.get(i).dietid==mainid){
                    float q1=aldietdetail.get(i).quantity;
                    String n=aldietdetail.get(i).nutrient;
                    n=n.toUpperCase();
                    String u=aldietdetail.get(i).unit;
                    String n1="CARBOHYDRATE";
                    String n2="PROTEIN";
                    String n3="FAT";
                    for(int j=0;j<alnutrients.size();j++)
                    {
                        String nn=alnutrients.get(j).name.toUpperCase();
                        if(n.equals(nn)&&u.equals(alnutrients.get(j).unit)){
                            if(nn.equals(n1)){
                                calories=calories+4*q1;
                            }
                            else if(nn.equals(n2)){
                                calories=calories+4*q1;
                            }
                            else if(nn.equals(n3)){
                                calories=calories+9*q1;
                            }
                        }
                    }

                }


            }
            tvh.append("Date:- "+exactdate);
            tvh.append("  Calories:- "+calories);
           alcal.add((int)calories);
        }
        setData(5,3);

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

    public void nextscreen(View view) {
        Intent in = new Intent(DietHistoryActivity.this, AddDietActivity.class);
        startActivity(in);
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

    public void showdpf(View view) {
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                //Toast.makeText(DietHistoryActivity.this, date, Toast.LENGTH_SHORT).show();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    Date d1 = sdf.parse(date);
                  startdate = d1.getTime();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, 2018, 8, 15);
        dpd.show();


    }
    public void showdpt(View view) {
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                //Toast.makeText(DietHistoryActivity.this, date, Toast.LENGTH_SHORT).show();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    Date d1 = sdf.parse(date);
                   enddate= d1.getTime();
                    fetchdiettabledata(startdate,enddate);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, 2018, 8, 15);
        dpd.show();


    }
    @Override
    public void onBackPressed () {
        super.onBackPressed();
        Intent in = new Intent(DietHistoryActivity.this, MainActivity.class);
        startActivity(in);
    }
}
