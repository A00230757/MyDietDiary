package com.example.app.dietdiary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.mydietdiary.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNutrientDialogActivity extends AppCompatActivity {
    EditText et1;
    RadioButton rb1,rb2,rb3;
    RadioGroup rbg;
    String selectedunit="";
    SQLiteDatabase db=null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefnutrients;
    DatabaseReference nutrientsref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nutrient_dialog);
        setTitle("");
        et1=(EditText)(findViewById(R.id.et1));
        rb1= (RadioButton) (findViewById(R.id.rb1));
        rb2= (RadioButton) (findViewById(R.id.rb2));
        rb3= (RadioButton) (findViewById(R.id.rb3));
        rbg = (RadioGroup) (findViewById(R.id.rg1));
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefnutrients = firebaseDatabase.getReference();
        nutrientsref =mainrefnutrients.child("nutrients");

        rbg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rb1)
                {
                    selectedunit=rb1.getText().toString();
                }
                else if(checkedId==R.id.rb2)
                {
                    selectedunit=rb2.getText().toString();
                }
                else if(checkedId==R.id.rb3)
                {
                    selectedunit=rb3.getText().toString();
                }
            }
        });
    }
    public void cancel(View view){
        finish();
    }
    public void add(View view)
    {
        if(checkForTableExists(db,"nutrients"))
        {
            String nn=et1.getText().toString();
            if(nn.equals(""))
            {
                Toast.makeText(getApplicationContext(),"enter nutrient name",Toast.LENGTH_SHORT).show();
            }
            else if(selectedunit.equals(""))
            {
                Toast.makeText(getApplicationContext(),"select unit",Toast.LENGTH_SHORT).show();
            }
            else {
                nutrients nutrient_object = new nutrients(nn.toString(),selectedunit.toString());
                DatabaseReference nutrients_reference = nutrientsref.child(nn.toString());

                    nutrients_reference.setValue(nutrient_object);

               // db.execSQL("insert into nutrients (nutrient,unit) values('" + nn + "','" + selectedunit + "')");
                Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                et1.setText("");
                Intent in = new Intent();
                setResult(RESULT_OK,in);
                finish();
            }
        }
        else {

            //db.execSQL("create table if not exists nutrients (nutrient VARCHAR(1000) PRIMARY KEY,unit VARCHAR(1000))");
            String nn=et1.getText().toString();
            if(nn.equals(""))
            {
                Toast.makeText(getApplicationContext(),"enter nutrient name",Toast.LENGTH_SHORT).show();
            }
            else if(selectedunit.equals(""))
            {
                Toast.makeText(getApplicationContext(),"select unit",Toast.LENGTH_SHORT).show();
            }
            else {
                nutrients nutrient_object = new nutrients(nn.toString(),selectedunit.toString());
                DatabaseReference nutrients_reference = nutrientsref.child(nn.toString());

                nutrients_reference.setValue(nutrient_object);

               // db.execSQL("insert into nutrients (nutrient,unit) values('" + nn + "','" + selectedunit + "')");
                Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                et1.setText("");
                Intent in = new Intent();
                setResult(RESULT_OK,in);
                finish();
            }
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
}
