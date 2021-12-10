package com.example.app.dietdiary;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.mydietdiary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class AddFoodItemDetailDialogActivity extends AppCompatActivity {
    ArrayAdapter<String> myadsp;
    ArrayList<nutrients> nutal=new ArrayList<>();
    ArrayList<String> nal=new ArrayList<>();
    EditText etq;
    int selectedfiid;
    String fname="";
    TextView tvf;
    String selectedunit="";
    String selectednutrient="";
    Spinner spfi,spn;
    TextView tvn;
    SQLiteDatabase db=null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainreffooditemsdetail;
    DatabaseReference fooditemsdetailref;
    DatabaseReference mainrefnutrients;
    DatabaseReference nutrientsref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item_detail_dialog);
        setTitle("");
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        tvf=(TextView)(findViewById(R.id.tvfid));
        Intent in=getIntent();
        selectedfiid=in.getIntExtra("fid",0);
        fname=in.getStringExtra("fn");
        tvf.setText(fname);
        tvn=(TextView)(findViewById(R.id.tvun)) ;
        etq=(EditText)(findViewById(R.id.etq));
        spfi=(Spinner)(findViewById(R.id.spfi));
        spn=(Spinner)(findViewById(R.id.spn));
        myadsp=  new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,nal);
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainreffooditemsdetail = firebaseDatabase.getReference();
        fooditemsdetailref =mainreffooditemsdetail.child("fooditemsdetail");
        mainrefnutrients = firebaseDatabase.getReference();
        nutrientsref =mainrefnutrients.child("nutrients");
        spn.setAdapter(myadsp);
        initiallogicnut();
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getApplicationContext(),spn.getSelectedItemPosition()+"",Toast.LENGTH_SHORT).show();
                if(position>0) {
                    selectednutrient=nal.get(position);
                    int p = position + 1;
                    tvn.setText(nutal.get(position-1).unit);
                    selectedunit=nutal.get(position-1).unit;
//                    Cursor c = db.rawQuery("select * from fooditemsdetail where foodid="+selectedfiid+" and nutrition='"+selectednutrient+"'", null);
//                    if (c.moveToNext()) {
//                        selectednutrient="";
//                        selectedunit="";
//                        tvn.setText("");
//                        spn.setSelection(0);
//                        Toast.makeText(getApplicationContext(),"Same nutrient already exist in this food item",Toast.LENGTH_SHORT).show();
//                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }
    public void cancel(View view){
        finish();
    }
    private void initiallogicnut() {
        nal.clear();
        nal.add("--select nutrient--");
        nutrientsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    nutrients nutrienttemp = singlesnapshot.getValue(nutrients.class);
                    try {
                        nal.add(nutrienttemp.name);
                        nutal.add(new nutrients(nutrienttemp.name,nutrienttemp.unit));

                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                myadsp.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        if(checkForTableExists(db,"nutrients"))
//        {
//            nal.clear();
//            nal.add("--select nutrient--");
//            int count=0;
//            Cursor c = db.rawQuery("select * from nutrients", null);
//            while (c.moveToNext()) {
//                count=count+1;
//                String nut = c.getString(c.getColumnIndex("nutrient"));
//                String unit= c.getString(c.getColumnIndex("unit"));
//                nutal.add(new nutrients(nut,unit));
//                nal.add(nut);
//            }
//            myadsp.notifyDataSetChanged();
//            if(count==0){
//
//            }
//        }
//        else {
//        }
    }
/*    private boolean checkForTableExists(SQLiteDatabase db, String table){
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            return true;
        }
        mCursor.close();
        return false;
    }*/
    public void add(View view) {
        String q = etq.getText().toString();
        try{
            if(spn.getSelectedItemPosition()==0)
            {
                Toast.makeText(getApplicationContext(),"Select Nutrient",Toast.LENGTH_SHORT).show();
            }
            else if(spn.getSelectedItemPosition()>0)
            {
//                Cursor c = db.rawQuery("select * from fooditemsdetail where foodid="+selectedfiid+" and nutrition='"+selectednutrient+"'", null);
//                if (c.moveToNext()) {
//                    selectednutrient="";
//                    spn.setSelection(0);
//                    Toast.makeText(getApplicationContext(),"Same nutrient already exist in this food item",Toast.LENGTH_SHORT).show();
//                }
//                else if(selectedunit.equals("")){
//                    Toast.makeText(getApplicationContext(),"Select Unit",Toast.LENGTH_SHORT).show();
//                }
//                else if(q.equals(""))
//                {
//                    Float v=Float.parseFloat(q);
//                    Toast.makeText(getApplicationContext(),"Enter Quantity",Toast.LENGTH_SHORT).show();
//                }
//                else {
                   // db.execSQL("insert into fooditemsdetail (foodid,nutrition,unit,quantity) values(" + selectedfiid + ",'" + selectednutrient + "','" + selectedunit + "'," + q + ")");
                long millis = System.currentTimeMillis();
                FoodItemDetail nutrient_object = new FoodItemDetail(millis+"",selectednutrient ,selectedunit ,Float.parseFloat(q),fname );

                DatabaseReference nutrients_reference = fooditemsdetailref.child(millis+"");

                nutrients_reference.setValue(nutrient_object);
                Toast.makeText(getApplicationContext(),"Added Successfully",Toast.LENGTH_SHORT).show();
                    Intent in = new Intent();
                    setResult(RESULT_OK,in);
                    finish();
                //}
            }
        }
        catch(Exception ex){
            Toast.makeText(getApplicationContext(),"enter quantity in numeric value",Toast.LENGTH_SHORT).show();
        }
    }
}
