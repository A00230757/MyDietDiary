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

public class AddFoodItemActivity extends AppCompatActivity {

    ArrayList<FoodItem> al = new ArrayList<>();
    RecyclerView rcv;
    AlertDialog.Builder ad;
    FloatingActionButton fab1;
    //MyRecyclerAdapter myad;
    SQLiteDatabase db = null;
    NavigationView nav1;
    DrawerLayout d1;
    ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);
        setTitle("Manage FoodItems");
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
                    Intent in = new Intent(AddFoodItemActivity.this, AddNutrientActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i2) {
                    Intent in = new Intent(AddFoodItemActivity.this, AddFoodItemActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task2", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.i3) {
                    Intent in = new Intent(AddFoodItemActivity.this, AddFoodDetailActivity.class);
                    startActivity(in);
                    // Toast.makeText(MainActivity.this, "Task3", Toast.LENGTH_SHORT).show();

                }
//
//                else if (item.getItemId() == R.id.i4){
//                    Intent in = new Intent(AddFoodItemActivity.this, AddDietFirstActivity.class);
//                    startActivity(in);
//                    //Toast.makeText(MainActivity.this, "Task4", Toast.LENGTH_SHORT).show();
//
//                }
//                else if(item.getItemId() == R.id.i5){
//                    Intent in = new Intent(AddFoodItemActivity.this, DietHistoryNewActivity.class);
//                    startActivity(in);
//                }


                else if(item.getItemId() == R.id.i6){
                    String message = "Whether you're trying to lose weight or just attempting to eat healthier, Download a DietDiary App(keeping calorie records)  can help you make positive changes. ";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
                }              else{
                    Intent in = new Intent(AddFoodItemActivity.this, MainActivity.class);
                    startActivity(in);
                }

                d1.closeDrawer(GravityCompat.START);


                return true;
            }
        });
        fab1 = (FloatingActionButton) (findViewById(R.id.fab1));
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        rcv = (RecyclerView) (findViewById(R.id.rcv1));
        //myad = new MyRecyclerAdapter();
        //rcv.setAdapter(myad);
        ad = new AlertDialog.Builder(this);
        //Specifying Layout Manager to RecyclerView is Compulsary for Proper Rendering
        LinearLayoutManager simpleverticallayout = new LinearLayoutManager(this);
        rcv.setLayoutManager(simpleverticallayout);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
//        rcv.setLayoutManager(gridLayoutManager);
        initiallogic();
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                                //Toast.makeText(AddFoodItemActivity.this, "Something clicked", Toast.LENGTH_SHORT).show();
                                Intent in = new Intent(AddFoodItemActivity.this, AddFoodItemDialogActivity.class);
                                startActivityForResult(in, 5);

            }
        });
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
        if (checkForTableExists(db, "fooditems")) {
            al.clear();
            Cursor c = db.rawQuery("select * from fooditems", null);
            int count = 0;
            while (c.moveToNext()) {
                count = count + 1;
                int icon = c.getInt(c.getColumnIndex("icon"));
                int foodid = c.getInt(c.getColumnIndex("foodid"));
                String fname = c.getString(c.getColumnIndex("fname"));
                String dname = c.getString(c.getColumnIndex("dname"));
                al.add(new FoodItem(foodid, fname, dname, icon));
            }
          //  myad.notifyDataSetChanged();
            if (count == 0) {
                Toast.makeText(getApplicationContext(), "no any food item present click on add button to add new", Toast.LENGTH_SHORT).show();
            }
        } else {
            db.execSQL("create table if not exists fooditems (foodid INTEGER PRIMARY KEY AUTOINCREMENT,fname VARCHAR(1000),dname VARCHAR(1000),icon INTEGER)");

        }
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

//    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
//        class MyViewHolder extends RecyclerView.ViewHolder {
//            CardView singlecardview;
//
//            public MyViewHolder(CardView itemView) {
//                super(itemView);
//                singlecardview = (itemView);
//            }
//        }
//
//        @Override
//        public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            View viewthatcontainscardview = inflater.inflate(R.layout.cardviewdesign, parent, false);
//            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.cardview1));
//            Log.d("MYMESSAGE", "On CreateView Holder Done");
//            return new MyViewHolder(cardView);
//        }
//
//        @Override
//        public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, final int position) {
//            CardView localcardview = holder.singlecardview;
//            localcardview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //Toast.makeText(getApplicationContext(), position + " clicked", Toast.LENGTH_LONG).show();
//                }
//            });
//            TextView tv1, tv2;
//            ImageView imv1,imv2;
//            tv1 = (TextView) (localcardview.findViewById(R.id.tv111));
//            tv2 = (TextView) (localcardview.findViewById(R.id.tv222));
//            imv1 = (ImageView) (localcardview.findViewById(R.id.imv1));
//            imv2 = (ImageView) (localcardview.findViewById(R.id.imv2));
//            final FoodItem fi = al.get(position);
//            tv1.setText( fi.name);
//            tv2.setText("("+fi.defaultname+")");
//           imv1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent in = new Intent(getApplicationContext(), DialogActivity.class);
//                    in.putExtra("fname", fi.name);
//                    in.putExtra("dname", fi.defaultname);
//                    in.putExtra("fid", fi.foodid);
//                    startActivityForResult(in, 10);
//                }
//            });
//            imv2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            final int fid = fi.foodid;
//                            ad.setTitle("Alert");
//                            ad.setIcon(R.drawable.ic_launcher_background);
//                            ad.setMessage("Do you really want to delete?");
//                            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    db.execSQL("delete from fooditems where foodid=" + fid + "");
//                                    Toast.makeText(getApplication(), "deleted successfully", Toast.LENGTH_SHORT).show();
//                                    al.remove(position);
//                                    myad.notifyDataSetChanged();
//                                }
//                            });
//                            ad.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            });
//                            ad.create();
//                            ad.show();
//                        }
//                    });
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return al.size();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Toast.makeText(this, "updated successfully", Toast.LENGTH_SHORT).show();
            initiallogic();
        } else if (requestCode == 5 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            initiallogic();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(AddFoodItemActivity.this, MainActivity.class);
        startActivity(in);
    }
}
