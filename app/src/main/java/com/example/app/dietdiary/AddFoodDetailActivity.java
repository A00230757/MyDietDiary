package com.example.app.dietdiary;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.mydietdiary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddFoodDetailActivity extends AppCompatActivity {
    ArrayAdapter<String> myadfi;
    ArrayList<FoodItemDetail> al = new ArrayList<>();
    ArrayList<FoodItem> alfi = new ArrayList<>();
    ArrayList<String> alfisimple=new ArrayList<>();
    RecyclerView rcv;
    int selectedfiid;
    String selectedfin="";
    Spinner spfi;
    MyRecyclerAdapter myad;
    SQLiteDatabase db=null;
    AlertDialog.Builder ad,ad1,ad2;
    FloatingActionButton fab1;
    NavigationView nav1;
    DrawerLayout d1;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainreffooditems;
    DatabaseReference fooditemsref;
    DatabaseReference mainreffooditemsdetail;
    DatabaseReference fooditemsdetailref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_detail);
        setTitle("Manage FoodItem Nutrients");
        nav1 = (NavigationView) (findViewById(R.id.nav1));
        d1 = (DrawerLayout) (findViewById(R.id.d1));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, d1, null, 0, 0);
        d1.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainreffooditems = firebaseDatabase.getReference();
        fooditemsref =mainreffooditems.child("fooditems");
        mainreffooditemsdetail = firebaseDatabase.getReference();
        fooditemsdetailref =mainreffooditemsdetail.child("fooditemsdetail");
        nav1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.i1) {
                    Intent in = new Intent(AddFoodDetailActivity.this, AddNutrientActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task1", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.i2) {
                    Intent in = new Intent(AddFoodDetailActivity.this, AddFoodItemActivity.class);
                    startActivity(in);

                    // Toast.makeText(MainActivity.this, "Task2", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.i3) {
                    Intent in = new Intent(AddFoodDetailActivity.this, AddFoodDetailActivity.class);
                    startActivity(in);
                    // Toast.makeText(MainActivity.this, "Task3", Toast.LENGTH_SHORT).show();

                }

                else if (item.getItemId() == R.id.i4){
                    Intent in = new Intent(AddFoodDetailActivity.this, AddDietFirstActivity.class);
                    startActivity(in);
                    //Toast.makeText(MainActivity.this, "Task4", Toast.LENGTH_SHORT).show();

                }
                else if(item.getItemId() == R.id.i5){
                    Intent in = new Intent(AddFoodDetailActivity.this, DietHistoryNewActivity.class);
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
                    Intent in = new Intent(AddFoodDetailActivity.this, MainActivity.class);
                    startActivity(in);
                }

                d1.closeDrawer(GravityCompat.START);


                return true;
            }
        });
        fab1 = (FloatingActionButton) (findViewById(R.id.fab1));
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        spfi=(Spinner)(findViewById(R.id.spfi));
        rcv = (RecyclerView) (findViewById(R.id.rcv1));
        myad = new MyRecyclerAdapter();
       rcv.setAdapter(myad);
        LinearLayoutManager simpleverticallayout = new LinearLayoutManager(this);
        rcv.setLayoutManager(simpleverticallayout);
        ad=new AlertDialog.Builder(this);
        ad1=new AlertDialog.Builder(this);
        ad2=new AlertDialog.Builder(this);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
//        rcv.setLayoutManager(gridLayoutManager);
        myadfi=  new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,alfisimple);
        spfi.setAdapter(myadfi);
        spfi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0) {
                    selectedfiid = alfi.get(position - 1).foodid;
                    selectedfin = alfi.get(position - 1).name;
                    initiallogic();
                }
                else{
                    al.clear();
                  myad.notifyDataSetChanged();
//                    myad.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AddFoodDetailActivity.this, AddFoodItemDetailDialogActivity.class);
                in.putExtra("fid", selectedfiid);
                in.putExtra("fn", selectedfin);
                startActivityForResult(in, 25);
//                                if(spfi.getSelectedItemPosition()>0) {
//                                    //Toast.makeText(AddFoodItemActivity.this, "Something clicked", Toast.LENGTH_SHORT).show();
//                                    if(checkForTableExists(db,"nutrients"))
//                                    {
//
//                                        int count=0;
//                                        Cursor c = db.rawQuery("select * from nutrients", null);
//                                        while (c.moveToNext()) {
//                                            count=count+1;
//                                            String nut = c.getString(c.getColumnIndex("nutrient"));
//                                            String unit= c.getString(c.getColumnIndex("unit"));
//
//                                        }
//                                        if(count==0){
//                                            ad2.show();
//                                        }
//                                        else{
//                                            Intent in = new Intent(AddFoodDetailActivity.this, AddFoodItemDetailDialogActivity.class);
//                                            in.putExtra("fid", selectedfiid);
//                                            in.putExtra("fn", selectedfin);
//                                            startActivityForResult(in, 25);
//                                        }
//                                    }
//                                    else {
//                                      ad2.show();
//                                    }
//
//                                }
//                                else{
//                                    Toast.makeText(getApplication(),"select any food item first",Toast.LENGTH_SHORT).show();
//                                }

            }
        });
        ad1.setTitle("Alert");
        ad1.setIcon(R.drawable.ic_launcher_background);
        ad1.setMessage("First Add some food items in MasterList. Want to proceed?");
        ad1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              Intent in=new Intent(AddFoodDetailActivity.this,AddFoodItemActivity.class);
              startActivity(in);
            }
        });

        ad1.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in=new Intent(AddFoodDetailActivity.this,MainActivity.class);
                startActivity(in);
            }
        });

        ad1.create();
        ad2.setTitle("Alert");
        ad2.setIcon(R.drawable.ic_launcher_background);
        ad2.setMessage("First Add Nutrients in master List. Want to proceed?");
        ad2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in=new Intent(AddFoodDetailActivity.this,AddNutrientActivity.class);
                startActivity(in);
            }
        });
        ad2.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in=new Intent(AddFoodDetailActivity.this,AddFoodDetailActivity.class);
                startActivity(in);
            }
        });
        ad2.create();
        initiallogicfi();
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
    private void initiallogicfi() {

        fooditemsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                al.clear();
                alfi.clear();
                alfisimple.clear();
                alfisimple.add("--select food item--");
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    FoodItem nutrienttemp = singlesnapshot.getValue(FoodItem.class);
                    try {

                        alfi.add(nutrienttemp);
                        alfisimple.add(nutrienttemp.name);

                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                myadfi.notifyDataSetChanged();
                if(alfisimple.size()==0) {
                    ad1.show();
                }
                else{
                    initiallogic();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       /* if(checkForTableExists(db,"fooditems"))
        {
            alfi.clear();
            alfisimple.clear();
            alfisimple.add("--select food item--");
            int count=0;
            Cursor c = db.rawQuery("select * from fooditems", null);
            while (c.moveToNext()) {
                count=count+1;
                int icon = c.getInt(c.getColumnIndex("icon"));
                int foodid = c.getInt(c.getColumnIndex("foodid"));
                String fname = c.getString(c.getColumnIndex("fname"));
                String dname = c.getString(c.getColumnIndex("dname"));
                alfi.add(new FoodItem(foodid,fname,dname,icon));
               alfisimple.add(fname);
            }
            //Toast.makeText(getApplicationContext(),count+"",Toast.LENGTH_SHORT).show();
            myadfi.notifyDataSetChanged();
            if(count==0) {
                ad1.show();
            }
            else{
              initiallogic();
            }
        }
        else {
   ad1.show();
        }*/
    }

    private void initiallogic() {
        al.clear();
        fooditemsdetailref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                al.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    FoodItemDetail nutrienttemp = singlesnapshot.getValue(FoodItemDetail.class);
                    try {
                        if(nutrienttemp.fooditemname.equals(selectedfin)){
                        al.add(nutrienttemp);}

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
       /* if(checkForTableExists(db,"fooditemsdetail"))
        {
            al.clear();
            Cursor c = db.rawQuery("select * from fooditemsdetail where foodid="+selectedfiid+"", null);
            int count=0;
            while (c.moveToNext()) {
                count=count+1;
                int fooddetailid = c.getInt(c.getColumnIndex("fooddetailid"));
                int foodid = c.getInt(c.getColumnIndex("foodid"));
                String nutrition = c.getString(c.getColumnIndex("nutrition"));
                String unit = c.getString(c.getColumnIndex("unit"));
                float quantity = c.getFloat(c.getColumnIndex("quantity"));
                al.add(new FoodItemDetail(fooddetailid,foodid,nutrition,unit,quantity));
            }
            myad.notifyDataSetChanged();
            if(count==0&&spfi.getSelectedItemPosition()>0){
                Toast.makeText(getApplicationContext(),"no any nutrient present in this food item click on add button to add new",Toast.LENGTH_SHORT).show();
            }
        }
        else {
if(!checkForTableExists(db,"nutrients")){
            db.execSQL("create table if not exists nutrients (nutrient VARCHAR(1000) PRIMARY KEY,unit VARCHAR(1000))");}
            db.execSQL("create table if not exists fooditemsdetail (fooddetailid INTEGER PRIMARY KEY AUTOINCREMENT,foodid INTEGER,nutrition VARCHAR(1000),unit VARCHAR(500),quantity FLOAT , FOREIGN KEY ('foodid') REFERENCES 'fooditems' ('foodid'), FOREIGN KEY ('nutrition') REFERENCES 'nutrients' ('nutrient'))");
        }*/
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

    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
        class MyViewHolder extends RecyclerView.ViewHolder {
            CardView singlecardview;
            public MyViewHolder(CardView itemView) {
                super(itemView);
                singlecardview = (itemView);
            }
        }
        @Override
        public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View viewthatcontainscardview = inflater.inflate(R.layout.cardviewdesignfooddetail, parent, false);
            CardView cardView = (CardView) (viewthatcontainscardview.findViewById(R.id.cardview1));
            Log.d("MYMESSAGE", "On CreateView Holder Done");
            return new MyViewHolder(cardView);
        }
        @Override
        public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, final int position) {
            CardView localcardview = holder.singlecardview;
            localcardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Toast.makeText(getApplicationContext(), position + " clicked", Toast.LENGTH_LONG).show();
                }
            });

            TextView  tv2,tv3,tv4;
            ImageView imv1,imv2;
            tv2 = (TextView) (localcardview.findViewById(R.id.tv222));
            tv3 = (TextView) (localcardview.findViewById(R.id.tv333));
            tv4 = (TextView) (localcardview.findViewById(R.id.tv444));
            imv1=(ImageView) (localcardview.findViewById(R.id.imv1)) ;
            imv2=(ImageView) (localcardview.findViewById(R.id.imv2)) ;
            final FoodItemDetail fi = al.get(position);
//            tv1.setText("Food Item Name:  " + selectedfin);
            tv2.setText( fi.nutritionname);
            tv3.setText("Unit: " + fi.unit);
            tv4.setText("Quantity: " + fi.quantity);
           imv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in=new Intent(getApplicationContext(),EditFoodDetailDialogActivity.class);
                    in.putExtra("nut",fi.nutritionname);
                    in.putExtra("unit",fi.unit);
                    in.putExtra("quan",fi.quantity);
//                   in.putExtra("fiddetail",fi.fooddetailid);
                  in.putExtra("fid",fi.key);
                    in.putExtra("position",position);
                    startActivityForResult(in,20);
                }
            });
            imv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                         // final  int did=fi.fooddetailid;
                            ad.setTitle("Alert");
                            ad.setIcon(R.drawable.ic_launcher_background);
                            ad.setMessage("Do you really want to delete?");
                            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //db.execSQL("delete from fooditemsdetail where fooddetailid="+did+"");
                                    fooditemsdetailref.child(fi.key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                                singleSnapshot.getRef().removeValue();
                                                initiallogic();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                    Toast.makeText(getApplication(),"deleted successfully",Toast.LENGTH_SHORT).show();
                                    al.remove(position);
                                    myad.notifyDataSetChanged();
                                }
                            });

                            ad.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            ad.create();
                            ad.show();

                        }
                    });
                }
            });
        }
        @Override
        public int getItemCount() {
            return al.size();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==20 && resultCode==RESULT_OK)
        {
            Toast.makeText(this, "updated successfully", Toast.LENGTH_SHORT).show();
            initiallogic();
        }
        if(requestCode==25 && resultCode==RESULT_OK)
        {
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            initiallogic();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MYMSG", "in on start");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MYMSG", "in on resume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MYMSG", "in on pause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MYMSG", "in on stop");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent in=new Intent(AddFoodDetailActivity.this,AddFoodDetailActivity.class);
        startActivity(in);

        Log.d("MYMSG", "in on restart");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("MYMSG", "in on destroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in=new Intent(AddFoodDetailActivity.this,MainActivity.class);
        startActivity(in);
    }
}
