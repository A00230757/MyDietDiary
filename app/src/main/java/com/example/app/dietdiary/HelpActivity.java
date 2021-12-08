package com.example.app.dietdiary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.app.mydietdiary.R;

public class HelpActivity extends AppCompatActivity {
    NavigationView nav1;
    DrawerLayout d1;
    ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
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
                    Intent in = new Intent(HelpActivity.this, AddNutrientActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i2) {
                    Intent in = new Intent(HelpActivity.this, AddFoodItemActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task2", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.i3) {
                    Intent in = new Intent(HelpActivity.this, AddFoodDetailActivity.class);
                    startActivity(in);
                    // Toast.makeText(MainActivity.this, "Task3", Toast.LENGTH_SHORT).show();

                }

                else if (item.getItemId() == R.id.i4){
                    Intent in = new Intent(HelpActivity.this, AddDietFirstActivity.class);
                    startActivity(in);
                    //Toast.makeText(MainActivity.this, "Task4", Toast.LENGTH_SHORT).show();

                }
                else if(item.getItemId() == R.id.i5){
                    Intent in = new Intent(HelpActivity.this, DietHistoryNewActivity.class);
                    startActivity(in);
                }
                else if(item.getItemId() == R.id.i6){
                    String message = "Whether you're trying to lose weight or just attempting to eat healthier, Download a  DietDiary App which will help you make positive changes. ";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
                }
                else{
                    Intent in = new Intent(HelpActivity.this, MainActivity.class);
                    startActivity(in);
                }

                d1.closeDrawer(GravityCompat.START);


                return true;
            }
        });
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
    @Override
    public void onBackPressed () {
        super.onBackPressed();
        Intent in = new Intent(HelpActivity.this, MainActivity.class);
        startActivity(in);
    }
}
