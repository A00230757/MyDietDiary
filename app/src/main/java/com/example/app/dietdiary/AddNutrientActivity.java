package com.example.app.dietdiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.mydietdiary.R;

import java.util.ArrayList;

public class AddNutrientActivity extends AppCompatActivity {
    ArrayList<nutrients> al = new ArrayList<>();
    RecyclerView rcv;
    AlertDialog.Builder ad;
    FloatingActionButton fab1;
    //MyRecyclerAdapter myad;
    SQLiteDatabase db=null;
    NavigationView nav1;
    DrawerLayout d1;
    ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nutrient);
        setTitle("Manage Nutrients");
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
                    Intent in = new Intent(AddNutrientActivity.this, AddNutrientActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i2) {
                    Intent in = new Intent(AddNutrientActivity.this, AddFoodItemActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task2", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.i3) {
                    Intent in = new Intent(AddNutrientActivity.this, AddFoodDetailActivity.class);
                    startActivity(in);
                    // Toast.makeText(MainActivity.this, "Task3", Toast.LENGTH_SHORT).show();

                }

//                else if (item.getItemId() == R.id.i4){
//                    Intent in = new Intent(AddNutrientActivity.this, AddDietFirstActivity.class);
//                    startActivity(in);
//                    //Toast.makeText(MainActivity.this, "Task4", Toast.LENGTH_SHORT).show();
//
//                }
//                else if(item.getItemId() == R.id.i5){
//                    Intent in = new Intent(AddNutrientActivity.this, DietHistoryNewActivity.class);
//                    startActivity(in);
//                }
                else if(item.getItemId() == R.id.i6){
                    String message = "Whether you're trying to lose weight or just attempting to eat healthier, Download a DietDiary App(keeping calorie records)  can help you make positive changes. ";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
                }
                else{
                    Intent in = new Intent(AddNutrientActivity.this, MainActivity.class);
                    startActivity(in);
                }

                d1.closeDrawer(GravityCompat.START);


                return true;
            }
        });
        fab1 = (FloatingActionButton) (findViewById(R.id.fab1));

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                                //Toast.makeText(AddFoodItemActivity.this, "Something clicked", Toast.LENGTH_SHORT).show();
                                Intent in=new Intent(AddNutrientActivity.this,AddNutrientDialogActivity.class);
                                startActivityForResult(in,6);


            }
        });
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);


        rcv = (RecyclerView) (findViewById(R.id.rcv1));

        //myad = new MyRecyclerAdapter();

       // rcv.setAdapter(myad);
        ad = new AlertDialog.Builder(this);
        //Specifying Layout Manager to RecyclerView is Compulsary for Proper Rendering
        LinearLayoutManager simpleverticallayout = new LinearLayoutManager(this);
        rcv.setLayoutManager(simpleverticallayout);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
//        rcv.setLayoutManager(gridLayoutManager);


        initiallogic();
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

    private void initiallogic() {
        if(checkForTableExists(db,"nutrients"))
        {
al.clear();
int count=0;
            Cursor c = db.rawQuery("select * from nutrients", null);
            while (c.moveToNext()) {
                count=count+1;
                String nut = c.getString(c.getColumnIndex("nutrient"));
                String unit= c.getString(c.getColumnIndex("unit"));
                al.add(new nutrients(nut,unit));

            }
        //myad.notifyDataSetChanged();
            if(count==0){
                Toast.makeText(getApplicationContext(),"no any nutrient present click on add button to add new",Toast.LENGTH_SHORT).show();
            }
        }
        else {

            db.execSQL("create table if not exists nutrients (nutrient VARCHAR(1000) PRIMARY KEY,unit VARCHAR(1000))");
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



    /////// Inner Class  ////////
    // Create ur own RecyclerAdapter
//    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
//
//        // Define ur own View Holder (Refers to Single Row)
//        class MyViewHolder extends RecyclerView.ViewHolder {
//            CardView singlecardview;
//
//            // We have Changed View (which represent single row) to CardView in whole code
//            public MyViewHolder(CardView itemView) {
//                super(itemView);
//                singlecardview = (itemView);
//            }
//        }
//
//        // Inflate ur Single Row / CardView from XML here
//        @Override
//        public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//
//            View viewthatcontainscardview = inflater.inflate(R.layout.cardviewnutrients, parent, false);
//
//            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.cardview1));
//
//            // This will call Constructor of MyViewHolder, which will further copy its reference
//            // to customview (instance variable name) to make its usable in all other methods of class
//            Log.d("MYMESSAGE", "On CreateView Holder Done");
//            return new MyViewHolder(cardView);
//        }
//
//        @Override
//        public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, final int position) {
//
//            CardView localcardview = holder.singlecardview;
//
//            localcardview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                   // Toast.makeText(getApplicationContext(), position + " clicked", Toast.LENGTH_LONG).show();
//                }
//            });
//
//            TextView tv1, tv2;
//            ImageView imv1;
//           // Button btdelete;
//            tv1 = (TextView) (localcardview.findViewById(R.id.tv111));
//            tv2 = (TextView) (localcardview.findViewById(R.id.tv222));
//          imv1 = (ImageView) (localcardview.findViewById(R.id.imv1));
//           // btdelete=(Button)(localcardview.findViewById(R.id.btdelete)) ;
//
//            final nutrients ni = al.get(position);
//
//            tv1.setText(ni.name);
//            tv2.setText("Unit : " + ni.unit);
//            // Picasso.with(getApplicationContext()).load(st.photo).resize(200, 100).into(imv1);
//
//            imv1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            final String nn=ni.name;
//
//                            ad.setTitle("Alert");
//                            ad.setIcon(R.drawable.ic_launcher_background);
//                            ad.setMessage("Do you really want to delete?");
//                            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    db.execSQL("delete from nutrients where nutrient='"+nn+"'");
//                                    Toast.makeText(getApplication(),"deleted successfully",Toast.LENGTH_SHORT).show();
//                                    al.remove(position);
//                                    myad.notifyDataSetChanged();
//                                }
//                            });
//
//                            ad.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            });
//
//                            ad.create();
//                            ad.show();
//                        }
//                    });
//
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//
//            return al.size();
//        }
//
//
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


         if(requestCode==6 && resultCode==RESULT_OK){
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            initiallogic();
        }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in=new Intent(AddNutrientActivity.this,MainActivity.class);
        startActivity(in);
    }

}
