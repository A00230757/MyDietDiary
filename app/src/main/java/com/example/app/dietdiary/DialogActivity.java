package com.example.app.dietdiary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.mydietdiary.R;

public class DialogActivity extends AppCompatActivity {
EditText et1,et2;
    SQLiteDatabase db=null;
    int fid;
    String origfname="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setTitle("");
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        et1=(EditText) (findViewById(R.id.et1));
        et2=(EditText) (findViewById(R.id.et2));
        Intent in=getIntent();
         fid=in.getIntExtra("fid",0);
        String fname=in.getStringExtra("fname");
        origfname=fname;
        String dn=in.getStringExtra("dname");
        et1.setText(fname);
        et2.setText(dn);
    }
    public void cancel(View view){
        finish();
    }
    public void edit(View view)
    {
        String fname=et1.getText().toString();
        String dname=et2.getText().toString();
        if(fname.equals("")){
            Toast.makeText(getApplicationContext(),"Enter Food Item Name",Toast.LENGTH_SHORT).show();

        }
        else{
            Cursor c = db.rawQuery("select * from fooditems where fname='"+fname+"'", null);
            if(c.moveToNext()) {
                if (!fname.equals(origfname)) {
                    et1.setText("");
                    Toast.makeText(getApplicationContext(), "Food item with same name already exists", Toast.LENGTH_SHORT).show();
                }
                else if(dname.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Enter Default Type",Toast.LENGTH_SHORT).show();
                }
                else {
                    db.execSQL("update fooditems set fname='"+fname+"' where foodid="+fid+"");
                    db.execSQL("update fooditems set dname='"+dname+"' where foodid="+fid+"");
                    //Toast.makeText(this, "updated successfully", Toast.LENGTH_SHORT).show();
                    et1.setText("");
                    et2.setText("");
                    Intent in = new Intent();
                    setResult(RESULT_OK,in);
                    finish();
                }
            }
            else if(dname.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Enter Default Type",Toast.LENGTH_SHORT).show();
            }
            else {
                db.execSQL("update fooditems set fname='"+fname+"' where foodid="+fid+"");
                db.execSQL("update fooditems set dname='"+dname+"' where foodid="+fid+"");
                //Toast.makeText(this, "updated successfully", Toast.LENGTH_SHORT).show();
                et1.setText("");
                et2.setText("");
                Intent in = new Intent();
                setResult(RESULT_OK,in);
                finish();
            }
        }

    }
}
