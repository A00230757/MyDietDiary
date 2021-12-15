package com.example.app.dietdiary;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.app.mydietdiary.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PieGraphHistoryActivity extends AppCompatActivity {
TextView tv1;
    PieChart pieChart;
    ArrayList<String> expenseTypeList;
    ArrayList<PieChartClass> pieChartValuesList;
    SQLiteDatabase db;
    NavigationView nav1;
    DrawerLayout d1;
    ActionBarDrawerToggle actionBarDrawerToggle;
    long selecteddate;
    float totalcalories=0;
    boolean flag=false;
    float mtt =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_graph_history);
tv1=(TextView)(findViewById(R.id.tv1)) ;
        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        nav1 = (NavigationView) (findViewById(R.id.nav1));
        d1 = (DrawerLayout) (findViewById(R.id.d1));
        expenseTypeList = new ArrayList<>();
        pieChartValuesList = new ArrayList<>();
setTitle("Diet History");
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       mtt= getIntent().getFloatExtra("mtt",0);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, d1, null, 0, 0);
        d1.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        nav1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.i1) {
                    Intent in = new Intent(PieGraphHistoryActivity.this, AddNutrientActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i2) {
                    Intent in = new Intent(PieGraphHistoryActivity.this, AddFoodItemActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task2", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.i3) {
                    Intent in = new Intent(PieGraphHistoryActivity.this, AddFoodDetailActivity.class);
                    startActivity(in);
                    // Toast.makeText(MainActivity.this, "Task3", Toast.LENGTH_SHORT).show();

                }

                else if (item.getItemId() == R.id.i4){
                    Intent in = new Intent(PieGraphHistoryActivity.this, AddDietFirstActivity.class);
                    startActivity(in);
                    //Toast.makeText(MainActivity.this, "Task4", Toast.LENGTH_SHORT).show();

                }
                else if(item.getItemId() == R.id.i5){
                    Intent in = new Intent(PieGraphHistoryActivity.this, DietHistoryNewActivity.class);
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
                    Intent in = new Intent(PieGraphHistoryActivity.this, MainActivity.class);
                    startActivity(in);
                }
                d1.closeDrawer(GravityCompat.START);
                return true;
            }
        });
getExpenseList();
    }
public void specifichistory(View view){
    Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);
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
flag=true;
                getExpenseList(selecteddate);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }, yy, mm, dd);
    dpd.show();
}
    public void overallhistory(View view){
        flag=false;
getExpenseList();
tv1.setText("");
    }
    // Getting expenses type whether (refueling or other expense) and adding in the list
    public void getExpenseList() {
totalcalories=0;
pieChartValuesList.clear();
expenseTypeList.clear();
        pieChart.setData(null);
        pieChart.invalidate();
        if(checkForTableExists(db,"dietdetail")) {
            Cursor c = db.rawQuery("select distinct nutrient from dietdetail ", null);
            int count=0;
            while (c.moveToNext()) {
                count=count+1;
                String expenseType = c.getString(c.getColumnIndex("nutrient"));
                Log.d("ExpType: ", expenseType + "---------");
                expenseTypeList.add(expenseType);
            }
            if(count>0){
                getPieChartValues();
            }
            else{
                Toast.makeText(getApplicationContext(),"No Nutrient History found on this date",Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"No Any Diet History Found ? Click on diet to add New",Toast.LENGTH_SHORT).show();
        }
    }
    // Getting expenses type whether (refueling or other expense) and adding in the list
    public void getExpenseList(long seld) {
totalcalories=0;
pieChartValuesList.clear();
expenseTypeList.clear();
        pieChart.setData(null);
        pieChart.invalidate();
        if(checkForTableExists(db,"dietdetail")) {
            Cursor c = db.rawQuery("select distinct nutrient from dietdetail where entrydate="+seld+"", null);
int count=0;
            while (c.moveToNext()) {
count=count+1;
                String expenseType = c.getString(c.getColumnIndex("nutrient"));
                Log.d("ExpType: ", expenseType + "---------");
                expenseTypeList.add(expenseType);

            }

if(count>0){
    getPieChartValues();
}
else{
    Toast.makeText(getApplicationContext(),"No Nutrient History found on this date",Toast.LENGTH_LONG).show();
}

        }
        else {
            Toast.makeText(getApplicationContext(),"No Any Diet History Found ? Click on diet to add New",Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {

            if (d1.isDrawerOpen(GravityCompat.START)) {
                d1.closeDrawer(GravityCompat.START);
            }
            else {
                d1.openDrawer(GravityCompat.START);
            }

        }


        return true;
    }
    // Getting values to show on Pie chart and adding in a arraylist
    public void getPieChartValues() {
        totalcalories=0;
        pieChartValuesList.clear();
        if(flag==false){

            for(int i=0;i<expenseTypeList.size();i++){
                String nutriento=expenseTypeList.get(i);
                String  nutrient=expenseTypeList.get(i).toUpperCase();
                float calpern=0;
                Cursor c = db.rawQuery("select * from dietdetail where nutrient='"+nutriento+"'", null);
                while (c.moveToNext()) {

                    float q1= c.getFloat(c.getColumnIndex("quantity"));
                    String n1="CARBOHYDRATE";
                    String n2="PROTEIN";
                    String n3="FAT";
                    String n4="VITAMIN";
                    String n5="MINERAL";
                    String n6="CALCIUM";
                    float cal=0;
                    if(nutrient.contains(n1)){
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n2)){
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n3)){
                        int v=9;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n4)){
                        int v=9;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n5)){
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n6)){
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else{
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }

                }
                pieChartValuesList.add(new PieChartClass(calpern, i));

            }

            Log.d("MMMMMM"," overall calories "+totalcalories+"");
            updatePieChart(totalcalories);
        }
        else if (flag==true){
            for(int i=0;i<expenseTypeList.size();i++){
                String nutriento=expenseTypeList.get(i);
                String  nutrient=expenseTypeList.get(i).toUpperCase();
                float calpern=0;
                Cursor c = db.rawQuery("select * from dietdetail where nutrient='"+nutriento+"' and entrydate="+selecteddate+"", null);
                while (c.moveToNext()) {

                    float q1= c.getFloat(c.getColumnIndex("quantity"));
                    String n1="CARBOHYDRATE";
                    String n2="PROTEIN";
                    String n3="FAT";
                    String n4="VITAMIN";
                    String n5="MINERAL";
                    String n6="CALCIUM";
                    float cal=0;
                    if(nutrient.contains(n1)){
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n2)){
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n3)){
                        int v=9;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n4)){
                        int v=9;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n5)){
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else if(nutrient.contains(n6)){
                        int v=4;
                        //calories=calories+((float)(v)*q1);
                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }
                    else{
                        int v=4;
                        //calories=calories+((float)(v)*q1);

                        cal=cal+((float)(v)*q1);
                        calpern=calpern+cal;
                        totalcalories=totalcalories+cal;
                        Log.d("MMMMMM",nutrient+" calories "+totalcalories+"  cal "+cal+"  q1"+q1);
                    }

                }
                pieChartValuesList.add(new PieChartClass(calpern, i));

            }
Toast.makeText(getApplicationContext(),totalcalories+"",Toast.LENGTH_SHORT).show();
            Log.d("MMMMMM"," overall calories "+totalcalories+"");
            updatePieChart(totalcalories);
        }


    }

    // updating pie chart with the pie chart values
    public void updatePieChart(float totalExpense){

        ArrayList<PieEntry> yvalues = new ArrayList<>();
yvalues.clear();
        for(int i=0; i<pieChartValuesList.size(); i++){

            float per = pieChartValuesList.get(i).percentage;
            String type = expenseTypeList.get(i);

            yvalues.add(new PieEntry(per, type));

        }

        PieDataSet dataSet = new PieDataSet(yvalues,"");

        PieData data = new PieData(dataSet);

        pieChart.invalidate();

        //data.setDataSet((IPieDataSet) xVals);
        // In Percentage
        data.setValueFormatter(new PercentFormatter());
        // Default value
        //data.setValueFormatter(new DefaultValueFormatter(0));
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(58f);
        pieChart.setCenterText(generateCenterSpannableText(totalExpense));
        // if no need to add description
        pieChart.getDescription().setEnabled(false);
        // animation and the center text color
        pieChart.animateY(3000);
        pieChart.setEntryLabelColor(Color.BLACK);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        //dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        //dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        //dataSet.setColors(ColorTemplate.LIBERTY_COLORS);

        //dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

    }
public class PieChartClass{
        Float percentage;
        int i;
        public PieChartClass(Float percentage,int i){
this.percentage=percentage;
this.i=i;
        }
}
    // If we need to display center text with textStyle
    private SpannableString generateCenterSpannableText(float totalExpense) {
        SpannableString s = new SpannableString("TOTAL Calories\n "+mtt+" cal ");
        s.setSpan(new RelativeSizeSpan(2f), 11, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 11, s.length(), 0);
        return s;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(PieGraphHistoryActivity.this, DietHistoryNewActivity.class);
        startActivity(in);
    }
}
