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

import java.util.ArrayList;

public class EditFoodDetailDialogActivity extends AppCompatActivity {

    ArrayAdapter<String> myadsp;
    ArrayList<nutrients> nutal=new ArrayList<>();
    ArrayList<String> nal=new ArrayList<>();
    EditText etq;
    String selectedunit="";
    String selectednutrient="";
    String orignut="";
    int position=0;
    int fiddetail;
    int fid;
    TextView tvn;
    float quantity=0;
    Spinner spn;
    SQLiteDatabase db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_detail_dialog);
        setTitle("");
        tvn=(TextView)(findViewById(R.id.tvun)) ;
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        etq=(EditText)(findViewById(R.id.etq));
        spn=(Spinner)(findViewById(R.id.spn));
        Intent in =getIntent();
        selectednutrient=in.getStringExtra("nut");
        orignut=selectednutrient;
        selectedunit=in.getStringExtra("unit");
        quantity=in.getFloatExtra("quan",0);
       // fiddetail=in.getIntExtra("fiddetail",0);
        fid=in.getIntExtra("fid",0);
        etq.setText(quantity+"");
        myadsp=  new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,nal);
        spn.setAdapter(myadsp);
        initiallogicnut();
       // Toast.makeText(getApplicationContext(),position+"",Toast.LENGTH_LONG).show();
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0) {
                    selectednutrient=nal.get(position);
                    tvn.setText(nutal.get(position-1).unit);
                    selectedunit=nutal.get(position-1).unit;
                    if(!orignut.equals(selectednutrient)) {
                        Cursor c = db.rawQuery("select * from fooditemsdetail where foodid=" + fid + " and nutrition='" + selectednutrient + "'", null);
                        if (c.moveToNext()) {
                            selectednutrient = "";
                            selectedunit = "";
                            tvn.setText("");
                            spn.setSelection(0);
                            Toast.makeText(getApplicationContext(), "Same nutrient already exist in this food item", Toast.LENGTH_SHORT).show();

                        }
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void initiallogicnut() {

            nal.clear();
            nal.add("--select nutrient--");
            Cursor c = db.rawQuery("select * from nutrients", null);
            int count=0;
            while (c.moveToNext()) {
                count=count+1;
                String nut = c.getString(c.getColumnIndex("nutrient"));
                String unit= c.getString(c.getColumnIndex("unit"));
                nutal.add(new nutrients(nut,unit));
                nal.add(nut);
                if(selectednutrient.equals(nut)){

                    //Toast.makeText(getApplicationContext(),"ok "+count,Toast.LENGTH_LONG).show();
                    position=count;
                }


            }
            myadsp.notifyDataSetChanged();

        spn.setSelection(position);
    }
    public void edit(View view) {
        String q = etq.getText().toString();
        try {

            if(spn.getSelectedItemPosition()==0)
            {
                Toast.makeText(getApplicationContext(),"Select Nutrient",Toast.LENGTH_SHORT).show();
            }
            else if(spn.getSelectedItemPosition()>0)
            {
                if(!orignut.equals(selectednutrient)) {
                    Cursor c = db.rawQuery("select * from fooditemsdetail where foodid=" + fid + " and nutrition='" + selectednutrient + "'", null);
                    if (c.moveToNext()) {
                        selectednutrient = "";
                        spn.setSelection(0);
                        tvn.setText("");
                        Toast.makeText(getApplicationContext(), "Same nutrient already exist in this food item", Toast.LENGTH_SHORT).show();

                    }
                    else if(selectedunit.equals("")){
                        Toast.makeText(getApplicationContext(),"Select Unit",Toast.LENGTH_SHORT).show();
                    }
                    else if(q.equals(""))
                    {
                        Float v=Float.parseFloat(q);
                        Toast.makeText(getApplicationContext(),"Enter Quantity",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        db.execSQL("update fooditemsdetail set nutrition='"+selectednutrient+"' where fooddetailid="+fiddetail+"");
                        db.execSQL("update fooditemsdetail set unit='"+selectedunit+"' where fooddetailid="+fiddetail+"");
                        db.execSQL("update fooditemsdetail set quantity='"+q+"' where fooddetailid="+fiddetail+"");
                        Intent in = new Intent();
                        setResult(RESULT_OK,in);
                        finish();
                    }
                }
                else if(selectedunit.equals("")){
                    Toast.makeText(getApplicationContext(),"Select Unit",Toast.LENGTH_SHORT).show();
                }
                else if(q.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Enter Quantity",Toast.LENGTH_SHORT).show();
                }
                else {
                    db.execSQL("update fooditemsdetail set nutrition='"+selectednutrient+"' where fooddetailid="+fiddetail+"");
                    db.execSQL("update fooditemsdetail set unit='"+selectedunit+"' where fooddetailid="+fiddetail+"");
                    db.execSQL("update fooditemsdetail set quantity='"+q+"' where fooddetailid="+fiddetail+"");
                    Intent in = new Intent();
                    setResult(RESULT_OK,in);
                    finish();
                }
            }
        }
        catch(Exception ex){
            Toast.makeText(getApplicationContext(),"enter quantity in numeric value",Toast.LENGTH_SHORT).show();
        }


    }
    public void cancel(View view){
        finish();
    }

}
