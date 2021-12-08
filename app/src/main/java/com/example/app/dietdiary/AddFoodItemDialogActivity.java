package com.example.app.dietdiary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.mydietdiary.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFoodItemDialogActivity extends AppCompatActivity {
    EditText et_fn, et_dn;
    SQLiteDatabase db=null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainreffooditems;
    DatabaseReference fooditemsref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item_dialog);
        setTitle("");
        et_fn=(EditText)(findViewById(R.id.et_fn));
        et_dn=(EditText)(findViewById(R.id.et_dn));
        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainreffooditems = firebaseDatabase.getReference();
        fooditemsref =mainreffooditems.child("fooditems");
       // initiallogic();
    }
//    private void initiallogic() {
//        if(checkForTableExists(db,"fooditems"))
//        {
//        }
//        else {
//            db.execSQL("create table if not exists fooditems (foodid INTEGER PRIMARY KEY AUTOINCREMENT,fname VARCHAR(1000),dname VARCHAR(1000),icon INTEGER)");
//
//        }
//    }
//    private boolean checkForTableExists(SQLiteDatabase db, String table){
//        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
//        Cursor mCursor = db.rawQuery(sql, null);
//        if (mCursor.getCount() > 0) {
//            return true;
//        }
//        mCursor.close();
//        return false;
//    }
    public void add(View view) {
        String name = et_fn.getText().toString();
        String defaultname = et_dn.getText().toString();
        if(name.equals("")){
            Toast.makeText(getApplicationContext(),"Enter Food Item Name",Toast.LENGTH_SHORT).show();
        }
        else{
//            Cursor c = db.rawQuery("select * from fooditems where fname='"+name+"'", null);
//            if(c.moveToNext()) {
//                et_fn.setText("");
//                Toast.makeText(getApplicationContext(),"Food item with same name already exists",Toast.LENGTH_SHORT).show();
//            }
//            else if(defaultname.equals(""))
//            {
//                Toast.makeText(getApplicationContext(),"Enter Default Type",Toast.LENGTH_SHORT).show();
//            }
//            else {
               // db.execSQL("insert into fooditems (fname,dname,icon) values('" + name + "','" + defaultname + "'," + R.drawable.cross + ")");
                //Toast.makeText(getApplicationContext(),"Added Successfully",Toast.LENGTH_SHORT).show();
          FoodItem   fooditem_object = new FoodItem(1,name,defaultname,2);
            DatabaseReference fooditems_reference = fooditemsref.child(name);

            fooditems_reference.setValue(fooditem_object);
                et_fn.setText("");
                et_dn.setText("");
                Intent in = new Intent();
                setResult(RESULT_OK,in);
                finish();
           // }
        }
    }
    public void cancel(View view){
        finish();
    }
}
